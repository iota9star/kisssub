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
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.design.widget.TextInputLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * @author Aidan Follestad (afollestad)
 */
@RestrictTo(LIBRARY_GROUP)
final class TextInputLayoutUtil {

    static void setHint(@NonNull TextInputLayout view, @ColorInt int hintColor) {
        try {
            final Field mDefaultTextColorField = TextInputLayout.class.getDeclaredField("mDefaultTextColor");
            mDefaultTextColorField.setAccessible(true);
            mDefaultTextColorField.set(view, ColorStateList.valueOf(hintColor));
            final Method updateLabelStateMethod = TextInputLayout.class.getDeclaredMethod("updateLabelState", boolean.class, boolean.class);
            updateLabelStateMethod.setAccessible(true);
            updateLabelStateMethod.invoke(view, false, true);
        } catch (Throwable t) {
            throw new IllegalStateException("Failed to set TextInputLayout hint (collapsed) color: " + t.getLocalizedMessage(), t);
        }
    }

    static void setAccent(@NonNull TextInputLayout view, @ColorInt int accentColor) {
        try {
            final Field mFocusedTextColorField = TextInputLayout.class.getDeclaredField("mFocusedTextColor");
            mFocusedTextColorField.setAccessible(true);
            mFocusedTextColorField.set(view, ColorStateList.valueOf(accentColor));
            final Method updateLabelStateMethod = TextInputLayout.class.getDeclaredMethod("updateLabelState", boolean.class, boolean.class);
            updateLabelStateMethod.setAccessible(true);
            updateLabelStateMethod.invoke(view, false, true);
        } catch (Throwable t) {
            throw new IllegalStateException("Failed to set TextInputLayout accent (expanded) color: " + t.getLocalizedMessage(), t);
        }
    }
}
