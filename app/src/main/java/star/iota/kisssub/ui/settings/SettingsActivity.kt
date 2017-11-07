package star.iota.kisssub.ui.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.support.design.widget.TextInputEditText
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Button
import com.afollestad.aesthetic.Aesthetic
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.liuguangqiang.cookie.OnActionClickListener
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify
import kotlinx.android.synthetic.main.activity_settings.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseActivity
import star.iota.kisssub.glide.GlideApp
import star.iota.kisssub.ui.lock.SecurityHelper
import star.iota.kisssub.ui.lock.SetPinLockActivity
import star.iota.kisssub.ui.selector.PhotoSelectorActivity
import star.iota.kisssub.utils.DisplayUtils
import star.iota.kisssub.utils.ToastUtils
import star.iota.kisssub.widget.MessageBar
import star.iota.kisssub.widget.ken.KenBurnsView

class SettingsActivity : BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.linearLayoutDiyTheme -> showDIYColorSetting()
            R.id.linearLayoutPinLock -> showPinLockSetting()
            R.id.linearLayoutFingerprintLock -> showFingerprintSetting()
            R.id.textViewDynamicBackground -> showBannerSetting()
        }
    }

    private fun initEvent() {
        linearLayoutPinLock.setOnClickListener(this)
        linearLayoutFingerprintLock.setOnClickListener(this)
        textViewDynamicBackground.setOnClickListener(this)
        linearLayoutDiyTheme.setOnClickListener(this)
    }

    private fun showDIYColorSetting() {
        ColorPickerDialogBuilder
                .with(this@SettingsActivity)
                .initialColor(ThemeHelper.getColor(this@SettingsActivity))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener {
                    applyTheme(it)
                    adapter.removeSelectedStatus()
                }
                .build()
                .show()
    }

    private fun showPinLockSetting() {
        if (SecurityHelper.isLock(this@SettingsActivity) != SecurityHelper.LOCK_TYPE_PIN) {
            startActivity(Intent(this@SettingsActivity, SetPinLockActivity::class.java))
        } else {
            AlertDialog.Builder(this@SettingsActivity)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle("设置 PIN 保护")
                    .setPositiveButton("关闭 PIN") { _, _ ->
                        SecurityHelper.setLock(this@SettingsActivity, SecurityHelper.LOCK_TYPE_NONE)
                        SecurityHelper.savePin(this@SettingsActivity, "")
                    }
                    .setNegativeButton("重新设置") { _, _ ->
                        startActivity(Intent(this@SettingsActivity, SetPinLockActivity::class.java))
                    }
                    .show()
        }
    }

    private fun showFingerprintSetting() {
        if (SecurityHelper.isLock(this@SettingsActivity) == SecurityHelper.LOCK_TYPE_NONE) {
            MessageBar.create(this@SettingsActivity, "请先设置至少一种解锁方式")
            return
        }
        val fingerprintIdentify = FingerprintIdentify(this@SettingsActivity)
        if (!fingerprintIdentify.isHardwareEnable) {
            MessageBar.create(this@SettingsActivity, "您的设备可能不支持指纹解锁")
            return
        }
        if (!fingerprintIdentify.isRegisteredFingerprint) {
            MessageBar.create(this@SettingsActivity,
                    "您可能还没有设置指纹，是否前往设置",
                    "嗯",
                    OnActionClickListener {
                        startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                    })
            return
        }
        val isOpen = SecurityHelper.isOpenFingerprint(this@SettingsActivity)
        AlertDialog.Builder(this@SettingsActivity)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(if (isOpen) "关闭指纹识别支持" else "开启指纹识别支持")
                .setPositiveButton("嗯") { _, _ ->
                    if (isOpen) {
                        SecurityHelper.setFingerprint(this@SettingsActivity, false)
                    } else {
                        SecurityHelper.setFingerprint(this@SettingsActivity, true)
                    }
                }
                .show()
    }

    private var bannerPath: TextInputEditText? = null
    @SuppressLint("InflateParams")
    private fun showBannerSetting() {
        val view = this@SettingsActivity.layoutInflater.inflate(R.layout.dialog_drawer_banner, null)
        val dialog = AlertDialog.Builder(this@SettingsActivity)
                .setView(view)
                .create()
        dialog.show()
        val preview = view.findViewById<KenBurnsView>(R.id.kenBurnsView)
        loadImage(preview, ThemeHelper.getBanner(this@SettingsActivity))
        bannerPath = view.findViewById(R.id.textInputEditTextPath)
        val setting = view.findViewById<Button>(R.id.buttonSetting)
        val load = view.findViewById<Button>(R.id.buttonPreview)
        val reset = view.findViewById<Button>(R.id.buttonReset)
        val local = view.findViewById<Button>(R.id.buttonLocal)
        setting.setOnClickListener {
            val path = bannerPath?.text.toString().trim()
            if (path.isBlank()) {
                ToastUtils.short(this@SettingsActivity, "路径为空，请确认输入路径的正确性")
            } else {
                ThemeHelper.setBanner(this@SettingsActivity, path)
                dialog.dismiss()
            }
        }
        load.setOnClickListener {
            val path = bannerPath?.text.toString().trim()
            if (path.isBlank()) {
                ToastUtils.short(this@SettingsActivity, "路径不能为空")
            } else {
                loadImage(preview, path)
            }
        }
        local.setOnClickListener {
            val intent = Intent(this@SettingsActivity, PhotoSelectorActivity::class.java)
            intent.putExtra(PhotoSelectorActivity.SOURCE_ACTIVITY, this@SettingsActivity::class.java.canonicalName)
            intent.putExtra(PhotoSelectorActivity.LIMIT_SELECT_PHOTO_COUNT, 1)
            startActivityForResult(intent, PhotoSelectorActivity.REQUEST_CODE)
        }
        reset.setOnClickListener {
            ThemeHelper.setBanner(this@SettingsActivity, null)
            dialog.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PhotoSelectorActivity.REQUEST_CODE) {
            if (resultCode == PhotoSelectorActivity.RESULT_CODE_FOR_OK) {
                val photos = data.getStringArrayListExtra(PhotoSelectorActivity.SELECTED_STRING_ARRAY_LIST_PHOTOS) ?: return
                photos.forEach {
                    bannerPath?.setText(it)
                }
            } else if (resultCode == PhotoSelectorActivity.RESULT_CODE_FOR_BACK) {
            }
        }
    }

    private fun loadImage(view: KenBurnsView, path: String) {
        GlideApp.with(this@SettingsActivity)
                .load(path)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        ToastUtils.short(this@SettingsActivity, "加载错误，请重新选择")
                        return true
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        view.setImageDrawable(resource)
                        return true
                    }
                })
                .into(view)
    }

    override fun getContentViewId(): Int = R.layout.activity_settings

    private lateinit var adapter: ThemeAdapter

    override fun doSome() {
        initToolbar()
        initEvent()
        DisplayUtils.tintImageView(linearLayoutContainer, ThemeHelper.getColor(this@SettingsActivity))
        initSwitchCompat()

        val linearLayoutManager = LinearLayoutManager(this@SettingsActivity, LinearLayoutManager.VERTICAL, false)
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

    private fun initSwitchCompat() {
        switchCompatNightly.isChecked = ThemeHelper.isDark(this)
        switchCompatNightly.setOnCheckedChangeListener { _, isChecked ->
            ThemeHelper.isDark(this@SettingsActivity, isChecked)
            if (isChecked) {
                Aesthetic.get()
                        .activityTheme(R.style.AppThemeDark)
                        .isDark(true)
                        .textColorPrimaryRes(R.color.text_color_primary_dark)
                        .textColorSecondaryRes(R.color.text_color_secondary_dark)
                        .apply()
            } else {
                Aesthetic.get()
                        .activityTheme(R.style.AppTheme)
                        .isDark(false)
                        .textColorPrimaryRes(R.color.text_color_primary)
                        .textColorSecondaryRes(R.color.text_color_secondary)
                        .apply()
            }
        }
        switchCompatTint.isChecked = ThemeHelper.isTint(this)
        switchCompatTint.setOnCheckedChangeListener { _, isChecked ->
            val color = ThemeHelper.getColor(this@SettingsActivity)
            ThemeHelper.isTint(this@SettingsActivity, isChecked)
            if (isChecked) {
                Aesthetic.get()
                        .colorPrimary(color)
                        .colorAccent(color)
                        .colorStatusBarAuto()
                        .colorNavigationBarAuto()
                        .apply()
            } else {
                Aesthetic.get()
                        .colorPrimaryRes(R.color.white)
                        .colorAccent(color)
                        .colorStatusBarAuto()
                        .colorNavigationBarAuto()
                        .apply()
            }
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    private fun applyTheme(color: Int) {
        DisplayUtils.tintImageView(linearLayoutContainer, color)
        ThemeHelper.setColor(this, color)
        if (ThemeHelper.isTint(this)) {
            Aesthetic.get()
                    .colorPrimary(color)
                    .colorAccent(color)
                    .colorStatusBarAuto()
                    .colorNavigationBarAuto()
                    .apply()
        } else {
            Aesthetic.get()
                    .colorPrimaryRes(R.color.white)
                    .colorAccent(color)
                    .colorStatusBarAuto()
                    .colorNavigationBarAuto()
                    .apply()
        }
    }

    private fun getThemes(): ArrayList<ThemeBean> {
        val themes = arrayListOf<ThemeBean>().apply {
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.red), "红色/Red", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.pink), "粉色/Pink", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.purple), "紫色/Purple", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.deep_purple), "深紫/Deep Purple", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.indigo), "靛蓝/Indigo", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.blue), "蓝色/Blue", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.light_blue), "亮蓝/Light Blue", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.cyan), "青色/Cyan", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.teal), "鸭绿/Teal", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.green), "绿色/Green", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.light_green), "亮绿/Light Green", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.lime), "酸橙/Lime", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.yellow), "黄色/Yellow", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.amber), "琥珀/Amber", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.orange), "橙色/Orange", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.deep_orange), "暗橙/Deep Orange", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.brown), "棕色/Brown", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.grey), "灰色/Grey", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.blue_grey), "蓝灰/Blue Grey", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.bilibili), "哔哩哔哩/BiliBili", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.black), "黑色/Black", false))
            add(ThemeBean(ContextCompat.getColor(this@SettingsActivity, R.color.dark_black), "深黑/Deep Dark", false))
        }
        val color = ThemeHelper.getColor(this)
        themes.filter { it.color == color }.forEach { it.isSelected = true }
        return themes
    }
}