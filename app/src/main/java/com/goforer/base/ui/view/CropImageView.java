package com.goforer.base.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Custom view that provides cropping capabilities to an image.
 */
public class CropImageView extends ImageView {
    private static final String TAG = "CropImageView";

    private static final boolean DEBUG = false;

    private static final int MAX_ZOOM_RATIO = 2;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;

    private Matrix mMatrix;
    private Matrix mSavedMatrix;
    private Matrix mSavedMatrix2;

    private RectF mCropRect;
    private RectF mCropInsideRect;
    private RectF[] mOutsideRect;
    private RectF[] mInsideRect;
    private Paint mOutSidePaint = new Paint();
    private Paint mInSidePaint = new Paint();
    private Paint mCropBorderPaint = new Paint();

    private Drawable mDrawable;

    private int mMode = NONE;

    private PointF mStart = new PointF();
    private PointF mMid = new PointF();

    private boolean mIsInitialized = false;
    private boolean mIsShownCropInside = false;

    private float mOldSpace = 1f;
    private float mMaxZoom;
    private float mCropSizeWidth, mCropSizeHeight;
    private float mCropInsideSizeWidth = 0, mCropInsideSizeHeight = 0;
    private int mCropRectWidth, mCropRectHeight;

    public CropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setScaleType(ScaleType.MATRIX);
        mMatrix = new Matrix();
        mSavedMatrix = new Matrix();
        mSavedMatrix2 = new Matrix();
        mOutSidePaint.setFilterBitmap(true);
        mOutSidePaint.setAntiAlias(true);
        mOutSidePaint.setDither(true);
        mCropBorderPaint.setFilterBitmap(true);
        mCropBorderPaint.setAntiAlias(true);
        mCropBorderPaint.setDither(true);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageView(Context context) {
        this(context, null);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mDrawable = this.getDrawable();
        super.onLayout(changed, left, top, right, bottom);
        if (!mIsInitialized) {
            mIsInitialized = true;
            setCropImage();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCropRect != null && mOutsideRect != null) {
            for (RectF r : mOutsideRect) {
                canvas.drawRect(r, mOutSidePaint);
            }

            if (mIsShownCropInside && mCropInsideRect != null) {
                for (RectF r : mInsideRect) {
                    canvas.drawRect(r, mInSidePaint);
                }

                canvas.drawRect(mCropInsideRect, mCropBorderPaint);
            }

            canvas.drawRect(mCropRect, mCropBorderPaint);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);

        mIsInitialized = false;
        setCropImage();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);

        mDrawable = getDrawable();
        mIsInitialized = false;
        setCropImage();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (DEBUG) {
            dumpEvent(event);
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mSavedMatrix.set(mMatrix);
                mStart.set(event.getX(), event.getY());
                mMode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mOldSpace = (float)spacing(event);
                if (mOldSpace > 10f) {
                    mSavedMatrix.set(mMatrix);
                    midPoint(mMid, event);
                    mMode = ZOOM;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mMode = NONE;
                restore();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == DRAG) {
                    mMatrix.set(mSavedMatrix);
                    mMatrix.postTranslate(event.getX() - mStart.x, event.getY() - mStart.y);

                } else if (mMode == ZOOM) {
                    double newSpace = spacing(event);
                    if (newSpace > 10f) {
                        mMatrix.set(mSavedMatrix);
                        float scale = (float)newSpace / mOldSpace;
                        mMatrix.postScale(scale, scale, mMid.x, mMid.y);
                    }
                }

                break;
        }

        setMatrixChange(mMatrix);
        setImageMatrix(mSavedMatrix2);

        return true;
    }

    private void setCropImage() {
        mDrawable = getDrawable();
        setCropRect(mCropSizeWidth, mCropSizeHeight);
        setImagePos();
        setImageMatrix(mMatrix);
    }

    public void setCropRect(float width, float height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        int screenWidth = getWidth();
        int screenHeight = getHeight();

        if (screenWidth == 0 || screenHeight == 0) {
            return;
        }

        int cropInsideRectWidth;
        int cropInsideRectHeight;
        int padding = 2;

        int max = screenHeight - (padding * 2);
        float ratio = max / height;

        mCropRectHeight = (int) (height * ratio);
        mCropRectWidth = (int) (width * ratio);
        cropInsideRectWidth = (int) (mCropInsideSizeWidth * ratio);
        cropInsideRectHeight = (int) (mCropInsideSizeHeight * ratio);

        if (mCropRectWidth > screenWidth - (padding * 2)) {
            int maxW = screenWidth - (padding * 2);
            float ratioW = maxW / width;
            mCropRectHeight = (int) (height * ratioW);
            mCropRectWidth = (int) (width * ratioW);
            cropInsideRectWidth = (int) (mCropInsideSizeWidth * ratioW);
            cropInsideRectHeight = (int) (mCropInsideSizeHeight * ratioW);
        }

        int paddingW = (screenWidth - mCropRectWidth) / 2;
        int paddingH = (screenHeight - mCropRectHeight) / 2;

        setPadding(paddingW, paddingH, paddingW, paddingH);

        if (cropInsideRectWidth > mCropRectWidth || cropInsideRectHeight > mCropRectHeight) {
            mCropInsideSizeWidth = 0;
            mCropSizeHeight = 0;
            mIsShownCropInside = false;
        }

        float x1, x2, y1, y2;

        if (mIsShownCropInside) {
            x1 = (screenWidth - cropInsideRectWidth) / 2;
            x2 = x1 + cropInsideRectWidth;
            y1 = (screenHeight - cropInsideRectHeight) / 2;
            y2 = y1 + cropInsideRectHeight;
            mCropInsideRect = new RectF(x1, y1, x2, y2);
        }

        x1 = (screenWidth - mCropRectWidth) / 2;
        x2 = x1 + mCropRectWidth;
        y1 = (screenHeight - mCropRectHeight) / 2;
        y2 = y1 + mCropRectHeight;
        mCropRect = new RectF(x1, y1, x2, y2);

        mOutsideRect = new RectF[] {
                new RectF(0, 0, screenWidth, y1),
                new RectF(0, y1, x1, y2),
                new RectF(x2, y1, screenWidth, y2),
                new RectF(0, y2, screenWidth, screenHeight)
        };

        mOutSidePaint.setColor(0xCD000000); //Back Alpha 35

        if (mIsShownCropInside && mCropInsideRect != null) {
            mInsideRect = new RectF[]{
                    new RectF(x1, y1, mCropRectWidth, mCropInsideRect.top),
                    new RectF(x1, mCropInsideRect.top, mCropInsideRect.left, mCropInsideRect.bottom),
                    new RectF(mCropInsideRect.right, mCropInsideRect.top, mCropRectWidth,
                            mCropInsideRect.bottom),
                    new RectF(x1, mCropInsideRect.bottom, mCropRectWidth, y2)
            };

            mInSidePaint.setColor(0x5A000000); //Back Alpha 30
        }

        mCropBorderPaint.setARGB(255, 255, 255, 255);
        mCropBorderPaint.setStyle(Paint.Style.STROKE);
        mCropBorderPaint.setStrokeWidth(2);

    }

    public void setCropSize(float width, float height) {
        mCropSizeWidth = width;
        mCropSizeHeight = height;
    }

    public int getCropHeight() {
        return mCropRectHeight;
    }

    public int getCropWidth() {
        return mCropRectWidth;
    }

    public void setCropInsideSize(float width, float height) {
        if (width > 0 && height > 0) {
            mCropInsideSizeHeight = width;
            mCropInsideSizeHeight = height;
            mIsShownCropInside = true;
        } else {
            mIsShownCropInside = false;
        }
    }

    /**
     * Sets the image in the image view using the matrix
     */
    public void setImagePos() {
        float[] value = new float[9];

        mMatrix.getValues(value);

        if (mDrawable == null) {
            return;
        }

        value[0] = value[4] = Math.max((float) mCropRectWidth / (float) mDrawable.getIntrinsicWidth(),
                (float) mCropRectHeight / (float) mDrawable.getIntrinsicHeight());

        int scaleWidth = (int) (mDrawable.getIntrinsicWidth() * value[0]);
        int scaleHeight = (int) (mDrawable.getIntrinsicHeight() * value[4]);

        value[2] = 0;
        value[5] = 0;

        value[2] = (float) mCropRectWidth / 2 - (float) scaleWidth / 2;
        value[5] = (float) mCropRectHeight / 2 - (float) scaleHeight / 2;

        imageX = value[2];
        imageY = value[5];
        scale = value[4];

        mMaxZoom = scale * MAX_ZOOM_RATIO;

        mMatrix.setValues(value);
        setMatrixChange(mMatrix);
        setImageMatrix(mMatrix);
    }

    private double spacing(MotionEvent event) {
        double x = event.getX(0) - event.getX(1);
        double y = event.getY(0) - event.getY(1);
        return Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private void setMatrixChange(Matrix matrix) {
        float[] value = new float[9];
        matrix.getValues(value);
        float[] savedValue = new float[9];
        mSavedMatrix2.getValues(savedValue);

        Drawable d = getDrawable();
        if (d == null) return;
        int imageWidth = d.getIntrinsicWidth();
        int imageHeight = d.getIntrinsicHeight();
        int scaleWidth = (int) (imageWidth * value[0]);
        int scaleHeight = (int) (imageHeight * value[4]);

        if (value[2] < mCropRectWidth - scaleWidth) value[2] = mCropRectWidth - scaleWidth;
        if (value[5] < mCropRectHeight - scaleHeight) value[5] = mCropRectHeight - scaleHeight;
        if (value[2] > 0) value[2] = 0;
        if (value[5] > 0) value[5] = 0;

        if (value[0] > mMaxZoom || value[4] > mMaxZoom) {
            value[0] = savedValue[0];
            value[4] = savedValue[4];
            value[2] = savedValue[2];
            value[5] = savedValue[5];
        }

        if (scaleWidth < mCropRectWidth) {
            value[0] = value[4] = (float) mCropRectWidth / (float) imageWidth;
            scaleHeight = (int) (imageHeight * value[4]);
            if (scaleHeight < mCropRectHeight) value[0] = value[4] = (float) mCropRectHeight /
                    (float) imageHeight;
            value[2] = savedValue[2];
            value[5] = savedValue[5];

        } else if (scaleHeight < mCropRectHeight) {
            value[0] = value[4] = (float) mCropRectHeight / (float) imageHeight;
            scaleWidth = (int) (imageWidth * value[0]);
            if (scaleWidth < mCropRectWidth) value[0] = value[4] = (float) mCropRectWidth /
                    (float) imageWidth;
            value[2] = savedValue[2];
            value[5] = savedValue[5];
        }

        // center the image
        scaleWidth = (int) (imageWidth * value[0]);
        scaleHeight = (int) (imageHeight * value[4]);
        if (scaleWidth < mCropRectWidth) {
            value[2] = (float) mCropRectWidth / 2 - (float) scaleWidth / 2;
        }

        if (scaleHeight < mCropRectHeight) {
            value[5] = (float) mCropRectHeight / 2 - (float) scaleHeight / 2;
        }

        imageX = value[2];
        imageY = value[5];
        scale = value[4];
        matrix.setValues(value);
        mSavedMatrix2.set(matrix);
    }

    private float imageX, imageY, scale;

    public float getImageX() {
        return imageX;
    }

    public float getImageY() {
        return imageY;
    }

    public float getScale() {
        return scale;
    }

    public Bitmap getCroppedImage() {
        Bitmap cropImage = null;
        try {
            int x, y, width, height;

            buildDrawingCache();
            Bitmap bitmap = getDrawingCache();
            x = (int) mCropRect.left;
            y = (int) mCropRect.top;
            width = (int) mCropRect.width();
            height = (int) mCropRect.height();
            cropImage = Bitmap.createBitmap(bitmap, x, y, width, height);
            bitmap.recycle();
        } catch (OutOfMemoryError err) {
            Log.d(TAG, "getCropImage OutOfMemoryError!!");
        }

        return cropImage;
    }

    private void restore() {
        setImageMatrix(mSavedMatrix);
    }

    /**
     * Show an event in the LogCat view, for debugging
     */
    private void dumpEvent(MotionEvent event) {
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP",
                "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode
                == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d(TAG, sb.toString());
    }
}
