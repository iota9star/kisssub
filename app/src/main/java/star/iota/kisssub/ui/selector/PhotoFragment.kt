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

package star.iota.kisssub.ui.selector

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.fragment_photo_selector_preview.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseFragment
import star.iota.kisssub.glide.GlideApp


class PhotoFragment : BaseFragment() {
    override fun getBackgroundView(): ImageView = imageViewContentBackground
    override fun getMaskView(): View = viewMask
    override fun getContainerViewId(): Int = R.layout.fragment_photo_selector_preview

    override fun doSome() {
        photoView?.tag = null
        GlideApp.with(this)
                .load(arguments!!.getString(PHOTO_PATH, ""))
                .into(photoView)
    }

    companion object {
        private const val PHOTO_PATH = "photo_paths"
        fun newInstance(path: String): PhotoFragment {
            val fragment = PhotoFragment()
            val bundle = Bundle()
            bundle.putString(PHOTO_PATH, path)
            fragment.arguments = bundle
            return fragment
        }
    }

}
