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

import com.goforer.base.model.data.Image;

/**
 * Define action to handle selecting an image on the eatery's gallery contents
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
public class GallerySelectImageAction {
    private Image mContentImage;

    private int mItemIndex;

    private String mImageTitle;
    private String mImageDescription;

    public Image getContentImage() {
        if (mContentImage == null) {
            return null;
        }

        return mContentImage;
    }

    public int getItemIndex() {
        return mItemIndex;
    }

    public String getImageTitle() {
        if (mImageTitle.length() == 0) {
            return "";
        }

        return mImageTitle;
    }

    public String getImageDescription() {
        if (mImageDescription.length() == 0) {
            return "";
        }

        return mImageDescription;
    }

    public void setContentViewImage(Image contentImage) {
        mContentImage = contentImage;
    }

    public void setItemIndex(int itemIndex) {
        mItemIndex = itemIndex;
    }

    public void setImageTitle(String imageTitle) {
        mImageTitle = imageTitle;
    }

    public void setImageDescription(String imageDescription) {
        mImageDescription = imageDescription;
    }
}
