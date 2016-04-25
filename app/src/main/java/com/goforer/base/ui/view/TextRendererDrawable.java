/*
 * Copyright (C) 2013 The Android Open Source Project
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
import android.graphics.drawable.ShapeDrawable;
import android.text.TextPaint;

public abstract  class TextRendererDrawable extends ShapeDrawable {
    private Context mContext;
    private TextPaint mTextPaint;
    private Object[] mExpandedObjects;
    private float mDensity = 1.0F;
    private int mWidth = -1;

    public TextRendererDrawable(Context context, TextPaint textPaint, Object ... objects) {
        if( context == null ) throw new IllegalArgumentException("context is null");
        if( textPaint == null ) throw new IllegalArgumentException("textPaint object is null");
        mContext = context;
        mTextPaint = textPaint;
        mExpandedObjects = objects;
        mDensity = mContext.getResources().getDisplayMetrics().density;
        prepare();
    }

    abstract public void prepare();

    public TextPaint getTextPaint() {
        return mTextPaint;
    }

    public Object getObejct(int index) {
        if( index >= mExpandedObjects.length ) return null;
        return mExpandedObjects[index];
    }

    public float getDensity() {
        return mDensity;
    }

    public int getWidth(){
        return mWidth;
    }
}
