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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.goforer.base.ui.fragment.ListDialogFragment;
import com.goforer.beatery.model.data.ListItem;
import com.goforer.beatery.model.data.response.EateryInfo;
import com.goforer.beatery.ui.adapter.ShareAdapter;
import com.goforer.beatery.R;

import java.util.List;

public class ShareDialogFragment extends ListDialogFragment<ListItem> {
    public static final String SHARE_DIALOG_TAG = "Share";

    private Context mContext;

    public static final String[] mTitles = new String[] { "Facebook", "Google+", "Gmail", "Twitter"};
    public static final Integer[] mImages = { R.drawable.ic_facebook, R.drawable.ic_googleplus,
            R.drawable.ic_gmail, R.drawable.ic_twitter};

    private EateryInfo mEateryInfo;

    private ShareAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_default_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mContext = view.getContext();
        setTitleStyle(ListDialogFragment.NO_TITLE_BAR);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected List<ListItem> addItems() {
        for (int i = 0; i < mTitles.length; i++) {
            ListItem item = new ListItem();
            item.setImageId(mImages[i]);
            item.setShareTo(mTitles[i]);

            mItems.add(item);
        }

        return mItems;
    }

    @Override
    protected ListAdapter createAdapter() {
        return mAdapter = new ShareAdapter(this, mContext, R.layout.list_share_item, mItems,
                mEateryInfo);
    }

    public ShareAdapter getAdapter() {
        return mAdapter;
    }

    public EateryInfo getEateryInfo() {
        return mEateryInfo;
    }

    public void setEateryInfo(EateryInfo eateryInfo) {
        mEateryInfo = eateryInfo;
    }
}
