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

import com.goforer.base.model.BaseModel;
import com.goforer.base.model.ImageMap;
import com.goforer.base.model.data.Image;
import com.google.gson.annotations.SerializedName;

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
public class User extends BaseModel {
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
