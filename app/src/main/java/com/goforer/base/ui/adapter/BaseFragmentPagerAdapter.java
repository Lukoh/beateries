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

package com.goforer.base.ui.adapter;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;

import com.goforer.beatery.BEatery;

import java.util.ArrayList;

/**
 * Extension of {@link FragmentPagerAdapter} that represents each page as a {@link Fragment}
 * that is persistently kept in the fragment manager as long as the user can return to the page.
 */
public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {
    public static final String FRAGMENT_KEY = "fragment_key";

    protected final ArrayList<Page> mPages = new ArrayList<>();

    public static final class Page {
        private final int mIconResId;

        private final String mTabTag;
        private final String mTabTitle;

        private Fragment mFragment;
        private final Class<?> mCls;
        private Bundle mArgs;

        @StringRes
        public int mPromotionMessage;

        public Page(int iconResId, String tag, String title, @StringRes int promotionMessage,
                    Class<?> cls, Bundle args) {
            this(iconResId, tag, title, cls, args);
            mPromotionMessage = promotionMessage;
        }

        public Page(String tag, String title, @StringRes int promotionMessage, Class<?> cls,
                    Bundle args) {
            this(0, tag, title, cls, args);
            mPromotionMessage = promotionMessage;
        }

        public Page(String tag, String title, Class<?> cls, Bundle args) {
            this(0, tag, title, cls, args);
        }

        public Page(int iconResId, String tabTag, String tabTitle, Class<?> cls, Bundle args) {
            mIconResId = iconResId;
            mTabTag = tabTag;
            mTabTitle = tabTitle;
            mCls = cls;
            mArgs = args;
        }

        public int getIconResourceId() {
            return mIconResId;
        }

        public String getTabTitle() {
            if (mTabTitle.length() == 0) {
                return "";
            }

            return mTabTitle;
        }

        /**
         * Return previously set Tab tag(Fragment tag).
         *
         * @return the previously set Tab tag(Fragment tag)
         */
        public String getTabTag() {
            if (mTabTag.length() == 0) {
                return "";
            }

            return mTabTag;
        }

        /**
         * Return previously set Fragment.
         *
         * @return the previously set Fragment
         */
        public Fragment getFragment() {
            return mFragment;
        }

        /**
         * Get the the component class that is to be used for BaseFragmentPagerAdapter.
         *
         * @return the previously set the component class that is to be used for
         *         BaseFragmentPagerAdapter.
         */
        public Class<?> getTheClass() {
            return mCls;
        }

        /**
         * Return the arguments supplied when the page was instantiated, if any.
         *
         * @return the previously set a bundle of arguments
         */
        public Bundle getArguments() {
            return mArgs;
        }

        /**
         * Sets the class name of a fragment to be associated with this BaseFragmentPagerAdapter.
         *
         * @param fragment the component class of the fragment associated with this
         *                 BaseFragmentPagerAdapter.
         */
        public void setFragment(Fragment fragment) {
            mFragment = fragment;
        }

        /**
         * Supply the construction arguments for this BaseFragmentPagerAdapter.
         *
         * @param args the arguments supplied
         */
        public void setArguments(Bundle args) {
            mArgs = args;
        }

    }

    public BaseFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        Page page = mPages.get(position);
        if (page.getArguments() == null) {
            page.setArguments(new Bundle());
        }
        if (!TextUtils.isEmpty(page.getTabTag())) {
            page.getArguments().putString(FRAGMENT_KEY, page.getTabTag());
        }
        page.setFragment(Fragment.instantiate(BEatery.mContext, page.getTheClass().getName(),
                page.getArguments()));
        return page.getFragment();
    }

    public ArrayList<Page> getPages() {
        return mPages;
    }

    /**
     * Get a item from the Fragment with given position.
     *
     * @param position to get the page from given position
     * @return a item from the Fragment with given position
     */
    public Fragment getFragmentItem(int position) {
        Page page = mPages.get(position);
        return page.mFragment;
    }

    @Override
    public int getCount() {
        return mPages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Page page = mPages.get(position);

        return page.getTabTitle();
    }

    /**
     * Get a page from given position.
     *
     * @param position to get the page from given position
     * @return a page from given position
     */
    public Page getPage(int position) {
        if (position < mPages.size()) {
            return mPages.get(position);
        }

        return null;
    }

    /**
     * Get a icon resource ID from given position.
     *
     * @param position to get the icon resource ID from given position
     * @return a icon resource ID from given position.
     */
    public int getIconResourceId(int position) {
        Page page = mPages.get(position);

        return page.getIconResourceId();
    }

    /**
     * Add the given page to this BaseFragmentPagerAdapter.
     *
     * @param page to add page
     */
    public void addPage(Page page) {
        mPages.add(page);
    }

    /**
     *
     * @param position
     */
    public void deletePage(int position) {
        mPages.remove(position);
    }

    /**
     * Clear all pages
     */
    public void clearPage() {
        mPages.clear();
    }
}
