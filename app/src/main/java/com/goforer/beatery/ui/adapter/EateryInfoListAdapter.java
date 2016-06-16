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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.goforer.beatery.model.data.response.EateryInfo;
import com.goforer.beatery.ui.fragment.EateryInfoFragment;

import java.util.List;

public class EateryInfoListAdapter extends FragmentStatePagerAdapter {
    private List<EateryInfo> mEateryInfoItems;
    private boolean mIsScrolledToComment;

    public EateryInfoListAdapter(FragmentManager fm, List<EateryInfo> eateryInfoItems,
                                 boolean isScrolledToComment) {
        super(fm);

        mEateryInfoItems = eateryInfoItems;
        mIsScrolledToComment = isScrolledToComment;
    }

    @Override
    public Fragment getItem(int position) {
        return EateryInfoFragment.newInstance(mEateryInfoItems, position, mIsScrolledToComment);
    }

    @Override
    public int getCount() {
        return mEateryInfoItems.size();
    }
}
