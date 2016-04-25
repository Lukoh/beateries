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

package com.goforer.beatery.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.goforer.base.ui.activity.BaseActivity;
import com.goforer.beatery.ui.fragment.EateryGalleryFragment;
import com.goforer.beatery.utillity.ActivityCaller;
import com.goforer.beatery.R;

public class EateryGalleryActivity extends BaseActivity {
    private long mEateryId;

    private String mEateryName;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        mEateryId = getIntent().getLongExtra(ActivityCaller.EXTRA_EATERY_ID, -1);
        mEateryName = getIntent().getStringExtra(ActivityCaller.EXTRA_EATERY_NAME);

        super.onCreate(onSavedInstanceState);
    }

    @Override
    public void setContent() {
        setContentView(R.layout.activity_gallery);
    }

    @Override
    public void initViews() {
        transactFragment(EateryGalleryFragment.class, R.id.content_holder, null);
    }

    @Override
    protected void initActionBar() {
        super.initActionBar();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.bar_back_mtrl_alpha_90);
            actionBar.setTitle(
                    mEateryName + "'s" + getResources().getString(R.string.eatery_gallery));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public long getEateryId() {
        return mEateryId;
    }
}
