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
import android.util.AttributeSet;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

/**
 * @author Aidan Follestad (afollestad)
 */
public class AestheticProgressBar extends MaterialProgressBar {

    private Disposable subscription;

    public AestheticProgressBar(Context context) {
        super(context);
    }

    public AestheticProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AestheticProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void invalidateColors(int color) {
        TintHelper.setTint(this, color);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        subscription =
                Aesthetic.get(getContext())
                        .colorAccent()
                        .compose(Rx.<Integer>distinctToMainThread())
                        .subscribe(
                                new Consumer<Integer>() {
                                    @Override
                                    public void accept(@NonNull Integer color) {
                                        invalidateColors(color);
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
