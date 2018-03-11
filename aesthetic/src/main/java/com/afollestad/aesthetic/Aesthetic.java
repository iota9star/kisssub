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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.f2prateek.rx.preferences2.RxSharedPreferences;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * @author Aidan Follestad (afollestad)
 */
@SuppressLint({"CommitPrefEdits", "StaticFieldLeak"})
public class Aesthetic {

    private static final String PREFS_NAME = "[aesthetic-prefs]";
    private static final String KEY_FIRST_TIME = "first_time";
    private static final String KEY_ACTIVITY_THEME = "activity_theme_%s";
    private static final String KEY_IS_DARK = "is_dark";
    private static final String KEY_PRIMARY_COLOR = "primary_color";
    private static final String KEY_PRIMARY_DARK_COLOR = "primary_dark_color";
    private static final String KEY_ACCENT_COLOR = "accent_color";
    private static final String KEY_PRIMARY_TEXT_COLOR = "primary_text";
    private static final String KEY_SECONDARY_TEXT_COLOR = "secondary_text";
    private static final String KEY_PRIMARY_TEXT_INVERSE_COLOR = "primary_text_inverse";
    private static final String KEY_SECONDARY_TEXT_INVERSE_COLOR = "secondary_text_inverse";
    private static final String KEY_ROUND_CORNER_WINDOW = "key_round_corner_window";
    private static final String KEY_WINDOW_BG_COLOR = "window_bg_color";
    private static final String KEY_STATUS_BAR_COLOR = "status_bar_color_%s";
    private static final String KEY_NAV_BAR_COLOR = "nav_bar_color_%s";
    private static final String KEY_LIGHT_STATUS_MODE = "light_status_mode";
    private static final String KEY_TAB_LAYOUT_BG_MODE = "tab_layout_bg_mode";
    private static final String KEY_TAB_LAYOUT_INDICATOR_MODE = "tab_layout_indicator_mode";
    private static final String KEY_NAV_VIEW_MODE = "nav_view_mode";
    private static final String KEY_BOTTOM_NAV_BG_MODE = "bottom_nav_bg_mode";
    private static final String KEY_BOTTOM_NAV_ICON_TEXT_MODE = "bottom_nav_icon_text_mode";
    private static final String KEY_CARD_VIEW_BG_COLOR = "card_view_bg_color";
    private static final String KEY_ICON_TITLE_ACTIVE_COLOR = "icon_title_active_color";
    private static final String KEY_ICON_TITLE_INACTIVE_COLOR = "icon_title_inactive_color";
    private static final String KEY_SNACKBAR_TEXT = "snackbar_text_color";
    private static final String KEY_SNACKBAR_ACTION_TEXT = "snackbar_action_text_color";

    private static Aesthetic instance;

    private final ArrayMap<String, List<ViewObservablePair>> backgroundSubscriberViews;
    private final ArrayMap<String, Integer> lastActivityThemes;
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    private final RxSharedPreferences rxPrefs;
    private CompositeDisposable backgroundSubscriptions;
    private CompositeDisposable defaultSubscriptions;
    private AppCompatActivity context;
    private boolean isResumed;

    private Aesthetic(AppCompatActivity context) {
        this.context = context;
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        rxPrefs = RxSharedPreferences.create(prefs);
        backgroundSubscriberViews = new ArrayMap<>(0);
        lastActivityThemes = new ArrayMap<>(2);
    }

    private static String key(@Nullable AppCompatActivity activity) {
        String key;
        if (activity instanceof AestheticKeyProvider) {
            key = ((AestheticKeyProvider) activity).key();
        } else {
            key = "default";
        }
        return key;
    }

    /**
     * Should be called before super.onCreate() in each Activity.
     */
    @NonNull
    public static Aesthetic attach(@NonNull AppCompatActivity activity) {
        if (instance == null) {
            instance = new Aesthetic(activity);
        }
        instance.isResumed = false;
        instance.context = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        Util.setInflaterFactory(inflater, activity);
        String activityThemeKey = String.format(KEY_ACTIVITY_THEME, key(activity));
        int latestActivityTheme = instance.prefs.getInt(activityThemeKey, 0);
        instance.lastActivityThemes.put(instance.context.getClass().getName(), latestActivityTheme);
        if (latestActivityTheme != 0) {
            activity.setTheme(latestActivityTheme);
        }
        return instance;
    }

    private static int getLastActivityTheme(@Nullable Context forContext) {
        if (forContext == null || instance == null) {
            return 0;
        }
        Integer lastActivityTheme = instance.lastActivityThemes.get(forContext.getClass().getName());
        if (lastActivityTheme == null) {
            return 0;
        }
        return lastActivityTheme;
    }

    @NonNull
    @CheckResult
    public static Aesthetic get() {
        if (instance == null) {
            throw new IllegalStateException("Not attached!");
        }
        return instance;
    }

    /**
     * Should be called in onPause() of each Activity.
     */
    public static void pause(@NonNull AppCompatActivity activity) {
        if (instance == null) {
            return;
        }
        instance.isResumed = false;
        if (instance.defaultSubscriptions != null) {
            instance.defaultSubscriptions.clear();
        }
        if (instance.backgroundSubscriptions != null) {
            instance.backgroundSubscriptions.clear();
        }
        if (activity.isFinishing()) {
            instance.backgroundSubscriberViews.remove(activity.getClass().getName());
            if (instance.context != null
                    && instance.context.getClass().getName().equals(activity.getClass().getName())) {
                instance.context = null;
            }
        }
    }

    /**
     * Should be called in onResume() of each Activity.
     */
    public static void resume(@NonNull AppCompatActivity activity) {
        if (instance == null) {
            return;
        }
        instance.context = activity;
        instance.isResumed = true;
        if (instance.defaultSubscriptions != null) {
            instance.defaultSubscriptions.clear();
        }
        instance.defaultSubscriptions = new CompositeDisposable();
        subscribeBackgroundListeners();
        instance.defaultSubscriptions.add(
                instance.colorPrimary()
                        .compose(Rx.distinctToMainThread())
                        .subscribe(color -> Util.setTaskDescriptionColor(instance.context, color), Rx.onErrorLogAndRethrow()));
        instance.defaultSubscriptions.add(
                instance.activityTheme()
                        .compose(Rx.distinctToMainThread())
                        .subscribe(themeId -> {
                            if (getLastActivityTheme(instance.context) == themeId) {
                                return;
                            }
                            instance.lastActivityThemes.put(instance.context.getClass().getName(), themeId);
                            instance.context.recreate();
                        }, Rx.onErrorLogAndRethrow()));
        instance.defaultSubscriptions.add(
                Observable.combineLatest(instance.colorStatusBar(), instance.lightStatusBarMode(), Pair::create)
                        .compose(Rx.distinctToMainThread())
                        .subscribe(result -> instance.invalidateStatusBar(), Rx.onErrorLogAndRethrow()));
        instance.defaultSubscriptions.add(
                instance.colorNavigationBar()
                        .compose(Rx.distinctToMainThread())
                        .subscribe(color -> Util.setNavBarColorCompat(instance.context, color), Rx.onErrorLogAndRethrow()));
        instance.defaultSubscriptions.add(
                instance.colorWindowBackground()
                        .compose(Rx.distinctToMainThread())
                        .subscribe(color -> instance.context.getWindow().setBackgroundDrawable(new ColorDrawable(color)), Rx.onErrorLogAndRethrow()));
//        instance.defaultSubscriptions.add(
//                instance.roundCornerWindow()
//                        .compose(Rx.distinctToMainThread())
//                        .subscribe(radius -> Util.roundCornerWindow(instance.context, radius), Rx.onErrorLogAndRethrow()));
        if (MaterialDialogsUtil.shouldSupport()) {
            instance.defaultSubscriptions.add(MaterialDialogsUtil.observe(instance));
        }
    }

    /**
     * Returns true if this method has never been called before.
     */
    public static boolean isFirstTime() {
        boolean firstTime = instance.prefs.getBoolean(KEY_FIRST_TIME, true);
        instance.editor.putBoolean(KEY_FIRST_TIME, false).commit();
        return firstTime;
    }

    private static void subscribeBackgroundListeners() {
        if (instance.backgroundSubscriptions != null) {
            instance.backgroundSubscriptions.clear();
        }
        instance.backgroundSubscriptions = new CompositeDisposable();
        if (instance.backgroundSubscriberViews.size() > 0) {
            List<ViewObservablePair> pairs = instance.backgroundSubscriberViews.get(instance.context.getClass().getName());
            if (pairs != null) {
                for (ViewObservablePair pair : pairs) {
                    instance.backgroundSubscriptions.add(
                            pair.observable()
                                    .compose(Rx.distinctToMainThread())
                                    .subscribeWith(ViewBackgroundSubscriber.create(pair.view())));
                }
            }
        }
    }

    void addBackgroundSubscriber(@NonNull View view, @NonNull Observable<Integer> colorObservable) {
        List<ViewObservablePair> subscribers = backgroundSubscriberViews.get(instance.context.getClass().getName());
        if (subscribers == null) {
            subscribers = new ArrayList<>(1);
            backgroundSubscriberViews.put(instance.context.getClass().getName(), subscribers);
        }
        subscribers.add(ViewObservablePair.create(view, colorObservable));
        if (isResumed) {
            instance.backgroundSubscriptions.add(
                    colorObservable.compose(Rx.distinctToMainThread())
                            .subscribeWith(ViewBackgroundSubscriber.create(view)));
        }
    }

    private void invalidateStatusBar() {
        String key = String.format(KEY_STATUS_BAR_COLOR, key(context));
        final int color = prefs.getInt(key, Util.resolveColor(context, R.attr.colorPrimaryDark));
        ViewGroup rootView = Util.getRootView(context);
        if (rootView instanceof DrawerLayout) {
            // Color is set to DrawerLayout, Activity gets transparent status bar
            Util.setLightStatusBarCompat(context, false);
            Util.setStatusBarColorCompat(context, ContextCompat.getColor(context, android.R.color.transparent));
            ((DrawerLayout) rootView).setStatusBarBackgroundColor(color);
        } else {
            Util.setStatusBarColorCompat(context, color);
        }
        final int mode = prefs.getInt(KEY_LIGHT_STATUS_MODE, AutoSwitchMode.AUTO);
        switch (mode) {
            case AutoSwitchMode.OFF:
                Util.setLightStatusBarCompat(context, false);
                break;
            case AutoSwitchMode.ON:
                Util.setLightStatusBarCompat(context, true);
                break;
            default:
                Util.setLightStatusBarCompat(context, Util.isColorLight(color));
                break;
        }
    }

    @CheckResult
    public Aesthetic activityTheme(@StyleRes int theme) {
        String key = String.format(KEY_ACTIVITY_THEME, key(context));
        editor.putInt(key, theme);
        return this;
    }

    @CheckResult
    public Observable<Integer> activityTheme() {
        String key = String.format(KEY_ACTIVITY_THEME, key(context));
        return rxPrefs.getInteger(key, 0)
                .asObservable()
                .filter(next -> next != 0 && next != getLastActivityTheme(instance.context));
    }

    @CheckResult
    public Aesthetic isDark(boolean isDark) {
        editor.putBoolean(KEY_IS_DARK, isDark).commit();
        return this;
    }

    @CheckResult
    public Observable<Boolean> isDark() {
        return rxPrefs.getBoolean(KEY_IS_DARK, false).asObservable();
    }

    @CheckResult
    public Aesthetic colorPrimary(@ColorInt int color) {
        // needs to be committed immediately so that for statusBarColorAuto() and other auto methods
        editor.putInt(KEY_PRIMARY_COLOR, color).commit();
        return this;
    }

    @CheckResult
    public Aesthetic colorPrimaryRes(@ColorRes int color) {
        return colorPrimary(ContextCompat.getColor(context, color));
    }

    @CheckResult
    public Observable<Integer> colorPrimary() {
        return rxPrefs.getInteger(KEY_PRIMARY_COLOR, Util.resolveColor(context, R.attr.colorPrimary))
                .asObservable();
    }

    @CheckResult
    public Aesthetic colorPrimaryDark(@ColorInt int color) {
        // needs to be committed immediately so that for statusBarColorAuto() and other auto methods
        editor.putInt(KEY_PRIMARY_DARK_COLOR, color).commit();
        return this;
    }

    @CheckResult
    public Aesthetic colorPrimaryDarkRes(@ColorRes int color) {
        return colorPrimaryDark(ContextCompat.getColor(context, color));
    }

    @CheckResult
    public Observable<Integer> colorPrimaryDark() {
        return rxPrefs.getInteger(KEY_PRIMARY_DARK_COLOR, Util.resolveColor(context, R.attr.colorPrimaryDark))
                .asObservable();
    }

    @CheckResult
    public Aesthetic colorAccent(@ColorInt int color) {
        editor.putInt(KEY_ACCENT_COLOR, color).commit();
        return this;
    }

    @CheckResult
    public Aesthetic colorAccentRes(@ColorRes int color) {
        return colorAccent(ContextCompat.getColor(context, color));
    }

    @CheckResult
    public Observable<Integer> colorAccent() {
        return rxPrefs.getInteger(KEY_ACCENT_COLOR, Util.resolveColor(context, R.attr.colorAccent))
                .asObservable();
    }

    @CheckResult
    public Aesthetic textColorPrimary(@ColorInt int color) {
        editor.putInt(KEY_PRIMARY_TEXT_COLOR, color);
        return this;
    }

    @CheckResult
    public Aesthetic textColorPrimaryRes(@ColorRes int color) {
        return textColorPrimary(ContextCompat.getColor(context, color));
    }

    @CheckResult
    public Observable<Integer> textColorPrimary() {
        return rxPrefs.getInteger(KEY_PRIMARY_TEXT_COLOR, Util.resolveColor(context, android.R.attr.textColorPrimary))
                .asObservable();
    }

    @CheckResult
    public Aesthetic textColorSecondary(@ColorInt int color) {
        editor.putInt(KEY_SECONDARY_TEXT_COLOR, color);
        return this;
    }

    @CheckResult
    public Aesthetic textColorSecondaryRes(@ColorRes int color) {
        return textColorSecondary(ContextCompat.getColor(context, color));
    }

    @CheckResult
    public Observable<Integer> textColorSecondary() {
        return rxPrefs.getInteger(KEY_SECONDARY_TEXT_COLOR, Util.resolveColor(context, android.R.attr.textColorSecondary))
                .asObservable();
    }

    @CheckResult
    public Aesthetic textColorPrimaryInverse(@ColorInt int color) {
        editor.putInt(KEY_PRIMARY_TEXT_INVERSE_COLOR, color);
        return this;
    }

    @CheckResult
    public Aesthetic textColorPrimaryInverseRes(@ColorRes int color) {
        return textColorPrimaryInverse(ContextCompat.getColor(context, color));
    }

    @CheckResult
    public Observable<Integer> textColorPrimaryInverse() {
        return rxPrefs.getInteger(KEY_PRIMARY_TEXT_INVERSE_COLOR, Util.resolveColor(context, android.R.attr.textColorPrimaryInverse))
                .asObservable();
    }

    @CheckResult
    public Aesthetic textColorSecondaryInverse(@ColorInt int color) {
        editor.putInt(KEY_SECONDARY_TEXT_INVERSE_COLOR, color);
        return this;
    }

    @CheckResult
    public Aesthetic textColorSecondaryInverseRes(@ColorRes int color) {
        return textColorSecondaryInverse(ContextCompat.getColor(context, color));
    }

    @CheckResult
    public Observable<Integer> textColorSecondaryInverse() {
        return rxPrefs.getInteger(KEY_SECONDARY_TEXT_INVERSE_COLOR, Util.resolveColor(context, android.R.attr.textColorSecondaryInverse))
                .asObservable();
    }

    @CheckResult
    public Aesthetic roundCornerWindow(int radius) {
        editor.putInt(KEY_ROUND_CORNER_WINDOW, radius);
        return this;
    }

    @CheckResult
    public Observable<Integer> roundCornerWindow() {
        return rxPrefs.getInteger(KEY_ROUND_CORNER_WINDOW, 0)
                .asObservable();
    }

    @CheckResult
    public Aesthetic colorWindowBackground(@ColorInt int color) {
        editor.putInt(KEY_WINDOW_BG_COLOR, color).commit();
        return this;
    }

    @CheckResult
    public Aesthetic colorWindowBackgroundRes(@ColorRes int color) {
        return colorWindowBackground(ContextCompat.getColor(context, color));
    }

    @CheckResult
    public Observable<Integer> colorWindowBackground() {
        return rxPrefs.getInteger(KEY_WINDOW_BG_COLOR, Util.resolveColor(context, android.R.attr.windowBackground))
                .asObservable();
    }

    @CheckResult
    public Aesthetic colorStatusBar(@ColorInt int color) {
        String key = String.format(KEY_STATUS_BAR_COLOR, key(context));
        editor.putInt(key, color);
        return this;
    }

    @CheckResult
    public Aesthetic colorStatusBarRes(@ColorRes int color) {
        return colorStatusBar(ContextCompat.getColor(context, color));
    }

    @CheckResult
    public Aesthetic colorStatusBarAuto(@FloatRange(from = 0.0f, to = 2.0f) float darken) {
        String key = String.format(KEY_STATUS_BAR_COLOR, key(context));
        editor.putInt(key, Util.shiftColor(prefs.getInt(KEY_PRIMARY_COLOR, Util.resolveColor(context, R.attr.colorPrimary)), darken));
        return this;
    }

    @CheckResult
    public Aesthetic colorStatusBarAuto() {
        String key = String.format(KEY_STATUS_BAR_COLOR, key(context));
        editor.putInt(key, Util.darkenColor(prefs.getInt(KEY_PRIMARY_COLOR, Util.resolveColor(context, R.attr.colorPrimary))));
        return this;
    }

    @CheckResult
    public Observable<Integer> colorStatusBar() {
        return colorPrimaryDark()
                .flatMap(primaryDarkColor -> {
                    String key = String.format(KEY_STATUS_BAR_COLOR, key(context));
                    return rxPrefs.getInteger(key, primaryDarkColor).asObservable();
                });
    }

    @CheckResult
    public Aesthetic colorNavigationBar(@ColorInt int color) {
        String key = String.format(KEY_NAV_BAR_COLOR, key(context));
        editor.putInt(key, color);
        return this;
    }

    @CheckResult
    public Aesthetic colorNavigationBarRes(@ColorRes int color) {
        return colorNavigationBar(ContextCompat.getColor(context, color));
    }

    @CheckResult
    public Aesthetic colorNavigationBarAuto() {
        int color = prefs.getInt(KEY_PRIMARY_COLOR, Util.resolveColor(context, R.attr.colorPrimary));
        String key = String.format(KEY_NAV_BAR_COLOR, key(context));
        editor.putInt(key, Util.isColorLight(color) ? Color.BLACK : color);
        return this;
    }

    @CheckResult
    public Observable<Integer> colorNavigationBar() {
        String key = String.format(KEY_NAV_BAR_COLOR, key(context));
        return rxPrefs.getInteger(key, Color.BLACK).asObservable();
    }

    @CheckResult
    public Aesthetic lightStatusBarMode(@AutoSwitchMode int mode) {
        editor.putInt(KEY_LIGHT_STATUS_MODE, mode);
        return this;
    }

    @CheckResult
    public Observable<Integer> lightStatusBarMode() {
        return rxPrefs.getInteger(KEY_LIGHT_STATUS_MODE, AutoSwitchMode.AUTO).asObservable();
    }

    @CheckResult
    public Aesthetic tabLayoutIndicatorMode(@TabLayoutIndicatorMode int mode) {
        editor.putInt(KEY_TAB_LAYOUT_INDICATOR_MODE, mode).commit();
        return this;
    }

    @CheckResult
    public Observable<Integer> tabLayoutIndicatorMode() {
        return rxPrefs.getInteger(KEY_TAB_LAYOUT_INDICATOR_MODE, TabLayoutIndicatorMode.ACCENT)
                .asObservable();
    }

    @CheckResult
    public Aesthetic tabLayoutBackgroundMode(@TabLayoutBgMode int mode) {
        editor.putInt(KEY_TAB_LAYOUT_BG_MODE, mode).commit();
        return this;
    }

    @CheckResult
    public Observable<Integer> tabLayoutBackgroundMode() {
        return rxPrefs.getInteger(KEY_TAB_LAYOUT_BG_MODE, TabLayoutBgMode.PRIMARY).asObservable();
    }

    @CheckResult
    public Aesthetic navigationViewMode(@NavigationViewMode int mode) {
        editor.putInt(KEY_NAV_VIEW_MODE, mode).commit();
        return this;
    }

    @CheckResult
    public Observable<Integer> navigationViewMode() {
        return rxPrefs.getInteger(KEY_NAV_VIEW_MODE, NavigationViewMode.SELECTED_PRIMARY)
                .asObservable();
    }

    @CheckResult
    public Aesthetic bottomNavigationBackgroundMode(@BottomNavBgMode int mode) {
        editor.putInt(KEY_BOTTOM_NAV_BG_MODE, mode).commit();
        return this;
    }

    @CheckResult
    public Observable<Integer> bottomNavigationBackgroundMode() {
        return rxPrefs.getInteger(KEY_BOTTOM_NAV_BG_MODE, BottomNavBgMode.BLACK_WHITE_AUTO)
                .asObservable();
    }

    @CheckResult
    public Aesthetic bottomNavigationIconTextMode(@BottomNavIconTextMode int mode) {
        editor.putInt(KEY_BOTTOM_NAV_ICON_TEXT_MODE, mode).commit();
        return this;
    }

    @CheckResult
    public Observable<Integer> bottomNavigationIconTextMode() {
        return rxPrefs.getInteger(KEY_BOTTOM_NAV_ICON_TEXT_MODE, BottomNavIconTextMode.SELECTED_ACCENT)
                .asObservable();
    }

    @CheckResult
    public Observable<Integer> colorCardViewBackground() {
        return isDark()
                .flatMap(isDark -> rxPrefs.getInteger(KEY_CARD_VIEW_BG_COLOR, ContextCompat.getColor(context, isDark ? R.color.ate_cardview_bg_dark : R.color.ate_cardview_bg_light))
                        .asObservable());
    }

    @CheckResult
    public Aesthetic colorCardViewBackground(@ColorInt int color) {
        editor.putInt(KEY_CARD_VIEW_BG_COLOR, color);
        return this;
    }

    @CheckResult
    public Aesthetic colorCardViewBackgroundRes(@ColorRes int color) {
        return colorCardViewBackground(ContextCompat.getColor(context, color));
    }

    @CheckResult
    public Observable<ActiveInactiveColors> colorIconTitle(@Nullable Observable<Integer> backgroundObservable) {
        if (backgroundObservable == null) {
            backgroundObservable = Aesthetic.get().colorPrimary();
        }
        return backgroundObservable.flatMap(
                primaryColor -> {
                    final boolean isDark = !Util.isColorLight(primaryColor);
                    return Observable.zip(
                            rxPrefs.getInteger(KEY_ICON_TITLE_ACTIVE_COLOR, ContextCompat.getColor(context, isDark ? R.color.ate_icon_dark : R.color.ate_icon_light))
                                    .asObservable(),
                            rxPrefs.getInteger(KEY_ICON_TITLE_INACTIVE_COLOR, ContextCompat.getColor(context, isDark ? R.color.ate_icon_dark_inactive : R.color.ate_icon_light_inactive))
                                    .asObservable(), ActiveInactiveColors::create);
                });
    }

    @CheckResult
    public Aesthetic colorIconTitleActive(@ColorInt int color) {
        editor.putInt(KEY_ICON_TITLE_ACTIVE_COLOR, color);
        return this;
    }

    @CheckResult
    public Aesthetic colorIconTitleActiveRes(@ColorRes int color) {
        return colorIconTitleActive(ContextCompat.getColor(context, color));
    }

    @CheckResult
    public Aesthetic colorIconTitleInactive(@ColorInt int color) {
        editor.putInt(KEY_ICON_TITLE_INACTIVE_COLOR, color);
        return this;
    }

    @CheckResult
    public Aesthetic colorIconTitleInactiveRes(@ColorRes int color) {
        return colorIconTitleActive(ContextCompat.getColor(context, color));
    }

    @CheckResult
    public Observable<Integer> snackbarTextColor() {
        return isDark()
                .flatMap(isDark -> (isDark ? textColorPrimary() : textColorPrimaryInverse())
                        .flatMap(defaultTextColor ->
                                rxPrefs.getInteger(KEY_SNACKBAR_TEXT, defaultTextColor)
                                        .asObservable()));
    }

    @CheckResult
    public Aesthetic snackbarTextColor(@ColorInt int color) {
        editor.putInt(KEY_SNACKBAR_TEXT, color);
        return this;
    }

    @CheckResult
    public Aesthetic snackbarTextColorRes(@ColorRes int color) {
        return colorCardViewBackground(ContextCompat.getColor(context, color));
    }

    @CheckResult
    public Observable<Integer> snackbarActionTextColor() {
        return colorAccent()
                .flatMap(accentColor ->
                        rxPrefs.getInteger(KEY_SNACKBAR_ACTION_TEXT, accentColor).asObservable());
    }

    @CheckResult
    public Aesthetic snackbarActionTextColor(@ColorInt int color) {
        editor.putInt(KEY_SNACKBAR_ACTION_TEXT, color);
        return this;
    }

    @CheckResult
    public Aesthetic snackbarActionTextColorRes(@ColorRes int color) {
        return colorCardViewBackground(ContextCompat.getColor(context, color));
    }

    /**
     * Notifies all listening views that theme properties have been updated.
     */
    public void apply() {
        editor.commit();
    }
}
