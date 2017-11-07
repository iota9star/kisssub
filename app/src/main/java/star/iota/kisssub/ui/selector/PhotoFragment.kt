package star.iota.kisssub.ui.selector

import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_photo_selector_preview.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseFragment
import star.iota.kisssub.glide.GlideApp


class PhotoFragment : BaseFragment() {
    override fun getContainerViewId(): Int = R.layout.fragment_photo_selector_preview

    override fun doSome() {
        photoView.tag = null
        GlideApp.with(this)
                .load(arguments!!.getString(PHOTO_PATH, ""))
                .into(photoView)
    }

    companion object {
        private val PHOTO_PATH = "photo_paths"
        fun newInstance(path: String): PhotoFragment {
            val fragment = PhotoFragment()
            val bundle = Bundle()
            bundle.putString(PHOTO_PATH, path)
            fragment.arguments = bundle
            return fragment
        }
    }

}
