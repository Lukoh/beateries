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

package com.goforer.beatery.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.goforer.base.model.ListModel;
import com.goforer.base.ui.adapter.DividerItemDecoration;
import com.goforer.base.ui.fragment.BaseFragment;
import com.goforer.beatery.R;
import com.goforer.beatery.model.data.response.Comment;
import com.goforer.beatery.model.data.response.EateryInfo;
import com.goforer.beatery.model.event.CommentsEvent;
import com.goforer.beatery.model.event.DetailCommentPostEvent;
import com.goforer.beatery.model.event.EateryInfoEvent;
import com.goforer.beatery.model.event.SelectHangoutEvent;
import com.goforer.beatery.model.event.action.EateryInfoUpdatedAction;
import com.goforer.beatery.model.event.action.EateryLikeAction;
import com.goforer.beatery.model.event.action.MakeCallAction;
import com.goforer.beatery.model.event.action.OlderCommentLoadAction;
import com.goforer.beatery.model.event.action.ViewMapAction;
import com.goforer.beatery.model.event.action.ViewWebAction;
import com.goforer.beatery.model.updater.EateryInfoUpdater;
import com.goforer.beatery.ui.adapter.EateryInfoAdapter;
import com.goforer.beatery.utillity.ActivityCaller;
import com.goforer.beatery.utillity.DisplayUtils;
import com.goforer.beatery.web.communicator.Intermediary;
import com.goforer.beatery.web.communicator.request.RequestClient;
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

public class EateryInfoFragment extends BaseFragment {
    private static final String TAG = "EateryInfoFragment";

    private static final int MIN_COMMENT_COUNT_FOR_FAST_SCROLLER = 15;
    private static final int MAX_COMMENT_LENGTH = 300;
    private static final int SCROLL_DELAY_TIME = 300;
    private static final int COMMENT_ITEM_COUNT = 10;

    private List<EateryInfo> mItems;
    private EateryInfoUpdater mUpdater;
    private List<Comment> mComments = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private EateryInfoAdapter mAdapter;
    private EateryInfo mEateryInfo;

    private RecyclerView.OnScrollListener mOnLoadMoreListener;

    private int mItemPosition;

    private boolean mIsScrolledToComment = false;
    private boolean mIsShownCommentScroll = false;
    private boolean mPostEnabled = true;

    @BindView(R.id.swipyrefreshlayout)
    SwipyRefreshLayout mSwipeLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.comment_bar)
    View mCommentBar;
    @BindView(R.id.et_comment)
    EditText mCommentText;
    @BindView(R.id.tv_post)
    TextView mPostView;
    @BindView(R.id.fam_menu)
    FloatingActionMenu mMenu;

    /**
     * Create a new instance of EateryInfoFragment
     */
    static public EateryInfoFragment newInstance(List<EateryInfo> eateryInfoItems, int position,
                                                 boolean isScrolledToComment) {
        EateryInfoFragment fragment = new EateryInfoFragment();
        fragment.mItems = eateryInfoItems;
        fragment.mItemPosition = position;
        fragment.mIsScrolledToComment = isScrolledToComment;

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eatery_detail_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        setViews();
    }

    @Override
    public void onPause() {
        DisplayUtils.INSTANCE.hideSoftKeyboard(mContext, mCommentText);

        super.onPause();

        removeScrollListener();
    }

    @Override
    public void onResume() {
        super.onResume();

        setScrollListener(mLayoutManager);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        if (mUpdater != null) {
            mUpdater.unregister();
        }

        super.onDestroy();
    }

    private void setViews() {
        mMenu.showMenuButton(true);
        mMenu.setClosedOnTouchOutside(true);

        setupSwipeLayout();
        mCommentBar.setVisibility(View.VISIBLE);
        mLayoutManager = new LinearLayoutManager(mContext);
        mAdapter = new EateryInfoAdapter(mItems.get(mItemPosition), mComments,
                R.layout.list_view_comments, true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,
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
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEvent(DetailCommentPostEvent event) {
        mActivity.dismissProgress();
        mPostEnabled = true;
        if (event.getResponseClient() != null && event.getResponseClient().isSuccessful()) {
            mCommentText.setText(null);
            setCommentCount(event.getEateryInfo().getCommentCount());
            requestCommentAndScroll();
        } else {
            Toast.makeText(mContext,
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

    private void infoFetchFail() {
        Toast.makeText(mContext, R.string.toast_eatery_info_fetch_fail,
                Toast.LENGTH_SHORT).show();
        mActivity.supportFinishAfterTransition();
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
        event.setEateryId(mItems.get(mItemPosition).getId());
        Intermediary.INSTANCE.getEateryInfo(mContext, mItems.get(mItemPosition).getId(), event);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEvent(EateryInfoEvent event) {
        if (event.getEateryId() == mItems.get(mItemPosition).getId()) {
            if (event.getResponseClient() != null && event.getResponseClient().isSuccessful()) {
                JsonArray jsonArray = event.getResponseClient().getResponseEntity().getAsJsonArray();
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
        if (mEateryInfo == null) {
            event.setEateryId(mItems.get(mItemPosition).getId());
        } else {
            event.setEateryId(mEateryInfo.getId());
        }

        event.setScrolledToBottom(isScrolledToBottom);
        event.setReloaded(isReloaded);
        event.setOlder(isOlder);
        event.setRefreshed(isRefreshed);

        String reqCommentSort = isOlder ?
                RequestClient.GET_TYPE_PREVIOUS : RequestClient.GET_TYPE_LATEST;
        // Have to get all comments with the eatery ID if commentId is -1L.
        if (mEateryInfo == null) {
            Intermediary.INSTANCE.getComments(mContext, mItems.get(mItemPosition).getId(),
                    commentId, reqCommentSort, comment_count, event);
        } else {
            Intermediary.INSTANCE.getComments(mContext, mEateryInfo.getId(), commentId,
                    reqCommentSort, comment_count, event);
        }
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
            mAdapter.setMoreData(event.getResponseClient().getResponseOption()
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
                (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        ActivityCaller.INSTANCE.callDial(mContext, action.getPhoneNumber());
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onAction(ViewMapAction action) {
        ActivityCaller.INSTANCE.callGoogleMap(mContext, action.getEateryInfo());
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onAction(ViewWebAction action) {
        ActivityCaller.INSTANCE.callWebsite(mContext, action.getWebsiteAddress());
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEvent(final CommentsEvent event) {
        long id;
        if (mEateryInfo == null) {
            id = mItems.get(mItemPosition).getId();
        } else {
            id = mEateryInfo.getId();
        }

        if (event.getEateryId() == id) {
            if (event.getResponseClient() != null && event.getResponseClient().isSuccessful()) {
                new AsyncTask<Void, Void, List<Comment>>() {
                    @Override
                    protected List<Comment> doInBackground(Void... params) {
                        return parseItems(event.getResponseClient().getResponseEntity());
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
            Toast.makeText(mContext, R.string.toast_select_hangout_success,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, R.string.toast_select_hangout_failure,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("")
    @OnClick(R.id.fab_share)
    void onShareFromFab() {
        FragmentManager fragmentManager =  mActivity.getSupportFragmentManager();
        ShareDialogFragment shareDialog = new ShareDialogFragment();
        if (mEateryInfo == null)  {
            shareDialog.setEateryInfo(mItems.get(mItemPosition));
        } else {
            shareDialog.setEateryInfo(mEateryInfo);
        }

        shareDialog.show(fragmentManager, "Share");
    }

    @SuppressWarnings("")
    @OnClick(R.id.fam_menu)
    void onMenuToggle() {
        mMenu.toggle(true);
    }

    @OnClick(R.id.fab_gallery)
    void onCallGallery() {
        if (mEateryInfo == null ) {
            ActivityCaller.INSTANCE.callEateryGallery(mContext, mItems.get(mItemPosition).getId(),
                    mEateryInfo.getName());
        } else {
            ActivityCaller.INSTANCE.callEateryGallery(mContext, mEateryInfo.getId(),
                    mEateryInfo.getName());
        }
    }

    @SuppressWarnings("")
    @OnClick(R.id.fab_event)
    void onCallEvent() {
        if (mEateryInfo == null ) {
            ActivityCaller.INSTANCE.callEateryEvent(mContext, mItems.get(mItemPosition).getId(),
                    mEateryInfo.getName());
        } else {
            ActivityCaller.INSTANCE.callEateryEvent(mContext, mEateryInfo.getId(),
                    mEateryInfo.getName());
        }
    }

    @SuppressWarnings("")
    @OnClick(R.id.fab_hangout)
    void onSelectHangout() {
        SelectHangoutEvent event = new SelectHangoutEvent();
        if (mEateryInfo == null ) {
            Intermediary.INSTANCE.selectHangout(mContext, mItems.get(mItemPosition).getId(), event);
        } else {
            Intermediary.INSTANCE.selectHangout(mContext, mEateryInfo.getId(), event);
        }
    }

    @SuppressWarnings("")
    @OnClick(R.id.btn_like)
    void onLikeAction() {
        EventBus.getDefault().post(new EateryLikeAction());
    }

    @OnClick(R.id.btn_share)
    void onShareFromComment() {
        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
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
                if (mEateryInfo == null) {
                    Intermediary.INSTANCE.postComment(mContext, mItems.get(mItemPosition).getId(),
                            comment, event);
                } else {
                    Intermediary.INSTANCE.postComment(mContext, mEateryInfo.getId(), comment, event);
                }

                if (mCommentText.hasFocus()) {
                    mCommentText.clearFocus();
                    DisplayUtils.INSTANCE.hideSoftKeyboard(mContext,
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

                    Intent intent = mContext.getPackageManager()
                            .getLaunchIntentForPackage(
                                    mContext.getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    isPhoneCalling = false;
                }

            }
        }
    }

}
