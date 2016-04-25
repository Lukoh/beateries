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
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.goforer.base.model.data.Image;
import com.goforer.base.ui.activity.BaseActivity;
import com.goforer.base.ui.view.SwipeViewPager;
import com.goforer.beatery.ui.adapter.GalleryViewerAdapter;
import com.goforer.beatery.utillity.ActivityCaller;
import com.goforer.beatery.R;

import butterknife.InjectView;

/**
 * The reason for using SwipeDetectViewPager derived from ViewPager is to extend this class
 * in the future. It allows the user to flip left and right through pages of image.
 * <p>
 * It means the user can view a number of images using gestures like flipping left and right.
 * This is very useful way to view several gallery images.
 * For example, in case of a couple of images can be delivered from Gallery and these are viewed
 * in GalleryImageViewer.
 * <p/>
 *
 * <p>
 * In case of a number of images, I'll use List with Image like this, List<Image>, instead Image.
 * I have to consider new UX which has a number of images later if users want to apply new UX to
 * this App. (In case of having a number of images in GridItem, most of Gallery module have to be
 * modified in that time.)
 * <p/>
 */
public class GalleryViewerActivity extends BaseActivity {
    private static final String TAG = "GalleryViewerActivity";
    private static final String TRANSITION_IMAGE = "GalleryViewerActivity:image";

    private static final int PAGE_MARGIN_VALUE = 50;

    private Image mImage;

    private int mItemIndex;

    private String mJsonString;

    @InjectView(R.id.pager_flip)
    SwipeViewPager mPager;
    @InjectView(R.id.tv_description)
    TextView mDescriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mJsonString = getIntent().getStringExtra(Image.class.getName());
        mItemIndex = getIntent().getIntExtra(ActivityCaller.EXTRA_GALLERY_IMAGE_INDEX, -1);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setContent() {
        setContentView(R.layout.activity_gallery_image_viewer);
        Log.d(TAG, "Set the activity content from a layout resource");
    }

    @Override
    protected void initViews() {
        ViewCompat.setTransitionName(mPager, TRANSITION_IMAGE);
        mImage = Image.gson().fromJson(mJsonString, Image.class);
        if (TextUtils.isEmpty(mJsonString)) {
            finish();
            Log.e(TAG, "Json is empty");
            return;
        }

        mPager.setPageMargin(PAGE_MARGIN_VALUE);
        if (mImage != null) {
            mPager.setAdapter(new GalleryViewerAdapter(mImage));
            mPager.setCurrentItem(0, false);
            mPager.setOnSwipeOutListener(new SwipeViewPager.OnSwipeOutListener() {
                @Override
                public void onSwipeOutAtStart() {
                }

                @Override
                public void onSwipeOutAtEnd() {
                }
            });

            setDescription();

            mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset,
                                           int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    setDescription();
                    Log.d(TAG, "called onPageSelected");
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
    }

    @Override
    protected void initActionBar() {
        super.initActionBar();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.bar_back_mtrl_alpha_90);
            actionBar.setTitle(mImage.getName() + " - " + mItemIndex);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ActivityCaller.EXTRA_GALLERY_IMAGE_INDEX, mItemIndex);
    }

    @Override
    protected void setEffectIn() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void setEffectOut() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void setDescription() {
        mDescriptionView.setText(mImage.getDescription());
        Log.d(TAG, "set the image-description");
    }
}
