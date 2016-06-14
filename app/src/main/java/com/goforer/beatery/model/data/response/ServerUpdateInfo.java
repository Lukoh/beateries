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

import com.goforer.base.model.event.ResponseEvent;
import com.goforer.base.model.BaseModel;
import com.goforer.beatery.web.communicator.reponse.ResponseClient;
import com.google.gson.annotations.SerializedName;

/**
 * The class for putting the updated BEatery server's information after parsing JSON formatted data
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
public class ServerUpdateInfo extends BaseModel {
    @SerializedName("title")
    private String mTitle;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("noticeUrl")
    private String mNoticeUrl;
    @SerializedName("event")
    private ResponseEvent mEvent;
    @SerializedName("response")
    private ResponseClient mResponse;

    public ServerUpdateInfo(String title, String description, String  noticeUrl,
                            ResponseClient response, ResponseEvent event) {
        mTitle = title;
        mDescription = description;
        mNoticeUrl = noticeUrl;
        mEvent= event;
        mResponse = response;
    }

    public String getTitle() {
        return  mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getNoticeUrl() {
        return mNoticeUrl;
    }

    public ResponseEvent getResponseEvent() {
        return mEvent;
    }

    public ResponseClient getResponse() {
        return mResponse;
    }
}
