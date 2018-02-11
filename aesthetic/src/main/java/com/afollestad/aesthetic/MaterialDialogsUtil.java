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
import android.support.annotation.RestrictTo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * Handles auto theming of dialogs from my Material Dialogs library, using the ThemeSingleton class.
 * Uses reflection so that Material Dialogs isn't a needed dependency if you depend on this library.
 *
 * @author Aidan Follestad (afollestad)
 */
@RestrictTo(LIBRARY_GROUP)
final class MaterialDialogsUtil {

    static boolean shouldSupport() {
        try {
            Class.forName("com.afollestad.materialdialogs.internal.ThemeSingleton");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    static void theme(Params params) {
        try {
            Class<?> cls = Class.forName("com.afollestad.materialdialogs.internal.ThemeSingleton");
            Method getMethod = cls.getMethod("get");
            Object instance = getMethod.invoke(null);

            Field fieldDarkTheme = cls.getField("darkTheme");
            fieldDarkTheme.set(instance, params.darkTheme);

            Field fieldTitleColor = cls.getField("titleColor");
            fieldTitleColor.set(instance, params.primaryTextColor);

            Field fieldContentColor = cls.getField("contentColor");
            fieldContentColor.set(instance, params.secondaryTextColor);

            Field fieldItemColor = cls.getField("itemColor");
            fieldItemColor.set(instance, params.secondaryTextColor);

            Field fieldPosColor = cls.getField("positiveColor");
            fieldPosColor.set(instance, ColorStateList.valueOf(params.accentColor));

            Field fieldNeuColor = cls.getField("neutralColor");
            fieldNeuColor.set(instance, ColorStateList.valueOf(params.accentColor));

            Field fieldNegColor = cls.getField("negativeColor");
            fieldNegColor.set(instance, ColorStateList.valueOf(params.accentColor));

            Field fieldWidgetColor = cls.getField("widgetColor");
            fieldWidgetColor.set(instance, ColorStateList.valueOf(params.accentColor));

            Field fieldLinkColor = cls.getField("linkColor");
            fieldLinkColor.set(instance, ColorStateList.valueOf(params.accentColor));

        } catch (Throwable ignored) {
        }
    }

    static Disposable observe(Aesthetic instance) {
        return Observable.combineLatest(instance.textColorPrimary(), instance.textColorSecondary(), instance.colorAccent(), instance.isDark(), Params::create)
                .distinctUntilChanged()
                .subscribe(MaterialDialogsUtil::theme);
    }

    static class Params {
        final int primaryTextColor;
        final int secondaryTextColor;
        final int accentColor;
        final boolean darkTheme;

        private Params(int primaryTextColor, int secondaryTextColor, int accentColor, boolean darkTheme) {
            this.primaryTextColor = primaryTextColor;
            this.secondaryTextColor = secondaryTextColor;
            this.accentColor = accentColor;
            this.darkTheme = darkTheme;
        }

        public static Params create(int primaryTextColor, int secondaryTextColor, int accentColor, boolean darkTheme) {
            return new Params(primaryTextColor, secondaryTextColor, accentColor, darkTheme);
        }
    }
}
