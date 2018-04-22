package com.thnki.queuebreaker.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.thnki.queuebreaker.R;


/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial transparency outside
 * it, as well as the laser scanner animation and result points.
 */
public final class QrCodeFinderView extends RelativeLayout {
    private static final int SCANNER_ANIM_LEN = 181;
    private static final int ALPHA_MAX = 155;
    private int angleColor;
    private int SCANNER_ALPHA[] = new int[SCANNER_ANIM_LEN];
    private double SCANNER_POS[] = new double[SCANNER_ANIM_LEN];
    private static final long ANIMATION_DELAY = 1L;
    private static final int OPAQUE = 0xFF;

    private Paint mPaint;
    private int mScannerAlpha;
    private int maskColor;
    private Rect mFrameRect;
    private int angleThickness;
    private int angleLength;
    private int mScannerPos;

    public QrCodeFinderView(Context context) {
        this(context, null);
    }

    public QrCodeFinderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QrCodeFinderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        for (int i = 0; i < SCANNER_ANIM_LEN; i++) {
            double radians = Math.toRadians(i);
            SCANNER_POS[i] = Math.abs(Math.sin(radians));
            SCANNER_ALPHA[i] = (int) (ALPHA_MAX * (Math.abs(Math.cos(radians))));
            SCANNER_ALPHA[i] += 100;
            Log.d("SCANNER_ALPHA", "SCANNER_ALPHA : " + SCANNER_ALPHA[i]);
        }

        Resources resources = getResources();
        maskColor = resources.getColor(R.color.transparentWhite);
        angleColor = resources.getColor(R.color.colorPrimary);

        angleThickness = 8;
        angleLength = 80;
        init(context);
    }

    private void init(Context context) {
        if (isInEditMode()) {
            return;
        }
        setWillNotDraw(false);
        LayoutInflater inflater = LayoutInflater.from(context);
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.layout_qr_code_scanner, this);
        FrameLayout frameLayout = relativeLayout.findViewById(R.id.qr_code_fl_scanner);
        mFrameRect = new Rect();
        RelativeLayout.LayoutParams layoutParams = (LayoutParams) frameLayout.getLayoutParams();
        mFrameRect.left = (ScreenUtils.getScreenWidth(context) - layoutParams.width) / 2;
        mFrameRect.top = layoutParams.topMargin;
        mFrameRect.right = mFrameRect.left + layoutParams.width;
        mFrameRect.bottom = mFrameRect.top + layoutParams.height;

    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            return;
        }
        Rect frame = mFrameRect;
        if (frame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        mPaint.setColor(maskColor);
        canvas.drawRect(0, 0, width, frame.top, mPaint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, mPaint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, mPaint);
        canvas.drawRect(0, frame.bottom + 1, width, height, mPaint);

        drawAngle(canvas, frame);
        drawLaser(canvas, frame);

        // Request another update at the animation interval, but only repaint the laser line,
        // not the entire viewfinder mask.
        postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
    }

    private void drawAngle(Canvas canvas, Rect rect) {
        mPaint.setColor(angleColor);
        mPaint.setAlpha(OPAQUE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(angleThickness);
        int left = rect.left;
        int top = rect.top;
        int right = rect.right;
        int bottom = rect.bottom;
        // Top left angle
        canvas.drawRect(left - angleThickness, top - angleThickness, left + angleLength, top, mPaint);
        canvas.drawRect(left - angleThickness, top, left, top + angleLength, mPaint);
        // Top right angle
        canvas.drawRect(right - angleLength, top, right + angleThickness, top - angleThickness, mPaint);
        canvas.drawRect(right + angleThickness, top, right, top + angleLength, mPaint);
        // bottom left angle
        canvas.drawRect(left - angleThickness, bottom - angleLength, left, bottom, mPaint);
        canvas.drawRect(left - angleThickness, bottom + angleThickness, left + angleLength, bottom, mPaint);
        // bottom right angle
        canvas.drawRect(right - angleLength, bottom + angleThickness, right + angleThickness, bottom, mPaint);
        canvas.drawRect(right, bottom - angleLength, right + angleThickness, bottom, mPaint);
    }

    private void drawLaser(Canvas canvas, Rect rect) {
        mPaint.setColor(angleColor);
        mPaint.setAlpha(SCANNER_ALPHA[mScannerAlpha]);
        mScannerAlpha = (mScannerAlpha + 1) % SCANNER_ALPHA.length;
        mScannerPos = (mScannerPos + 1) % SCANNER_POS.length;
        int bottom = (int) (rect.height() * SCANNER_POS[mScannerPos] + rect.top);
        canvas.drawRect(rect.left, rect.top, rect.right + 1, bottom, mPaint);
    }
}
