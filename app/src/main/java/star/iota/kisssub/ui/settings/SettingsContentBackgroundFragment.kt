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

package star.iota.kisssub.ui.settings

import android.content.Intent
import android.os.Bundle
import android.support.annotation.ColorInt
import android.view.View
import android.widget.ImageView
import com.afollestad.materialdialogs.color.ColorChooserDialog
import kotlinx.android.synthetic.main.fragment_settings_content_background.*
import org.greenrobot.eventbus.EventBus
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseFragment
import star.iota.kisssub.eventbus.ChangeContentBackgroundEvent
import star.iota.kisssub.glide.GlideApp
import star.iota.kisssub.helper.ThemeHelper
import star.iota.kisssub.ui.selector.PhotoSelectorActivity
import star.iota.kisssub.utils.ToastUtils

class SettingsContentBackgroundFragment : BaseFragment(), View.OnClickListener, ColorChooserDialog.ColorCallback {
    override fun onColorSelection(dialog: ColorChooserDialog, selectedColor: Int) {
        when (dialog.tag()) {
            ThemeHelper.THEME_CONTENT_MASK_COLOR -> {
                imageViewMaskColor?.setColorFilter(selectedColor)
                ThemeHelper.setContentMaskColor(context!!, selectedColor)
                EventBus.getDefault().post(ChangeContentBackgroundEvent())
            }
            ThemeHelper.THEME_CONTENT_MASK_COLOR_DARK -> {
                imageViewMaskColorDark?.setColorFilter(selectedColor)
                ThemeHelper.setContentMaskColorDark(context!!, selectedColor)
                EventBus.getDefault().post(ChangeContentBackgroundEvent())
            }
        }
    }

    override fun onColorChooserDismissed(dialog: ColorChooserDialog) {
    }

    companion object {
        const val TITLE = "title"
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
                val path = textInputEditTextPath?.text.toString().trim()
                if (path.isBlank()) {
                    ToastUtils.short(context!!, "路径不能为空")
                } else {
                    GlideApp.with(this)
                            .load(path)
                            .into(imageViewContentBackground)
                }
            }
            R.id.buttonSetting -> {
                val path = textInputEditTextPath?.text.toString().trim()
                if (path.isBlank()) {
                    ToastUtils.short(context!!, "路径不能为空")
                } else {
                    ThemeHelper.setContentBanner(context!!, path)
                    EventBus.getDefault().post(ChangeContentBackgroundEvent())
                    finish()
                }
            }
            R.id.linearLayoutMaskColor -> showColorDialog(ThemeHelper.getContentMaskColor(context!!), ThemeHelper.THEME_CONTENT_MASK_COLOR)
            R.id.linearLayoutMaskColorDark -> showColorDialog(ThemeHelper.getContentMaskColorDark(context!!), ThemeHelper.THEME_CONTENT_MASK_COLOR_DARK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PhotoSelectorActivity.REQUEST_CODE) {
            if (resultCode == PhotoSelectorActivity.RESULT_CODE_FOR_OK) {
                val photos = data.getStringArrayListExtra(PhotoSelectorActivity.SELECTED_STRING_ARRAY_LIST_PHOTOS)
                        ?: return
                photos.forEach {
                    textInputEditTextPath?.setText(it)
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

    private fun showColorDialog(@ColorInt color: Int, tag: String) {
        ColorChooserDialog.Builder(context!!, R.string.label_color_dialog)
                .tag(tag)
                .titleSub(R.string.label_color_choose)
                .preselect(color)
                .doneButton(R.string.action_done)
                .cancelButton(R.string.action_cancel)
                .backButton(R.string.action_back)
                .customButton(R.string.action_custom)
                .presetsButton(R.string.action_preset)
                .allowUserColorInput(true)
                .allowUserColorInputAlpha(true)
                .dynamicButtonColor(false)
                .show(childFragmentManager)
    }

    private fun initView() {
        imageViewMaskColor?.setColorFilter(ThemeHelper.getContentMaskColor(context!!))
        imageViewMaskColorDark?.setColorFilter(ThemeHelper.getContentMaskColorDark(context!!))
    }

    private fun initEvent() {
        buttonLocal?.setOnClickListener(this)
        buttonPreview?.setOnClickListener(this)
        buttonReset?.setOnClickListener(this)
        buttonSetting?.setOnClickListener(this)
        linearLayoutMaskColor?.setOnClickListener(this)
        linearLayoutMaskColorDark?.setOnClickListener(this)
    }

}
