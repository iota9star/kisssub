/*
 *
 *  *    Copyright 2018. iota9star
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package com.afollestad.aesthetic;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.util.Random;

/**
 * Created by srain on 11/6/14.
 */
public class StoreHouseBarItem extends Animation {

    private final Paint mPaint = new Paint();
    PointF midPoint;
    float translationX;
    private float mFromAlpha = 1.0f;
    private float mToAlpha = 0.4f;
    private PointF mCStartPoint;
    private PointF mCEndPoint;

    StoreHouseBarItem(PointF start, PointF end, int color, int lineWidth) {
        midPoint = new PointF((start.x + end.x) / 2, (start.y + end.y) / 2);
        mCStartPoint = new PointF(start.x - midPoint.x, start.y - midPoint.y);
        mCEndPoint = new PointF(end.x - midPoint.x, end.y - midPoint.y);
        setColor(color);
        setLineWidth(lineWidth);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    void setLineWidth(int width) {
        mPaint.setStrokeWidth(width);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    void resetPosition(int horizontalRandomness) {
        Random random = new Random();
        translationX = -random.nextInt(horizontalRandomness) + horizontalRandomness;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float alpha = mFromAlpha;
        alpha = alpha + ((mToAlpha - alpha) * interpolatedTime);
        setAlpha(alpha);
    }

    public void start(float fromAlpha, float toAlpha) {
        mFromAlpha = fromAlpha;
        mToAlpha = toAlpha;
        super.start();
    }

    public void setAlpha(float alpha) {
        mPaint.setAlpha((int) (alpha * 255));
    }

    void draw(Canvas canvas) {
        canvas.drawLine(mCStartPoint.x, mCStartPoint.y, mCEndPoint.x, mCEndPoint.y, mPaint);
    }
}
