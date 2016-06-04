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
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.goforer.base.model.ListModel;
import com.goforer.base.ui.adapter.GapItemDecoration;
import com.goforer.base.ui.fragment.RecyclerFragment;
import com.goforer.beatery.BEatery;
import com.goforer.beatery.model.data.response.EateryInfo;
import com.goforer.beatery.model.event.SearchEvent;
import com.goforer.beatery.model.event.action.SearchQueryAction;
import com.goforer.beatery.ui.adapter.EateryListAdapter;
import com.goforer.beatery.utillity.DisplayUtils;
import com.goforer.beatery.web.wire.connecter.Intermediary;
import com.goforer.beatery.R;

import com.google.gson.JsonElement;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class EaterySearchFragment extends RecyclerFragment<EateryInfo> {
    private static final String TAG = "EaterySearchFragment";

    private String mKeyword;
    private String mSearchTag;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eatery_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setItemHasFixedSize(true);
    }

    @Override
    protected LayoutManager createLayoutManager() {
        super.setOnProcessListener(new RecyclerFragment.OnProcessListener() {
            @Override
            public void onCompleted(int result) {
                Log.i(TAG, "onCompleted");

                if (result == OnProcessListener.RESULT_SUCCESS) {
                    if (mItems.isEmpty()) {
                        Toast.makeText(mContext, R.string.toast_search_result_none, Toast.LENGTH_SHORT).show();
                        BEatery.closeApplication();
                    }
                } else {
                    Toast.makeText(mContext,
                            R.string.toast_process_error, Toast.LENGTH_SHORT).show();
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
            }

            @Override
            public void onScrolled() {
                Log.i(TAG, "onScrolled");
            }
        });

        return new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
    }

    @Override
    protected RecyclerView.ItemDecoration createItemDecoration() {
        int gap = DisplayUtils.INSTANCE.dpToPx(mContext, 5);
        return new GapItemDecoration(GapItemDecoration.VERTICAL_LIST, gap);

    }

    @Override
    protected RecyclerView.Adapter createAdapter() {
        return new EateryListAdapter(mActivity, mItems, R.layout.list_eatery_item, true);
    }

    @Override
    protected void requestData(boolean isNew) {
        request(isNew);

        Log.i(TAG, "requestData");
    }

    @Override
    protected void updateData() {
        doneRefreshing();

        Log.i(TAG, "updateData");
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(SearchEvent event) {
        if (event.isMine(mKeyword)){
            handleEvent(event);
        }
    }

    @SuppressWarnings("")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SearchQueryAction action) {
        mKeyword = action.getKeyword();
        mSearchTag = action.getTabTag();
        if (action.isMine(getClass().getName())) {
            refresh();
        }
    }

    @Override
    protected List<EateryInfo> parseItems(JsonElement json) {
        return new ListModel<>(EateryInfo.class).fromJson(json);
    }

    private void request(boolean is_new) {
        if (!TextUtils.isEmpty(mKeyword)) {
            SearchEvent event = new SearchEvent(is_new);
            event.setTag(mKeyword);
            if (mSearchTag.equals(getString(R.string.eatery_search_by_name))) {
                Intermediary.INSTANCE.searchByName(mContext, mKeyword, mCurrentPage, event);
            } else {
                Intermediary.INSTANCE.searchByAddress(mContext, mKeyword, mCurrentPage, event);
            }
        }
    }
}
