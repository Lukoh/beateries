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

package com.goforer.beatery.model.data.request;

import com.google.gson.annotations.SerializedName;

public class Device {
    private final String OS = "android";

    @SerializedName("app_name")
    private String mAppName;
    @SerializedName("device_version")
    private String mDeviceVersion;
    @SerializedName("os_version")
    private String mOsVersion;
    @SerializedName("device_id")
    private String mDeviceId;
    @SerializedName("device_name")
    private String mName;
    @SerializedName("device_brand")
    private String mBrand;
    @SerializedName("device_model")
    private String mModel;
    @SerializedName("device_manufacture")
    private String mManufacture;
    @SerializedName("device_token")
    private String mToken;
    @SerializedName("app_version")
    private int mAppVersion;
    @SerializedName("device_type")
    private int mDeviceTypeIndex = 2;

    public final String getOs() {
        return OS;
    }

    public String getAppName() {
        return mAppName;
    }

    public String getDeviceVersion() {
        return mDeviceVersion;
    }

    public String getOsVersion() {
        return mOsVersion;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    public String getName() {
        return mName;
    }

    public String getBrand() {
        return mBrand;
    }

    public String getModel() {
        return mModel;
    }

    public String getManufacture() {
        return mManufacture;
    }

    public String getToken() {
        return mToken;
    }

    public int getAppVersion() {
        return mAppVersion;
    }

    public int getDeviceTypeIndex() {
        return mDeviceTypeIndex;
    }

    public void setAppName(String name) {
        mAppName = name;
    }

    public void setDeviceVersion(String version) {
        mDeviceVersion = version;
    }

    public void setDeviceId(String id) {
        mDeviceId = id;
    }

    public void setOsVersion(String version) {
        mOsVersion = version;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setBrand(String brand) {
        mBrand = brand;
    }

    public void setModel(String model) {
        mModel = model;
    }

    public void setManufacture(String manufacture) {
        mManufacture = manufacture;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public void setAppVersion(int version) {
        mAppVersion = version;
    }

    public void setDeviceTypeIndex(int index) {
        mDeviceTypeIndex = index;
    }
}
