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

package com.goforer.beatery.web.communicator.reponse;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

/**
 * The class for receiving data and encapsulating HTTP-response information from BEatery Server.
 * And a response is a object with JSON formatted data and consists of a status code(Success or
 * parseItems, data as list type, and a option body.
 *
 * <p>
 * Please visit to below link if you'd like to see a object with JSON formatted data.
 * <p/>
 *
 * @see <a href="https://developers.google.com/cloud-messaging/android/client>A object with JSON
 * formatted data</a>
 */
public class ResponseClient {
    public static final int CODE_SUCCESS = 4000;
    public static final int CODE_BLOCKED_USER = 5001;
    public static final int CODE_PROCESS_DONE = 6001;
    public static final int CODE_AUTH_FAIL = 9999;
    public static final int CODE_AUTH_FAIL_UNKNOWN_USER = 9998;
    public static final int CODE_SERVER_UPDATE = 8000;
    public static final int CODE_NONE_USER = 5002;
    public static final int CODE_NETWORK_ERROR = 5100;
    public static final int CODE_GENERAL_ERROR = 5101;

    @SerializedName("code")
    private int mCode;
    @SerializedName("entity")
    private JsonElement mEntity;
    @SerializedName("message")
    private String mMessage;
    @SerializedName("option")
    private Option mOption;

    private int mStatus;

    public static class Option {
        @SerializedName("remaining_rows")
        private int mRemainingRows;
        @SerializedName("remaining_pages")
        private int mRemainingPages;

        public int getRemainingRows() {
            return mRemainingRows;
        }

        public int getRemainingPages() {
            return mRemainingPages;
        }

        public boolean hasMorePage(int current){
            return mRemainingPages > 0 && mRemainingPages >= current;
        }
    }

    public boolean isSuccessful(){
        if (CODE_SUCCESS == mCode || CODE_PROCESS_DONE == mCode) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isServerUpdated(){
        return CODE_SERVER_UPDATE == mCode;
    }

    public int getResponseCode() {
        return mCode;
    }

    public JsonElement getResponseEntity() {
        return mEntity;
    }

    public String getResponseMessage() {
        return mMessage;
    }

    public int getResponseStatus() {
        return mStatus;
    }

    public Option getResponseOption() {
        return mOption;
    }

    public void setResponseCode(int code) {
        mCode = code;
    }

    public void setResponseEntity(JsonElement entity) {
        mEntity = entity;
    }

    public void setResponseMessage(String message) {
        mMessage = message;
    }

    public void setResponseStatus(int status) {
        mStatus = status;
    }

    public void setResponseOption(Option option) {
        mOption = option;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
