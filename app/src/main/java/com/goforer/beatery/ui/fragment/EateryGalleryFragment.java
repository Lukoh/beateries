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

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.goforer.base.model.ListModel;
import com.goforer.base.ui.adapter.GapItemDecoration;
import com.goforer.base.ui.fragment.RecyclerFragment;
import com.goforer.beatery.BEatery;
import com.goforer.beatery.R;
import com.goforer.beatery.model.data.response.EateryGalleryContent;
import com.goforer.beatery.model.event.EateryGalleryEvent;
import com.goforer.beatery.model.event.action.GallerySelectImageAction;
import com.goforer.beatery.ui.activity.EateryGalleryActivity;
import com.goforer.beatery.ui.adapter.EateryGalleryAdapter;
import com.goforer.beatery.utillity.ActivityCaller;
import com.goforer.beatery.utillity.DisplayUtils;
import com.goforer.beatery.web.wire.connecter.Intermediary;
import com.google.gson.JsonElement;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class EateryGalleryFragment extends RecyclerFragment<EateryGalleryContent> {
    private static final String TAG = "EateryGalleryFragment";

    private static final int SPAN_COUNT = 3;
    private static final int SPAN_NUMBER_ONE = 1;

    private static final int REQUEST_ITEM_COUNT = 30;

    private EateryGalleryAdapter mAdapter;

    @Override
    protected RecyclerView.Adapter createAdapter() {
        mAdapter = new EateryGalleryAdapter(mItems, R.layout.grid_gallery_item, true);
        return mAdapter;
    }

    @Override
    protected LayoutManager createLayoutManager() {
        super.setOnProcessListener(new RecyclerFragment.OnProcessListener() {
            @Override
            public void onCompleted(int result) {
                Log.i(TAG, "onCompleted");

                if (result == OnProcessListener.RESULT_ERROR) {
                    Toast.makeText(mContext, R.string.toast_process_error, Toast.LENGTH_SHORT).show();
                    BEatery.closeApplication();
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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, SPAN_COUNT,
                GridLayoutManager.VERTICAL, false);
        GridLayoutManager.SpanSizeLookup spanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return SPAN_NUMBER_ONE;
            }
        };

        spanSizeLookup.setSpanIndexCacheEnabled(true);
        gridLayoutManager.setSpanSizeLookup(spanSizeLookup);
        return gridLayoutManager;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eatery_gallery, container, false);
    }

    private void request(boolean is_new) {
        EateryGalleryEvent event = new EateryGalleryEvent(is_new);
        Intermediary.INSTANCE.getGalleryContents(mContext,
                ((EateryGalleryActivity) this.getActivity()).getEateryId(), mCurrentPage,
                REQUEST_ITEM_COUNT, event);
    }

    private void update() {
        EateryGalleryEvent event = new EateryGalleryEvent(false);
        Intermediary.INSTANCE.updateGalleryContents(mContext,
                ((EateryGalleryActivity) this.getActivity()).getEateryId(), mCurrentPage,
                REQUEST_ITEM_COUNT, event);
    }

    @Override
    protected void requestData(boolean is_new) {
        Log.i(TAG, "requestData");

        request(is_new);
    }

    @Override
    protected void updateData() {
        update();

        Log.i(TAG, "updateData");
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(EateryGalleryEvent event) {
        handleEvent(event);
    }

    @Override
    protected RecyclerView.ItemDecoration createItemDecoration() {
        int gap = DisplayUtils.INSTANCE.dpToPx(mContext, 5);
        return new GapItemDecoration(GapItemDecoration.VERTICAL_LIST, gap) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);

                if ((position + 1) % SPAN_COUNT == 1) {
                    outRect.set(mGap, mGap, 0, 0);
                } else if ((position + 1) % SPAN_COUNT == 2) {
                    outRect.set(mGap, mGap, 0, 0);
                } else if ((position + 1) % SPAN_COUNT == 0) {
                    outRect.set(mGap, mGap, mGap, 0);
                }
            }
        };
    }

    @Override
    protected List<EateryGalleryContent> parseItems(JsonElement json) {
        return new ListModel<>(EateryGalleryContent.class).fromJson(json);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAction(GallerySelectImageAction action) {
        if (mActivity.resumed()) {
            ActivityCaller.INSTANCE.callGalleryImageViewer(mContext, action.getContentImage(),
                    action.getItemIndex());
        }
    }
}
