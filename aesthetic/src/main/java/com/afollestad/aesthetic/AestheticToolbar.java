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
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.TintHelper.createTintedDrawable;
import static com.afollestad.aesthetic.Util.setOverflowButtonColor;

/**
 * @author Aidan Follestad (afollestad)
 */
public class AestheticToolbar extends Toolbar {

    private BgIconColorState lastState;
    private CompositeDisposable compositeDisposable;
    private PublishSubject<Integer> onColorUpdated;

    private boolean transparentBackground = false;

    public AestheticToolbar(Context context) {
        super(context);
    }

    public AestheticToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AestheticToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AestheticToolbar);
        transparentBackground = a.getBoolean(R.styleable.AestheticToolbar_transparentBackground, false);
        a.recycle();
    }

    private void invalidateColors(BgIconColorState state) {
        lastState = state;
        if (!transparentBackground) {
            setBackgroundColor(state.bgColor());
        }
        setTitleTextColor(state.iconTitleColor().activeColor());
        setOverflowButtonColor(this, state.iconTitleColor().activeColor());
        if (getNavigationIcon() != null) {
            setNavigationIcon(getNavigationIcon());
        }
        onColorUpdated.onNext(state.bgColor());
        ViewUtil.tintToolbarMenu(this, getMenu(), state.iconTitleColor());
    }

    public Observable<Integer> colorUpdated() {
        return onColorUpdated;
    }

    @Override
    public void setNavigationIcon(@Nullable Drawable icon) {
        if (lastState == null) {
            super.setNavigationIcon(icon);
            return;
        }
        super.setNavigationIcon(createTintedDrawable(icon, lastState.iconTitleColor().toEnabledSl()));
    }

    public void setNavigationIcon(@Nullable Drawable icon, @ColorInt int color) {
        if (lastState == null) {
            super.setNavigationIcon(icon);
            return;
        }
        super.setNavigationIcon(createTintedDrawable(icon, color));
    }

    public void setTransparentBackground(boolean transparentBackground) {
        this.transparentBackground = transparentBackground;
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        compositeDisposable = new CompositeDisposable();
        onColorUpdated = PublishSubject.create();
        compositeDisposable.add(Observable.combineLatest(
                Aesthetic.get(getContext()).colorPrimary(),
                Aesthetic.get(getContext()).colorIconTitle(null),
                BgIconColorState.creator())
                .take(1)
                .subscribe(this::invalidateColors));
        compositeDisposable.add(
                Observable.combineLatest(Aesthetic.get(getContext()).colorPrimary(), Aesthetic.get(getContext()).colorIconTitle(null), BgIconColorState.creator())
                        .compose(Rx.distinctToMainThread())
                        .subscribe(this::invalidateColors, onErrorLogAndRethrow()));
    }

    @Override
    protected void onDetachedFromWindow() {
        lastState = null;
        onColorUpdated = null;
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
        super.onDetachedFromWindow();
    }
}
