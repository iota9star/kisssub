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

import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;

/**
 * @author Aidan Follestad (afollestad)
 */
@SuppressWarnings("WeakerAccess")
public final class ActiveInactiveColors {

    @ColorInt
    private final int activeColor;
    @ColorInt
    private final int inactiveColor;

    private ActiveInactiveColors(@ColorInt int activeColor, @ColorInt int inactiveColor) {
        this.activeColor = activeColor;
        this.inactiveColor = inactiveColor;
    }

    public static ActiveInactiveColors create(
            @ColorInt int activeColor, @ColorInt int inactiveColor) {
        return new ActiveInactiveColors(activeColor, inactiveColor);
    }

    @ColorInt
    public int activeColor() {
        return activeColor;
    }

    @ColorInt
    public int inactiveColor() {
        return inactiveColor;
    }

    public ColorStateList toEnabledSl() {
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_enabled}, new int[]{-android.R.attr.state_enabled}
                },
                new int[]{activeColor(), inactiveColor()});
    }
}
