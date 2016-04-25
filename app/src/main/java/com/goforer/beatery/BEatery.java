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

package com.goforer.beatery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.facebook.FacebookSdk;
import com.goforer.base.ui.activity.BaseActivity;
import com.goforer.base.ui.view.CustomDialog;
import com.goforer.beatery.helper.model.data.UpdateInfo;
import com.goforer.beatery.model.data.response.ServerUpdateInfo;
import com.goforer.beatery.utillity.ActivityCaller;
import com.goforer.beatery.utillity.ExceptionHandler;
import com.goforer.beatery.web.storage.PreferenceStorage;
import com.goforer.beatery.web.wire.connecter.request.RequestClient;

/**
 * Base class for those who need to maintain global application state.
 *
 * @see android.app.Application
 */
public class BEatery extends android.app.Application {
    private static final String TAG = "BEatery";

    public static Context mContext;
    public static Resources mResources;

    private static CustomDialog mDialogUpdate;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
        mResources = getResources();

        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(getApplicationContext());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.UncaughtExceptionHandler exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
                try {
                    exceptionHandler = new ExceptionHandler(BEatery.this, exceptionHandler, new ExceptionHandler.OnFindCrashLogListener() {

                        @Override
                        public void onFindCrashLog(String log) {
                            Log.e("onFindCrashLog", log);
                        }

                        @Override
                        public void onCaughtCrash(Throwable throwable) {
                            Log.e(TAG, "Ooooooops Crashed!!");
                        }
                    });

                    Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
                } catch( NullPointerException e ) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public synchronized static void updateNotify(final UpdateInfo updateInfo) {
        if (BaseActivity.mCurrentActivity == null) {
            return;
        }

        if (isPreferenceUpdateSkip()) return;

        if (updateInfo.getForcedUpdate().equalsIgnoreCase(RequestClient.FORCED_UPDATE_YES)) {
            showUpdateNotifyForce(updateInfo);
        } else {
            showUpdateNotifyManual(updateInfo);
        }
    }

    public static boolean isPreferenceUpdateSkip() {
        boolean ret = false;
        if (PreferenceStorage.getInstance().get(mContext, UpdateInfo.KEY_UPDATE_STORAGE,
                UpdateInfo.KEY_UPDATE_SKIP_DATA, RequestClient.FORCED_UPDATE_NO)
                .equalsIgnoreCase(RequestClient.FORCED_UPDATE_YES)) {
            ret = true;
        }
        return ret;
    }

    public static void showServerUpdate(final ServerUpdateInfo serverUpdateInfo) {
        if (mDialogUpdate != null) {
            mDialogUpdate.dismiss();
        }

        CustomDialog.Builder build = new CustomDialog.Builder(BaseActivity.mCurrentActivity);
        build.setTitle(serverUpdateInfo.getTitle());
        build.setMessage(serverUpdateInfo.getDescription());
        build.setPositiveButton(R.string.custom_dialog_exit_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeApplication();
                dialog.dismiss();
            }
        });

        if (serverUpdateInfo.getNoticeUrl() != null && serverUpdateInfo.getNoticeUrl().length() > 0) {
            build.setNegativeButton(R.string.custom_dialog_notification_title, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (serverUpdateInfo.getNoticeUrl() == null || serverUpdateInfo.getNoticeUrl().length() <= 0) return;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(serverUpdateInfo.getNoticeUrl()));
                    BaseActivity.mCurrentActivity.startActivity(intent);
                }
            });
        }

        mDialogUpdate = build.create();
        mDialogUpdate.show();
    }

    public static void closeApplication() {
        System.exit(0);
    }

    private static void showUpdateNotifyForce(final UpdateInfo updateInfo) {
        if (mDialogUpdate != null) mDialogUpdate.dismiss();
        mDialogUpdate = new CustomDialog.Builder(BaseActivity.mCurrentActivity)
                .setTitle(R.string.update_dialog_title)
                .setMessage(updateInfo.getDescription())
                .setPositiveButton(R.string.update_dialog_btn_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCaller.INSTANCE.callMarketUpdate(BaseActivity.mCurrentActivity, updateInfo.getMarketUrl());
                        PreferenceStorage storage = PreferenceStorage.getInstance();
                        storage.put(mContext, UpdateInfo.KEY_UPDATE_STORAGE, UpdateInfo.KEY_UPDATE_SKIP_DATA, RequestClient.FORCED_UPDATE_NO);
                        dialog.dismiss();
                    }
                })
                .create();
        mDialogUpdate.show();
    }

    private static void showUpdateNotifyManual(final UpdateInfo updateInfo) {
        if (mDialogUpdate != null) mDialogUpdate.dismiss();
        mDialogUpdate = new CustomDialog.Builder(BaseActivity.mCurrentActivity)
                .setTitle(R.string.update_dialog_title)
                .setMessage(updateInfo.getDescription())
                .setPositiveButton(R.string.update_dialog_btn_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCaller.INSTANCE.callMarketUpdate(BaseActivity.mCurrentActivity, updateInfo.getMarketUrl());
                        PreferenceStorage storage = PreferenceStorage.getInstance();
                        storage.put(mContext, UpdateInfo.KEY_UPDATE_STORAGE, UpdateInfo.KEY_UPDATE_SKIP_DATA, RequestClient.FORCED_UPDATE_NO);
                        dialog.dismiss();
                    }
                })
                .create();
        mDialogUpdate.show();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    public static int getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
