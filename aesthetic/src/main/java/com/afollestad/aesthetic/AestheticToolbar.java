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
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.TintHelper.createTintedDrawable;

/**
 * @author Aidan Follestad (afollestad)
 */
public class AestheticToolbar extends Toolbar {

    private BgIconColorState lastState;
    private Disposable subscription;
    private PublishSubject<Integer> onColorUpdated;

    public AestheticToolbar(Context context) {
        super(context);
    }

    public AestheticToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AestheticToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void invalidateColors(BgIconColorState state) {
        this.lastState = state;
        this.onColorUpdated.onNext(state.bgColor());
        setBackgroundColor(state.bgColor());
        final ActiveInactiveColors iconTitleColors = state.iconTitleColor();
        if (iconTitleColors != null) {
            setTitleTextColor(iconTitleColors.activeColor());
            Util.setOverflowButtonColor(this, iconTitleColors.activeColor());
            ViewUtil.tintToolbarMenu(this, getMenu(), iconTitleColors);
        }
        if (getNavigationIcon() != null) {
            setNavigationIcon(getNavigationIcon());
        }
    }

    public Observable<Integer> colorUpdated() {
        return onColorUpdated;
    }

    @Override
    public void setNavigationIcon(@Nullable Drawable icon) {
        if (this.lastState == null) {
            super.setNavigationIcon(icon);
            return;
        }
        final ActiveInactiveColors iconTitleColors = this.lastState.iconTitleColor();
        if (iconTitleColors != null) {
            super.setNavigationIcon(createTintedDrawable(icon, iconTitleColors.toEnabledSl()));
        } else {
            super.setNavigationIcon(icon);
        }
    }

    public void setNavigationIcon(@Nullable Drawable icon, @ColorInt int color) {
        if (this.lastState == null) {
            super.setNavigationIcon(icon);
            return;
        }
        super.setNavigationIcon(createTintedDrawable(icon, color));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onColorUpdated = PublishSubject.create();
        subscription = Observable.combineLatest(
                Aesthetic.get().colorPrimary(),
                Aesthetic.get().colorIconTitle(null),
                BgIconColorState.creator())
                .compose(Rx.distinctToMainThread())
                .subscribe(this::invalidateColors, onErrorLogAndRethrow());
    }

    @Override
    protected void onDetachedFromWindow() {
        lastState = null;
        onColorUpdated = null;
        if (subscription != null) {
            subscription.dispose();
        }
        super.onDetachedFromWindow();
    }
}
