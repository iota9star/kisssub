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

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;

/**
 * @author Aidan Follestad (afollestad)
 */
public final class Rx {

    public static Consumer<Throwable> onErrorLogAndRethrow() {
        return throwable -> {
            throwable.printStackTrace();
            throw Exceptions.propagate(throwable);
        };
    }

    public static <T> ObservableTransformer<T, T> distinctToMainThread() {
        return obs -> obs.observeOn(AndroidSchedulers.mainThread()).distinctUntilChanged();
    }
}
