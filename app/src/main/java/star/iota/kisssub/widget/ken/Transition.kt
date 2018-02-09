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
import android.view.animation.Interpolator

class Transition(
        private val sourceRect: RectF,
        val destinyRect: RectF,
        val duration: Long,
        private val mInterpolator: Interpolator) {

    private val mCurrentRect = RectF()
    private val mWidthDiff: Float
    private val mHeightDiff: Float
    private val mCenterXDiff: Float
    private val mCenterYDiff: Float

    init {
        if (!MathUtils.haveSameAspectRatio(sourceRect, destinyRect)) {
            throw IncompatibleRatioException()
        }
        mWidthDiff = destinyRect.width() - sourceRect.width()
        mHeightDiff = destinyRect.height() - sourceRect.height()
        mCenterXDiff = destinyRect.centerX() - sourceRect.centerX()
        mCenterYDiff = destinyRect.centerY() - sourceRect.centerY()
    }

    fun getInterpolatedRect(elapsedTime: Long): RectF {
        val elapsedTimeFraction = elapsedTime / duration.toFloat()
        val interpolationProgress = Math.min(elapsedTimeFraction, 1f)
        val interpolation = mInterpolator.getInterpolation(interpolationProgress)
        val currentWidth = sourceRect.width() + interpolation * mWidthDiff
        val currentHeight = sourceRect.height() + interpolation * mHeightDiff
        val currentCenterX = sourceRect.centerX() + interpolation * mCenterXDiff
        val currentCenterY = sourceRect.centerY() + interpolation * mCenterYDiff
        val left = currentCenterX - currentWidth / 2
        val top = currentCenterY - currentHeight / 2
        val right = left + currentWidth
        val bottom = top + currentHeight
        mCurrentRect.set(left, top, right, bottom)
        return mCurrentRect
    }

}
