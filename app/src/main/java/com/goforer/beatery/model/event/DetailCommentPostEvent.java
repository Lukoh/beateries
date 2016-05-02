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

package com.goforer.beatery.model.event;

import com.goforer.base.model.event.ResponseEvent;
import com.goforer.beatery.model.data.response.EateryInfo;
import com.google.gson.JsonElement;

/**
 * Define event to handle posting comment from the eatery's information Activity.
 *
 * <p>
 * Please refer to EventBus.
 *
 * @see <a href="http://greenrobot.org/eventbus//">
 *     EventBus</a>
 * @see <a href="http://greenrobot.org/eventbus/documentation/how-to-get-started/">
 *     How to get started</a>
 * </p>
 *
 */
public class DetailCommentPostEvent extends ResponseEvent {
    private EateryInfo mEateryInfo;

    private boolean mIsSucceeded;

    @Override
    public void doInResponse() {
        if (mResponseClient != null && mResponseClient.isSuccessful()) {
            mIsSucceeded = true;
            JsonElement result = mResponseClient.getResponseEntity().getAsJsonArray().get(0);
            setEateryInfo(EateryInfo.gson().fromJson(result, EateryInfo.class));
        } else {
            mIsSucceeded = false;
        }
    }

    public EateryInfo getEateryInfo() {
        return mEateryInfo;
    }

    public boolean isSuccessful() {
        return mIsSucceeded;
    }

    public void setEateryInfo(EateryInfo eateryInfo) {
        mEateryInfo = eateryInfo;
    }
}
