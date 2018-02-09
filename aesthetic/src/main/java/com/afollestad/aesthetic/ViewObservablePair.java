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

import android.support.annotation.RestrictTo;
import android.view.View;

import io.reactivex.Observable;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * @author Aidan Follestad (afollestad)
 */
@RestrictTo(LIBRARY_GROUP)
final class ViewObservablePair {

    private final View view;
    private final Observable<Integer> observable;

    private ViewObservablePair(View view, Observable<Integer> observable) {
        this.view = view;
        this.observable = observable;
    }

    static ViewObservablePair create(View view, Observable<Integer> observable) {
        return new ViewObservablePair(view, observable);
    }

    View view() {
        return view;
    }

    Observable<Integer> observable() {
        return observable;
    }
}
