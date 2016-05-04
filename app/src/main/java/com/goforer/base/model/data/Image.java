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

import com.goforer.base.model.BaseModel;
import com.google.gson.annotations.SerializedName;

public class Image extends BaseModel {
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

    public Image(String url) { this(url, 0, 0); }

    public Image(String url, int width, int height) {
        this.mUrl = url;
        this.mWidth = width;
        this.mHeight = height;
    }

    public long getImageId() { return mId; }
    public String getImageUrl() { return mUrl; }
    public int getImageWidth() { return mWidth; }
    public int getImageHeight() { return mHeight; }
    public String getMimeType() { return mMimeType; }
    public String getImageKey() { return mKey; }
    public String getName() { return mName; }
    public String getDescription() { return mDescription; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Image)) return false;
        Image target = (Image) obj;
        return (this == obj || (mUrl != null && mUrl.equals(target.mUrl)));
    }

    @Override
    public  int hashCode() { return String.valueOf(mUrl).hashCode(); }
}
