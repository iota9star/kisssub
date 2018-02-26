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
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

/**
 * @author Aidan Follestad (afollestad)
 */
public class AestheticEditText extends AppCompatEditText {

    private CompositeDisposable subscriptions;
    private int backgroundResId;
    private int textColorResId;
    private int textColorHintResId;

    public AestheticEditText(Context context) {
        super(context);
    }

    public AestheticEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AestheticEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            int[] attrsArray =
                    new int[]{
                            android.R.attr.background, android.R.attr.textColor, android.R.attr.textColorHint
                    };
            TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
            backgroundResId = ta.getResourceId(0, 0);
            textColorResId = ta.getResourceId(1, 0);
            textColorHintResId = ta.getResourceId(2, 0);
            ta.recycle();
        }
    }

    private void invalidateColors(ColorIsDarkState state) {
        TintHelper.setTintAuto(this, state.color(), true, state.isDark());
        TintHelper.setCursorTint(this, state.color());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        subscriptions = new CompositeDisposable();
        Observable<Integer> obs1 = ViewUtil.getObservableForResId(
                getContext(), backgroundResId, Aesthetic.get().colorAccent());
        if (obs1 != null) {
            subscriptions.add(
                    Observable.combineLatest(
                            obs1,
                            Aesthetic.get().isDark(),
                            ColorIsDarkState.creator())
                            .compose(Rx.distinctToMainThread())
                            .subscribe(
                                    this::invalidateColors,
                                    onErrorLogAndRethrow()));
        }
        Observable<Integer> obs2 = ViewUtil.getObservableForResId(getContext(), textColorResId, Aesthetic.get().textColorPrimary());
        if (obs2 != null) {
            subscriptions.add(
                    obs2.compose(Rx.distinctToMainThread())
                            .subscribe(ViewTextColorAction.create(this), onErrorLogAndRethrow()));
        }
        Observable<Integer> obs3 = ViewUtil.getObservableForResId(getContext(), textColorHintResId, Aesthetic.get().textColorSecondary());
        if (obs3 != null) {
            subscriptions.add(
                    obs3.compose(Rx.distinctToMainThread())
                            .subscribe(ViewHintTextColorAction.create(this), onErrorLogAndRethrow()));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (subscriptions != null) {
            subscriptions.clear();
        }
        super.onDetachedFromWindow();
    }
}
