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

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.goforer.base.ui.activity.BaseActivity;
import com.goforer.base.ui.adapter.BaseFragmentPagerAdapter;
import com.goforer.base.ui.view.SlidingTabLayout;
import com.goforer.beatery.model.event.action.SearchQueryAction;
import com.goforer.beatery.ui.fragment.EaterySearchFragment;
import com.goforer.beatery.utillity.ActivityCaller;
import com.goforer.beatery.utillity.DisplayUtils;
import com.goforer.beatery.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

public class EaterySearchActivity extends BaseActivity {
    private static final String TAG = "EaterySearchActivity";
    private static final String STATE_SELECTED_TAB = "beatery:selected_tab";

    public static final int TAB_NAME = 0;
    public static final int TAB_ADDRESS = 1;

    private int mSelectedPosition;
    private String mKeyword;
    private SearchView mSearchView;
    private BaseFragmentPagerAdapter mAdapter;

    private float mBeforeGetY = 0;

    @IntDef({TAB_NAME, TAB_ADDRESS})
    public @interface SearchTab{}

    @BindView(R.id.pager) ViewPager mPager;
    @BindView(R.id.pager_strip)
    SlidingTabLayout mPagerStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_TAB);
        }else if(getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            mSelectedPosition = bundle.getInt(ActivityCaller.EXTRA_SELECT_TAB, TAB_NAME);
            mKeyword = bundle.getString(ActivityCaller.EXTRA_SEARCH_KEYWORD, "");
            mKeyword = mKeyword.trim();
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_SELECTED_TAB, mSelectedPosition);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void setViews() {
        super.setViews();
        mAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager());
        BaseFragmentPagerAdapter.Page name =
                new BaseFragmentPagerAdapter.Page(R.drawable.ic_name,
                        EaterySearchFragment.class.getName(),
                        getString(R.string.eatery_search_by_name), EaterySearchFragment.class,
                        null);
        BaseFragmentPagerAdapter.Page address =
                new BaseFragmentPagerAdapter.Page(R.drawable.ic_address,
                        EaterySearchFragment.class.getName(),
                        getString(R.string.eatery_search_by_address), EaterySearchFragment.class,
                        null);

        mAdapter.addPage(name);
        mAdapter.addPage(address);
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(mAdapter.getCount() - 1);
        initPagerStrip();
        mPagerStrip.setViewPager(mPager);
        if (mPager .getCurrentItem() != mSelectedPosition) {
            mPager.setCurrentItem(mSelectedPosition, true);
        }
    }

    private void initPagerStrip() {
        mPagerStrip.setCustomTabView(R.layout.layout_tab, android.R.id.title, android.R.id.icon);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPagerStrip.setDividerColors(getResources().getColor(R.color.black_alpha_20, null));
            mPagerStrip.setSelectedIndicatorColors(
                    getResources().getColor(R.color.tab_title_selected, null));
            mPagerStrip.setTitleColor(getResources().getColor(R.color.tab_title, null),
                    getResources().getColor(R.color.tab_title_selected));
        } else {
            mPagerStrip.setDividerColors(getResources().getColor(R.color.black_alpha_20));
            mPagerStrip.setSelectedIndicatorColors(
                    getResources().getColor(R.color.tab_title_selected));
            mPagerStrip.setTitleColor(getResources().getColor(R.color.tab_title),
                    getResources().getColor(R.color.tab_title_selected));
        }

        mPagerStrip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mBeforeGetY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int gap = (int) (mBeforeGetY - event.getY());
                        if (mAdapter.getFragmentItem(mSelectedPosition) != null) {
                            ((EaterySearchFragment) mAdapter.getFragmentItem(mSelectedPosition))
                                    .getRecyclerView().scrollBy(0, gap);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        break;
                }
                return false;
            }
        });

        mPagerStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected = " + position);

                mSelectedPosition = position;
                if (mSearchView != null) {
                    CharSequence query = mSearchView.getQuery();
                    if (!TextUtils.isEmpty(query)) {
                        SearchQueryAction action = new SearchQueryAction();
                        BaseFragmentPagerAdapter.Page page = mAdapter.getPage(mSelectedPosition);
                        action.setTabTag(page.getTabTag());
                        action.setKeyword(query.toString());
                        EventBus.getDefault().post(action);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void setEffectIn() {
        overridePendingTransition(0, 0);
    }

    @Override
    protected void setEffectOut() {
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_now, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.menu_search);

        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.requestFocus();
        if(mKeyword != null && !mKeyword.isEmpty()){
            mSearchView.setQuery(mKeyword, true);
            mKeyword = "";
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d(TAG, "onNewIntent");

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (isValidQuery(query)) {
                mSearchView.clearFocus();
                BaseFragmentPagerAdapter.Page page = mAdapter.getPage(mSelectedPosition);
                SearchQueryAction action = new SearchQueryAction();
                action.setTabTag(page.getTabTag());
                action.setKeyword(query);
                EventBus.getDefault().post(action);
            }
        }
    }

    private boolean isValidQuery(String query) {
        return !TextUtils.isEmpty(query.trim());
    }

    @Override
    protected void onPause() {
        super.onPause();

        DisplayUtils.INSTANCE.hideSoftKeyboard(this, mSearchView);
    }
}
