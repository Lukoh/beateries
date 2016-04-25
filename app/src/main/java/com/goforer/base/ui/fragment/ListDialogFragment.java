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

package com.goforer.base.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.goforer.beatery.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.Optional;

/**
 * A {@link DialogFragment} subclass with a {@link ListView}.
 */
public abstract class ListDialogFragment<T> extends DialogFragment {
    public static final int NO_TITLE_BAR = 0;
    public static final int TITLE_BAR = 1;

    private int mTitleStyle = TITLE_BAR;

    protected List<T> mItems = new ArrayList<>();

    @Optional
    @InjectView(R.id.list_view)
    protected ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_default_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    private void initViews() {
        mListView.setAdapter(createAdapter());

        mItems = addItems();

        if (mTitleStyle == NO_TITLE_BAR) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
    }

    /**
     * Return the {@link ListView} set in {@link ListDialogFragment}.
     *
     * @return the previously set {@link ListView}
     */
    public ListView getListView() {
        return mListView;
    }

    /**
     * Create a new adapter to provide child views on demand.
     * <p>
     * To create a new adapter to provide child views on demand, you must override
     * </p>
     * <p>
     * When adapter is changed, all existing views are recycled back to the pool. If the pool has
     * only one adapter, it will be cleared.
     *
     * @return The new adapter to set, or null to set no adapter.
     */
    protected abstract ListAdapter createAdapter();

    /**
     * Add all item to a {@link List} and return the {@link List}.
     */
    protected abstract List<T> addItems();

    /**
     * Sets the title style on ListDialogFragment.
     *
     * @param style the style to set to a {@link ListDialogFragment}.
     */
    protected void setTitleStyle(int style) {
        mTitleStyle = style;
    }

    /**
     * Clear all items.
     */
    protected void clear() {
        if (mItems != null && mItems.size() > 0) mItems.clear();
    }
}
