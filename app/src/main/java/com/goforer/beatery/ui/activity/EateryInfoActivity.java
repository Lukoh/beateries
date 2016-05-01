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

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.goforer.base.model.ListModel;
import com.goforer.base.ui.activity.BaseActivity;
import com.goforer.base.ui.adapter.DividerItemDecoration;
import com.goforer.beatery.R;
import com.goforer.beatery.model.data.response.Comment;
import com.goforer.beatery.model.data.response.EateryInfo;
import com.goforer.beatery.model.event.DetailCommentPostEvent;
import com.goforer.beatery.model.event.CommentsEvent;
import com.goforer.beatery.model.event.EateryInfoEvent;
import com.goforer.beatery.model.event.SelectHangoutEvent;
import com.goforer.beatery.model.event.action.EateryInfoUpdatedAction;
import com.goforer.beatery.model.event.action.EateryLikeAction;
import com.goforer.beatery.model.event.action.MakeCallAction;
import com.goforer.beatery.model.event.action.MapCallAction;
import com.goforer.beatery.model.event.action.OlderCommentLoadAction;
import com.goforer.beatery.model.event.action.WebCallAction;
import com.goforer.beatery.model.updater.EateryInfoUpdater;
import com.goforer.beatery.ui.adapter.EateryInfoAdapter;
import com.goforer.beatery.ui.fragment.ShareDialogFragment;
import com.goforer.beatery.utillity.ActivityCaller;
import com.goforer.beatery.utillity.DisplayUtils;
import com.goforer.beatery.web.wire.connecter.Intermediary;
import com.goforer.beatery.web.wire.connecter.request.RequestClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * This class shows the detailed information about the eatery which is selected on the list.
 */
public class EateryInfoActivity extends BaseActivity {
    private static final String TAG = "EateryInfoActivity";

    private static final int MIN_COMMENT_COUNT_FOR_FAST_SCROLLER = 15;
    private static final int MAX_COMMENT_LENGTH = 300;
    private static final int SCROLL_DELAY_TIME = 300;
    private static final int COMMENT_ITEM_COUNT = 10;

    private EateryInfo mEateryInfo;
    private EateryInfoUpdater mUpdater;
    private List<Comment> mComments = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private EateryInfoAdapter mAdapter;

    private RecyclerView.OnScrollListener mOnLoadMoreListener;

    private int navigation_bar_height = 0;

    private boolean mIsScrolledToComment = false;
    private boolean mIsShownCommentScroll = false;
    private boolean mPostEnabled = true;

    @BindView(R.id.swipyrefreshlayout)
    SwipyRefreshLayout mSwipeLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.comment_bar)
    View mCommentBar;
    @BindView(R.id.comment_holder)
    View mCommentHolder;
    @BindView(R.id.et_comment)
    EditText mCommentText;
    @BindView(R.id.tv_post)
    TextView mPostView;
    @BindView(R.id.fam_menu)
    FloatingActionMenu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mIsScrolledToComment = getIntent().getBooleanExtra(ActivityCaller.EXTRA_SCROLL_TO_COMMENT,
                false);
        String postString = getIntent().getStringExtra(EateryInfo.class.getName());
        if (!TextUtils.isEmpty(postString)) {
            mEateryInfo = EateryInfo.gson().fromJson(postString, EateryInfo.class);
        }

        if (mEateryInfo == null) {
            long eventId = fetchId();

            if (eventId < 0) {
                infoFetchFail();
                return;
            }

            mEateryInfo = new EateryInfo();
            mEateryInfo.setId(eventId);
        }

        super.onCreate(savedInstanceState);

        mUpdater = new EateryInfoUpdater();
        mUpdater.setOnUpdateListener(new EateryInfoUpdater.onUpdateListener() {
            @Override
            public void onChanged(EateryInfo eateryInfo, boolean isNeedNotify) {
                Log.i(TAG, "onChanged");

                if (mEateryInfo.getId() != eateryInfo.getId()) {
                    mEateryInfo = eateryInfo;
                    setEateryInfoChanged(isNeedNotify);
                }
            }
        });

        mUpdater.register();

        refreshComment();
    }

    @Override
    protected void onPause() {
        DisplayUtils.INSTANCE.hideSoftKeyboard(this, mCommentText);
        super.onPause();

        removeScrollListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setScrollListener(mLayoutManager);
    }

    @Override
    protected void onDestroy() {
        if (mUpdater != null) {
            mUpdater.unregister();
        }
        super.onDestroy();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_eatery_detail_view);
    }

    @Override
    protected void setViews() {
        super.setViews();

        mMenu.showMenuButton(true);
        mMenu.setClosedOnTouchOutside(true);

        setupSwipeLayout();
        mCommentBar.setVisibility(View.VISIBLE);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new EateryInfoAdapter(mEateryInfo, mComments, R.layout.list_view_comments);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        setScrollListener(mLayoutManager);

        InputFilter lengthFilter = new InputFilter.LengthFilter(MAX_COMMENT_LENGTH);
        mCommentText.setFilters(new InputFilter[]{lengthFilter});

        if (mCommentText.getText().toString().isEmpty()) {
            mPostView.setEnabled(false);
        }

        mCommentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkCommentContent();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
    }

    @Override
    protected void setActionBar() {
        super.setActionBar();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.bar_back_mtrl_alpha_90);
            actionBar.setTitle(mEateryInfo.getName() + "'s" +
                            getResources().getString(R.string.eatery_information));
        }
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
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEvent(DetailCommentPostEvent event) {
        dismissProgress();
        mPostEnabled = true;
        if (event.getResponse() != null && event.getResponse().isSuccessful()) {
            mCommentText.setText(null);
            setCommentCount(event.getEateryInfo().getCommentCount());
            requestCommentAndScroll();
        } else {
            Toast.makeText(getApplicationContext(),
                    R.string.toast_comment_send_fail, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * An OnScrollListener can be set on a RecyclerView to receive messages
     * when a scrolling event has occurred on that RecyclerView.
     *
     * If you are planning to have several listeners at the same time, use
     * RecyclerView#addOnScrollListener. If there will be only one listener at the time and you
     * want your components to be able to easily replace the listener use
     * RecyclerView#setOnScrollListener.
     */
    private void setScrollListener(final LinearLayoutManager layoutManager) {
        removeScrollListener();

        mOnLoadMoreListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0) {
                    showOlderViewPhaseScroller();
                } else {
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                    if (lastVisibleItemPosition >= totalItemCount - 1) {
                        hideOlderViewPhaseScroller();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mMenu.showMenu(true);
                } else {
                    mMenu.hideMenu(true);
                }
            }
        };

        mRecyclerView.addOnScrollListener(mOnLoadMoreListener);
    }

    /**
     * Remove a listener that was notified of any changes in scroll state or position.
     */
    private void removeScrollListener() {
        if (mOnLoadMoreListener != null) {
            mRecyclerView.removeOnScrollListener(mOnLoadMoreListener);
        }
    }

    private long fetchId() {
        long eateryId = -1;

        Uri uri = getIntent().getData();
        String fragment = uri.getFragment();
        if (!TextUtils.isEmpty(fragment)) {
            try {
                eateryId = Long.parseLong(fragment.split("=")[1]);
            } catch (Exception ignored) {
            }
        } else {
            try {
                String path = uri.getLastPathSegment();
                eateryId = Long.parseLong(path);
            } catch (Exception ignored) {
            }
        }
        return eateryId;
    }

    private void infoFetchFail() {
        Toast.makeText(getApplicationContext(), R.string.toast_eatery_info_fetch_fail,
                Toast.LENGTH_SHORT).show();
        supportFinishAfterTransition();
    }

    private boolean checkCommentContent() {
        if (mCommentText.getText().toString().trim().length() > 0) {
            mPostView.setEnabled(true);
            return true;
        } else {
            mPostView.setEnabled(false);
            return false;
        }
    }

    /**
     * RequestClient to get the Eatery Information or comments from server.
     *
     * @param swipyRefreshLayoutDirection set to TOP to request Eatery information
     *                                    set to BOTTOM to request Comments
     */
    private void request(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
        if (swipyRefreshLayoutDirection == SwipyRefreshLayoutDirection.TOP) {
            requestEateryInfo();
        } else if (swipyRefreshLayoutDirection == SwipyRefreshLayoutDirection.BOTTOM) {
            if (mComments.isEmpty()) {
                requestComment(false);
            } else {
                requestComment(mComments.get(mComments.size() - 1).getCommentId(),
                        COMMENT_ITEM_COUNT, false, false, false, true);
            }
        }
    }

    /**
     * The comments should be refreshed whenever the user refresh the contents of a view via
     * a vertical swipe gesture.
     * <p>
     * Be refreshed as a result of the gesture.
     * The comments must be provided to allow refresh of the content wherever this gesture
     * is used.
     * </p>
     */
    private void refreshComment() {
        mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
                if (mIsScrolledToComment) {
                    request(SwipyRefreshLayoutDirection.BOTTOM);
                } else {
                    request(SwipyRefreshLayoutDirection.TOP);
                }

                mSwipeLayout.setRefreshing(true);
            }
        });
    }

    /**
     * Set the {@link SwipeRefreshLayout}'s setting that this activity will use.
     */
    private void setupSwipeLayout() {
        mSwipeLayout.setColorSchemeResources(
                R.color.swipeBar);
        mSwipeLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
                request(swipyRefreshLayoutDirection);
            }
        });
    }

    private void requestEateryInfo() {
        EateryInfoEvent event = new EateryInfoEvent();
        event.setEateryId(mEateryInfo.getId());
        Intermediary.INSTANCE.getEateryInfo(this, mEateryInfo.getId(), event);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEvent(EateryInfoEvent event) {
        if (event.getEateryId() == mEateryInfo.getId()) {
            if (event.getResponse() != null && event.getResponse().isSuccessful()) {
                JsonArray jsonArray = event.getResponse().getResponseEntity().getAsJsonArray();
                if (jsonArray.size() > 0) {
                    JsonElement element = jsonArray.get(0);
                    mEateryInfo = EateryInfo.gson().fromJson(element, EateryInfo.class);
                    setEateryInfoChanged(true);
                    requestComment(false);
                    EventBus.getDefault().post(new EateryInfoUpdatedAction(mEateryInfo));
                } else {
                    infoFetchFail();
                }
            } else {
                infoFetchFail();
            }

            notifyRefreshFinished();
        }
    }

    private void requestCommentAndScroll() {
        if (mComments.isEmpty()) {
            requestComment(true);
        } else {
            requestComment(mComments.get(mComments.size() - 1).getCommentId(), 10, true, false,
                    false, true);
        }
    }

    private void requestComment(boolean isScrolledToBottom) {
        requestComment(-1L, 20, isScrolledToBottom, true, false, false);
    }

    private void requestComment(long commentId, int comment_count, boolean isScrolledToBottom,
                                boolean isReloaded, boolean isOlder, boolean isRefreshed) {
        CommentsEvent event = new CommentsEvent(true);
        event.setEateryId(mEateryInfo.getId());
        event.setScrolledToBottom(isScrolledToBottom);
        event.setReloaded(isReloaded);
        event.setOlder(isOlder);
        event.setRefreshed(isRefreshed);

        String reqCommentSort = isOlder ? RequestClient.GET_TYPE_PREVIOUS : RequestClient.GET_TYPE_LATEST;
        // Have to get all comments with the eatery ID if commentId is -1L.
        Intermediary.INSTANCE.getComments(this, mEateryInfo.getId(), commentId, reqCommentSort,
                comment_count, event);
    }

    private  List<Comment> parseItems(JsonElement json) {
        return new ListModel<>(Comment.class).fromJson(json);
    }

    private void showComments(final CommentsEvent event, List<Comment> comments) {
        if (event.isReloaded() || event.isOlder()) {
            mComments.clear();
        }

        if (!event.isOlder()) {
            mComments.addAll(comments);
        } else {
            mComments.addAll(0, comments);
        }

        if (mComments.size() <= 0) {
            mAdapter.setEmptyItems(true);
        } else {
            mAdapter.setEmptyItems(false);
        }

        if (event.isRefreshed()) {
            mAdapter.setMoreData(event.getResponse().getResponseOption()
                    .getRemainingRows());
            mAdapter.notifyDataSetChanged();
        } else {
            mAdapter.notifyItemRangeChanged(mAdapter.getItemCount() - comments.size(),
                    comments.size());
        }

        if (mIsScrolledToComment || event.isScrolledToBottom()) {
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.smoothScrollToPosition(mAdapter.getHeaderCount() +
                            mRecyclerView.getAdapter().getItemCount() - 1);
                }
            }, SCROLL_DELAY_TIME);
            mIsShownCommentScroll = true;
            mIsScrolledToComment = false;
        } else if (!event.isOlder()) {
            int lastItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findLastVisibleItemPosition();
            if (!event.isReloaded())
                mRecyclerView.scrollToPosition(lastItem + comments.size());
        }

        showOlderViewPhaseScroller();
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onAction(OlderCommentLoadAction action) {
        requestComment(-1L, 10, false, true, true, false);
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onAction(MakeCallAction action) {
        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager =
                (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        ActivityCaller.INSTANCE.callDial(this, action.getPhoneNumber());
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onAction(MapCallAction action) {
        ActivityCaller.INSTANCE.callGoogleMap(this, action.getEateryInfo());
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onAction(WebCallAction action) {
        ActivityCaller.INSTANCE.callWebsite(this, action.getWebsiteAddress());
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEvent(final CommentsEvent event) {
        if (event.getEateryId() == mEateryInfo.getId()) {
            if (event.getResponse() != null && event.getResponse().isSuccessful()) {
                new AsyncTask<Void, Void, List<Comment>>() {
                    @Override
                    protected List<Comment> doInBackground(Void... params) {
                        return parseItems(event.getResponse().getResponseEntity());
                    }

                    @Override
                    protected void onPostExecute(List<Comment> items) {
                        super.onPostExecute(items);

                        showComments(event, items);
                    }
                }.execute();
            } else {
                infoFetchFail();
            }
        }

        notifyRefreshFinished();
    }

    private void showOlderViewPhaseScroller() {
        if (mIsShownCommentScroll) return;
        int count = mRecyclerView.getAdapter().getItemCount();
        if (count >= MIN_COMMENT_COUNT_FOR_FAST_SCROLLER && mAdapter.isCommentPhaseVisible()) {
            mAdapter.showCommentPhase();
            mIsShownCommentScroll = true;
        }
    }

    private void setEateryInfoChanged(boolean isNeedNotify) {
        if (isNeedNotify) {
            mAdapter.setEateryInfo(mEateryInfo);
        } else {
            mAdapter.setEateryInfo(mEateryInfo, false);
        }
    }

    private void notifyRefreshFinished() {
        if (mSwipeLayout.isRefreshing()) {
            mSwipeLayout.setRefreshing(false);
        }
    }

    private void hideCommentHolder(boolean showStatBar) {
        mCommentBar.setVisibility(showStatBar ? View.VISIBLE : View.GONE);
        mCommentHolder.setVisibility(View.GONE);
    }

    private void hideOlderViewPhaseScroller() {
        mAdapter.hideCommentPhase();
    }

    private void setCommentCount(long count) {
        mAdapter.setCommentCount(count);
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEvent(final SelectHangoutEvent event) {
        Log.i(TAG, "onEvent - SelectHangoutEvent");

        if (event.getResult() == SelectHangoutEvent.SELECT_HANGOUT_SUCCESS) {
            Toast.makeText(getApplicationContext(),
                    R.string.toast_select_hangout_success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    R.string.toast_select_hangout_failure, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("")
    @OnClick(R.id.fab_share)
    void onShareFromFab() {
        FragmentManager fragmentManager =  getSupportFragmentManager();
        ShareDialogFragment shareDialog = new ShareDialogFragment();
        shareDialog.setEateryInfo(mEateryInfo);
        shareDialog.show(fragmentManager, "Share");
    }

    @SuppressWarnings("")
    @OnClick(R.id.fam_menu)
    void onMenuToggle() {
        mMenu.toggle(true);
    }

    @OnClick(R.id.fab_gallery)
    void onCallGallery() {
        ActivityCaller.INSTANCE.callEateryGallery(getApplicationContext(), mEateryInfo.getId(),
                mEateryInfo.getName());
    }

    @SuppressWarnings("")
    @OnClick(R.id.fab_event)
    void onCallEvent() {
        ActivityCaller.INSTANCE.callEateryEvent(getApplicationContext(), mEateryInfo.getId(),
                mEateryInfo.getName());
    }

    @SuppressWarnings("")
    @OnClick(R.id.fab_hangout)
    void onSelectHangout() {
        SelectHangoutEvent event = new SelectHangoutEvent();
        Intermediary.INSTANCE.selectHangout(getApplicationContext(), mEateryInfo.getId(), event);
    }

    @SuppressWarnings("")
    @OnClick(R.id.btn_like)
    void onLikeAction() {
        EventBus.getDefault().post(new EateryLikeAction());
    }

    @OnClick(R.id.btn_share)
    void onShareFromComment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ShareDialogFragment shareDialog = new ShareDialogFragment();
        shareDialog.show(fragmentManager, ShareDialogFragment.SHARE_DIALOG_TAG);
    }

    @SuppressWarnings("")
    @OnClick(R.id.tv_post)
    void onPostView() {
        if (mPostEnabled) {
            if (checkCommentContent()) {
                mPostEnabled = false;
                String comment = mCommentText.getText().toString().trim();

                DetailCommentPostEvent event = new DetailCommentPostEvent();
                Intermediary.INSTANCE.postComment(getApplicationContext(),
                        mEateryInfo.getId(), comment, event);

                if (mCommentText.hasFocus()) {
                    mCommentText.clearFocus();
                    DisplayUtils.INSTANCE.hideSoftKeyboard(getApplicationContext(),
                            mCommentText);
                }
            }
        }
    }

    private class PhoneCallListener extends PhoneStateListener {
        private static final String TAG = "PhoneCallListener";

        private boolean isPhoneCalling = false;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (TelephonyManager.CALL_STATE_RINGING == state) {
                Log.i(TAG, "Call State : RINGING");
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                Log.i(TAG, "Call State : OFF-HOOK");

                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                Log.i(TAG, "IDLE");

                if (isPhoneCalling) {
                    Log.i(TAG, "restart app");

                    Intent intent = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(
                                    getBaseContext().getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    isPhoneCalling = false;
                }

            }
        }
    }
}
