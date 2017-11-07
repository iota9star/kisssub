/*
 * Copyright 2014 Flavio Faria
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package star.iota.kisssub.widget.ken

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

import com.afollestad.aesthetic.AestheticImageView

class KenBurnsView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : AestheticImageView(context, attrs, defStyle) {

    private val mMatrix = Matrix()

    private var mTransGen: TransitionGenerator = RandomTransitionGenerator()

    private var mTransitionListener: TransitionListener? = null

    private var mCurrentTrans: Transition? = null

    private val mViewportRect = RectF()

    private var mDrawableRect: RectF? = null

    private var mElapsedTime: Long = 0

    private var mLastFrameTime: Long = 0

    private var mPaused: Boolean = false

    init {
        super.setScaleType(ImageView.ScaleType.MATRIX)
    }


    override fun setScaleType(scaleType: ImageView.ScaleType) {
    }


    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == View.VISIBLE) resume()
        else pause()
    }


    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        handleImageChange()
    }


    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        handleImageChange()
    }


    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        handleImageChange()
    }


    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        handleImageChange()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        restart()
    }


    override fun onDraw(canvas: Canvas) {
        val d = drawable
        if (!mPaused && d != null) {
            if (mDrawableRect!!.isEmpty) {
                updateDrawableBounds()
            } else if (hasBounds()) {
                if (mCurrentTrans == null) {
                    startNewTransition()
                }
                if (mCurrentTrans?.destinyRect != null) {
                    mElapsedTime += System.currentTimeMillis() - mLastFrameTime
                    val currentRect = mCurrentTrans!!.getInterpolatedRect(mElapsedTime)
                    val widthScale = mDrawableRect!!.width() / currentRect.width()
                    val heightScale = mDrawableRect!!.height() / currentRect.height()
                    val currRectToDrwScale = Math.min(widthScale, heightScale)
                    val vpWidthScale = mViewportRect.width() / currentRect.width()
                    val vpHeightScale = mViewportRect.height() / currentRect.height()
                    val currRectToVpScale = Math.min(vpWidthScale, vpHeightScale)
                    val totalScale = currRectToDrwScale * currRectToVpScale
                    val translX = totalScale * (mDrawableRect!!.centerX() - currentRect.left)
                    val translY = totalScale * (mDrawableRect!!.centerY() - currentRect.top)
                    mMatrix.reset()
                    mMatrix.postTranslate(-mDrawableRect!!.width() / 2, -mDrawableRect!!.height() / 2)
                    mMatrix.postScale(totalScale, totalScale)
                    mMatrix.postTranslate(translX, translY)
                    imageMatrix = mMatrix
                    if (mElapsedTime >= mCurrentTrans!!.duration) {
                        fireTransitionEnd(mCurrentTrans)
                        startNewTransition()
                    }
                    fireTransitionEnd(mCurrentTrans)
                }
            }
            mLastFrameTime = System.currentTimeMillis()
            postInvalidateDelayed(FRAME_DELAY)
        }
        super.onDraw(canvas)
    }

    private fun startNewTransition() {
        if (!hasBounds()) {
            return
        }
        mCurrentTrans = mTransGen.generateNextTransition(mDrawableRect, mViewportRect)
        mElapsedTime = 0
        mLastFrameTime = System.currentTimeMillis()
        fireTransitionStart(mCurrentTrans)
    }

    private fun restart() {
        val width = width
        val height = height
        if (width == 0 || height == 0) {
            return
        }
        updateViewport(width.toFloat(), height.toFloat())
        updateDrawableBounds()
        startNewTransition()
    }

    private fun hasBounds(): Boolean {
        return !mViewportRect.isEmpty
    }

    private fun fireTransitionStart(transition: Transition?) {
        if (mTransitionListener != null && transition != null) {
            mTransitionListener!!.onTransitionStart(transition)
        }
    }

    private fun fireTransitionEnd(transition: Transition?) {
        if (mTransitionListener != null && transition != null) {
            mTransitionListener!!.onTransitionEnd(transition)
        }
    }

    fun setTransitionGenerator(transgen: TransitionGenerator) {
        mTransGen = transgen
        startNewTransition()
    }

    private fun updateViewport(width: Float, height: Float) {
        mViewportRect.set(0f, 0f, width, height)
    }

    private fun updateDrawableBounds() {
        if (mDrawableRect == null) {
            mDrawableRect = RectF()
        }
        val d = drawable
        if (d != null && d.intrinsicHeight > 0 && d.intrinsicWidth > 0) {
            mDrawableRect!!.set(0f, 0f, d.intrinsicWidth.toFloat(), d.intrinsicHeight.toFloat())
        }
    }

    private fun handleImageChange() {
        updateDrawableBounds()
        startNewTransition()
    }


    fun setTransitionListener(transitionListener: TransitionListener) {
        mTransitionListener = transitionListener
    }

    private fun pause() {
        mPaused = true
    }

    private fun resume() {
        mPaused = false
        mLastFrameTime = System.currentTimeMillis()
        invalidate()
    }

    interface TransitionListener {

        fun onTransitionStart(transition: Transition)

        fun onTransitionEnd(transition: Transition)
    }

    companion object {
        private val FRAME_DELAY = (1000 / 60).toLong()
    }
}
