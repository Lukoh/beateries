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

package com.goforer.beatery.model.event.action;

import com.goforer.beatery.model.data.response.EateryInfo;

/**
 * Define action to handle selecting the eatery
 *
 * <p>
 * Please refer to EventBus.
 *
 * @see <a href="http://greenrobot.org/eventbus//">
 *     EventBus</a>
 * @see <a href="http://greenrobot.org/eventbus/documentation/how-to-get-started/">
 *     How to get started</a>
 * </p>
 *
 */
public class EaterySelectAction {
    private EateryInfo mEateryInfo;

    private boolean mIsScrolledToComment;

    public EateryInfo getEateryInfo() { return mEateryInfo; }

    public boolean isScrolledToComment() { return mIsScrolledToComment; }

    public void setEateryInfo(EateryInfo eateryInfo) { mEateryInfo = eateryInfo; }

    public void setScrolledToComment(boolean isScrolledTo) { mIsScrolledToComment = isScrolledTo; }
}
