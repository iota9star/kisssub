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

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import io.reactivex.Observable;

public final class AestheticBar {

    public static final int SHORT = 1600;
    public static final int LONG = 2800;
    private Snackbar mBar;
    private View mView;
    private String mText;
    private String mAction;
    private View.OnClickListener mOnClickListener;
    private int mDuration;
    private boolean isBuild = false;

    private AestheticBar(View view) {
        if (view == null)
            throw new IllegalArgumentException("AestheticBar builder arg view can't be null");
        this.mDuration = SHORT;
        this.mView = view;
    }

    public static AestheticBar builder(View view) {
        return new AestheticBar(view);
    }

    public final AestheticBar setText(String text) {
        this.mText = text;
        return this;
    }

    public final AestheticBar setText(@StringRes int resId) {
        this.mText = mView.getContext().getString(resId);
        return this;
    }

    public final AestheticBar setDuration(int duration) {
        this.mDuration = duration;
        return this;
    }

    public final AestheticBar setAction(String action, View.OnClickListener listener) {
        this.mAction = action;
        this.mOnClickListener = listener;
        return this;
    }

    public final AestheticBar setAction(@StringRes int resId, View.OnClickListener listener) {
        this.mAction = mView.getContext().getString(resId);
        this.mOnClickListener = listener;
        return this;
    }

    public final void build() {
        mBar = Snackbar.make(mView, mText, mDuration);
        if (mAction != null) {
            mBar.setAction(mAction, mOnClickListener);
        }
        isBuild = true;
    }

    public final void show() {
        if (!isBuild) build();
        aesthetic();
        mBar.show();
    }

    private void aesthetic() {
        View view = mBar.getView();
        Observable.combineLatest(
                Aesthetic.get().colorAccent(),
                Aesthetic.get().isDark(),
                ColorIsDarkState.creator())
                .take(1)
                .subscribe(state -> {
                    Drawable drawable1;
                    Drawable drawable2 = new ColorDrawable(state.color());
                    if (state.isDark()) {
                        drawable1 = new ColorDrawable(Color.BLACK);
                    } else {
                        drawable1 = new ColorDrawable(Color.WHITE);
                    }
                    Drawable[] layers = {drawable2, drawable1};
                    LayerDrawable layerDrawable = new LayerDrawable(layers);
                    layerDrawable.setLayerInset(1, view.getContext().getResources().getDimensionPixelOffset(R.dimen.v8dp), 0, 0, 0);
                    view.setBackground(layerDrawable);
                }, Rx.onErrorLogAndRethrow());
    }

}
