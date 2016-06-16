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

package com.goforer.beatery.model.data.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.goforer.base.model.BaseModel;
import com.goforer.base.model.data.Image;
import com.google.gson.annotations.SerializedName;

/**
 * The class for putting the eatery's information after parsing JSON formatted data that
 * got from BEatery server
 *
 * <p>
 * Please refer to below link if you'd like to get more details.
 * The link provides REST APIs.
 *
 * @see <a href="https://github.com/goforer/beatery/BEatery_REST_API.pdf">
 *     BEatery REST APIs</a>
 * </p>
 */
public class EateryInfo extends BaseModel implements Parcelable {
    @SerializedName("id")
    private long mId;
    @SerializedName("country_code")
    private String mCountryCode;
    @SerializedName("name")
    private String mName;
    @SerializedName("best_menu")
    private String mBestMenu;
    @SerializedName("address")
    private String mAddress;
    @SerializedName("telephone")
    private String mTelephone;
    @SerializedName("website")
    private String mWebsite;
    @SerializedName("type")
    private String mType;
    @SerializedName("logo")
    private Image mLogo;
    @SerializedName("preference")
    private int mPreference;
    @SerializedName("information")
    private String mInformation;
    @SerializedName("more_information")
    private String mMoreInformation;
    @SerializedName("has_event")
    private boolean mHasEvent;
    @SerializedName("has_gallery")
    private boolean mHasGallery;
    @SerializedName("comment_count")
    private long mCommentCount;
    @SerializedName("like_count")
    private long mLikeCount;
    @SerializedName("latitude")
    private double mLatitude;
    @SerializedName("longitude")
    private double mLongitude;

    public EateryInfo() {
    }

    protected EateryInfo(Parcel in) {
        mId = in.readLong();
        mCountryCode = in.readString();
        mName = in.readString();
        mBestMenu = in.readString();
        mAddress = in.readString();
        mTelephone = in.readString();
        mWebsite = in.readString();
        mType = in.readString();
        mLogo = in.readParcelable(Image.class.getClassLoader());
        mPreference = in.readInt();
        mInformation = in.readString();
        mMoreInformation = in.readString();
        mHasEvent = in.readByte() != 0;
        mHasGallery = in.readByte() != 0;
        mCommentCount = in.readLong();
        mLikeCount = in.readLong();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
    }

    public long getId() {
        return mId;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public String getName() {
        return mName;
    }

    public String getBestMenu() {
        return mBestMenu;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getTelephone() {
        return mTelephone;
    }

    public String getWebsite() {
        return mWebsite;
    }

    public Image getLogo() {
        return mLogo;
    }

    public String getType() {
        return mType;
    }

    public int getPreference() {
        return mPreference;
    }

    public String getInformation() {
        return mInformation;
    }

    public String getDetailInformation() {
        return mMoreInformation;
    }

    public long getCommentCount() {
        return mCommentCount;
    }

    public long getLikeCount() {
        return mLikeCount;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public boolean hasEvent() {
        return mHasEvent;
    }

    public boolean hasGallery() {
        return mHasGallery;
    }

    public void setId(long id) { mId = id; }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setBestMenu(String menu) {
        mBestMenu = menu;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public void setTelephone(String telephone) {
        mTelephone = telephone;
    }

    public void setWebsite(String website) {
        mWebsite = website;
    }

    public void setType(String type) {
        mType = type;
    }

    public void setLogo(Image logo) {
        mLogo = logo;
    }

    public void setPreference(int preference) {
        mPreference = preference;
    }

    public void setInformation(String information) {
        mInformation = information;
    }

    public void setDetailInformation(String detailInformation) {
        mMoreInformation = detailInformation;
    }

    public void setHasEvent(boolean hasEvent) {
        mHasEvent = hasEvent;
    }

    public void setHasGallery(boolean hasGallery) {
        mHasGallery = hasGallery;
    }

    public void setCommentCount(long commentCount) {
        mCommentCount = commentCount;
    }

    public void setLikeCount(long likeCount) {
        mLikeCount = likeCount;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mCountryCode);
        dest.writeString(mName);
        dest.writeString(mBestMenu);
        dest.writeString(mAddress);
        dest.writeString(mTelephone);
        dest.writeString(mWebsite);
        dest.writeString(mType);
        dest.writeParcelable(mLogo, flags);
        dest.writeInt(mPreference);
        dest.writeString(mInformation);
        dest.writeString(mMoreInformation);
        dest.writeByte((byte) (mHasEvent ? 1 : 0));
        dest.writeByte((byte) (mHasGallery ? 1 : 0));
        dest.writeLong(mCommentCount);
        dest.writeLong(mLikeCount);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<EateryInfo> CREATOR = new Parcelable.Creator<EateryInfo>() {
        @Override
        public EateryInfo createFromParcel(Parcel in) {
            return new EateryInfo(in);
        }

        @Override
        public EateryInfo[] newArray(int size) {
            return new EateryInfo[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EateryInfo)) return false;
        EateryInfo target = (EateryInfo) obj;
        return (this == obj || mPreference == target.mPreference);
    }

    @Override
    public int hashCode() {
        return String.valueOf(mId).hashCode();
    }

}
