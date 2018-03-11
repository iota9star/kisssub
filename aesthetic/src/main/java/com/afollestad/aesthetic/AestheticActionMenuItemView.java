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
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.AttributeSet;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.TintHelper.createTintedDrawable;

/**
 * @author Aidan Follestad (afollestad)
 */
@SuppressWarnings("RestrictedApi")
final class AestheticActionMenuItemView extends ActionMenuItemView {

    private Drawable icon;
    private Disposable subscription;

    public AestheticActionMenuItemView(Context context) {
        super(context);
    }

    public AestheticActionMenuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AestheticActionMenuItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void invalidateColors(@NonNull ActiveInactiveColors colors, Drawable icon) {
        if (icon != null) {
            setIcon(icon, colors.toEnabledSl());
        }
        setTextColor(colors.activeColor());
    }

    @Override
    public void setIcon(final Drawable icon) {

        // We need to retrieve the color again here.
        // For some reason, without this, a transparent color is used and the icon disappears
        // when the overflow menu opens.
        Aesthetic.get()
                .colorIconTitle(null)
                .observeOn(AndroidSchedulers.mainThread())
                .take(1)
                .subscribe(colors -> invalidateColors(colors, icon), onErrorLogAndRethrow());
        super.setIcon(icon);
    }

    public void setIcon(final Drawable icon, ColorStateList colors) {
        this.icon = icon;
        super.setIcon(createTintedDrawable(icon, colors));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        subscription = Aesthetic.get()
                .colorIconTitle(null)
                .compose(Rx.distinctToMainThread())
                .subscribe(colors -> invalidateColors(colors, icon), onErrorLogAndRethrow());
    }

    @Override
    protected void onDetachedFromWindow() {
        if (subscription != null) {
            subscription.dispose();
        }
        super.onDetachedFromWindow();
    }
}
