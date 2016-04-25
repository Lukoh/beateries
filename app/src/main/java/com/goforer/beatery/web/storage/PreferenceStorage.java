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

package com.goforer.beatery.web.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class PreferenceStorage extends AbstractStorage<String, String> {
    private PreferenceStorage() {}

    private static class SingletonHelper {
        private static final PreferenceStorage INSTANCE = new PreferenceStorage();
    }

    public static PreferenceStorage getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public boolean put(Context context, String name, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);

        if (prefs != null) {
            SharedPreferences.Editor edit = prefs.edit();
            if (!TextUtils.isEmpty(value)) {
                edit.putString(key, value);
                return edit.commit();
            }
        }

        return false;
    }

    @Override
    public String get(Context context, String name, String key) {
        SharedPreferences prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);

        if (prefs != null) {
            return prefs.getString(key, null);
        }

        return null;
    }

    @Override
    public String get(Context context, String name, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);

        if (prefs != null) {
            return prefs.getString(key, value);
        }

        return null;
    }

    @Override
    public boolean contains(Context context, String name, String key) {
        SharedPreferences prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        if (prefs != null) {
            String value = prefs.getString(key, null);
            return value != null;
        }
        return false;
    }

    public boolean remove(Context context, String name, String key) {
        SharedPreferences prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);

        if (prefs != null) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.remove(key);
            return edit.commit();
        }

        return false;
    }

    public boolean wipe(Context context, String name) {
        SharedPreferences prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);

        if (prefs != null) {
            SharedPreferences.Editor edit = prefs.edit();
            return edit.clear().commit();
        }

        return false;
    }
}
