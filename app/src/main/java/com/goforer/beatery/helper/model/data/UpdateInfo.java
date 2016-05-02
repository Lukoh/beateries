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

package com.goforer.beatery.helper.model.data;

import com.goforer.base.model.event.ResponseEvent;
import com.goforer.beatery.web.wire.connecter.reponse.ResponseClient;

public class UpdateInfo {
    public static String KEY_UPDATE_STORAGE = "beatery::update";
    public static String KEY_UPDATE_SKIP_DATA = "beatery::update.skip.data";

    private String mDescription;
    private String mForcedUpdate;
    private String mMarketUrl;
    private String mUrl;

    private ResponseEvent mEvent;
    private ResponseClient mResponse;

    public UpdateInfo(String description, String forcedUpdate, String market_url, String url,
                      ResponseClient response, ResponseEvent event) {
        mDescription = description;
        mForcedUpdate = forcedUpdate;
        mMarketUrl = market_url;
        mUrl = url;
        mEvent = event;
        mResponse = response;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getForcedUpdate() {
        return mForcedUpdate;
    }

    public String getMarketUrl() {
        return mMarketUrl;
    }

    public String getUrl() {
        return mUrl;
    }

    public ResponseEvent getResponseEvent() {
        return mEvent;
    }

    public ResponseClient getResponse() {
        return mResponse;
    }

    public void setResponse(ResponseClient response) {
        mResponse = response;
    }
}
