/*
 * Copyright (C) 2015-2016 Lukoh Nam, goForer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goforer.beatery.ui.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.goforer.base.ui.activity.BaseActivity;
import com.goforer.base.ui.fragment.BaseFragment;
import com.goforer.base.ui.view.CustomDialog;
import com.goforer.beatery.R;
import com.goforer.beatery.helper.AccountHelper;
import com.goforer.beatery.model.data.response.User;
import com.goforer.beatery.model.event.LoginEvent;
import com.goforer.beatery.ui.activity.LoginActivity;
import com.goforer.beatery.ui.activity.SignUpActivity;
import com.goforer.beatery.utillity.ActivityCaller;
import com.goforer.beatery.web.wire.connecter.Intermediary;
import com.goforer.beatery.web.wire.connecter.reponse.ResponseClient;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.JsonElement;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import butterknife.OnClick;

public class LoginFragment extends BaseFragment {
    public static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    public static final String LOG_IN_GOOGLE = "google+";
    public static final String LOG_IN_FACEBOOK = "facebook";

    private ProgressDialog mConnectionProgressDialog;

    private GoogleApiClient mGoogleApiClientForClear;
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mGoogleConnectionResult;

    private CallbackManager mFacebookCallbackManager;

    private String mSnsId;
    private String mToken;
    private String mEmail;

    private int mAccountType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(!FacebookSdk.isInitialized()){
            FacebookSdk.sdkInitialize(mContext);
        }

        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mConnectionProgressDialog = new ProgressDialog(mActivity);
        mConnectionProgressDialog.setMessage(getString(R.string.progress_login));

        startGoogleLogIn();
        startFacebookLogIn();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        if(mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }

    private void startGoogleLogIn() {
        GoogleApiClient.ConnectionCallbacks googleConnectionCallbacks =
                new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
                            String scope = "oauth2:" + Scopes.PLUS_LOGIN + " " + Scopes.PLUS_ME;
                            mEmail = accountName;
                            return GoogleAuthUtil.getToken(mActivity, accountName, scope);
                        } catch (UserRecoverableAuthException e) {
                            startActivityForResult(e.getIntent(), REQUEST_CODE_RESOLVE_ERR);
                            return UserRecoverableAuthException.class.getName();
                        } catch (IOException | GoogleAuthException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(String accessToken) {
                        super.onPostExecute(accessToken);
                        if (UserRecoverableAuthException.class.getName().equals(accessToken)) {
                            return;
                        } else if (!TextUtils.isEmpty(accessToken)) {
                            Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                            if (person != null) {
                                mSnsId = person.getId();
                                mToken = accessToken;
                                if (!TextUtils.isEmpty(mSnsId)) {
                                    mAccountType = AccountHelper.ACCOUNT_TYPE_GOOGLE;
                                    requestLogin(mSnsId, mToken, mEmail, LOG_IN_GOOGLE);
                                    return;
                                }
                            }
                        }

                        mConnectionProgressDialog.dismiss();
                        Toast.makeText(mActivity, R.string.toast_login_fail, Toast.LENGTH_SHORT).show();
                        mGoogleApiClient.disconnect();
                    }
                }.execute();
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        };

        GoogleApiClient.OnConnectionFailedListener googleConnectionFailedListener =
                new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                mGoogleConnectionResult = connectionResult;
                if (connectionResult.hasResolution()) {
                    googleConnectionRetry();
                    if (!mConnectionProgressDialog.isShowing()) {
                        mConnectionProgressDialog.show();
                    }
                }
            }
        };

        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addConnectionCallbacks(googleConnectionCallbacks)
                .addOnConnectionFailedListener(googleConnectionFailedListener)
                .build();
    }

    private void startFacebookLogIn(){
        mFacebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mSnsId = loginResult.getAccessToken().getUserId();
                mToken = loginResult.getAccessToken().getToken();
                CheckFacebookEmail(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                mConnectionProgressDialog.dismiss();
                Toast.makeText(mActivity, R.string.toast_login_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CheckFacebookEmail(AccessToken facebookToken){
        GraphRequest request = GraphRequest.newMeRequest(facebookToken,
                new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                mEmail = graphResponse.getJSONObject().optString("email", "");
                mAccountType = AccountHelper.ACCOUNT_TYPE_FACEBOOK;
                if (TextUtils.isEmpty(mEmail)) {
                    mEmail = facebookAccountSingle(mActivity);
                    if (TextUtils.isEmpty(mEmail)) {
                        CustomDialog builder = new CustomDialog.Builder(BaseActivity.mCurrentActivity)
                                .setTitle(R.string.custom_dialog_notification_title)
                                .setMessage(R.string.custom_dialog_login_facebook_no_email)
                                .setPositiveButton(R.string.custom_dialog_login_action_confirm,
                                        new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create();
                        builder.show();
                    } else {
                        requestLogin(mSnsId, mToken, mEmail, LOG_IN_FACEBOOK);
                    }
                }else{
                    requestLogin(mSnsId, mToken, mEmail, LOG_IN_FACEBOOK);
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public static String facebookAccountSingle(Context context) {
        String ret = "";
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccounts();
        int accountCnt = 0;
        if (accounts != null) {
            for (Account account : accounts) {
                if (account.type.contains("com.facebook")) {
                    ret = account.name;
                    accountCnt++;
                }
            }

            if (accountCnt != 1) {
                ret = "";
            }
        }
        return ret;
    }

    private void googleConnectionRetry() {
        try {
            mGoogleConnectionResult.startResolutionForResult(mActivity, REQUEST_CODE_RESOLVE_ERR);
        } catch (IntentSender.SendIntentException e) {
            mGoogleConnectionResult = null;
            mGoogleApiClient.connect();
        }
    }

    private void googleClearConnection() {
        if(mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

        mGoogleApiClientForClear = new GoogleApiClient.Builder(mActivity)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClientForClear);
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                            }
                        }).build();
        mGoogleApiClientForClear.connect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_RESOLVE_ERR) {
            mConnectionProgressDialog.dismiss();
            if (resultCode == Activity.RESULT_OK) {
                mGoogleConnectionResult = null;
                mGoogleApiClient.connect();
            }
        }
    }

    private void GoogleLogin() {
        googleClearConnection();
        if (!mGoogleApiClient.isConnected()) {
            if (mGoogleConnectionResult == null) {
                mConnectionProgressDialog.show();
                mGoogleApiClient.connect();
            } else {
                googleConnectionRetry();
            }
        }
    }

    private void FacebookLogin() {
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("public_profile, email"));
    }

    private void requestLogin(String sns_id, String token, String email, String snsType) {
        LoginEvent event = new LoginEvent();
        event.setTag(LoginFragment.class.getName());
        Intermediary.INSTANCE.login(mContext, sns_id, token, email, snsType, event);
        if (!mConnectionProgressDialog.isShowing()) {
            mConnectionProgressDialog.show();
        }
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent event) {
        if(!event.isMine(LoginFragment.class.getName())) {
            return;
        }

        mConnectionProgressDialog.dismiss();
        if(event.getResponseClient() != null &&
                event.getResponseClient().getResponseCode() == ResponseClient.CODE_NONE_USER) {
            if (mActivity instanceof LoginActivity) {
                ActivityCaller.INSTANCE.callSignUp(mContext, mSnsId, mToken, mEmail, mAccountType,
                        SignUpActivity.CALL_SIGN_UP_TYPE);
            }
        } else if (event.getResponseClient() != null && event.getResponseClient().isSuccessful()) {
            JsonElement jsonElement = event.getResponseClient().getResponseEntity().getAsJsonArray().get(0);
            User me = User.gson().fromJson(jsonElement, User.class);
            AccountHelper.putMe(mContext, me);
            if(mActivity instanceof LoginActivity) {
                ((LoginActivity)mActivity).moveToEateryList();
            }
        } else {
            CustomDialog builder = new CustomDialog.Builder(mActivity)
            .setTitle(R.string.custom_dialog_login_title)
            .setMessage(event.getResponseClient().getResponseMessage())
            .setPositiveButton(R.string.custom_dialog_login_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .create();
            builder.show();
        }
    }

    @SuppressWarnings("")
    @OnClick(R.id.google_signup_bar)
    void onLoginGoogle() {
        GoogleLogin();
    }

    @SuppressWarnings("")
    @OnClick(R.id.facebook_signup_bar)
    void onLoginFacebook() {
        FacebookLogin();
    }

}
