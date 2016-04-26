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

package com.goforer.beatery.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.goforer.base.ui.activity.BaseActivity;
import com.goforer.base.ui.view.CropImageView;
import com.goforer.beatery.R;
import com.goforer.beatery.model.event.action.CropImageAction;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.InjectView;
import butterknife.OnClick;

public class PictureEditActivity extends BaseActivity {
    private static final String TAG = "PictureEditActivity";

    private static final String EXTRA_IMAGE_PATH = "image_path";

    private static final int RESIZE_PX_LONG_REF = 1920;
    private static final int RESIZE_PX_SHORT_REF = 1080;
    private static final float RESIZE_RATIO_REF = 2.0f;

    private static final int CROP_WIDTH_SIZE = 720;
    private static final int CROP_HEIGHT_SIZE = 480;

    private static final int CROP_INSIDE_WIDTH_SIZE = 480;
    private static final int CROP_INSIDE_HEIGHT_SIZE = 480;

    private Bitmap mBitmap;

    private Matrix mRotationMatrix = new Matrix();

    private String mFilePath;
    private String mSaveFile;

    private int mWidth = 0;
    private int mHeight = 0;

    @InjectView(R.id.iv_crop_picture)
    CropImageView mCropPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = null;
        if (savedInstanceState != null) {
            bundle = savedInstanceState;
        } else if (getIntent() != null) {
            bundle = getIntent().getExtras();
        }

        if (bundle != null) {
            mFilePath = bundle.getString(EXTRA_IMAGE_PATH, "");
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setActionBar() {
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_picture_edit);
    }

    @Override
    protected void setViews() {
        if (mFilePath == null || mFilePath.isEmpty()) {
            finish();
            return;
        }

        mSaveFile = getFileExtension(mFilePath);
        mCropPicture.setCropSize(CROP_WIDTH_SIZE, CROP_HEIGHT_SIZE);
        mCropPicture.setCropInsideSize(CROP_INSIDE_WIDTH_SIZE, CROP_INSIDE_HEIGHT_SIZE);

        setPicture(getRotationValue());
    }

    public Bitmap rotate(Bitmap bitmap, int degrees) {
        if (bitmap != null && degrees != 0) {
            mRotationMatrix.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);
            try {
                Bitmap br;
                br = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                        mRotationMatrix, true);
                if (bitmap.getGenerationId() != br.getGenerationId()) {
                    bitmap.recycle();
                    bitmap = br;
                }
            } catch (OutOfMemoryError e) {
                Log.d(TAG, "Rotate OutOfMemoryError!!");
            }

        }

        return bitmap;
    }

    private int getRotationValue() {
        ExifInterface exif;
        int rotation = 0;

        try {
            exif = new ExifInterface(mFilePath);
            mWidth = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
            mHeight = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    rotation = 0;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;

                default:
                    rotation = 0;
                    break;
            }

        } catch (IOException e) {
            Log.d(TAG, "getExif Err");
        }

        return rotation;
    }

    private void setPicture(int rotation) {
        int resizeWidth, resizeHeight;
        boolean isWidthLonger = (mWidth >= mHeight);

        if (mWidth == 0 || mHeight == 0) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mFilePath, bmOptions);
            mWidth = bmOptions.outWidth;
            mHeight = bmOptions.outHeight;
        }

        float imageRatio = (float) Math.max(mWidth, mHeight) / (float) Math.min(mWidth, mHeight);
        if (imageRatio > RESIZE_RATIO_REF) {
            if (isWidthLonger) {
                resizeWidth = RESIZE_PX_LONG_REF;
                resizeHeight = (int) (RESIZE_PX_LONG_REF / imageRatio);
            } else {
                resizeWidth = (int) (RESIZE_PX_LONG_REF / imageRatio);
                resizeHeight = RESIZE_PX_LONG_REF;
            }
        } else {
            if (isWidthLonger) {
                resizeWidth = (int) (RESIZE_PX_SHORT_REF * imageRatio);
                resizeHeight = RESIZE_PX_SHORT_REF;
            } else {
                resizeWidth = RESIZE_PX_SHORT_REF;
                resizeHeight = (int) (RESIZE_PX_SHORT_REF * imageRatio);
            }
        }

        setImageBitmap(mWidth, resizeWidth, mHeight, resizeHeight, rotation, imageRatio);
    }

    private void setImageBitmap(int width, int resizeWidth, int height, int resizeHeight,
                                int rotation, float imageRatio) {
        Bitmap editBitmap;

        if (width <= resizeWidth && height <= resizeHeight) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            try {
                mBitmap = BitmapFactory.decodeFile(mFilePath, options);
                mBitmap = rotate(mBitmap, rotation);
                if (mBitmap.getConfig() != null)
                    editBitmap = mBitmap.copy(mBitmap.getConfig(), true);
                else
                    editBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);

                mCropPicture.setImageBitmap(editBitmap);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            return;
        }

        setResizeImageBitmap(resizeWidth, resizeHeight, rotation, imageRatio);
    }

    private void setResizeImageBitmap(int resizeWidth, int resizeHeight,
                                       int rotation, float imageRatio) {
        Bitmap editBitmap;
        int baseSample = 1;

        while (Math.pow(2, baseSample) <= imageRatio) {
            baseSample *= 2;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = baseSample;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        try {
            Bitmap srcBitmap = BitmapFactory.decodeFile(mFilePath, options);
            Bitmap resizeBitmap = Bitmap.createBitmap(resizeWidth, resizeHeight, Bitmap.Config.ARGB_8888);

            Canvas resizeCanvas = new Canvas(resizeBitmap);

            Paint resizePaint = new Paint();
            resizePaint.setFilterBitmap(true);
            resizePaint.setDither(true);
            resizePaint.setAntiAlias(true);

            Rect srcRect = new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
            Rect resizeRect = new Rect(0, 0, resizeWidth, resizeHeight);

            resizeCanvas.drawBitmap(srcBitmap, srcRect, resizeRect, resizePaint);

            srcBitmap.recycle();

            mBitmap = resizeBitmap;
            mBitmap = rotate(mBitmap, rotation);

            if (mBitmap.getConfig() != null) {
                editBitmap = mBitmap.copy(mBitmap.getConfig(), true);
            } else {
                editBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
            }

            mCropPicture.setImageBitmap(editBitmap);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            finish();
        }
    }

    private String saveEditPicture() {
        int cropW, cropH;
        int imgX, imgY;
        int imgW = mBitmap.getWidth();
        int imgH = mBitmap.getHeight();

        cropW = mCropPicture.getCropWidth();
        cropH = mCropPicture.getCropHeight();

        imgX = (int) mCropPicture.getImageX();
        imgY = (int) mCropPicture.getImageY();

        float scale = mCropPicture.getScale();
        imgW = (int) (imgW * scale);
        imgH = (int) (imgH * scale);

        float ratio = (float) Math.max(mBitmap.getWidth(), mBitmap.getHeight())
                / (float) Math.max(imgH, imgW);

        cropW = (int) (cropW * ratio);
        cropH = (int) (cropH * ratio);
        imgW = (int) (imgW * ratio);
        imgH = (int) (imgH * ratio);
        imgX = Math.abs((int) (imgX * ratio));
        imgY = Math.abs((int) (imgY * ratio));

        int infoCropX, infoCropY, infoWidth, infoHeight;

        if (cropW < imgW) {
            infoCropX = imgX;
            infoWidth = cropW;
        } else {
            infoCropX = 0;
            infoWidth = imgW;
        }

        if (cropH < imgH) {
            infoCropY = imgY;
            infoHeight = cropH;
        } else {
            infoCropY = 0;
            infoHeight = imgH;
        }

        if (mBitmap.getWidth() < infoWidth) {
            infoWidth = mBitmap.getWidth();
        }

        if (mBitmap.getHeight() < infoHeight) {
            infoHeight = mBitmap.getHeight();
        }

        Bitmap bitmap = Bitmap.createBitmap(mBitmap, infoCropX, infoCropY, infoWidth, infoHeight);
        String editImagePath = saveImage(bitmap);
        bitmap.recycle();

        return editImagePath;
    }

    private String saveImage(Bitmap bitmap) {
        String cacheDir;

        if (getExternalCacheDir() == null) {
            cacheDir = "";
        } else {
            cacheDir = getExternalCacheDir().getAbsolutePath();
        }

        if (TextUtils.isEmpty(cacheDir)) {
            cacheDir = getCacheDir().getAbsolutePath();
        }
        if (TextUtils.isEmpty(mSaveFile)) {
            mSaveFile = "/EditImg.png";
        }
        String path = cacheDir + mSaveFile;

        try {
            FileOutputStream outputStream = new FileOutputStream(new File(path));

            if (mSaveFile.endsWith(".jpg"))
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            else if (mSaveFile.endsWith(".png"))
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            else
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

            outputStream.flush();
            outputStream.close();

        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            return "";
        }

        return path;
    }

    private String getFileExtension(String filePath) {
        if (filePath == null)
            return null;
        if (filePath.endsWith(".jpeg") || filePath.endsWith(".JPEG")) {
            return "/EditImg.jpeg";
        } else if (filePath.endsWith(".jpg") || filePath.endsWith(".JPG")) {
            return "/EditImg.jpg";
        } else if (filePath.endsWith(".png") || filePath.endsWith(".PNG")) {
            return "/EditImg.png";
        } else
            return "/EditImg.png";

    }

    @SuppressWarnings("")
    @OnClick(R.id.tv_cancel)
    void onCancel() {
        finish();
    }

    @SuppressWarnings("")
    @OnClick(R.id.tv_done)
    void onComplete() {
        showProgress(0);
        CropImageAction action = new CropImageAction();
        action.setPicturePath(saveEditPicture());
        EventBus.getDefault().post(action);
        dismissProgress();
        finish();
    }
}
