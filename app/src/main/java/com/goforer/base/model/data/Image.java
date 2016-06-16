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

import android.os.Parcel;
import android.os.Parcelable;

import com.goforer.base.model.BaseModel;
import com.google.gson.annotations.SerializedName;

public class Image extends BaseModel implements Parcelable {
    @SerializedName("id")
    private long mId;
    @SerializedName("url")
    private String mUrl;
    @SerializedName("width")
    private int mWidth;
    @SerializedName("height")
    private int mHeight;
    @SerializedName("mime_type")
    private String mMimeType;
    @SerializedName("key")
    private String mKey;
    @SerializedName("name")
    private String mName;
    @SerializedName("description")
    private String mDescription;

    public Image() {
    }

    public Image(String url) { this(url, 0, 0); }

    public Image(String url, int width, int height) {
        this.mUrl = url;
        this.mWidth = width;
        this.mHeight = height;
    }

    protected Image(Parcel in) {
        mId = in.readLong();
        mUrl = in.readString();
        mWidth = in.readInt();
        mHeight = in.readInt();
        mMimeType = in.readString();
        mKey = in.readString();
        mName = in.readString();
        mDescription = in.readString();
    }

    public long getImageId() { return mId; }

    public String getImageUrl() { return mUrl; }

    public int getImageWidth() { return mWidth; }

    public int getImageHeight() { return mHeight; }

    public String getMimeType() { return mMimeType; }

    public String getImageKey() { return mKey; }

    public String getName() { return mName; }

    public String getDescription() { return mDescription; }

    public void setImageId(long id) {
        mId = id;
    }

    public void setImageUrl(String url) {
        mUrl = url;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public void setMimeType(String mimeType) {
        mMimeType = mimeType;
    }

    public void setImageKey(String key) {
        mKey = key;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mUrl);
        dest.writeInt(mWidth);
        dest.writeInt(mHeight);
        dest.writeString(mMimeType);
        dest.writeString(mKey);
        dest.writeString(mName);
        dest.writeString(mDescription);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Image)) return false;
        Image target = (Image) obj;
        return (this == obj || (mUrl != null && mUrl.equals(target.mUrl)));
    }

    @Override
    public  int hashCode() { return String.valueOf(mUrl).hashCode(); }
}
