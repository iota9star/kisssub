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

import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.materialdialogs.color.ColorChooserDialog
import kotlinx.android.synthetic.main.fragment_settings_theme_color.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseFragment
import star.iota.kisssub.helper.ThemeHelper


class SettingsThemeColorFragment : BaseFragment(), View.OnClickListener, ColorChooserDialog.ColorCallback {

    override fun onColorSelection(dialog: ColorChooserDialog, selectedColor: Int) {
        when (dialog.tag()) {
            ThemeHelper.THEME_PRIMARY_COLOR -> {
                imageViewPrimaryColor?.setColorFilter(selectedColor)
                ThemeHelper.setPrimaryColor(context!!, selectedColor)
                Aesthetic.get()
                        .colorPrimary(selectedColor)
                        .colorStatusBarAuto()
                        .colorNavigationBarAuto()
                        .apply()
                adapter.removeSelectedStatus()
            }
            ThemeHelper.THEME_ACCENT_COLOR -> {
                imageViewAccentColor?.setColorFilter(selectedColor)
                ThemeHelper.setAccentColor(context!!, selectedColor)
                Aesthetic.get()
                        .colorAccent(selectedColor)
                        .colorStatusBarAuto()
                        .colorNavigationBarAuto()
                        .apply()
            }
            ThemeHelper.THEME_PRIMARY_TEXT_COLOR -> {
                imageViewPrimaryTextColor?.setColorFilter(selectedColor)
                ThemeHelper.setPrimaryTextColor(context!!, selectedColor)
                Aesthetic.get()
                        .isDark
                        .take(1)
                        .subscribe {
                            if (!it) {
                                Aesthetic.get()
                                        .textColorPrimary(selectedColor)
                                        .apply()
                            }
                        }
            }
            ThemeHelper.THEME_PRIMARY_TEXT_COLOR_DARK -> {
                imageViewPrimaryTextColorDark?.setColorFilter(selectedColor)
                ThemeHelper.setPrimaryTextColorDark(context!!, selectedColor)
                Aesthetic.get()
                        .isDark
                        .take(1)
                        .subscribe {
                            if (it) {
                                Aesthetic.get()
                                        .textColorPrimary(selectedColor)
                                        .apply()
                            }
                        }
            }
            ThemeHelper.THEME_SECONDARY_TEXT_COLOR -> {
                imageViewSecondaryTextColor?.setColorFilter(selectedColor)
                ThemeHelper.setSecondaryTextColor(context!!, selectedColor)
                Aesthetic.get()
                        .isDark
                        .take(1)
                        .subscribe {
                            if (!it) {
                                Aesthetic.get()
                                        .textColorSecondary(selectedColor)
                                        .apply()
                            }
                        }
            }
            ThemeHelper.THEME_SECONDARY_TEXT_COLOR_DARK -> {
                imageViewSecondaryTextColorDark?.setColorFilter(selectedColor)
                ThemeHelper.setSecondaryTextColorDark(context!!, selectedColor)
                Aesthetic.get()
                        .isDark
                        .take(1)
                        .subscribe {
                            if (it) {
                                Aesthetic.get()
                                        .textColorSecondary(selectedColor)
                                        .apply()
                            }
                        }
            }
        }
    }

    override fun onColorChooserDismissed(dialog: ColorChooserDialog) {
    }

    companion object {
        const val TITLE = "title"
        fun newInstance(title: String): SettingsThemeColorFragment {
            val fragment = SettingsThemeColorFragment()
            val bundle = Bundle()
            bundle.putString(SettingsDynamicBackgroundFragment.TITLE, title)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.linearLayoutPrimaryColor -> {
                showColorDialog(ThemeHelper.getPrimaryColor(context!!), ThemeHelper.THEME_PRIMARY_COLOR)
            }
            R.id.linearLayoutAccentColor -> {
                showColorDialog(ThemeHelper.getAccentColor(context!!), ThemeHelper.THEME_ACCENT_COLOR)
            }
            R.id.linearLayoutPrimaryTextColor -> {
                showColorDialog(ThemeHelper.getPrimaryTextColor(context!!), ThemeHelper.THEME_PRIMARY_TEXT_COLOR)
            }
            R.id.linearLayoutPrimaryTextColorDark -> {
                showColorDialog(ThemeHelper.getPrimaryTextColorDark(context!!), ThemeHelper.THEME_PRIMARY_TEXT_COLOR_DARK)
            }
            R.id.linearLayoutSecondaryTextColor -> {
                showColorDialog(ThemeHelper.getSecondaryTextColor(context!!), ThemeHelper.THEME_SECONDARY_TEXT_COLOR)
            }
            R.id.linearLayoutSecondaryTextColorDark -> {
                showColorDialog(ThemeHelper.getSecondaryTextColorDark(context!!), ThemeHelper.THEME_SECONDARY_TEXT_COLOR_DARK)
            }
        }
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

    override fun getBackgroundView(): ImageView = imageViewContentBackground
    override fun getMaskView(): View = viewMask
    override fun getContainerViewId(): Int = R.layout.fragment_settings_theme_color

    override fun doSome() {
        setToolbarTitle(arguments!!.getString(TITLE))
        initEvent()
        bindColor()
        initRecyclerView()
    }

    private lateinit var adapter: ThemeAdapter
    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.isSmoothScrollbarEnabled = true
        recyclerView?.layoutManager = linearLayoutManager
        recyclerView?.setHasFixedSize(true)
        adapter = ThemeAdapter(getThemes())
        recyclerView?.adapter = adapter
        recyclerView?.isNestedScrollingEnabled = false
        adapter.setOnItemClickListener(object : ThemeAdapter.OnItemClickListener {
            override fun onClick(theme: ThemeBean) {
                val color = theme.color
                imageViewPrimaryColor?.setColorFilter(color)
                ThemeHelper.setPrimaryColor(context!!, color)
                Aesthetic.get()
                        .colorPrimary(color)
                        .colorStatusBarAuto()
                        .colorNavigationBarAuto()
                        .apply()
            }
        })
    }

    private fun initEvent() {
        linearLayoutAccentColor?.setOnClickListener(this)
        linearLayoutPrimaryColor?.setOnClickListener(this)
        linearLayoutPrimaryTextColor?.setOnClickListener(this)
        linearLayoutPrimaryTextColorDark?.setOnClickListener(this)
        linearLayoutSecondaryTextColor?.setOnClickListener(this)
        linearLayoutSecondaryTextColorDark?.setOnClickListener(this)
    }

    private fun bindColor() {
        imageViewAccentColor?.setColorFilter(ThemeHelper.getAccentColor(context!!))
        imageViewPrimaryColor?.setColorFilter(ThemeHelper.getPrimaryColor(context!!))
        imageViewPrimaryTextColor?.setColorFilter(ThemeHelper.getPrimaryTextColor(context!!))
        imageViewPrimaryTextColorDark?.setColorFilter(ThemeHelper.getPrimaryTextColorDark(context!!))
        imageViewSecondaryTextColor?.setColorFilter(ThemeHelper.getSecondaryTextColor(context!!))
        imageViewSecondaryTextColorDark?.setColorFilter(ThemeHelper.getSecondaryTextColorDark(context!!))
    }

    private fun getThemes(): ArrayList<ThemeBean> {
        val themes = arrayListOf<ThemeBean>().apply {
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.white), "白色/White", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.black), "黑色/Black", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.dark_black), "深黑/Deep Dark", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.red), "红色/Red", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.pink), "粉色/Pink", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.purple), "紫色/Purple", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.deep_purple), "深紫/Deep Purple", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.indigo), "靛蓝/Indigo", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.blue), "蓝色/Blue", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.light_blue), "亮蓝/Light Blue", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.cyan), "青色/Cyan", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.teal), "鸭绿/Teal", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.green), "绿色/Green", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.light_green), "亮绿/Light Green", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.lime), "酸橙/Lime", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.yellow), "黄色/Yellow", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.amber), "琥珀/Amber", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.orange), "橙色/Orange", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.deep_orange), "暗橙/Deep Orange", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.brown), "棕色/Brown", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.grey), "灰色/Grey", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.blue_grey), "蓝灰/Blue Grey", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.bilibili), "哔哩哔哩/BiliBili", false))
        }
        val color = ThemeHelper.getAccentColor(context!!)
        themes.filter { it.color == color }.forEach { it.isSelected = true }
        return themes
    }
}
