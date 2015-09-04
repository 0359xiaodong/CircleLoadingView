package com.example.administrator.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2015/9/3 0003.
 */
public class CircleLoading extends View {

    public static final int DEFAULT_CIRCLE_RING_COLOR =Color.WHITE;
    public static final int DEFAULT_STROKE_WIDTH = 3;
    public static final int DEFAULT_ROLLING_CIRCLE_RADIUS = 10;
    public static final int DEFAULT_ROLLING_CIRCLE_COLOR = Color.WHITE;
    public static final int DEFAULT_ROLLING_TIME = 1000;
    public static final int CHANGE_SPEED_MODE=1;
    public static final int UNIFORM_SPEED=0;


    private int rollingCircleColor;
    private int strokeWidth;
    private int circleRingColor;
    private int rollingCircleRadius;
    private float radius;

    private long currentTime;
    private int timeOfOneRoll ;

    Paint mPaint;

    int rollingMode;


    //acceleration
    double accel ;
    double bottomSpeed;

    //x,y of rolling circle coordinate
    float centre_x;
    float centre_y;


    public CircleLoading(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleLoading, 0, 0);



        circleRingColor = a.getColor(R.styleable.CircleLoading_circleRingColor, DEFAULT_CIRCLE_RING_COLOR);
        strokeWidth = a.getDimensionPixelSize(R.styleable.CircleLoading_ringWidth, DEFAULT_STROKE_WIDTH);
        rollingCircleColor = a.getColor(R.styleable.CircleLoading_rollingCircleColor, DEFAULT_ROLLING_CIRCLE_COLOR);
        rollingCircleRadius = a.getDimensionPixelSize(R.styleable.CircleLoading_rollingCircleRadius, DEFAULT_ROLLING_CIRCLE_RADIUS);
        timeOfOneRoll = a.getInt(R.styleable.CircleLoading_rollingTime, DEFAULT_ROLLING_TIME);
        rollingMode = a.getInt(R.styleable.CircleLoading_rollingMode, UNIFORM_SPEED);

        a.recycle();


        if (rollingMode == CHANGE_SPEED_MODE) {

            accel = 8 * Math.PI / timeOfOneRoll / timeOfOneRoll;
            bottomSpeed = accel * timeOfOneRoll / 2;
        } else {
            accel = 0;
            bottomSpeed = (2 * Math.PI)/timeOfOneRoll ;
        }


        mPaint = new Paint();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    postInvalidate();
                    currentTime += 12;
                    if (currentTime >= timeOfOneRoll) {
                        currentTime = 0;
                    }
                    try {
                        Thread.sleep(12);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //draw the ring
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(circleRingColor);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, mPaint);


        /**draw the rolling circle*/

        double angle;

        switch (rollingMode) {

            case CHANGE_SPEED_MODE:
                if (currentTime <= timeOfOneRoll / 2) {
                    angle = accel * currentTime * currentTime / 2;

                } else {
                    long t = currentTime - timeOfOneRoll / 2;
                    angle = Math.PI + (bottomSpeed * t - accel * t * t / 2);
                }
                break;
            default:
                angle = currentTime * bottomSpeed;

        }


        //x,y of rolling circle coordinate
         centre_x= (float) (getWidth() / 2 + radius * Math.sin(angle));
         centre_y = (float) (getHeight() / 2 - radius * Math.cos(angle));

        mPaint.setColor(rollingCircleColor);
        mPaint.setStyle(Paint.Style.FILL);


        canvas.drawCircle(centre_x, centre_y, rollingCircleRadius, mPaint);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        radius = Math.min(getWidth() / 2, getHeight() / 2) - rollingCircleRadius - strokeWidth / 2;


    }


}
