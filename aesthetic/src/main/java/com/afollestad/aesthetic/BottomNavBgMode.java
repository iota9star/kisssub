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

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.afollestad.aesthetic.BottomNavBgMode.ACCENT;
import static com.afollestad.aesthetic.BottomNavBgMode.BLACK_WHITE_AUTO;
import static com.afollestad.aesthetic.BottomNavBgMode.PRIMARY;
import static com.afollestad.aesthetic.BottomNavBgMode.PRIMARY_DARK;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author Aidan Follestad (afollestad)
 */
@SuppressWarnings("WeakerAccess")
@Retention(SOURCE)
@IntDef(value = {BLACK_WHITE_AUTO, PRIMARY, PRIMARY_DARK, ACCENT})
public @interface BottomNavBgMode {
    int BLACK_WHITE_AUTO = 0;
    int PRIMARY = 1;
    int PRIMARY_DARK = 2;
    int ACCENT = 3;
}
