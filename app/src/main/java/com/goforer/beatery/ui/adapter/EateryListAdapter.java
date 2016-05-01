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

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.goforer.base.ui.activity.BaseActivity;
import com.goforer.base.ui.adapter.BaseListAdapter;
import com.goforer.base.ui.adapter.BaseViewHolder;
import com.goforer.base.ui.adapter.DefaultViewHolder;
import com.goforer.base.ui.view.SquircleImageView;
import com.goforer.beatery.R;
import com.goforer.beatery.model.data.response.EateryInfo;
import com.goforer.beatery.model.event.ListCommentPostEvent;
import com.goforer.beatery.model.event.ListLikeEateryEvent;
import com.goforer.beatery.model.event.action.EateryEventAction;
import com.goforer.beatery.model.event.action.EateryGalleryAction;
import com.goforer.beatery.model.event.action.EaterySelectAction;
import com.goforer.beatery.model.event.action.MapCallAction;
import com.goforer.beatery.ui.fragment.ShareDialogFragment;
import com.goforer.beatery.utillity.ActivityCaller;
import com.goforer.beatery.utillity.DisplayUtils;
import com.goforer.beatery.web.wire.connecter.Intermediary;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class EateryListAdapter extends BaseListAdapter<EateryInfo> {
    private static final int FIRST_EATERY_GRADE = 1;
    private static final int SECOND_EATERY_GRADE = 2;
    private static final int THIRD_EATERY_GRADE = 3;
    private static final int FOURTH_EATERY_GRADE = 4;
    private static final int FIFTH_EATERY_GRADE = 5;

    private static final int COMMENTS_MAX_LENGTH = 300;

    private static BaseActivity mActivity;

    public EateryListAdapter(BaseActivity activity, List<EateryInfo> items, int layoutResId) {
        super(items, layoutResId);

        mActivity = activity;
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
                view = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.list_loading_item, viewGroup, false);
                return new DefaultViewHolder(view);
            default:
                return super.onCreateViewHolder(viewGroup, type);
        }
    }

    @Override
    protected RecyclerView.ViewHolder createViewHolder(View view, int type) {
        return new EateryListViewHolder(view);
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

    static class EateryListViewHolder extends BaseViewHolder<EateryInfo> {
        private EateryInfo mEateryInfo;

        @BindView(R.id.iv_logo)
        SquircleImageView mLogoImageView;
        @BindView(R.id.tv_name)
        TextView mNameView;
        @BindView(R.id.tv_type)
        TextView mTypeView;
        @BindView(R.id.tv_info)
        TextView mInfoView;
        @BindView(R.id.tv_like_count)
        TextView mLikeCountView;
        @BindView(R.id.tv_comment_count)
        TextView mCommentCountView;
        @BindView(R.id.iv_preference)
        ImageView mPreferenceView;
        @BindView(R.id.iv_event)
        ImageView mEventView;
        @BindView(R.id.iv_viewer)
        ImageView mGalleryView;
        @BindView(R.id.et_comment)
        EditText mCommentText;
        @BindView(R.id.tv_post)
        TextView mPostView;

        public EateryListViewHolder(View itemView) {
            super(itemView);

            DisplayUtils.INSTANCE.hideSoftKeyboard(mContext, mCommentText);
        }

        @Override
        public void bindItem(@NonNull final EateryInfo eateryInfo) {
            mEateryInfo = eateryInfo;

            InputFilter lengthFilter = new InputFilter.LengthFilter(COMMENTS_MAX_LENGTH);
            mCommentText.setFilters(new InputFilter[]{lengthFilter});
            if (mCommentText.getText().toString().isEmpty()) {
                mPostView.setEnabled(false);
            }

            mLogoImageView.setImage(mEateryInfo.getLogo());
            mNameView.setText(mEateryInfo.getName());
            mTypeView.setText(mEateryInfo.getType());
            mInfoView.setText(mEateryInfo.getInformation());
            mLikeCountView.setText(String.valueOf(mEateryInfo.getLikeCount()));
            mCommentCountView.setText(String.valueOf(mEateryInfo.getCommentCount()));
            switch(mEateryInfo.getPreference()) {
                case FIRST_EATERY_GRADE:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mPreferenceView.setImageDrawable(
                                mContext.getResources().getDrawable(R.drawable.ic_one, null));
                    } else {
                        mPreferenceView.setImageDrawable(
                                mContext.getResources().getDrawable(R.drawable.ic_one));
                    }
                    break;
                case SECOND_EATERY_GRADE:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mPreferenceView.setImageDrawable(
                                mContext.getResources().getDrawable(R.drawable.ic_two, null));
                    } else {
                        mPreferenceView.setImageDrawable(
                                mContext.getResources().getDrawable(R.drawable.ic_two));
                    }
                    break;
                case THIRD_EATERY_GRADE:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mPreferenceView.setImageDrawable(
                                mContext.getResources().getDrawable(R.drawable.ic_three, null));
                    } else {
                        mPreferenceView.setImageDrawable(
                                mContext.getResources().getDrawable(R.drawable.ic_three));
                    }
                    break;
                case FOURTH_EATERY_GRADE:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mPreferenceView.setImageDrawable(
                                mContext.getResources().getDrawable(R.drawable.ic_four, null));
                    } else {
                        mPreferenceView.setImageDrawable(
                                mContext.getResources().getDrawable(R.drawable.ic_four));
                    }
                    break;
                case FIFTH_EATERY_GRADE:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mPreferenceView.setImageDrawable(
                                mContext.getResources().getDrawable(R.drawable.ic_five, null));
                    } else {
                        mPreferenceView.setImageDrawable(
                                mContext.getResources().getDrawable(R.drawable.ic_five));
                    }
                    break;
                default:
            }

            if (mEateryInfo.hasEvent()) {
                mEventView.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mGalleryView.setImageDrawable(
                            mContext.getResources().getDrawable(R.drawable.ic_event, null));
                } else {
                    mGalleryView.setImageDrawable(
                            mContext.getResources().getDrawable(R.drawable.ic_event));
                }
            } else {
                mEventView.setVisibility(View.GONE);
            }

            if (mEateryInfo.hasGallery()) {
                mGalleryView.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mGalleryView.setImageDrawable(
                            mContext.getResources().getDrawable(R.drawable.ic_viewer, null));
                } else {
                    mGalleryView.setImageDrawable(
                            mContext.getResources().getDrawable(R.drawable.ic_viewer));
                }
            } else {
                mGalleryView.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EaterySelectAction action = new EaterySelectAction();
                    action.setEateryInfo(mEateryInfo);
                    action.setScrolledToComment(false);
                    EventBus.getDefault().post(action);
                }
            });

            mCommentText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    isEmptyComment();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        @SuppressWarnings("")
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEvent(ListLikeEateryEvent event) {
            if (event.getEateryInfo().getLikeCount() == 0) {
                mLikeCountView.setVisibility(View.GONE);
            } else {
                mLikeCountView.setVisibility(View.VISIBLE);
                mLikeCountView.setText(String.valueOf(event.getEateryInfo().getLikeCount()));
            }
        }

        @SuppressWarnings("")
        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onEvent(ListCommentPostEvent event) {
            if (event.getEateryInfo().getCommentCount() == 0) {
                mCommentCountView.setVisibility(View.GONE);
            } else {
                mCommentCountView.setVisibility(View.VISIBLE);
                mCommentCountView.setText(String.valueOf(event.getEateryInfo().getCommentCount()));
            }
        }

        private boolean isEmptyComment() {
            if (mCommentText.getText().toString().trim().length() > 0) {
                mPostView.setEnabled(true);
                return true;
            } else {
                mPostView.setEnabled(false);
                return false;
            }
        }

        @SuppressWarnings("")
        @OnClick(R.id.container_info)
        void onViewEateryMap() {
            MapCallAction action = new MapCallAction();
            action.setEateryInfo(mEateryInfo);
            EventBus.getDefault().post(action);
        }

        @SuppressWarnings("")
        @OnClick(R.id.container_bar)
        void onSelectItem() {
            EaterySelectAction action = new EaterySelectAction();
            action.setEateryInfo(mEateryInfo);
            action.setScrolledToComment(true);
            EventBus.getDefault().post(action);
        }

        @SuppressWarnings("")
        @OnClick(R.id.iv_viewer)
        void onViewGallery() {
            if (mEateryInfo.hasGallery()) {
                EateryGalleryAction action = new EateryGalleryAction();
                action.setEateryId(mEateryInfo.getId());
                action.setEateryName(mEateryInfo.getName());
                EventBus.getDefault().post(action);
            }
        }

        @SuppressWarnings("")
        @OnClick(R.id.iv_event)
        void onViewEvent() {
            if (mEateryInfo.hasEvent()) {
                EateryEventAction action = new EateryEventAction();
                action.setEateryId(mEateryInfo.getId());
                action.setEateryName(mEateryInfo.getName());
                EventBus.getDefault().post(action);
            }
        }

        @SuppressWarnings("")
        @OnClick(R.id.btn_like)
        void onLike() {
            ListLikeEateryEvent event = new ListLikeEateryEvent();
            Intermediary.INSTANCE.postLike(mContext, mEateryInfo.getId(), event);
        }

        @SuppressWarnings("")
        @OnClick(R.id.tv_post)
        void onPostComment() {
            if (isEmptyComment()) {
                String comment = mCommentText.getText().toString().trim();

                ListCommentPostEvent event = new ListCommentPostEvent();
                Intermediary.INSTANCE.postComment(mContext, mEateryInfo.getId(), comment, event);

                if (mCommentText.hasFocus()) {
                    mCommentText.clearFocus();
                    DisplayUtils.INSTANCE.hideSoftKeyboard(mContext, mCommentText);
                }
            }
        }

        @SuppressWarnings("")
        @OnClick(R.id.btn_share)
        void onShareFromComment() {
            FragmentManager fragmentManager =  mActivity.getSupportFragmentManager();
            ShareDialogFragment shareDialog = new ShareDialogFragment();
            shareDialog.setEateryInfo(mEateryInfo);
            shareDialog.show(fragmentManager, "Share");
        }
    }
}
