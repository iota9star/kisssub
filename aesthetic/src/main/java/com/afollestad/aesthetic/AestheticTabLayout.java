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
import android.support.annotation.ColorInt;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.TintHelper.createTintedDrawable;
import static com.afollestad.aesthetic.Util.adjustAlpha;

/**
 * @author Aidan Follestad (afollestad)
 */
public class AestheticTabLayout extends TabLayout {

    private static final float UNFOCUSED_ALPHA = 0.5f;
    private Disposable indicatorModeSubscription;
    private Disposable bgModeSubscription;
    private Disposable indicatorColorSubscription;
    private Disposable bgColorSubscription;

    public AestheticTabLayout(Context context) {
        super(context);
    }

    public AestheticTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AestheticTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void setIconsColor(int color) {
        final ColorStateList sl = new ColorStateList(new int[][]{new int[]{-android.R.attr.state_selected}, new int[]{android.R.attr.state_selected}},
                new int[]{adjustAlpha(color, UNFOCUSED_ALPHA), color});
        for (int i = 0; i < getTabCount(); i++) {
            final Tab tab = getTabAt(i);
            if (tab != null && tab.getIcon() != null) {
                tab.setIcon(createTintedDrawable(tab.getIcon(), sl));
            }
        }
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        super.setBackgroundColor(color);
        Aesthetic.get()
                .colorIconTitle(Observable.just(color))
                .take(1)
                .subscribe(activeInactiveColors -> {
                    setIconsColor(activeInactiveColors.activeColor());
                    setTabTextColors(adjustAlpha(activeInactiveColors.inactiveColor(), UNFOCUSED_ALPHA), activeInactiveColors.activeColor());
                });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        bgModeSubscription = Aesthetic.get()
                .tabLayoutBackgroundMode()
                .compose(Rx.distinctToMainThread())
                .subscribe(mode -> {
                    if (bgColorSubscription != null) {
                        bgColorSubscription.dispose();
                    }
                    switch (mode) {
                        case TabLayoutIndicatorMode.PRIMARY:
                            bgColorSubscription = Aesthetic.get()
                                    .colorPrimary()
                                    .compose(Rx.distinctToMainThread())
                                    .subscribe(ViewBackgroundAction.create(AestheticTabLayout.this), onErrorLogAndRethrow());
                            break;
                        case TabLayoutIndicatorMode.ACCENT:
                            bgColorSubscription = Aesthetic.get()
                                    .colorAccent()
                                    .compose(Rx.distinctToMainThread())
                                    .subscribe(ViewBackgroundAction.create(AestheticTabLayout.this), onErrorLogAndRethrow());
                            break;
                        default:
                            throw new IllegalStateException("Unimplemented bg mode: " + mode);
                    }
                }, onErrorLogAndRethrow());

        indicatorModeSubscription = Aesthetic.get()
                .tabLayoutIndicatorMode()
                .compose(Rx.distinctToMainThread())
                .subscribe(mode -> {
                    if (indicatorColorSubscription != null) {
                        indicatorColorSubscription.dispose();
                    }
                    switch (mode) {
                        case TabLayoutIndicatorMode.PRIMARY:
                            indicatorColorSubscription = Aesthetic.get()
                                    .colorPrimary()
                                    .compose(Rx.distinctToMainThread())
                                    .subscribe(this::setSelectedTabIndicatorColor, onErrorLogAndRethrow());
                            break;
                        case TabLayoutIndicatorMode.ACCENT:
                            indicatorColorSubscription = Aesthetic.get()
                                    .colorAccent()
                                    .compose(Rx.distinctToMainThread())
                                    .subscribe(this::setSelectedTabIndicatorColor, onErrorLogAndRethrow());
                            break;
                        default:
                            throw new IllegalStateException("Unimplemented bg mode: " + mode);
                    }
                }, onErrorLogAndRethrow());
    }

    @Override
    protected void onDetachedFromWindow() {
        if (bgColorSubscription != null) {
            bgColorSubscription.dispose();
        }
        if (indicatorColorSubscription != null) {
            indicatorColorSubscription.dispose();
        }
        if (bgModeSubscription != null) {
            bgModeSubscription.dispose();
        }
        if (indicatorModeSubscription != null) {
            indicatorModeSubscription.dispose();
        }
        super.onDetachedFromWindow();
    }
}
