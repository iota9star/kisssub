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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.Util.resolveResId;

/**
 * @author Aidan Follestad (afollestad)
 */
public class AestheticFab extends FloatingActionButton {

    private Disposable subscription;
    private int backgroundResId;
    private int iconColor;

    public AestheticFab(Context context) {
        super(context);
    }

    public AestheticFab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AestheticFab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            backgroundResId = resolveResId(context, attrs, android.R.attr.background);
        }
    }

    private void invalidateColors(ColorIsDarkState state) {
        TintHelper.setTintAuto(this, state.color(), true, state.isDark());
        iconColor = Util.isColorLight(state.color()) ? Color.BLACK : Color.WHITE;
        setImageDrawable(getDrawable());
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(TintHelper.createTintedDrawable(drawable, iconColor));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //noinspection ConstantConditions
        subscription =
                Observable.combineLatest(
                        ViewUtil.getObservableForResId(
                                getContext(), backgroundResId, Aesthetic.get(getContext()).colorAccent()),
                        Aesthetic.get(getContext()).isDark(),
                        ColorIsDarkState.creator())
                        .compose(Rx.<ColorIsDarkState>distinctToMainThread())
                        .subscribe(
                                new Consumer<ColorIsDarkState>() {
                                    @Override
                                    public void accept(@NonNull ColorIsDarkState colorIsDarkState) {
                                        invalidateColors(colorIsDarkState);
                                    }
                                },
                                onErrorLogAndRethrow());
    }

    @Override
    protected void onDetachedFromWindow() {
        subscription.dispose();
        super.onDetachedFromWindow();
    }
}
