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

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.goforer.base.model.ListModel;
import com.goforer.base.ui.fragment.RecyclerFragment;
import com.goforer.beatery.BEatery;
import com.goforer.beatery.R;
import com.goforer.beatery.common.Facility;
import com.goforer.beatery.helper.AccountHelper;
import com.goforer.beatery.model.data.response.EateryInfo;
import com.goforer.beatery.model.event.BestEateryListEvent;
import com.goforer.beatery.model.event.EateryListEvent;
import com.goforer.beatery.model.event.MyHangoutsEvent;
import com.goforer.beatery.model.event.OptimalEateriesEvent;
import com.goforer.beatery.model.event.action.EateryEventAction;
import com.goforer.beatery.model.event.action.EateryGalleryAction;
import com.goforer.beatery.model.event.action.EaterySelectAction;
import com.goforer.beatery.model.event.action.RequestDoneAction;
import com.goforer.beatery.model.event.action.SearchEnableAction;
import com.goforer.beatery.ui.activity.SignUpActivity;
import com.goforer.beatery.ui.adapter.EateryListAdapter;
import com.goforer.beatery.utillity.ActivityCaller;
import com.goforer.beatery.web.wire.connecter.Intermediary;
import com.google.gson.JsonElement;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

public class EateryListFragment extends RecyclerFragment<EateryInfo> {
    private static final String TAG = "EateryListFragment";

    private static final int OPTIMAL_EATERY_LIST_BY_ADDRESS = 0;
    private static final int OPTIMAL_EATERY_LIST_BY_COORDINATES = 1;

    private EateryListAdapter mAdapter;

    @InjectView(R.id.fam_menu)
    FloatingActionMenu mMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eatery_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Facility.INSTANCE.setFabIndex(Facility.FAB_BASE_EATERY_INFO_INDEX);

        mMenu.showMenuButton(true);
        mMenu.setClosedOnTouchOutside(true);

        setItemHasFixedSize(true);
        refresh();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Facility.INSTANCE.setFabIndex(Facility.FAB_BASE_EATERY_INFO_INDEX);
    }

    @Override
    protected LayoutManager createLayoutManager() {
        super.setOnProcessListener(new RecyclerFragment.OnProcessListener() {
            @Override
            public void onCompleted(int result) {
                if (result == OnProcessListener.RESULT_ERROR) {
                    Toast.makeText(mContext, R.string.toast_process_error,
                            Toast.LENGTH_SHORT).show();
                    BEatery.closeApplication();
                } else {
                    RequestDoneAction action = new RequestDoneAction();
                    action.setSearchEnabled(true);
                    EventBus.getDefault().post(action);
                }

            }

            @Override
            public void onScrolledToLast(RecyclerView recyclerView, int dx, int dy) {
                Log.i(TAG, "onScrolledToLast");

                request(false);
            }

            @Override
            public void onScrolling() {
                Log.i(TAG, "onScrolling");

                mMenu.showMenu(true);
            }

            @Override
            public void onScrolled() {
                Log.i(TAG, "onScrolled");

                mMenu.hideMenu(true);
            }
        });

        return new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
    }

    @Override
    protected RecyclerView.Adapter createAdapter() {
        return mAdapter = new EateryListAdapter(mActivity, mItems, R.layout.list_eatery_item);
    }

    @Override
    protected void requestData(boolean isNew) {
        request(isNew);

        Log.i(TAG, "requestData");
    }

    @Override
    protected List<EateryInfo> parseItems(JsonElement json) {
        return new ListModel<>(EateryInfo.class).fromJson(json);
    }

    @Override
    protected void setReachedToLastItem(int itemCount) {
        if (itemCount < REQUEST_ITEM_COUNT) {
            setReachedToLast();
            mAdapter.setReachToLast(true);
        } else {
            mAdapter.setReachToLast(false);
        }
    }

    private void requestEateryList(boolean isNew) {
        searchEnabled(false);

        EateryListEvent event = new EateryListEvent(isNew);
        Intermediary.INSTANCE.getAllEateryListOrderByAddress(mContext, mCurrentPage,
                REQUEST_ITEM_COUNT, event);

        /*
         * In case of ordering by GPS information with latitude and longitude ....
         * Just use the below code if the server can't handles the operation like above
         *
         * EateryListEvent event = new EateryListEvent(isNew);
         * Intermediary.INSTANCE.getEateryAllListOrderByCoordinates(mContext, mCurrentPage,
         *       REQUEST_ITEM_COUNT, event);
         */
    }

    private void requestBestEateryList(boolean isNew) {
        searchEnabled(false);

        BestEateryListEvent event = new BestEateryListEvent(isNew);
        Intermediary.INSTANCE.getBestEateryList(mContext, mCurrentPage, REQUEST_ITEM_COUNT, event);

    }

    private void requestMyHangoutsList(boolean isNew) {
        searchEnabled(false);

        MyHangoutsEvent event = new MyHangoutsEvent(isNew);
        Intermediary.INSTANCE.getMyHangoutsList(mContext, mCurrentPage, REQUEST_ITEM_COUNT, event);
    }

    private void requestOptimalEateryList(boolean isNew, int byWhat) {
        searchEnabled(false);

        OptimalEateriesEvent event = new OptimalEateriesEvent(isNew);
        if (byWhat == OPTIMAL_EATERY_LIST_BY_ADDRESS) {
            Intermediary.INSTANCE.getOptimalEateryListByAddress(mContext, mCurrentPage,
                    REQUEST_ITEM_COUNT, event);
        } else if (byWhat == OPTIMAL_EATERY_LIST_BY_COORDINATES) {
            Intermediary.INSTANCE.getOptimalEateryListByCoordinates(mContext, mCurrentPage,
                    REQUEST_ITEM_COUNT, event);
        }
    }

    private void searchEnabled(boolean searchEnabled) {
        SearchEnableAction action = new SearchEnableAction();
        action.setSearchEnabled(searchEnabled);
        EventBus.getDefault().post(action);
    }

    private void request(boolean isNew) {
        switch (Facility.INSTANCE.getFabIndex()) {
            case Facility.FAB_BASE_EATERY_INFO_INDEX:
            case Facility.FAB_CLOSEST_EATERY_INDEX:
                requestEateryList(isNew);
                break;
            case Facility.FAB_BEST_EATERY_INDEX:
                requestBestEateryList(isNew);
                break;
            case Facility.FAB_HANGOUT_INDEX:
                requestMyHangoutsList(isNew);
                break;
            case Facility.FAB_OPTIMAL_EATERY_INDEX:
                requestOptimalEateryList(isNew, OPTIMAL_EATERY_LIST_BY_ADDRESS);
                break;
            default:
                requestEateryList(isNew);
                break;
        }
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(EateryListEvent event) {
        handleEvent(event);
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(BestEateryListEvent event) {
        handleEvent(event);
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(MyHangoutsEvent event) {
        handleEvent(event);
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(OptimalEateriesEvent event) {
        handleEvent(event);
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAction(EaterySelectAction action) {
        if (mActivity.resumed()) {
            if (action.isScrolledToComment()) {
                ActivityCaller.INSTANCE.callEateryInfo(mContext, action.getEateryInfo(), true);
            } else {
                ActivityCaller.INSTANCE.callEateryInfo(mContext, action.getEateryInfo(), false);
            }
        }
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAction(EateryEventAction action) {
        if (mActivity.resumed()) {
            ActivityCaller.INSTANCE.callEateryEvent(mContext, action.getEateryId(),
                    action.getEateryName());
        }
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAction(EateryGalleryAction action) {
        if (mActivity.resumed()) {
            ActivityCaller.INSTANCE.callEateryGallery(mContext, action.getEateryId(),
                    action.getEateryName());
        }
    }

    @SuppressWarnings("")
    @OnClick(R.id.fam_menu)
    void onMenuToggle() {
        mMenu.toggle(true);
    }

    @SuppressWarnings("")
    @OnClick(R.id.fab_best_eatery)
    void onViewBestEatery() {
        Facility.INSTANCE.setFabIndex(Facility.FAB_BEST_EATERY_INDEX);
        refresh();
    }

    @SuppressWarnings("")
    @OnClick(R.id.fab_closest_eatery)
    void onViewLatestEatery() {
        Facility.INSTANCE.setFabIndex(Facility.FAB_CLOSEST_EATERY_INDEX);
        refresh();
    }

    @SuppressWarnings("")
    @OnClick(R.id.fab_eatery_review)
    void onRequestEateryReview() {
        // Will be implemented
    }

    @SuppressWarnings("")
    @OnClick(R.id.fab_view_my_hangout)
    void onViewMyHangout() {
        Facility.INSTANCE.setFabIndex(Facility.FAB_HANGOUT_INDEX);
        refresh();
    }

    @SuppressWarnings("")
    @OnClick(R.id.fab_optimal_eatery)
    void onViewOptimalEatery() {
        Facility.INSTANCE.setFabIndex(Facility.FAB_OPTIMAL_EATERY_INDEX);
        refresh();
    }

    @SuppressWarnings("")
    @OnClick(R.id.fab_my_profile)
    void onViewMyProfile() {
        ActivityCaller.INSTANCE.callProfileChange(mContext, SignUpActivity.CALL_PROFILE_TYPE,
                AccountHelper.getMe(mContext));
    }
}
