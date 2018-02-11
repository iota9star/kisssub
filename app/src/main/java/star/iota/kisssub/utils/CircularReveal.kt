import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import io.codetail.animation.ViewAnimationUtils
import java.util.*

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

object CircularReveal {
    fun create(view: View) {
        val cx = if (Random().nextBoolean()) 0 else view.left + view.right
        val cy = if (Random().nextBoolean()) 0 else view.top + view.bottom
        val dx = Math.max(cx, view.width - cx)
        val dy = Math.max(cy, view.height - cy)
        val finalRadius = Math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
        val animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius)
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.duration = 720
        animator.start()
    }
}
