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

package com.goforer.beatery.model.updater;

import com.goforer.beatery.model.data.response.EateryInfo;
import com.goforer.beatery.model.event.ListCommentPostEvent;
import com.goforer.beatery.model.event.ListLikeEateryEvent;
import com.goforer.beatery.model.event.action.EateryInfoUpdatedAction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * This class updates the eatery information.
 */
public class EateryInfoUpdater {
    private onUpdateListener mListener;

    /**
     * Registers the given subscriber to receive events.
     */
    public void register() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    /**
     * Unregisters the given subscriber from all event classes.
     */
    public void unregister() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * Register a listener to be invoked when the eatery information is changed.
     * @param listener a listener to be invoked when the eatery information is changed
     */
    public void setOnUpdateListener(onUpdateListener listener) {
        mListener = listener;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAction(EateryInfoUpdatedAction action){
        if (mListener != null) {
            mListener.onChanged(action.getEateryInfo(), true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ListLikeEateryEvent event) {
        if (event.getResponseClient() != null && event.getResponseClient().isSuccessful()) {
            if (mListener != null) {
                mListener.onChanged(event.getEateryInfo(), false);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ListCommentPostEvent event) {
        if (event.getResponseClient() != null && event.getResponseClient().isSuccessful()) {
            if (mListener != null) {
                mListener.onChanged(event.getEateryInfo(), false);
            }
        }
    }

    /**
     * A listener to be invoked when the eatery information is changed.
     */
    public interface onUpdateListener {
        /**
         * Invoked when the eatery information is changed.
         *
         * @param eateryInfo the changed eatery information
         * @param isNeedNotify true if the changed eatery information should be notified
         */
        void onChanged(EateryInfo eateryInfo, boolean isNeedNotify);
    }
}
