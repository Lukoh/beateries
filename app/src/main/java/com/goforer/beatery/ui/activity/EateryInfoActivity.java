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

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.goforer.base.ui.activity.BaseActivity;
import com.goforer.base.ui.view.SwipeViewPager;
import com.goforer.beatery.R;
import com.goforer.beatery.model.data.response.EateryInfo;
import com.goforer.beatery.ui.adapter.EateryInfoListAdapter;
import com.goforer.beatery.utillity.ActivityCaller;
import com.sembozdemir.viewpagerarrowindicator.library.ViewPagerArrowIndicator;

import java.util.List;

import butterknife.BindView;

/**
 * This class shows the detailed information about the eatery which is selected on the list.
 */
public class EateryInfoActivity extends BaseActivity {
    private static final String TAG = "EateryInfoActivity";
    private static final String TRANSITION_IMAGE = "EateryInfoActivity:image";

    private static final int PAGE_MARGIN_VALUE = 40;

    private EateryInfo mEateryInfo;
    private List<EateryInfo> mEateryInfoItems;
    private ActionBar mActionBar;

    private int mItemPosition;
    private int navigation_bar_height = 0;

    private boolean mIsScrolledToComment = false;

    @BindView(R.id.pager_flip)
    SwipeViewPager mSwipePager;
    @BindView(R.id.viewPagerArrowIndicator)
    ViewPagerArrowIndicator mViewPagerArrowIndicator;
    @BindView(R.id.comment_bar)
    View mCommentBar;
    @BindView(R.id.comment_holder)
    View mCommentHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * For using ViewPager in EateryInfoActivity, the list of EateryInfo and
         * the position of an item have been passed into EateryInfoActivity.
         * It means that I'm going to put ViewPager and implement some module to allow a user to see
         * each Offers's information by flipping left and right through pages of data.
         */
        mEateryInfoItems = getIntent().getExtras().getParcelableArrayList(
                ActivityCaller.EXTRA_EATERY_INFO_LIST);
        mItemPosition = getIntent().getIntExtra(ActivityCaller.EXTRA_EATERY_ITEM_POSITION, -1);
        mIsScrolledToComment = getIntent().getBooleanExtra(ActivityCaller.EXTRA_SCROLL_TO_COMMENT,
                false);

        /*
        // To set Offers's information using GSon from the String to  of an object of Offers.
        String postString = getIntent().getStringExtra(EateryInfo.class.getName());
        if (!TextUtils.isEmpty(postString)) {
            mEateryInfo = EateryInfo.gson().fromJson(postString, EateryInfo.class);
        }
        */

        if (mEateryInfoItems != null && mItemPosition != -1) {
            mEateryInfo = mEateryInfoItems.get(mItemPosition);

            super.onCreate(savedInstanceState);
        } else {
            Toast.makeText(this, getString(R.string.toast_eatery_info_fetch_fail),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_eatery_detail_view);
    }

    @Override
    protected void setViews() {
        super.setViews();

        if (mEateryInfoItems != null && mItemPosition != -1) {
            EateryInfoListAdapter adapter = new EateryInfoListAdapter(getSupportFragmentManager(),
                    mEateryInfoItems, mIsScrolledToComment);
            mSwipePager.setAdapter(adapter);
            ViewCompat.setTransitionName(mSwipePager, TRANSITION_IMAGE);
            mSwipePager.setPageMargin(PAGE_MARGIN_VALUE);

            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

            if (hasBackKey && hasHomeKey) {
                navigation_bar_height = 0;
            } else {
                int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen",
                        "android");
                if (resourceId > 0) {
                    navigation_bar_height = getResources().getDimensionPixelSize(resourceId);
                }
            }

            handleSwipePager();
        }
    }

    @Override
    protected void setActionBar() {
        super.setActionBar();

        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeAsUpIndicator(R.drawable.bar_back_mtrl_alpha_90);
            mActionBar.setTitle(mEateryInfo.getName());
            mActionBar.setElevation(0);
            mActionBar.setDisplayShowTitleEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra(ActivityCaller.EXTRA_SELECTED_ITEM_POSITION, mItemPosition);
            this.setResult(RESULT_OK, intent);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void setEffectIn() {
        Log.i(TAG, "setEffectIn");

        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.scale_down_exit);
    }

    @Override
    protected void setEffectOut() {
        Log.i(TAG, "setEffectOut");

        overridePendingTransition(R.anim.scale_up_enter, R.anim.slide_out_to_bottom);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            final View contentView = findViewById(R.id.content_holder);
            if (contentView != null) {
                contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        contentView.getWindowVisibleDisplayFrame(r);
                        int screenHeight = contentView.getRootView().getHeight();
                        int keypadHeight = screenHeight - r.bottom;
                        if (keypadHeight > screenHeight * 0.15) {
                            if (!mCommentHolder.isShown()) {
                                mCommentHolder.getLayoutParams().height =
                                        keypadHeight - navigation_bar_height;
                                mCommentBar.setVisibility(View.GONE);

                            } else {
                                hideCommentHolder(false);
                            }
                        } else {
                            if (!mCommentHolder.isShown()) {
                                mCommentBar.setVisibility(View.GONE);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(ActivityCaller.EXTRA_SELECTED_ITEM_POSITION, mItemPosition);
        this.setResult(RESULT_OK, intent);

        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
    }

    private void hideCommentHolder(boolean showStatBar) {
        mCommentBar.setVisibility(showStatBar ? View.VISIBLE : View.GONE);
        mCommentHolder.setVisibility(View.GONE);
    }

    private void handleSwipePager() {
        mSwipePager.setCurrentItem(mItemPosition, false);
        mViewPagerArrowIndicator.bind(mSwipePager);
        mViewPagerArrowIndicator.setArrowIndicatorRes(R.drawable.arrowleft,
                R.drawable.arrowright);
        mSwipePager.setOnSwipeOutListener(new SwipeViewPager.OnSwipeOutListener() {
            @Override
            public void onSwipeOutAtStart() {
            }

            @Override
            public void onSwipeOutAtEnd() {
            }
        });

        mActionBar.setTitle(mEateryInfoItems.get(mItemPosition).getName());

        mSwipePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mItemPosition = position;
                mActionBar.setTitle(mEateryInfoItems.get(position).getName());
                Log.d(TAG, "called onPageSelected");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
