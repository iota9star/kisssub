/*
 *
 *  *    Copyright 2017. iota9star
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

package star.iota.kisssub.ui.settings

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_settings_dynamic_background.*
import org.greenrobot.eventbus.EventBus
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseFragment
import star.iota.kisssub.eventbus.ChangeContentBackgroundEvent
import star.iota.kisssub.eventbus.ChangeDynamicBackgroundEvent
import star.iota.kisssub.glide.GlideApp
import star.iota.kisssub.ui.selector.PhotoSelectorActivity
import star.iota.kisssub.utils.ToastUtils

class SettingsDynamicBackgroundFragment : BaseFragment(), View.OnClickListener {

    companion object {
        val TITLE = "title"
        fun newInstance(title: String): SettingsDynamicBackgroundFragment {
            val fragment = SettingsDynamicBackgroundFragment()
            val bundle = Bundle()
            bundle.putString(TITLE, title)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonLocal -> {
                val intent = Intent(activity!!, PhotoSelectorActivity::class.java)
                intent.putExtra(PhotoSelectorActivity.SOURCE_ACTIVITY, activity!!::class.java.canonicalName)
                intent.putExtra(PhotoSelectorActivity.LIMIT_SELECT_PHOTO_COUNT, 1)
                startActivityForResult(intent, PhotoSelectorActivity.REQUEST_CODE)
            }
            R.id.buttonReset -> {
                ThemeHelper.setDynamicBanner(context!!, null)
                EventBus.getDefault().post(ChangeContentBackgroundEvent())
                finish()
            }
            R.id.buttonPreview -> {
                val path = textInputEditTextPath.text.toString().trim()
                if (path.isBlank()) {
                    ToastUtils.short(context!!, "路径不能为空")
                } else {
                    loadImage(path)
                }
            }
            R.id.buttonSetting -> {
                val path = textInputEditTextPath.text.toString().trim()
                if (path.isBlank()) {
                    ToastUtils.short(context!!, "路径不能为空")
                } else {
                    ThemeHelper.setDynamicBanner(context!!, path)
                    EventBus.getDefault().post(ChangeDynamicBackgroundEvent())
                    finish()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PhotoSelectorActivity.REQUEST_CODE) {
            if (resultCode == PhotoSelectorActivity.RESULT_CODE_FOR_OK) {
                val photos = data.getStringArrayListExtra(PhotoSelectorActivity.SELECTED_STRING_ARRAY_LIST_PHOTOS) ?: return
                photos.forEach {
                    textInputEditTextPath.setText(it)
                }
            } else if (resultCode == PhotoSelectorActivity.RESULT_CODE_FOR_BACK) {
            }
        }
    }

    private fun loadImage(path: String) {
        GlideApp.with(context!!)
                .load(path)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        ToastUtils.short(context!!, "加载错误，请重新选择")
                        return true
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        return true
                    }
                })
                .into(kenBurnsView)
    }

    override fun getBackgroundView(): ImageView = imageViewContentBackground
    override fun getMaskView(): View = viewMask

    override fun getContainerViewId(): Int = R.layout.fragment_settings_dynamic_background

    override fun doSome() {
        setToolbarTitle(arguments!!.getString(TITLE))
        initEvent()
        loadImage(ThemeHelper.getDynamicBanner(activity!!))
    }

    private fun initEvent() {
        buttonLocal.setOnClickListener(this)
        buttonPreview.setOnClickListener(this)
        buttonReset.setOnClickListener(this)
        buttonSetting.setOnClickListener(this)
    }

}