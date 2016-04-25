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

package com.goforer.beatery.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goforer.base.ui.adapter.BaseListAdapter;
import com.goforer.base.ui.adapter.BaseViewHolder;
import com.goforer.base.ui.adapter.DefaultViewHolder;
import com.goforer.base.ui.view.ContentImageView;
import com.goforer.beatery.model.data.response.EateryGalleryContent;
import com.goforer.beatery.model.event.action.GallerySelectImageAction;
import com.goforer.beatery.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.InjectView;

public class EateryGalleryAdapter extends
        BaseListAdapter<EateryGalleryContent, RecyclerView.ViewHolder> {

    public EateryGalleryAdapter(List<EateryGalleryContent> items, int layoutResId) {
        super(items, layoutResId);
    }

    @Override
    public int getItemCount() {
        int count  = super.getItemCount();

        if (isReachedToLast() && count >= 0) {
            count++;
        } else if (count > 1) {
            count++;
        }

        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (isReachedToLast() && position == getItemCount() - 1) {
            return VIEW_TYPE_FOOTER;
        } else if (position > 1 && position == getItemCount() - 1) {
            return VIEW_TYPE_LOADING;
        }

        return VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view;
        switch (type) {
            case VIEW_TYPE_FOOTER:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_last_item,
                        viewGroup, false);
                return new DefaultViewHolder(view);
            case VIEW_TYPE_LOADING:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_loading_item,
                        viewGroup, false);
                return new DefaultViewHolder(view);
            default:
                return super.onCreateViewHolder(viewGroup, type);
        }
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolder(View view, int type) {
        return new GalleryContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)){
            case VIEW_TYPE_FOOTER:
            case VIEW_TYPE_LOADING:
                return;
            default:
                super.onBindViewHolder(viewHolder, position);
        }
    }

    static class GalleryContentViewHolder extends BaseViewHolder<EateryGalleryContent> {
        private EateryGalleryContent mContent;

        @InjectView(R.id.iv_content)
        ContentImageView mContentImageView;
        @InjectView(R.id.tv_title)
        TextView mTitleView;

        public GalleryContentViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindItem(@NonNull final EateryGalleryContent content) {
            mContent = content;

            mContentImageView.setImage(mContent.getContentThumbnailImage());
            mTitleView.setText(mContent.getContentThumbnailImage().getName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GallerySelectImageAction action = new GallerySelectImageAction();
                    action.setContentImage(mContent.getContentImage());
                    action.setItemIndex(mContent.getContentIndex());
                    EventBus.getDefault().post(action);
                }
            });
        }
    }
}
