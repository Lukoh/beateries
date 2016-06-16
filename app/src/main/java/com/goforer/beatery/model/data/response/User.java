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

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.goforer.base.model.BaseModel;
import com.goforer.base.model.ImageMap;
import com.goforer.base.model.data.Image;
import com.google.gson.annotations.SerializedName;

import java.util.Set;

/**
 * The class for putting the User's information after parsing JSON formatted data
 * that got from BEatery server
 *
 * <p>
 * Please refer to below link if you'd like to get more details.
 * The link provides REST APIs.
 *
 * @see <a href="https://github.com/goforer/beatery/BEatery_REST_API.pdf">
 *     BEatery REST APIs</a>
 * </p>
 */
public class User extends BaseModel implements Parcelable {
    private static final String PICTURE_KEY = "picture_image";
    private static final String PICTURE_THUMBNAIL_KEY = "thumbnail_image";

    @SerializedName("idx")
    private long mIdx;
    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("gender")
    private String mGender;
    @SerializedName("birth")
    private String mBirth;

    // The content_images contains a thumbnail-image and real-image about content.
    @SerializedName("picture")
    private ImageMap mPicture;

    protected User(Parcel in) {
        mIdx = in.readLong();
        mId = in.readString();
        mName = in.readString();
        mGender = in.readString();
        mBirth = in.readString();
        mBirth = in.readString();
        mPicture = readMap(in);
    }

    public User(){
        super();
    }

    public long getUserIdx() {
        return mIdx;
    }

    public String getUserId() {
        return mId;
    }

    public String getUserNickName() {
        return mName;
    }

    public String getGender() {
        return mGender;
    }

    public String getBirth() {
        return mBirth;
    }

    public void setUserIdx(long userIdx) {
        mIdx = userIdx;
    }

    public Image getPictureImage(){
        Image image =  mPicture.get(PICTURE_KEY);
        if (image == null){
            image = mPicture.get(PICTURE_THUMBNAIL_KEY);
        }

        return image;
    }

    public ImageMap readMap(Parcel in) {
        String[] keys = in.createStringArray();
        Bundle bundle = in.readBundle(Image.class.getClassLoader());

        ImageMap imageMap = new ImageMap();
        for(String key: keys) {
            imageMap.put(key, (Image) bundle.getParcelable(key));
        }

        return imageMap;
    }

    public void writeMap(Parcel dest) {
        if (mPicture.size() > 0) {
            Set<String> keySet = mPicture.keySet();
            Bundle bundle = new Bundle();
            for(String key: keySet) {
                bundle.putParcelable(key, mPicture.get(key));
            }

            String[] keys = keySet.toArray(new String[keySet.size()]);
            dest.writeStringArray(keys);
            dest.writeBundle(bundle);
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mIdx);
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeString(mGender);
        dest.writeString(mBirth);
        writeMap(dest);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) return false;
        User target = (User) obj;
        return (this == obj || mIdx == target.mIdx);
    }

    @Override
    public int hashCode() {
        return String.valueOf(mIdx).hashCode();
    }
}
