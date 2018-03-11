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
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Random;

import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

public final class AestheticToast extends Toast {

    private final static String TAG = "AestheticToast";
    private LinearLayout mToastContent;
    private TextView mToastText;
    private ImageView mToastIcon;
    private ImageView mAppIcon;
    private Context mContext;
    private float mRadius = 1024f;

    private AestheticToast(Context context) {
        super(context);
        init(context);
    }

    public static AestheticToast builder(Context context) {
        return new AestheticToast(context);
    }

    private static boolean setFieldValue(Object object, String fieldName, Object newFieldValue) {
        Field field = getDeclaredField(object, fieldName);
        if (field != null) {
            try {
                int accessFlags = field.getModifiers();
                if (Modifier.isFinal(accessFlags)) {
                    Field modifiersField = Field.class.getDeclaredField("accessFlags");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                }
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                field.set(object, newFieldValue);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static Object getFieldValue(Object obj, final String fieldName) {
        Field field = getDeclaredField(obj, fieldName);
        return getFieldValue(obj, field);
    }

    private static Object getFieldValue(Object obj, Field field) {
        if (field != null) {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field.get(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static Field getDeclaredField(final Object obj, final String fieldName) {
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
            }
        }
        return null;
    }

    private void init(Context context) {
        this.mContext = context;
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.aesthetic_toast, null);
        this.mToastText = view.findViewById(R.id.text_view_toast_text);
        this.mToastIcon = view.findViewById(R.id.image_view_toast_icon);
        this.mAppIcon = view.findViewById(R.id.image_view_app_icon);
        this.mToastContent = view.findViewById(R.id.linear_layout_toast_content);
        this.setView(view);
    }

    public final AestheticToast setMessage(String msg) {
        this.mToastText.setText(msg);
        return this;
    }

    public final AestheticToast setMessage(@StringRes int msgRes) {
        this.mToastText.setText(msgRes);
        return this;
    }

    public final AestheticToast setToastIcon(Drawable drawable) {
        this.mToastIcon.setVisibility(View.VISIBLE);
        this.mToastIcon.setImageDrawable(drawable);
        return this;
    }

    public final AestheticToast setToastIcon(@DrawableRes int drawable) {
        this.mToastIcon.setVisibility(View.VISIBLE);
        this.mToastIcon.setImageResource(drawable);
        return this;
    }

    public final AestheticToast setToastIcon(Bitmap bitmap) {
        this.mToastIcon.setVisibility(View.VISIBLE);
        this.mToastIcon.setImageBitmap(bitmap);
        return this;
    }

    public final AestheticToast setAppIcon(Drawable drawable) {
        this.mAppIcon.setVisibility(View.VISIBLE);
        this.mAppIcon.setImageDrawable(drawable);
        return this;
    }

    public final AestheticToast setAppIcon(@DrawableRes int drawable) {
        this.mAppIcon.setVisibility(View.VISIBLE);
        this.mAppIcon.setImageResource(drawable);
        return this;
    }

    public final AestheticToast setAppIcon(Bitmap bitmap) {
        this.mAppIcon.setVisibility(View.VISIBLE);
        this.mAppIcon.setImageBitmap(bitmap);
        return this;
    }

    public final AestheticToast setRadius(float radius) {
        this.mRadius = radius;
        return this;
    }

    public final AestheticToast setRadius(@DimenRes int radius) {
        this.mRadius = mContext.getResources().getDimension(radius);
        return this;
    }

    public final AestheticToast showDuration(int duration) {
        this.setDuration(duration);
        return this;
    }

    @Override
    public final void show() {
        aesthetic();
        if (checkIfNeedToHack()) {
            tryToHack();
        }
        super.show();
    }

    private void aesthetic() {
        Aesthetic.get()
                .colorAccent()
                .take(1)
                .subscribe(color -> {
                    setToastColor(color);
                    setBackgroundDrawable(color);
                }, onErrorLogAndRethrow());
    }

    private void setToastColor(int color) {
        int contentColor = Util.titleColor(color);
        mToastText.setTextColor(contentColor);
        mToastIcon.setColorFilter(contentColor);
    }

    private void setBackgroundDrawable(int color) {
        GradientDrawable.Orientation[] orientations = {
                GradientDrawable.Orientation.TOP_BOTTOM,
                GradientDrawable.Orientation.TR_BL,
                GradientDrawable.Orientation.RIGHT_LEFT,
                GradientDrawable.Orientation.BR_TL,
                GradientDrawable.Orientation.BOTTOM_TOP,
                GradientDrawable.Orientation.BL_TR,
                GradientDrawable.Orientation.LEFT_RIGHT,
                GradientDrawable.Orientation.TL_BR
        };
        boolean isLight = Util.isColorLight(color);
        int primaryBgColor;
        int secondaryColor;
        int secondaryBgColor;
        if (isLight) {
            primaryBgColor = Util.shiftColor(color, 0.72f);
            secondaryColor = Util.shiftColor(color, 0.9f);
            secondaryBgColor = Util.shiftColor(primaryBgColor, 0.9f);
        } else {
            primaryBgColor = Util.shiftColor(color, 1.28f);
            secondaryColor = Util.shiftColor(color, 1.1f);
            secondaryBgColor = Util.shiftColor(primaryBgColor, 1.1f);
        }
        int[] colors1;
        int[] colors2;
        Random random = new Random();
        if (random.nextBoolean()) {
            colors1 = new int[]{color, secondaryColor};
            colors2 = new int[]{primaryBgColor, secondaryBgColor};
        } else {
            colors1 = new int[]{secondaryColor, color};
            colors2 = new int[]{secondaryBgColor, primaryBgColor};
        }
        Drawable drawable = createLayerDrawable(colors1, colors2, mRadius, orientations[random.nextInt(orientations.length)]);
        mToastContent.setBackground(drawable);
    }

    private Drawable createLayerDrawable(int[] colors1, int[] colors2, float radius, GradientDrawable.Orientation orientation) {
        GradientDrawable drawable1 = new GradientDrawable(orientation, colors1);
        GradientDrawable drawable2 = new GradientDrawable(orientation, colors2);
        drawable1.setCornerRadius(radius);
        drawable2.setCornerRadius(radius);
        Drawable[] layers = {drawable1, drawable2};
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        layerDrawable.setLayerInset(1, 0, 0, 0, mContext.getResources().getDimensionPixelOffset(R.dimen.v4dp));
        return layerDrawable;
    }

    private boolean checkIfNeedToHack() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1 || Build.VERSION.SDK_INT == Build.VERSION_CODES.N;
    }

    private void tryToHack() {
        try {
            Object mTN = getFieldValue(this, "mTN");
            if (mTN != null) {
                boolean isSuccess = false;

                //a hack to some device which use the code between android 6.0 and android 7.1.1
                Object rawShowRunnable = getFieldValue(mTN, "mShow");
                if (rawShowRunnable != null && rawShowRunnable instanceof Runnable) {
                    isSuccess = setFieldValue(mTN, "mShow", new InternalRunnable((Runnable) rawShowRunnable));
                }

                // hack to android 7.1.1,these cover 99% devices.
                if (!isSuccess) {
                    Object rawHandler = getFieldValue(mTN, "mHandler");
                    if (rawHandler != null && rawHandler instanceof Handler) {
                        isSuccess = setFieldValue(rawHandler, "mCallback", new InternalHandlerCallback((Handler) rawHandler));
                    }
                }

                if (!isSuccess) {
                    Log.e(TAG, "Toast tryToHack error.");
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private class InternalRunnable implements Runnable {
        private final Runnable mRunnable;

        InternalRunnable(Runnable mRunnable) {
            this.mRunnable = mRunnable;
        }

        @Override
        public void run() {
            try {
                this.mRunnable.run();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private class InternalHandlerCallback implements Handler.Callback {
        private final Handler mHandler;

        InternalHandlerCallback(Handler mHandler) {
            this.mHandler = mHandler;
        }

        @Override
        public boolean handleMessage(Message msg) {
            try {
                mHandler.handleMessage(msg);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
