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
import android.view.View;

import io.reactivex.functions.Consumer;

/**
 * @author Aidan Follestad (afollestad)
 */
@SuppressWarnings("WeakerAccess")
public final class ViewBackgroundAction implements Consumer<Integer> {

    private final View view;

    private ViewBackgroundAction(View view) {
        this.view = view;
    }

    public static ViewBackgroundAction create(@NonNull View view) {
        return new ViewBackgroundAction(view);
    }

    @Override
    public void accept(@io.reactivex.annotations.NonNull Integer color) {
        if (view != null) {
            view.setBackgroundColor(color);
        }
    }
}
