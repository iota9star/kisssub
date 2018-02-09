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
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.Util.adjustAlpha;
import static com.afollestad.aesthetic.Util.resolveResId;

/**
 * @author Aidan Follestad (afollestad)
 */
public class AestheticTextInputLayout extends TextInputLayout {

    private CompositeDisposable subs;
    private int backgroundResId;

    public AestheticTextInputLayout(Context context) {
        super(context);
    }

    public AestheticTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AestheticTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            backgroundResId = resolveResId(context, attrs, android.R.attr.background);
        }
    }

    private void invalidateColors(int color) {
        TextInputLayoutUtil.setAccent(this, color);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        subs = new CompositeDisposable();
        subs.add(
                Aesthetic.get(getContext())
                        .textColorSecondary()
                        .compose(Rx.<Integer>distinctToMainThread())
                        .subscribe(
                                new Consumer<Integer>() {
                                    @Override
                                    public void accept(@NonNull Integer color) {
                                        TextInputLayoutUtil.setHint(
                                                AestheticTextInputLayout.this, adjustAlpha(color, 0.7f));
                                    }
                                },
                                onErrorLogAndRethrow()));
        subs.add(
                ViewUtil.getObservableForResId(getContext(), backgroundResId, Aesthetic.get(getContext()).colorAccent())
                        .compose(Rx.<Integer>distinctToMainThread())
                        .subscribe(
                                new Consumer<Integer>() {
                                    @Override
                                    public void accept(@NonNull Integer color) {
                                        invalidateColors(color);
                                    }
                                },
                                onErrorLogAndRethrow()));
    }

    @Override
    protected void onDetachedFromWindow() {
        subs.clear();
        super.onDetachedFromWindow();
    }
}
