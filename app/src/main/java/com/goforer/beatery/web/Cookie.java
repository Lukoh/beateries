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

package com.goforer.beatery.web;

public class Cookie {
    private String mKey;
    private String mValue;

    public String mData;

    public Cookie(String key, String value) {
        mKey = key;
        mValue = value;
    }

    public String getCookieKey() { return mKey; }

    public String getCookieValue() { return mValue; }

    public String getCookieData() { return mData; }

    public void setCookieData(String data) { mData = data; }
}
