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
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.Util.resolveResId;

/**
 * @author Aidan Follestad (afollestad)
 */
public class AestheticButton extends AppCompatButton {

    private Disposable subscription;
    private int backgroundResId;

    public AestheticButton(Context context) {
        super(context);
    }

    public AestheticButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AestheticButton(Context context, AttributeSet attrs, int defStyleAttr) {
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
        ColorStateList textColorSl = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_enabled},
                        new int[]{-android.R.attr.state_enabled}},
                new int[]{Util.isColorLight(state.color()) ? Color.BLACK : Color.WHITE, state.isDark() ? Color.WHITE : Color.BLACK});
        setTextColor(textColorSl);

        // Hack around button color not updating
        setEnabled(!isEnabled());
        setEnabled(!isEnabled());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Observable<Integer> obs = ViewUtil.getObservableForResId(getContext(), backgroundResId, Aesthetic.get().colorAccent());
        if (obs != null) {
            subscription = Observable.combineLatest(
                    obs,
                    Aesthetic.get().isDark(),
                    ColorIsDarkState.creator())
                    .compose(Rx.distinctToMainThread())
                    .subscribe(this::invalidateColors, onErrorLogAndRethrow());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (subscription != null) {
            subscription.dispose();
        }
        super.onDetachedFromWindow();
    }
}
