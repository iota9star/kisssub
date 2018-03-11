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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

/**
 * @author Aidan Follestad (afollestad)
 */
@SuppressWarnings("RestrictedApi")
public class AestheticNavigationView extends NavigationView {

    private Disposable modeSubscription;
    private Disposable colorSubscription;

    public AestheticNavigationView(Context context) {
        super(context);
    }

    public AestheticNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AestheticNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void invalidateColors(ColorIsDarkState state) {
        int selectedColor = state.color();
        boolean isDark = state.isDark();
        int baseColor = isDark ? Color.WHITE : Color.BLACK;
        int unselectedIconColor = Util.adjustAlpha(baseColor, .54f);
        int unselectedTextColor = Util.adjustAlpha(baseColor, .87f);
        int selectedItemBgColor = ContextCompat.getColor(getContext(), isDark ? R.color.ate_navigation_drawer_selected_dark : R.color.ate_navigation_drawer_selected_light);

        final ColorStateList iconSl = new ColorStateList(new int[][]{new int[]{-android.R.attr.state_checked}, new int[]{android.R.attr.state_checked}},
                new int[]{unselectedIconColor, selectedColor});
        final ColorStateList textSl = new ColorStateList(new int[][]{new int[]{-android.R.attr.state_checked}, new int[]{android.R.attr.state_checked}},
                new int[]{unselectedTextColor, selectedColor});
        StateListDrawable bgDrawable = new StateListDrawable();
        bgDrawable.addState(new int[]{android.R.attr.state_checked}, new ColorDrawable(selectedItemBgColor));

        setItemTextColor(textSl);
        setItemIconTintList(iconSl);
        setItemBackground(bgDrawable);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        modeSubscription = Aesthetic.get()
                .navigationViewMode()
                .compose(Rx.distinctToMainThread())
                .subscribe(mode -> {
                            switch (mode) {
                                case NavigationViewMode.SELECTED_PRIMARY:
                                    colorSubscription = Observable.combineLatest(
                                            Aesthetic.get().colorPrimary(),
                                            Aesthetic.get().isDark(),
                                            ColorIsDarkState.creator())
                                            .compose(Rx.distinctToMainThread())
                                            .subscribe(this::invalidateColors, onErrorLogAndRethrow());
                                    break;
                                case NavigationViewMode.SELECTED_ACCENT:
                                    colorSubscription = Observable.combineLatest(
                                            Aesthetic.get().colorAccent(),
                                            Aesthetic.get().isDark(),
                                            ColorIsDarkState.creator())
                                            .compose(Rx.distinctToMainThread())
                                            .subscribe(this::invalidateColors, onErrorLogAndRethrow());
                                    break;
                                default:
                                    throw new IllegalStateException("Unknown nav view mode: " + mode);
                            }
                        },
                        onErrorLogAndRethrow());
    }

    @Override
    protected void onDetachedFromWindow() {
        if (colorSubscription != null) {
            colorSubscription.dispose();
        }
        if (modeSubscription != null) {
            modeSubscription.dispose();
        }
        super.onDetachedFromWindow();
    }
}
