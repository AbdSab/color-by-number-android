package com.test.colorbynumber;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by omen on 20/06/2018.
 */

public class ColoringView extends View {

    //Zooming
    /*private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 5f;
    private float scaleFactor = 1.f;
    private ScaleGestureDetector detector;*/

    public static boolean dragMode = false;

    private float mLastTouchX;
    private float mLastTouchY;
    public float mPosX;
    public float mPosY;
    private Bitmap bitmap;
    private Rect rect;

    private ScaleGestureDetector mScaleDetector;
    public float mScaleFactor = 1f;
    private float scalePointX;
    private float scalePointY;
    public int vX, vY;


    //GridView colorsGrid;

    Paint paint = new Paint();
    InputStream is;
    Bitmap picture;
    int pictureW, pictureH;
    int size, sizeY;
    int canvasWidth, canvasHeight;

    public int selectedColor = Color.WHITE;

    //Coloring list
    public List<Integer> colors = new ArrayList<>();
    public int[][] image;

    public ColoringView(Context context, int img) {
        super(context);
        init(null, img);
    }

    public ColoringView(Context context, @Nullable AttributeSet attrs, int img) {
        super(context, attrs);
        init(attrs, img);
    }

    public ColoringView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int img) {
        super(context, attrs, defStyleAttr);
        init(attrs, img);
    }

    public ColoringView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes, int img) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, img);
    }

    private void init(@Nullable AttributeSet set, int img){
        //detector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());

        //is = getResources().openRawResource(img)
        //Log.d("asdasdasd", ""+img);
        picture = BitmapFactory.decodeResource(getResources(), img);

        //picture = BitmapFactory.decodeStream(is);
        pictureW = picture.getWidth();
        pictureH = picture.getHeight();



        //Getting Colors Numbers !!
        for (int x = 1; x < pictureW; x++) {
            for (int y = 0; y < pictureH; y++) {
                int c = picture.getPixel(x,y);
                if (Color.alpha(c) != 0) {
                    if (!colors.contains(c)) {
                        colors.add(c);
                    }
                }
            }
        }

        //Creating second layer
        image = new int[pictureW][pictureH];

        //Creating Buttons
        //colorsGrid = (GridView) findViewById(R.id.colorsGrid);
        //colorsGrid.setAdapter(new ButtonAdapter(this.getContext(), colors));
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
       // detector.onTouchEvent(ev);
        mScaleDetector.onTouchEvent(ev);


        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float xx = (ev.getX() - scalePointX)/mScaleFactor;
                final float yy = (ev.getY() - scalePointY)/mScaleFactor;
                vX = (int)(xx - mPosX + scalePointX)*pictureW/canvasWidth;
                vY = (int)(yy - mPosY + scalePointY)*pictureW/canvasWidth;
                mLastTouchX = xx;
                mLastTouchY = yy;
                if (!dragMode)
                    paintInPos(vX, vY);
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                final float xx = (ev.getX() - scalePointX)/mScaleFactor;
                final float yy = (ev.getY() - scalePointY)/mScaleFactor;
                vX = (int)(xx - mPosX + scalePointX)*pictureW/canvasWidth;
                vY = (int)(yy - mPosY + scalePointY)*pictureW/canvasWidth;
                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress() && dragMode) {
                    final float dx = xx - mLastTouchX; // change in X
                    final float dy = yy - mLastTouchY; // change in Y
                    mPosX += dx;
                    mPosY += dy;
                    invalidate();
                }else
                    paintInPos(vX, vY);

                mLastTouchX = xx;
                mLastTouchY = yy;

                break;
            }
            case MotionEvent.ACTION_UP:{
                final float xx = (ev.getX() - scalePointX)/mScaleFactor;
                final float yy = (ev.getY() - scalePointY)/mScaleFactor;
                //cX = x - mPosX + scalePointX; // canvas X
                //cY = y - mPosY + scalePointY; // canvas Y
                mLastTouchX = 0;
                mLastTouchY = 0;
                invalidate();
            }
        }
        invalidate();
        return true;
    }

    private void paintInPos(int x, int y){
        if(x < 0 || y < 0 || x> pictureW-1|| y>pictureH-1 || picture.getPixel(x, y) == 0)
            return;

        if(picture.getPixel(x, y) == selectedColor || Color.alpha(selectedColor) == 0) {
            image[x][y] = selectedColor;
        }
        else {
            int R = (selectedColor >> 16) & 0xff;
            int G = (selectedColor >> 8) & 0xff;
            int B = (selectedColor) & 0xff;
            image[x][y] = Color.argb(100, R, G, B);

        }
    }

    protected void onDraw(Canvas canvas){
        //Drawing here
        rect = canvas.getClipBounds();
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor, scalePointX, scalePointY);
        canvas.translate(mPosX, mPosY);
        //canvas.scale(scaleFactor, scaleFactor);
        //Draw Back
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        canvasWidth = getWidth();
        canvasHeight = getHeight();
        size = canvasWidth/pictureW;
        sizeY = size;

        //Drawing numbers
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);

        for (int x = 0; x < pictureW; x++) {
            for (int y = 0; y < pictureH; y++) {
                int c = picture.getPixel(x,y);
                if (Color.alpha(c) != 0) {
                    int number = colors.indexOf(c);
                    int s = number >= 9? (int) (size * .8) : size;
                    s = number >= 99? (int) (s*.8):s;
                    paint.setTextSize(s);
                    canvas.drawText(String.valueOf(number+1), x*size+s/4, y*sizeY+s, paint);
                    int left = x * size;
                    int top = y * sizeY;
                    int right = left + size;
                    int bottom = top + sizeY;
                    canvas.drawRect(left, top, right, bottom, paint);
                }
            }
        }



        paint.setStyle(Paint.Style.FILL);
        //Drawing Colors Layer
        for(int x=0; x<pictureW; x++){
            for(int y=0; y<pictureH; y++){
                int c = image[x][y];
                if (Color.alpha(c) != 0) {
                    int left = x * size;
                    int top = y * sizeY;
                    int right = left + size;
                    int bottom = top + sizeY;
                    paint.setColor(c);
                    canvas.drawRect(left, top, right, bottom, paint);
                }
            }
        }



        canvas.restore();
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            scalePointX =  detector.getFocusX();
            scalePointY = detector.getFocusY();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(.5f, Math.min(mScaleFactor, 5.0f));

            invalidate();
            return true;
        }
    }
}
