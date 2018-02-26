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
package star.iota.kisssub.widget.ken

import android.graphics.RectF
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import java.util.*

class RandomTransitionGenerator @JvmOverloads constructor(transitionDuration: Long = DEFAULT_TRANSITION_DURATION.toLong(), transitionInterpolator: Interpolator = AccelerateDecelerateInterpolator()) : TransitionGenerator {

    private val mRandom = Random(System.currentTimeMillis())

    private var mTransitionDuration: Long = 0

    private var mTransitionInterpolator: Interpolator? = null

    private var mLastGenTrans: Transition? = null

    private var mLastDrawableBounds: RectF? = null

    init {
        setTransitionDuration(transitionDuration)
        setTransitionInterpolator(transitionInterpolator)
    }

    override fun generateNextTransition(drawableBounds: RectF?, viewport: RectF): Transition {
        val firstTransition = mLastGenTrans == null
        var drawableBoundsChanged = true
        var viewportRatioChanged = true

        val srcRect: RectF?
        var dstRect: RectF? = null

        if (!firstTransition) {
            dstRect = mLastGenTrans!!.destinyRect
            drawableBoundsChanged = drawableBounds != mLastDrawableBounds
            viewportRatioChanged = !MathUtils.haveSameAspectRatio(dstRect, viewport)
        }

        srcRect = if (dstRect == null || drawableBoundsChanged || viewportRatioChanged) {
            generateRandomRect(drawableBounds, viewport)
        } else {
            dstRect
        }
        dstRect = generateRandomRect(drawableBounds, viewport)

        mLastGenTrans = Transition(srcRect, dstRect, mTransitionDuration,
                mTransitionInterpolator!!)

        mLastDrawableBounds = RectF(drawableBounds)

        return mLastGenTrans as Transition
    }

    private fun generateRandomRect(drawableBounds: RectF?, viewportRect: RectF): RectF {
        val drawableRatio = MathUtils.getRectRatio(drawableBounds!!)
        val viewportRectRatio = MathUtils.getRectRatio(viewportRect)
        val maxCrop: RectF

        maxCrop = if (drawableRatio > viewportRectRatio) {
            val r = drawableBounds.height() / viewportRect.height() * viewportRect.width()
            val b = drawableBounds.height()
            RectF(0f, 0f, r, b)
        } else {
            val r = drawableBounds.width()
            val b = drawableBounds.width() / viewportRect.width() * viewportRect.height()
            RectF(0f, 0f, r, b)
        }

        val randomFloat = MathUtils.truncate(mRandom.nextFloat(), 2)
        val factor = MIN_RECT_FACTOR + (1 - MIN_RECT_FACTOR) * randomFloat

        val width = factor * maxCrop.width()
        val height = factor * maxCrop.height()
        val widthDiff = (drawableBounds.width() - width).toInt()
        val heightDiff = (drawableBounds.height() - height).toInt()
        val left = if (widthDiff > 0) mRandom.nextInt(widthDiff) else 0
        val top = if (heightDiff > 0) mRandom.nextInt(heightDiff) else 0
        return RectF(left.toFloat(), top.toFloat(), left + width, top + height)
    }


    private fun setTransitionDuration(transitionDuration: Long) {
        mTransitionDuration = transitionDuration
    }

    private fun setTransitionInterpolator(interpolator: Interpolator) {
        mTransitionInterpolator = interpolator
    }

    companion object {
        const val DEFAULT_TRANSITION_DURATION = 10000
        private const val MIN_RECT_FACTOR = 0.75f
    }
}
