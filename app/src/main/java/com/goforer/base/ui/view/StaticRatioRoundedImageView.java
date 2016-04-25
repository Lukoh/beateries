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
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.goforer.beatery.R;

public class StaticRatioRoundedImageView extends SquircleImageView {

    private float mRatio = 1f;

    public StaticRatioRoundedImageView(Context context) {
        super(context);
    }

    public StaticRatioRoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StaticRatioRoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StaticRatioRoundedImageView, defStyle, 0);
        mRatio = a.getFloat(R.styleable.StaticRatioRoundedImageView_static_ratio, 0.75f);
        a.recycle();
    }

    public void setRatio(float ratio) {
        this.mRatio = ratio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode != View.MeasureSpec.EXACTLY && heightMode != View.MeasureSpec.EXACTLY) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else if (widthMode == View.MeasureSpec.EXACTLY) {
            heightSize = (int) (widthSize * mRatio);
        } else {
            widthSize = (int) (heightSize * mRatio);
        }
        setMeasuredDimension(widthSize, heightSize);
    }
}
