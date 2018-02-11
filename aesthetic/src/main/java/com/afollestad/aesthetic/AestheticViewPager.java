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
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import io.reactivex.disposables.Disposable;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

/**
 * @author Aidan Follestad (afollestad)
 */
public class AestheticViewPager extends ViewPager {

    private Disposable disposable;

    public AestheticViewPager(Context context) {
        super(context);
    }

    public AestheticViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void invalidateColors(int color) {
        EdgeGlowUtil.setEdgeGlowColor(this, color);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        disposable = Aesthetic.get(getContext())
                .colorAccent()
                .compose(Rx.distinctToMainThread())
                .subscribe(this::invalidateColors, onErrorLogAndRethrow());
    }

    @Override
    protected void onDetachedFromWindow() {
        if (disposable != null) {
            disposable.dispose();
        }
        super.onDetachedFromWindow();
    }
}
