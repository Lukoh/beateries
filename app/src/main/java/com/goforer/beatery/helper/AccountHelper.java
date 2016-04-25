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

package com.goforer.beatery.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.goforer.beatery.model.data.response.User;
import com.goforer.beatery.model.event.action.LogoutAction;
import com.goforer.beatery.web.storage.PreferenceStorage;

import org.greenrobot.eventbus.EventBus;

public class AccountHelper {
    private static final String ACCOUNT_STORAGE = "beatery:account";
    private static final String ACCOUNT_ME = "beatery:account.me";
    private static final String ACCOUNT_SESSION_ID = "beatery:session";

    public static final int ACCOUNT_TYPE_GOOGLE = 10000;
    public static final int ACCOUNT_TYPE_FACEBOOK = 10001;

    private static long mMyIndex = -1;

    public static void putSessionId(String sessionId, Context context) {
        if (!TextUtils.isEmpty(sessionId)) {
            PreferenceStorage.getInstance().put(context, ACCOUNT_STORAGE, ACCOUNT_ME, sessionId);
        } else {
            PreferenceStorage.getInstance().remove(context, ACCOUNT_STORAGE, ACCOUNT_SESSION_ID);
        }
    }

    public static String getSessionId(Context context) {
        return PreferenceStorage.getInstance().get(context, ACCOUNT_STORAGE, ACCOUNT_SESSION_ID);
    }

    public static void putMe(Context context, final User me) {
        if (me != null) {
            mMyIndex = me.getUserIdx();
            String userString = User.gson().toJson(me);
            PreferenceStorage.getInstance().put(context, ACCOUNT_STORAGE, ACCOUNT_ME, userString);
        }
    }

    public static User getMe(Context context) {
        String userString = PreferenceStorage.getInstance().get(context, ACCOUNT_STORAGE, ACCOUNT_ME);
        User me = null;

        if (!TextUtils.isEmpty(userString)) {
            try {
                me = User.gson().fromJson(userString, User.class);
            } catch (Exception ignored) {
            }
        }

        return me;
    }

    public static boolean isMe(long userIndex) {
        return mMyIndex == userIndex;
    }

    public static boolean hasAccount(Context context) {
        long userIndex = getMyIdx(context);
        return userIndex >= 0;
    }

    public static long getMyIdx(Context context) {
        if (mMyIndex < 0) {
            User user = getMe(context);
            if (user != null) {
                mMyIndex = user.getUserIdx();
            }
        }

        return mMyIndex;
    }

    public static String getMyId(Context context) {
        User user = getMe(context);

        if (user != null) {
            return user.getUserId();
        } else {
            return "";
        }
    }

    public static void removeAccount(final Context context, final LogoutAction action) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                PreferenceStorage.getInstance().remove(context, ACCOUNT_STORAGE, ACCOUNT_ME);
                PreferenceStorage.getInstance().remove(context, ACCOUNT_STORAGE, ACCOUNT_SESSION_ID);
                mMyIndex = -1;
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                EventBus.getDefault().post(action);
            }
        }.execute();
    }
}
