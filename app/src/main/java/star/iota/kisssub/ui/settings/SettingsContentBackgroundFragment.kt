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
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_settings_content_background.*
import org.greenrobot.eventbus.EventBus
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseFragment
import star.iota.kisssub.eventbus.ChangeContentBackgroundEvent
import star.iota.kisssub.glide.GlideApp
import star.iota.kisssub.helper.ThemeHelper
import star.iota.kisssub.ui.selector.PhotoSelectorActivity
import star.iota.kisssub.utils.ToastUtils

class SettingsContentBackgroundFragment : BaseFragment(), View.OnClickListener {

    companion object {
        val TITLE = "title"
        fun newInstance(title: String): SettingsContentBackgroundFragment {
            val fragment = SettingsContentBackgroundFragment()
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
                ThemeHelper.setContentBanner(context!!, null)
                EventBus.getDefault().post(ChangeContentBackgroundEvent())
                finish()
            }
            R.id.buttonPreview -> {
                val path = textInputEditTextPath.text.toString().trim()
                if (path.isBlank()) {
                    ToastUtils.short(context!!, "路径不能为空")
                } else {
                    GlideApp.with(this)
                            .load(path)
                            .into(imageViewContentBackground)
                }
            }
            R.id.buttonSetting -> {
                val path = textInputEditTextPath.text.toString().trim()
                if (path.isBlank()) {
                    ToastUtils.short(context!!, "路径不能为空")
                } else {
                    ThemeHelper.setContentBanner(context!!, path)
                    EventBus.getDefault().post(ChangeContentBackgroundEvent())
                    finish()
                }
            }
            R.id.linearLayoutMaskColor -> showMaskColorSetting()
            R.id.linearLayoutMaskColorDark -> showMaskColorDarkSetting()
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

    override fun getBackgroundView(): ImageView = imageViewContentBackground
    override fun getMaskView(): View = viewMask

    override fun getContainerViewId(): Int = R.layout.fragment_settings_content_background

    override fun doSome() {
        setToolbarTitle(arguments!!.getString(TITLE))
        initEvent()
        initView()
    }

    private fun showMaskColorSetting() {
        ColorPickerDialogBuilder
                .with(context!!)
                .initialColor(ThemeHelper.getAccentColor(context!!))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener {
                    imageViewMaskColor.setColorFilter(it)
                    ThemeHelper.setContentMaskColor(context!!, it)
                    EventBus.getDefault().post(ChangeContentBackgroundEvent())
                }
                .build()
                .show()
    }

    private fun showMaskColorDarkSetting() {
        ColorPickerDialogBuilder
                .with(context!!)
                .initialColor(ThemeHelper.getAccentColor(context!!))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener {
                    imageViewMaskColorDark.setColorFilter(it)
                    ThemeHelper.setContentMaskColorDark(context!!, it)
                    EventBus.getDefault().post(ChangeContentBackgroundEvent())
                }
                .build()
                .show()
    }

    private fun initView() {
        imageViewMaskColor.setColorFilter(ThemeHelper.getContentMaskColor(context!!))
        imageViewMaskColorDark.setColorFilter(ThemeHelper.getContentMaskColorDark(context!!))
        val alpha = ThemeHelper.getContentMaskAlpha(context!!)
        seekBarAlpha.progress = (100 * alpha).toInt()
        viewMask.alpha = alpha
        seekBarAlpha.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                viewMask.alpha = (p1 / 100f)
                ThemeHelper.setContentMaskAlpha(context!!, p1 / 100f)
                EventBus.getDefault().post(ChangeContentBackgroundEvent())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
    }

    private fun initEvent() {
        buttonLocal.setOnClickListener(this)
        buttonPreview.setOnClickListener(this)
        buttonReset.setOnClickListener(this)
        buttonSetting.setOnClickListener(this)
        linearLayoutMaskColor.setOnClickListener(this)
        linearLayoutMaskColorDark.setOnClickListener(this)
    }

}
