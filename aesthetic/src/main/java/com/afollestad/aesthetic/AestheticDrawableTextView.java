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
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.Util.resolveResId;

/**
 * @author Aidan Follestad (afollestad)
 */
public class AestheticDrawableTextView extends AppCompatTextView {

    private CompositeDisposable compositeDisposable;
    private int textColorResId;

    public AestheticDrawableTextView(Context context) {
        super(context);
    }

    public AestheticDrawableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AestheticDrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            textColorResId = resolveResId(context, attrs, android.R.attr.textColor);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        compositeDisposable = new CompositeDisposable();
        Observable<Integer> obs = ViewUtil.getObservableForResId(getContext(), textColorResId, Aesthetic.get(getContext()).textColorSecondary());
        if (obs != null) {
            compositeDisposable.add(
                    obs.compose(Rx.distinctToMainThread())
                            .subscribe(ViewTextColorAction.create(this), onErrorLogAndRethrow()));
        }
        compositeDisposable.add(
                Aesthetic.get(getContext())
                        .colorAccent()
                        .compose(Rx.distinctToMainThread())
                        .subscribe(this::tintDrawables, onErrorLogAndRethrow()));
    }

    private void tintDrawables(Integer integer) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables.length > 0) {
            for (int i = 0; i < drawables.length; i++) {
                Drawable drawable = drawables[i];
                if (drawable instanceof GradientDrawable) {
                    ((GradientDrawable) drawable).setColor(integer);
                    if (i == 0) {
                        setCompoundDrawables(drawable, null, null, null);
                    } else if (i == 1) {
                        setCompoundDrawables(null, drawable, null, null);
                    } else if (i == 2) {
                        setCompoundDrawables(null, null, drawable, null);
                    } else if (i == 3) {
                        setCompoundDrawables(null, null, null, drawable);
                    }
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
        super.onDetachedFromWindow();
    }
}
