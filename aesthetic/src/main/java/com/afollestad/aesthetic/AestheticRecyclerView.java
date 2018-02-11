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
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import io.reactivex.disposables.Disposable;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

/**
 * @author Aidan Follestad (afollestad)
 */
public class AestheticRecyclerView extends RecyclerView {

    private Disposable disposable;

    public AestheticRecyclerView(Context context) {
        super(context);
    }

    public AestheticRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AestheticRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void invalidateColors(int color) {
        EdgeGlowUtil.setEdgeGlowColor(this, color, null);
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
