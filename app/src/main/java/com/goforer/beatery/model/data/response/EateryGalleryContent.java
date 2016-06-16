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
 * The class for putting the eatery's gallery contents after parsing JSON formatted data that
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
public class EateryGalleryContent extends BaseModel implements Parcelable {
    private static final String IMAGE_KEY = "gallery_image";
    private static final String IMAGE_THUMBNAIL_KEY = "thumbnail_image";

    @SerializedName("eatery_id")
    private long mEateryId;
    @SerializedName("content_id")
    private long mContentId;
    @SerializedName("content_index")
    private int mContentIndex;

    // The content_images contains a thumbnail-image and real-image about content.
    @SerializedName("content_images")
    private ImageMap mContentImages;

    protected EateryGalleryContent(Parcel in) {
        mEateryId = in.readLong();
        mContentId = in.readLong();
        mContentIndex = in.readInt();
        mContentImages = readMap(in);
    }

    public long getEateryId() {
        return mEateryId;
    }

    public long getContentId() {
        return mContentId;
    }

    public int getContentIndex() {
        return mContentIndex;
    }

    public Image getContentImage(){
        Image image = null;
        if (mContentImages != null){
            image =  mContentImages.get(IMAGE_KEY);
            if (image == null){
                image = mContentImages.get(IMAGE_THUMBNAIL_KEY);
            }
        }

        return image;
    }

    public Image getContentThumbnailImage(){
        Image image = null;
        if (mContentImages != null){
            image =  mContentImages.get(IMAGE_THUMBNAIL_KEY);
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
        if (mContentImages.size() > 0) {
            Set<String> keySet = mContentImages.keySet();
            Bundle bundle = new Bundle();
            for(String key: keySet) {
                bundle.putParcelable(key, mContentImages.get(key));
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
        dest.writeLong(mEateryId);
        dest.writeLong(mContentId);
        dest.writeInt(mContentIndex);
        writeMap(dest);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<EateryGalleryContent> CREATOR =
            new Parcelable.Creator<EateryGalleryContent>() {
        @Override
        public EateryGalleryContent createFromParcel(Parcel in) {
            return new EateryGalleryContent(in);
        }

        @Override
        public EateryGalleryContent[] newArray(int size) {
            return new EateryGalleryContent[size];
        }
    };

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EateryGalleryContent)) return false;
        EateryGalleryContent target = (EateryGalleryContent) object;
        return (this == object || mContentId == target.mContentId);
    }

    @Override
    public int hashCode() {
        return String.valueOf(mContentId).hashCode();
    }
}
