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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goforer.base.ui.adapter.BaseListAdapter;
import com.goforer.base.ui.adapter.BaseViewHolder;
import com.goforer.base.ui.adapter.DefaultViewHolder;
import com.goforer.base.ui.view.SquircleImageView;
import com.goforer.beatery.R;
import com.goforer.beatery.model.data.response.Comment;
import com.goforer.beatery.model.data.response.EateryInfo;
import com.goforer.beatery.model.event.LikeCommentEvent;
import com.goforer.beatery.model.event.DetailLikeEateryEvent;
import com.goforer.beatery.model.event.action.EateryLikeAction;
import com.goforer.beatery.model.event.action.MakeCallAction;
import com.goforer.beatery.model.event.action.ViewMapAction;
import com.goforer.beatery.model.event.action.OlderCommentLoadAction;
import com.goforer.beatery.model.event.action.ViewWebAction;
import com.goforer.beatery.web.wire.connecter.Intermediary;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class EateryInfoAdapter extends BaseListAdapter<Comment> {
    private static final int HEADER_ITEM_COUNT = 1;

    private EateryInfo mEateryInfo;
    private DetailInfoHolder mDetailInfoHolder;

    private boolean hasMoreData = false;

    public EateryInfoAdapter(EateryInfo eateryInfo, List<Comment> items, int layoutResId) {
        super(items, layoutResId);

        mEateryInfo = eateryInfo;
    }

    @Override
    public int getItemCount() {
        int count = super.getItemCount() + HEADER_ITEM_COUNT;

        if (hasMoreData) count++;
        if (isEmptyItems() && count == HEADER_ITEM_COUNT) count++;

        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else if(hasMoreData){
            return VIEW_TYPE_LOADING;
        } else if (isEmptyItems() && (position == getItemCount()-1)) {
            return VIEW_TYPE_NONE_COMMENT;
        }

        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view;

        switch (type) {
            case VIEW_TYPE_HEADER:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.list_eatery_information_item, viewGroup, false);
                return createViewHolder(view, type);
            case VIEW_TYPE_LOADING:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.list_loading_item, viewGroup, false);
                return new DefaultViewHolder(view);
            case VIEW_TYPE_NONE_COMMENT:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.list_last_item, viewGroup, false);
                return new DefaultViewHolder(view);
            default:
                return super.onCreateViewHolder(viewGroup, type);
        }
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolder(View view, int type) {
        if (type == VIEW_TYPE_HEADER) {
            mDetailInfoHolder = new EateryInfoAdapter.DetailInfoHolder(view);
            return mDetailInfoHolder;
        }

        return new EateryInfoAdapter.CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)){
            case VIEW_TYPE_HEADER:
                ((DetailInfoHolder) viewHolder).bindItem(mEateryInfo);
            case VIEW_TYPE_FOOTER:
            case VIEW_TYPE_LOADING:
                return;
            default:
                super.onBindViewHolder(viewHolder, position - getHeaderCount());
        }
    }

    public void setMoreData(int moreDataCount){
        hasMoreData = moreDataCount > 0;
    }

    public void setEateryInfo(EateryInfo eateryInfo) {
        mEateryInfo = eateryInfo;
        notifyItemChanged(0);
    }

    public void setEateryInfo(EateryInfo eateryInfo, boolean isNeedNotify) {
        mEateryInfo = eateryInfo;
        if (isNeedNotify) {
            notifyItemChanged(0);
        }
    }

    public void hideCommentPhase() {
        mDetailInfoHolder.hideCommentPhase();
    }

    public void showCommentPhase() {
        mDetailInfoHolder.showCommentPhase();
    }

    public boolean isCommentPhaseVisible() {
        return mDetailInfoHolder.isVisible();
    }

    public int getHeaderCount(){
        return HEADER_ITEM_COUNT;
    }

    public void setCommentCount(long count) {
        mDetailInfoHolder.setCommentCount(count);
    }

    static class DetailInfoHolder extends BaseViewHolder<EateryInfo> {
        private EateryInfo mEateryInfo;

        @BindView(R.id.iv_logo)
        SquircleImageView mLogoImageView;
        @BindView(R.id.tv_name)
        TextView mNameView;
        @BindView(R.id.tv_type)
        TextView mTypeView;
        @BindView(R.id.tv_menu)
        TextView mMenuView;
        @BindView(R.id.tv_detail_info)
        TextView mDetailInfoView;
        @BindView(R.id.tv_address)
        TextView mAddressView;
        @BindView(R.id.tv_tel)
        TextView mTelephoneView;
        @BindView(R.id.tv_website_address)
        TextView mWebAddressView;
        @BindView(R.id.tv_like_count)
        TextView mLikeCountView;
        @BindView(R.id.tv_comment_count)
        TextView mCommentCountView;
        @BindView(R.id.container_comment_phase)
        LinearLayout mCommentPhaseContainer;

        public DetailInfoHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindItem(@NonNull final EateryInfo eateryInfo) {
            mEateryInfo = eateryInfo;

            if (mEateryInfo.getCommentCount() > 0) {
                mCommentPhaseContainer.setVisibility(View.VISIBLE);
            } else {
                mCommentPhaseContainer.setVisibility(View.GONE);
            }

            mLogoImageView.setImage(mEateryInfo.getLogo());
            mNameView.setText(mEateryInfo.getName());
            mTypeView.setText(mEateryInfo.getType());
            mMenuView.setText(mEateryInfo.getBestMenu());
            mDetailInfoView.setText(mEateryInfo.getDetailInformation());
            mAddressView.setText(mEateryInfo.getAddress());
            mTelephoneView.setText(mEateryInfo.getTelephone());
            mWebAddressView.setText(mEateryInfo.getWebsite());
            mLikeCountView.setText(String.valueOf(mEateryInfo.getLikeCount()));
            mCommentCountView.setText(String.valueOf(mEateryInfo.getCommentCount()));
        }

        public void hideCommentPhase() {
            if (mCommentPhaseContainer.getVisibility() == View.VISIBLE) {
                mCommentPhaseContainer.setVisibility(View.GONE);
            }
        }

        public void showCommentPhase() {
            if (mCommentPhaseContainer.getVisibility() == View.GONE) {
                mCommentPhaseContainer.setVisibility(View.VISIBLE);
            }
        }

        public boolean isVisible() {
            return mCommentPhaseContainer.getVisibility() != View.GONE;
        }

        public void setCommentCount(long count) {
            mCommentCountView.setText(String.valueOf(count));
        }

        @SuppressWarnings("")
        @Subscribe(threadMode = ThreadMode.ASYNC)
        public void onAction(final EateryLikeAction action) {
            DetailLikeEateryEvent event = new DetailLikeEateryEvent();
            Intermediary.INSTANCE.postLike(mContext, mEateryInfo.getId(), event);
        }

        @SuppressWarnings("")
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEvent(final DetailLikeEateryEvent event) {
            if (event.getEateryInfo().getLikeCount() == 0) {
                mLikeCountView.setVisibility(View.GONE);
            } else {
                mLikeCountView.setVisibility(View.VISIBLE);
                mLikeCountView.setText(String.valueOf(event.getEateryInfo().getLikeCount()));
            }
        }

        @SuppressWarnings("")
        @OnClick(R.id.container_comment_phase)
        void onCommentLoad() {
            OlderCommentLoadAction action = new OlderCommentLoadAction();
            EventBus.getDefault().post(action);
        }

        @SuppressWarnings("")
        @OnClick(R.id.tv_tel)
        void onCall() {
            MakeCallAction action = new MakeCallAction();
            action.setPhoneNumber(mTelephoneView.getText().toString().trim());
            EventBus.getDefault().post(action);
        }

        @SuppressWarnings("")
        @OnClick(R.id.tv_website_address)
        void onGoToWebsite() {
            ViewWebAction action = new ViewWebAction();
            action.setWebsiteAddress(mWebAddressView.getText().toString());
            EventBus.getDefault().post(action);
        }

        @SuppressWarnings("")
        @OnClick(R.id.tv_address)
        void onViewEateryMap() {
            ViewMapAction action = new ViewMapAction();
            action.setEateryInfo(mEateryInfo);
            EventBus.getDefault().post(action);
        }
    }

    static class CommentHolder extends BaseViewHolder<Comment> {
        private Comment mComment;

        @BindView(R.id.iv_picture)
        SquircleImageView mPictureImageView;
        @BindView(R.id.tv_name)
        TextView mNameView;
        @BindView(R.id.tv_comment)
        TextView mCommentView;
        @BindView(R.id.tv_date)
        TextView mDateView;
        @BindView(R.id.tv_like_count)
        TextView mLikeCountView;

        public CommentHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindItem(@NonNull final Comment comment) {
            mComment = comment;

            mPictureImageView.setImage(mComment.getPicture());
            mNameView.setText(mComment.getCommenterName());
            mCommentView.setText(mComment.getComment());
            mDateView.setText(mComment.getDate());
        }

        @SuppressWarnings("")
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEvent(final LikeCommentEvent event) {
            if (event.getComment().getLikeCount() == 0) {
                mLikeCountView.setVisibility(View.GONE);
            } else {
                mLikeCountView.setVisibility(View.VISIBLE);
                mLikeCountView.setText(String.valueOf(event.getComment().getLikeCount()));
            }
        }

        @SuppressWarnings("")
        @OnClick(R.id.iv_like)
        void onLikeComment() {
            LikeCommentEvent event = new LikeCommentEvent();
            Intermediary.INSTANCE.postLikeComment(mContext, mComment.getEateryId(),
                    mComment.getCommenterId(), mComment.getCommentId(), event);
        }
    }
}
