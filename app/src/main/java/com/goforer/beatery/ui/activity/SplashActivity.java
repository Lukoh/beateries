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

package com.goforer.beatery.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.goforer.base.ui.activity.BaseActivity;
import com.goforer.beatery.R;
import com.goforer.beatery.helper.AccountHelper;
import com.goforer.beatery.helper.model.data.UpdateInfo;
import com.goforer.beatery.model.event.LogoutEvent;
import com.goforer.beatery.model.event.action.LogoutAction;
import com.goforer.beatery.service.gcm.RegistrationIntentService;
import com.goforer.beatery.utillity.ActivityCaller;
import com.goforer.beatery.web.storage.PreferenceStorage;
import com.goforer.beatery.web.communicator.Intermediary;
import com.goforer.beatery.web.communicator.request.RequestClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SplashActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "SplashActivity";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int MIN_SPLASH_TIME = 2000;

    private long mSplashStart;

    private GoogleApiClient mGoogleApiClient;
    private Intent mRegistrationIntent;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_splash);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSplashStart = System.currentTimeMillis();

        PreferenceStorage storage = PreferenceStorage.getInstance();
        storage.put(this, UpdateInfo.KEY_UPDATE_STORAGE, UpdateInfo.KEY_UPDATE_SKIP_DATA, "N");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPlayServices()) {
            Log.e(TAG, "Can't search google play service!!!");
            return;
        }

        mRegistrationIntent = new Intent(this, RegistrationIntentService.class);
        startService(mRegistrationIntent);

        String url = getIntent().getStringExtra("URL");

        if (url != null) {
            RequestClient.INSTANCE.setURL(url);
            if (AccountHelper.getMyIdx(this) < 0) {
                RequestClient.INSTANCE.getRequestMethod(this);
                moveToLogin();
            } else {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if(accessToken != null){
                    LoginManager.getInstance().logOut();
                    requestLogout();
                } else {
                    mGoogleApiClient = new GoogleApiClient.Builder(SplashActivity.this)
                            .addApi(Plus.API)
                            .addScope(Plus.SCOPE_PLUS_PROFILE)
                            .addConnectionCallbacks(SplashActivity.this)
                            .addOnConnectionFailedListener(SplashActivity.this)
                            .build();
                    mGoogleApiClient.connect();
                }

                showProgress(R.string.logout);
            }
            Log.i(TAG, "URL change " + url);
        } else {
            onWait();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
        requestLogout();
    }

    @Override
    public void onConnectionSuspended(int i) {
        requestLogout();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        requestLogout();
    }

    private void onWait() {
        long elapsed = System.currentTimeMillis() - mSplashStart;

        long more_splash = MIN_SPLASH_TIME <= elapsed ? 0 : MIN_SPLASH_TIME - elapsed;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AccountHelper.getMyIdx(getApplicationContext()) < 0) {
                    moveToLogin();
                } else {
                    moveToMain();
                }
            }
        }, more_splash);
    }

    private void moveToLogin() {
        ActivityCaller.INSTANCE.callLogIn(this);
        stopService(mRegistrationIntent);
        finish();
    }

    private void moveToMain() {
        ActivityCaller.INSTANCE.callEateryList(this);
        stopService(mRegistrationIntent);
        finish();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }

            return false;
        }

        return true;
    }

    private void requestLogout() {
        LogoutEvent event = new LogoutEvent();
        Intermediary.INSTANCE.logout(this, event);
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(LogoutEvent event) {
        AccountHelper.removeAccount(this, new LogoutAction());
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAction(LogoutAction action) {
        dismissProgress();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(LoginActivity.EXTRA_LOGIN_MODE, LoginActivity.LOGIN_MODE_GOOGLE_ID);
        startActivity(intent);
        super.onAction(action);
    }
}
