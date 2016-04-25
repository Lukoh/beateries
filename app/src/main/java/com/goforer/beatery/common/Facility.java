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

package com.goforer.beatery.common;

public enum Facility {
    INSTANCE;

    public static final int FAB_BASE_EATERY_INFO_INDEX = 0;
    public static final int FAB_BEST_EATERY_INDEX = 1;
    public static final int FAB_CLOSEST_EATERY_INDEX = 2;
    public static final int FAB_OPTIMAL_EATERY_INDEX = 3;
    public static final int FAB_HANGOUT_INDEX = 4;

    public int mFabIndex;

    public int getFabIndex() {
        return mFabIndex;
    }

    public void setFabIndex(int index) {
        mFabIndex = index;
    }
}
