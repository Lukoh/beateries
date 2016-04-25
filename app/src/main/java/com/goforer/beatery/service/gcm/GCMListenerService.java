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

package com.goforer.beatery.service.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.goforer.beatery.R;
import com.goforer.beatery.ui.activity.EventViewerActivity;
import com.goforer.beatery.utillity.ActivityCaller;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * The class shows how to communicate with Google Cloud Messageing.
 * It also provides functionality such as automatically displaying notifications
 * when requested by app server.
 * @see <a href="https://developers.google.com/cloud-messaging/">notifications
 * when requested by app server</a>
 */
public class GCMListenerService extends GcmListenerService {
    private static final String TAG = "GCMListenerService";

    public static final String TYPE_NOTICE = "notice";
    public static final String TYPE_CUSTOM = "custom";
    public static final String TYPE_EVENT = "event";
    public static final String TYPE_ADMIN = "admin";

    private long mEateryId;

    private String mEateryName;
    private String mCustomData;

    private int mSmallIconId;
    private int mLargeIconId;

    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        String title = bundle.getString("title");
        String message = bundle.getString("message");
        String type = bundle.getString("type");

        if (type == null) {
            return;
        }

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Type: " + type);
        Log.d(TAG, "Title: " + title);
        Log.d(TAG, "Message: " + message);

        switch (type) {
            case TYPE_EVENT:
                mEateryId = bundle.getLong(ActivityCaller.BUNDLE_EATERY_ID, -1);
                mEateryName = bundle.getString(ActivityCaller.BUNDLE_EATERY_NAME);
                break;
            case TYPE_CUSTOM:
                mCustomData =  bundle.getString(ActivityCaller.BUNDLE_CUSTOM_DATA);
                break;
            case TYPE_ADMIN:
                // Will be implemented after setting up Admin server
                break;
            default:
                break;
        }

        callNotification(type, title, message);
    }

    private Intent getPendingIntent(Context context, String type){
        if (type == null) {
            return null;
        }

        Intent intent = null;
        switch (type) {
            case TYPE_NOTICE:
                mLargeIconId = R.drawable.ic_gcm_notice;
                break;
            case TYPE_EVENT:
                intent = ActivityCaller.INSTANCE.createIntent(context, EventViewerActivity.class,
                        true);
                intent.putExtra(ActivityCaller.EXTRA_EATERY_ID, mEateryId);
                intent.putExtra(ActivityCaller.EXTRA_EATERY_NAME, mEateryName);
                mLargeIconId = R.drawable.ic_gcm_event;
                break;
            case TYPE_CUSTOM:
                intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(ActivityCaller.INSTANCE.getBase().toString() + mCustomData);
                intent.setData(uri);
                mLargeIconId = R.drawable.ic_gcm_custom;
                break;
            case TYPE_ADMIN:
                mLargeIconId = R.drawable.ic_gcm_admin;
                break;
            default:
                mLargeIconId = R.drawable.ic_gcm_notice;
                break;
        }

        mSmallIconId = R.drawable.ic_gcm_statusbar_notice;
        return intent;
    }

    private Notification build(Context context, String type, String title, String message) {
        int color;
        Intent intent = getPendingIntent(context, type);
        if (intent == null) {
            return null;
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = context.getResources().getColor(R.color.bg_common, null);
        } else {
            color = context.getResources().getColor(R.color.bg_common);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
        .setCategory(NotificationCompat.CATEGORY_SOCIAL)
        .setColor(color)
        .setContentTitle(title)
        .setContentText(message)
        .setContentIntent(PendingIntent.getActivity(context.getApplicationContext(),
                (int)System.currentTimeMillis()/1000, intent, PendingIntent.FLAG_UPDATE_CURRENT))
        .setTicker(message)
        .setSmallIcon(mSmallIconId)
        .setLargeIcon(mLargeIconId > 0 ? BitmapFactory.decodeResource(getResources(),
                mLargeIconId) : BitmapFactory.decodeResource(getResources(), R.drawable.ic_beatery))
        .setAutoCancel(true)
        .setOnlyAlertOnce(false)
        .setLights(color, 1000, 500)
        .setSound(defaultSoundUri)
        .setVibrate(new long[]{0, 500, 500})
        .setWhen(System.currentTimeMillis());
        return builder.build();
    }

    private void callNotification(String type, String title, String message) {
        Notification notification = build(this, type, title, message);
        if (notification == null) {
            return;
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(type.hashCode() /* ID of notification */, notification);
    }
}
