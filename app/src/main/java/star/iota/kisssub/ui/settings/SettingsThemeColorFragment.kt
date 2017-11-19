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

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import com.afollestad.aesthetic.Aesthetic
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_settings_theme_color.*
import star.iota.kisssub.base.BaseFragment
import star.iota.kisssub.helper.ThemeHelper

class SettingsThemeColorFragment : BaseFragment(), View.OnClickListener {

    companion object {
        val TITLE = "title"
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
            R.id.linearLayoutAccentColor -> {
                showAccentColorSetting()
            }
            R.id.linearLayoutPrimaryColor -> {
                showPrimaryColorSetting()
            }
            R.id.linearLayoutPrimaryTextColor -> {
                showPrimaryTextColorSetting()
            }
            R.id.linearLayoutSecondaryTextColor -> {
                showSecondaryTextColorSetting()
            }
            R.id.linearLayoutPrimaryTextColorDark -> {
                showPrimaryTextColorDarkSetting()
            }
            R.id.linearLayoutSecondaryTextColorDark -> {
                showSecondaryTextColorDarkSetting()
            }
        }
    }


    private fun showSecondaryTextColorDarkSetting() {
        ColorPickerDialogBuilder
                .with(context!!)
                .initialColor(ThemeHelper.getSecondaryTextColorDark(context!!))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener {
                    imageViewSecondaryTextColorDark.setColorFilter(it)
                    ThemeHelper.setSecondaryTextColorDark(context!!, it)
                    if (ThemeHelper.isDark(context!!)) {
                        Aesthetic.get()
                                .textColorSecondary(it)
                                .apply()
                    }
                }
                .build()
                .show()
    }

    private fun showPrimaryTextColorDarkSetting() {
        ColorPickerDialogBuilder
                .with(context!!)
                .initialColor(ThemeHelper.getPrimaryTextColorDark(context!!))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener {
                    imageViewPrimaryTextColorDark.setColorFilter(it)
                    ThemeHelper.setPrimaryTextColorDark(context!!, it)
                    if (ThemeHelper.isDark(context!!)) {
                        Aesthetic.get()
                                .textColorPrimary(it)
                                .apply()
                    }
                }
                .build()
                .show()
    }

    private fun showSecondaryTextColorSetting() {
        ColorPickerDialogBuilder
                .with(context!!)
                .initialColor(ThemeHelper.getSecondaryTextColor(context!!))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener {
                    imageViewSecondaryTextColor.setColorFilter(it)
                    ThemeHelper.setSecondaryTextColor(context!!, it)
                    if (!ThemeHelper.isDark(context!!)) {
                        Aesthetic.get()
                                .textColorSecondary(it)
                                .apply()
                    }
                }
                .build()
                .show()
    }

    private fun showPrimaryTextColorSetting() {
        ColorPickerDialogBuilder
                .with(context!!)
                .initialColor(ThemeHelper.getPrimaryTextColor(context!!))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener {
                    imageViewPrimaryTextColor.setColorFilter(it)
                    ThemeHelper.setPrimaryTextColor(context!!, it)
                    if (!ThemeHelper.isDark(context!!)) {
                        Aesthetic.get()
                                .textColorPrimary(it)
                                .apply()
                    }
                }
                .build()
                .show()
    }

    private fun showPrimaryColorSetting() {
        ColorPickerDialogBuilder
                .with(context!!)
                .initialColor(ThemeHelper.getPrimaryColor(context!!))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener {
                    imageViewPrimaryColor.setColorFilter(it)
                    ThemeHelper.setPrimaryColor(context!!, it)
                    Aesthetic.get()
                            .colorPrimary(it)
                            .colorStatusBarAuto()
                            .colorNavigationBarAuto()
                            .apply()
                }
                .build()
                .show()
    }

    private fun showAccentColorSetting() {
        ColorPickerDialogBuilder
                .with(context!!)
                .initialColor(ThemeHelper.getAccentColor(context!!))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener {
                    imageViewAccentColor.setColorFilter(it)
                    ThemeHelper.setAccentColor(context!!, it)
                    Aesthetic.get()
                            .colorAccent(it)
                            .colorStatusBarAuto()
                            .colorNavigationBarAuto()
                            .apply()
                    adapter.removeSelectedStatus()
                }
                .build()
                .show()
    }

    override fun getBackgroundView(): ImageView = imageViewContentBackground
    override fun getMaskView(): View = viewMask
    override fun getContainerViewId(): Int = R.layout.fragment_settings_theme_color

    override fun doSome() {
        setToolbarTitle(arguments!!.getString(TITLE))
        initView()
        initEvent()
        bindColor()
        initRecyclerView()
    }

    private lateinit var adapter: ThemeAdapter
    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.isSmoothScrollbarEnabled = true
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        adapter = ThemeAdapter(getThemes())
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false
        adapter.setOnItemClickListener(object : ThemeAdapter.OnItemClickListener {
            override fun onClick(theme: ThemeBean) {
                applyTheme(theme.color)
            }
        })
    }

    private fun initView() {
        switchCompatTint.isChecked = ThemeHelper.isTint(context!!)
        switchCompatTint.setOnCheckedChangeListener { _, isChecked ->
            val color = ThemeHelper.getAccentColor(context!!)
            ThemeHelper.isTint(context!!, isChecked)
            if (isChecked) {
                ThemeHelper.setPrimaryColor(context!!, color)
                Aesthetic.get()
                        .colorPrimary(color)
                        .colorAccent(color)
                        .colorStatusBarAuto()
                        .colorNavigationBarAuto()
                        .apply()
            } else {
                ThemeHelper.setPrimaryColor(context!!, ContextCompat.getColor(context!!, R.color.white))
                Aesthetic.get()
                        .colorPrimaryRes(R.color.white)
                        .colorAccent(color)
                        .colorStatusBarAuto()
                        .colorNavigationBarAuto()
                        .apply()
            }
            resetTextColor()
            bindColor()
        }
    }

    private fun initEvent() {
        linearLayoutAccentColor.setOnClickListener(this)
        linearLayoutPrimaryColor.setOnClickListener(this)
        linearLayoutPrimaryTextColor.setOnClickListener(this)
        linearLayoutPrimaryTextColorDark.setOnClickListener(this)
        linearLayoutSecondaryTextColor.setOnClickListener(this)
        linearLayoutSecondaryTextColorDark.setOnClickListener(this)
    }

    private fun bindColor() {
        imageViewAccentColor.setColorFilter(ThemeHelper.getAccentColor(context!!))
        imageViewPrimaryColor.setColorFilter(ThemeHelper.getPrimaryColor(context!!))
        imageViewPrimaryTextColor.setColorFilter(ThemeHelper.getPrimaryTextColor(context!!))
        imageViewPrimaryTextColorDark.setColorFilter(ThemeHelper.getPrimaryTextColorDark(context!!))
        imageViewSecondaryTextColor.setColorFilter(ThemeHelper.getSecondaryTextColor(context!!))
        imageViewSecondaryTextColorDark.setColorFilter(ThemeHelper.getSecondaryTextColorDark(context!!))
    }

    private fun applyTheme(color: Int) {
        ThemeHelper.setAccentColor(context!!, color)
        if (ThemeHelper.isTint(context!!)) {
            ThemeHelper.setPrimaryColor(context!!, color)
            Aesthetic.get()
                    .colorPrimary(color)
                    .colorAccent(color)
                    .colorStatusBarAuto()
                    .colorNavigationBarAuto()
                    .apply()
        } else {
            ThemeHelper.setPrimaryColor(context!!, ContextCompat.getColor(context!!, R.color.white))
            Aesthetic.get()
                    .colorPrimaryRes(R.color.white)
                    .colorAccent(color)
                    .colorStatusBarAuto()
                    .colorNavigationBarAuto()
                    .apply()
        }
        resetTextColor()
        bindColor()
    }

    private fun resetTextColor() {
        ThemeHelper.setPrimaryTextColorDark(context!!, ContextCompat.getColor(context!!, R.color.text_color_primary_dark))
        ThemeHelper.setPrimaryTextColor(context!!, ContextCompat.getColor(context!!, R.color.text_color_primary))
        ThemeHelper.setSecondaryTextColorDark(context!!, ContextCompat.getColor(context!!, R.color.text_color_secondary_dark))
        ThemeHelper.setSecondaryTextColor(context!!, ContextCompat.getColor(context!!, R.color.text_color_secondary))
        if (ThemeHelper.isDark(context!!)) {
            Aesthetic.get()
                    .textColorSecondaryRes(R.color.text_color_secondary_dark)
                    .textColorPrimaryRes(R.color.text_color_primary_dark)
                    .apply()
        } else {
            Aesthetic.get()
                    .textColorSecondaryRes(R.color.text_color_secondary)
                    .textColorPrimaryRes(R.color.text_color_primary)
                    .apply()
        }
    }

    private fun getThemes(): ArrayList<ThemeBean> {
        val themes = arrayListOf<ThemeBean>().apply {
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
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.black), "黑色/Black", false))
            add(ThemeBean(ContextCompat.getColor(context!!, R.color.dark_black), "深黑/Deep Dark", false))
        }
        val color = ThemeHelper.getAccentColor(context!!)
        themes.filter { it.color == color }.forEach { it.isSelected = true }
        return themes
    }
}