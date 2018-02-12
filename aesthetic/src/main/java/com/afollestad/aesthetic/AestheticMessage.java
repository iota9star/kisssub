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

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

import io.reactivex.disposables.CompositeDisposable;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

public class AestheticMessage extends LinearLayout {
    private Animation slideOutAnimation;
    private int mDuration = 2000;
    private CardView mContainerView;
    private TextView mTitleView;
    private TextView mMsgView;
    private TextView mActionView;
    private LinearLayout mContentView;

    private CompositeDisposable subscriptions;
    private OnActionClickListener onActionClickListener;

    public AestheticMessage(Context context) {
        super(context);
        init(context);
    }

    public static AestheticMessage builder(Context context) {
        return new AestheticMessage(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.layout_message, this);
        this.mContainerView = findViewById(R.id.cardViewContainer);
        this.mTitleView = findViewById(R.id.textViewTitle);
        this.mMsgView = findViewById(R.id.textViewMsg);
        this.mActionView = findViewById(R.id.textViewAction);
        this.mContentView = findViewById(R.id.linearLayoutContent);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        subscriptions = new CompositeDisposable();
        subscriptions.add(
                Aesthetic.get()
                        .colorIconTitle(Aesthetic.get().colorAccent())
                        .take(1)
                        .subscribe(activeInactiveColors -> {
                            int color = activeInactiveColors.activeColor();
                            mTitleView.setTextColor(color);
                            mMsgView.setTextColor(Util.isColorLight(color) ? Util.shiftColor(color, 0.96f) : Util.shiftColor(color, 1.28f));
                            mActionView.setTextColor(color);
                        }));
        subscriptions.add(
                Aesthetic.get()
                        .colorAccent()
                        .take(1)
                        .subscribe(this::setBackgroundDrawable, onErrorLogAndRethrow())
        );
    }

    private void setBackgroundDrawable(int color) {
        GradientDrawable.Orientation[] orientations = {
                GradientDrawable.Orientation.TOP_BOTTOM,
                GradientDrawable.Orientation.TR_BL,
                GradientDrawable.Orientation.RIGHT_LEFT,
                GradientDrawable.Orientation.BR_TL,
                GradientDrawable.Orientation.BOTTOM_TOP,
                GradientDrawable.Orientation.BL_TR,
                GradientDrawable.Orientation.LEFT_RIGHT,
                GradientDrawable.Orientation.TL_BR
        };
        Random random = new Random();
        int rawColor = Util.isColorLight(color) ? Util.shiftColor(color, 0.9f) : Util.shiftColor(color, 1.1f);
        int[] colorArr;
        if (random.nextBoolean()) {
            colorArr = new int[]{color, rawColor};
        } else {
            colorArr = new int[]{rawColor, color};
        }
        GradientDrawable drawable = new GradientDrawable(orientations[random.nextInt(orientations.length)], colorArr);
        mContentView.setBackground(drawable);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (subscriptions != null) {
            subscriptions.clear();
        }
        super.onDetachedFromWindow();
    }

    public AestheticMessage setTitle(String title) {
        this.mTitleView.setVisibility(View.VISIBLE);
        this.mTitleView.setText(title);
        return this;
    }

    public AestheticMessage setTitle(@StringRes int title) {
        this.mTitleView.setVisibility(View.VISIBLE);
        this.mTitleView.setText(title);
        return this;
    }

    public AestheticMessage setMessage(String msg) {
        this.mMsgView.setVisibility(View.VISIBLE);
        this.mMsgView.setText(msg);
        return this;
    }

    public AestheticMessage setMessage(@StringRes int msg) {
        this.mMsgView.setVisibility(View.VISIBLE);
        this.mMsgView.setText(msg);
        return this;
    }

    public AestheticMessage setCornerRadius(float radius) {
        this.mContainerView.setRadius(radius);
        return this;
    }

    public AestheticMessage setAction(String action, OnActionClickListener listener) {
        this.mActionView.setVisibility(View.VISIBLE);
        this.mActionView.setText(action);
        this.onActionClickListener = listener;
        this.mContainerView.setOnClickListener(v -> {
            if (onActionClickListener != null) {
                onActionClickListener.onClick();
                dismiss();
            }
        });
        return this;
    }

    public AestheticMessage setAction(@StringRes int action, OnActionClickListener listener) {
        this.mActionView.setVisibility(View.VISIBLE);
        this.mActionView.setText(action);
        this.onActionClickListener = listener;
        mContainerView.setOnClickListener(v -> {
            if (onActionClickListener != null) {
                onActionClickListener.onClick();
            }
        });
        return this;
    }

    public void show() {
        createInAnim();
        createOutAnim();
        final ViewGroup decorView = (ViewGroup) ((AppCompatActivity) getContext()).getWindow().getDecorView();
        if (getParent() == null) {
            decorView.addView(this);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, 0, r, mContainerView.getMeasuredHeight());
    }

    private void createInAnim() {
        Animation slideInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in);
        slideInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                postDelayed(() -> dismiss(), mDuration);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        setAnimation(slideInAnimation);
    }

    private void createOutAnim() {
        slideOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out);
        slideOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void dismiss() {
        slideOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(final Animation animation) {
            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                destroy();
            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });
        startAnimation(slideOutAnimation);
    }

    private void destroy() {
        postDelayed(() -> {
            ViewParent parent = getParent();
            if (parent != null) {
                AestheticMessage.this.clearAnimation();
                ((ViewGroup) parent).removeView(AestheticMessage.this);
            }
        }, 200);
    }

    public interface OnActionClickListener {
        void onClick();
    }

}
