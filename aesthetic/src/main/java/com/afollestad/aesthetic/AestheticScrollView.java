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
import android.widget.ScrollView;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

/**
 * @author Aidan Follestad (afollestad)
 */
public class AestheticScrollView extends ScrollView {

    private Disposable subscription;

    public AestheticScrollView(Context context) {
        super(context);
    }

    public AestheticScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AestheticScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void invalidateColors(int color) {
        EdgeGlowUtil.setEdgeGlowColor(this, color);
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
