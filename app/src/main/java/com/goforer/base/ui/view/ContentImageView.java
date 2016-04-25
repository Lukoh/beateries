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

package com.goforer.base.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.goforer.base.model.data.Image;

public class ContentImageView extends ImageView {
    private Drawable mDefaultImage;

    public ContentImageView(Context context) {
        super(context);
    }

    public ContentImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDefaultImage(Drawable defaultImage) {
        mDefaultImage = defaultImage;
    }

    public void setImage(Image image, Drawable defaultImage) {
        setDefaultImage(defaultImage);
        setImage(image);
    }

    public void setImage(Image image) {
        loadImage(image.getImageUrl());
    }

    public void loadImage(final String url) {
        DrawableRequestBuilder builder = Glide.with(getContext())
                .load(url)
                .placeholder(mDefaultImage)
                .error(mDefaultImage);
        builder.into(this);
    }
}
