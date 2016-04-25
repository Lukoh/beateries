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

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.goforer.beatery.R;
import com.goforer.beatery.helper.DeviceHelper;
import com.goforer.beatery.web.wire.connecter.Intermediary;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

/**
 * The class shows the steps involved in making a GCM client-side application on Android.
 * At a minimum, a GCM client app must include code to register (and thereby get a registration token),
 * and a receiver to receive messages sent by GCM. See {@link GCMListenerService}
 *
 * <p>
 * Please visit to below site if you'd like to see how to set up a GCM Client App.
 * <p/>
 *
 * @see <a href="https://developers.google.com/cloud-messaging/android/client>How to set up
 * a GCM Clent App</a>
 */
public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            registerToServer(token);
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
        }
    }

    /**
     * Register the device information and token to BEeatery servers.
     *
     * Modify this method to associate the user's GCM registration token with any
     * server-side account maintained by your application.
     *
     * @param token The new token.
     */
    private void registerToServer(String token) {
        if (!TextUtils.isEmpty(token)) {
            DeviceHelper.INSTANCE.storeToken(this, token);
        }

        Intermediary.INSTANCE.setDeviceInfo(this, DeviceHelper.INSTANCE.getDevice(this, token),
                null);
    }

}
