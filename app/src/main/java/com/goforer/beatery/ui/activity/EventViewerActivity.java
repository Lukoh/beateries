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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.goforer.base.model.ListModel;
import com.goforer.base.ui.activity.BaseActivity;
import com.goforer.base.ui.view.SwipeViewPager;
import com.goforer.beatery.R;
import com.goforer.beatery.model.data.response.EventInfo;
import com.goforer.beatery.model.event.EateryOccasionEvent;
import com.goforer.beatery.ui.adapter.EventViewerAdapter;
import com.goforer.beatery.utillity.ActivityCaller;
import com.goforer.beatery.web.wire.connecter.Intermediary;
import com.google.gson.JsonElement;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public class EventViewerActivity extends BaseActivity {
    private static final String TAG = "EventViewerActivity";
    private static final String TRANSITION_IMAGE = "EventViewerActivity:image";

    private static final int PAGE_MARGIN_VALUE = 50;

    private int mPagerPosition;
    private long mEateryId;

    private String mEateryName;

    private List<EventInfo> mEvents = new ArrayList<>();
    private ActionBar mActionBar;

    @InjectView(R.id.pager_flip)
    SwipeViewPager mPager;
    @InjectView(R.id.tv_description)
    TextView mDescriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mEateryId = getIntent().getLongExtra(ActivityCaller.EXTRA_EATERY_ID, -1);
        mEateryName = getIntent().getStringExtra(ActivityCaller.EXTRA_EATERY_NAME);

        super.onCreate(savedInstanceState);

        requestEvent();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_event_viewer);
        Log.d(TAG, "Set the activity content from a layout resource");
    }

    @Override
    protected void setViews() {
        ViewCompat.setTransitionName(mPager, TRANSITION_IMAGE);
        mPager.setPageMargin(PAGE_MARGIN_VALUE);
    }

    @Override
    protected void setActionBar() {
        super.setActionBar();
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeAsUpIndicator(R.drawable.bar_back_mtrl_alpha_90);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_more).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ActivityCaller.INSTANCE.callWebsite(this, mEvents.get(mPagerPosition).getUrl());

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void setEffectIn() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void setEffectOut() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void setDescription(int position) {
        mDescriptionView.setText(mEvents.get(position).getDescription());
        Log.d(TAG, "set the image-description");
    }

    private void requestEvent() {
        EateryOccasionEvent event = new EateryOccasionEvent(true);

        Intermediary.INSTANCE.getEventContents(this, mEateryId, event);
    }

    private void infoFetchFail() {
        Toast.makeText(getApplicationContext(), R.string.toast_eatery_info_fetch_fail,
                Toast.LENGTH_SHORT).show();
        supportFinishAfterTransition();
    }

    private  List<EventInfo> parseItems(JsonElement json) {
        return new ListModel<>(EventInfo.class).fromJson(json);
    }

    private void addItems(List<EventInfo> items) {
        Log.i(TAG, "addItems");

        if (items != null && !items.isEmpty()) {
            this.mEvents.addAll(items);
        }
    }

    private void showEvent() {
        if (mEvents.size() > 0 ) {
            mPager.setAdapter(new EventViewerAdapter(mEvents));
            mPager.setCurrentItem(0, false);
            mPager.setOnSwipeOutListener(new SwipeViewPager.OnSwipeOutListener() {
                @Override
                public void onSwipeOutAtStart() {
                }

                @Override
                public void onSwipeOutAtEnd() {
                }
            });

            setDescription(0);
            mActionBar.setTitle(mEvents.get(0).getTitle());

            mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    mPagerPosition = position;
                    setDescription(position);
                    mActionBar.setTitle(mEvents.get(position).getTitle());
                    Log.d(TAG, "called onPageSelected");
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEvent(final EateryOccasionEvent event) {
        if (event.getResponse() != null && event.getResponse().isSuccessful()) {
            new AsyncTask<Void, Void, List<EventInfo>>() {
                @Override
                protected List<EventInfo> doInBackground(Void... params) {
                    return parseItems(event.getResponse().getResponseEntity());
                }

                @Override
                protected void onPostExecute(List<EventInfo> items) {
                    super.onPostExecute(items);
                    addItems(items);
                    showEvent();
                }
            }.execute();
        } else {
            infoFetchFail();
        }
    }
}