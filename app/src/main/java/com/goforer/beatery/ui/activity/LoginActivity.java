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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.goforer.beatery.model.event.action.LoginAction;
import com.goforer.beatery.R;
import com.goforer.base.ui.activity.BaseActivity;
import com.goforer.beatery.model.event.LoginSoftKeyBoardEvent;
import com.goforer.beatery.model.event.action.LogoutAction;
import com.goforer.beatery.ui.fragment.LoginFragment;
import com.goforer.beatery.utillity.ActivityCaller;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LoginActivity extends BaseActivity {
    public static final String EXTRA_LOGIN_MODE = "extra_login_mode";

    public static final int LOGIN_MODE_GOOGLE_ID = 100;

    private int mLoginMode = LOGIN_MODE_GOOGLE_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = null;

        if(savedInstanceState != null){
            bundle = savedInstanceState;
        }else if(getIntent() != null){
            bundle = getIntent().getExtras();
        }

        if(bundle != null){
            mLoginMode = bundle.getInt(EXTRA_LOGIN_MODE, LOGIN_MODE_GOOGLE_ID);
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_LOGIN_MODE, mLoginMode);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void setActionBar() {
    }

    @Override
    protected void setViews() {
        transactFragment(LoginFragment.class, R.id.content_holder, null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAction(LogoutAction action) {
        if(!TextUtils.isEmpty(action.getMessage())){
            Toast.makeText(this, action.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAction(LoginAction action) {
        finish();
    }

    public void moveToEateryList() {
        ActivityCaller.INSTANCE.callEateryList(this);
        supportFinishAfterTransition();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LoginFragment.REQUEST_CODE_RESOLVE_ERR) {
            if (resultCode == Activity.RESULT_OK) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentByTag(LoginFragment.class.getName());
                if(fragment != null){
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            final View contentView = findViewById(R.id.content_holder);
            if (contentView != null) {
                contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        contentView.getWindowVisibleDisplayFrame(r);
                        int screenHeight = contentView.getRootView().getHeight();
                        int keypadHeight = screenHeight - r.bottom;
                        if (keypadHeight > screenHeight * 0.15) { 
                            LoginSoftKeyBoardEvent event = new LoginSoftKeyBoardEvent();
                            event.mState = LoginSoftKeyBoardEvent.KEYBOARD_SHOW;
                            EventBus.getDefault().post(event);
                        } else {
                            LoginSoftKeyBoardEvent event = new LoginSoftKeyBoardEvent();
                            event.mState = LoginSoftKeyBoardEvent.KEYBOARD_HIDE;
                            EventBus.getDefault().post(event);
                        }
                    }
                });
            }
        }
    }
}
