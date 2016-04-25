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

package com.goforer.base.model.data;

public enum GPSData {
    INSTANCE;

    private static String mCountry;
    private static String mCountryCode;
    private static String mAdminArea;
    private static String mCity;
    private static String mThoroughfare;
    private static String mSubThoroughfare;

    private static double mLatitude;
    private static double mLongitude;

    public String getContry() {
        return mCountry;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public String getAdminArea() {
        return mAdminArea;
    }

    public String getCity() {
        return mCity;
    }

    public String getThoroughfare() {
        return mThoroughfare;
    }

    public String getSubThoroughfare() {
        return mSubThoroughfare;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }

    public void setAminArea(String aminArea) {
        mAdminArea = aminArea;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public void setThoroughfare(String thoroughfare) {
        mThoroughfare = thoroughfare;
    }

    public void setSubThoroughfare(String subThoroughfare) {
        mSubThoroughfare = subThoroughfare;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }
}
