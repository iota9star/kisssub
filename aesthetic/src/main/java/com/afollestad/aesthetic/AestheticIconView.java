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
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.Util.resolveResId;

public class AestheticIconView extends AppCompatImageView {

    private CompositeDisposable compositeDisposable;
    private int backgroundResId;

    public AestheticIconView(Context context) {
        super(context);
    }

    public AestheticIconView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AestheticIconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            backgroundResId = resolveResId(context, attrs, android.R.attr.background);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        compositeDisposable = new CompositeDisposable();
        Observable<Integer> obs = ViewUtil.getObservableForResId(getContext(), backgroundResId, null);
        if (obs != null) {
            compositeDisposable.add(
                    obs.compose(Rx.distinctToMainThread())
                            .subscribe(ViewBackgroundAction.create(this), onErrorLogAndRethrow()));
        }
        compositeDisposable.add(
                Aesthetic.get(getContext())
                        .colorAccent()
                        .compose(Rx.distinctToMainThread())
                        .subscribe(this::setColorFilter, onErrorLogAndRethrow()));
    }

    @Override
    protected void onDetachedFromWindow() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
        super.onDetachedFromWindow();
    }
}
