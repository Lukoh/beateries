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

package com.goforer.beatery.utillity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.goforer.base.model.data.Image;
import com.goforer.beatery.BEatery;
import com.goforer.beatery.R;
import com.goforer.beatery.model.data.response.EateryInfo;
import com.goforer.beatery.model.data.response.User;
import com.goforer.beatery.ui.activity.EateryGalleryActivity;
import com.goforer.beatery.ui.activity.EateryInfoActivity;
import com.goforer.beatery.ui.activity.EateryListActivity;
import com.goforer.beatery.ui.activity.EaterySearchActivity;
import com.goforer.beatery.ui.activity.EventViewerActivity;
import com.goforer.beatery.ui.activity.GalleryViewerActivity;
import com.goforer.beatery.ui.activity.PictureEditActivity;
import com.goforer.beatery.ui.activity.SignUpActivity;

public enum  ActivityCaller {
    INSTANCE;

    public static final String EXTRA_SCROLL_TO_COMMENT = "beatery:scroll_to_comment";
    public static final String EXTRA_EATERY_ID = "beatery:eatery_id";
    public static final String EXTRA_EATERY_NAME = "beatery:eatery_name";
    public static final String EXTRA_GALLERY_IMAGE_INDEX = "beatery:image_index";
    public static final String EXTRA_SELECT_TAB = "beatery:select_tab";
    public static final String EXTRA_SEARCH_KEYWORD = "beatery:search_keyword";

    public static final String EXTRA_TOKEN = "beatery:token";
    public static final String EXTRA_CALL_TYPE = "beatery:call_type";
    public static final String EXTRA_SNS_ID = "beatery:sns_id";
    public static final String EXTRA_EMAIL = "beatery:email";
    public static final String EXTRA_ACCOUNT_TYPE = "beatery:account_type";
    public static final String EXTRA_PICTURE_PATH = "beatery:picture_path";

    public static final String BUNDLE_EATERY_ID = "beatery:eatery_id";
    public static final String BUNDLE_EATERY_NAME = "beatery:eatery_name";
    public static final String BUNDLE_CUSTOM_DATA = "beatery:eatery_custom_data";

    public Uri.Builder getBase() {
        return new Uri.Builder().scheme(BEatery.mResources.getString(R.string.url_scheme)).
                authority(BEatery.mResources.getString(R.string.url_content_host));
    }

    public Intent createIntent(Context context, Class<?> cls, boolean isNewTask) {
        Intent intent = new Intent(context, cls);

        if (isNewTask && !(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return intent;
    }

    private Uri me(long userIdx) {
        return getBase().appendPath("user").appendPath(String.valueOf(userIdx)).build();
    }

    private Intent createIntent(String action) {
        Intent intent = new Intent(action);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    public void callMarketUpdate(Context context, String uri) {
        Intent intent = createIntent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uri));

        context.startActivity(intent);
    }

    public void callSignUp(Context context, String snsId, String token, String email, int snsType,
                           int callType) {
        Intent intent = createIntent(context, SignUpActivity.class, true);
        intent.putExtra(EXTRA_SNS_ID, snsId);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_EMAIL, email);
        intent.putExtra(EXTRA_ACCOUNT_TYPE, snsType);
        intent.putExtra(EXTRA_CALL_TYPE, callType);
        context.startActivity(intent);
    }

    public void callProfileChange(Context context, int callType, User me) {
        Intent intent = createIntent(context, SignUpActivity.class, true);
        intent.putExtra(EXTRA_CALL_TYPE, callType);
        intent.setData(me(me.getUserIdx()));
        intent.putExtra(User.class.getName(), User.gson().toJson(me));

        context.startActivity(intent);
    }

    public void callPictureEdit(Context context, String path) {
        Intent intent = createIntent(context, PictureEditActivity.class, true);
        intent.putExtra(EXTRA_PICTURE_PATH, path);

        context.startActivity(intent);
    }

    public void callEateryList(Context context) {
        Intent intent = createIntent(context, EateryListActivity.class, true);
        context.startActivity(intent);
    }

    public void callEateryInfo(Context context, EateryInfo eateryInfo,
                                     boolean isScrolledToComment) {
        Intent intent = createIntent(context, EateryInfoActivity.class, true);
        String info = EateryInfo.gson().toJson(eateryInfo);
        intent.putExtra(EateryInfo.class.getName(), info);
        intent.putExtra(EXTRA_SCROLL_TO_COMMENT, isScrolledToComment);

        context.startActivity(intent);
    }

    public void callEateryEvent(Context context, long eateryId, String eateryName) {
        Intent intent = createIntent(context, EventViewerActivity.class, true);
        intent.putExtra(EXTRA_EATERY_ID, eateryId);
        intent.putExtra(EXTRA_EATERY_NAME, eateryName);

        context.startActivity(intent);
    }

    public void callEateryGallery(Context context, long eateryId, String eateryName) {
        Intent intent = createIntent(context, EateryGalleryActivity.class, true);
        intent.putExtra(EXTRA_EATERY_ID, eateryId);
        intent.putExtra(EXTRA_EATERY_NAME, eateryName);

        context.startActivity(intent);
    }

    public void callGalleryImageViewer(Context context, Image image, int imageIndex) {
        Intent intent = createIntent(context, GalleryViewerActivity.class, true);
        String jsonString = Image.gson().toJson(image);
        intent.putExtra(Image.class.getName(), jsonString);
        intent.putExtra(EXTRA_GALLERY_IMAGE_INDEX, imageIndex);

        context.startActivity(intent);
    }

    public void callSearch(Context context, @EaterySearchActivity.SearchTab int search_tab,
                           String keyword) {
        Intent intent = createIntent(context, EaterySearchActivity.class, false);
        intent.putExtra(EXTRA_SELECT_TAB, search_tab);
        intent.putExtra(EXTRA_SEARCH_KEYWORD, keyword);

        context.startActivity(intent);
    }

    public void callDial(Context context, String number) {
        Intent intent = createIntent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number.trim()));

        context.startActivity(intent);
    }

    public void callWebsite(Context context,  String url) {
        Intent intent = createIntent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        context.startActivity(intent);
    }
}
