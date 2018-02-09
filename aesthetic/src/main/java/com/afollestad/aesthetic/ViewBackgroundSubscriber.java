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

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.CardView;
import android.view.View;

import io.reactivex.exceptions.Exceptions;
import io.reactivex.observers.DisposableObserver;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * @author Aidan Follestad (afollestad)
 */
@RestrictTo(LIBRARY_GROUP)
final class ViewBackgroundSubscriber extends DisposableObserver<Integer> {

    private final View view;

    private ViewBackgroundSubscriber(@NonNull View view) {
        this.view = view;
    }

    public static ViewBackgroundSubscriber create(@NonNull View view) {
        return new ViewBackgroundSubscriber(view);
    }

    @Override
    public void onError(Throwable e) {
        throw Exceptions.propagate(e);
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onNext(Integer color) {
        if (view instanceof CardView) {
            ((CardView) view).setCardBackgroundColor(color);
        } else {
            view.setBackgroundColor(color);
        }
    }
}
