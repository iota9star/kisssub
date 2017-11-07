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

import android.graphics.RectF

object MathUtils {

    fun truncate(f: Float, decimalPlaces: Int): Float {
        val decimalShift = Math.pow(10.0, decimalPlaces.toDouble()).toFloat()
        return Math.round(f * decimalShift) / decimalShift
    }

    fun haveSameAspectRatio(r1: RectF, r2: RectF): Boolean {
        val srcRectRatio = truncate(getRectRatio(r1), 3)
        val dstRectRatio = truncate(getRectRatio(r2), 3)
        return Math.abs(srcRectRatio - dstRectRatio) <= 0.01f
    }

    fun getRectRatio(rect: RectF): Float {
        return rect.width() / rect.height()
    }
}
