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

package com.goforer.beatery.helper;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.goforer.beatery.BEatery;
import com.goforer.beatery.R;
import com.goforer.beatery.model.data.request.Device;
import com.goforer.beatery.web.storage.PreferenceStorage;

public enum DeviceHelper {
    INSTANCE;

    public static final String PROPERTY_TOKEN = "beatery::token";
    public static final String PROPERTY_APP_VERSION = "beatery::appVersion";
    public static final String KEY_DEVICE_INFO = "beatery::device_info";

    public Device getDevice(Context context, String token) {
        Device device = new Device();

        device.setAppName(context.getString(R.string.app_name));
        device.setAppVersion(BEatery.getVersionCode(context));
        device.setDeviceVersion(Build.VERSION.RELEASE);
        device.setOsVersion(String.valueOf(Build.VERSION.SDK_INT));
        device.setDeviceId(getUniqueString(context));
        device.setName(Build.DEVICE);
        device.setBrand(Build.BRAND);
        device.setModel(Build.MODEL);
        device.setManufacture(Build.MANUFACTURER);
        device.setToken(token);
        return device;
    }

    public String getUniqueString(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String getToken(Context context) {
        PreferenceStorage storage = PreferenceStorage.getInstance();
        String authorizedEntity = storage.get(context, KEY_DEVICE_INFO, PROPERTY_TOKEN);
        String registeredVersion = storage.get(context, KEY_DEVICE_INFO, PROPERTY_APP_VERSION);
        int currentVersion = BEatery.getVersionCode(context);

        if (!String.valueOf(currentVersion).equals(registeredVersion)) {
            return null;
        }

        return authorizedEntity;
    }

    public void storeToken(Context context, String token) {
        PreferenceStorage storage = PreferenceStorage.getInstance();

        int appVersion = BEatery.getVersionCode(context);
        storage.put(context, KEY_DEVICE_INFO, PROPERTY_TOKEN, token);
        storage.put(context, KEY_DEVICE_INFO, PROPERTY_APP_VERSION, String.valueOf(appVersion));
    }
}
