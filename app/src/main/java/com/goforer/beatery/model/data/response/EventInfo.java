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
import com.goforer.base.model.data.Image;
import com.google.gson.annotations.SerializedName;

/**
 * The class for putting the eatery's event information after parsing JSON formatted data that
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
public class EventInfo extends BaseModel {
    @SerializedName("id")
    private long mId;
    @SerializedName("country_code")
    private String mCountryCode;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("url")
    private String mUrl;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("image")
    private Image mImage;

    public long getId() {
        return mId;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public Image getImage() {
        return mImage;
    }
}
