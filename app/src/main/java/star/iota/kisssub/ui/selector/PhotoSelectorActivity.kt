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


import android.app.ProgressDialog
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_photo_selector.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseActivity
import star.iota.kisssub.helper.ThemeHelper
import star.iota.kisssub.ui.selector.adapter.DirAdapter
import star.iota.kisssub.ui.selector.adapter.SelectorPhotoAdapter
import star.iota.kisssub.ui.selector.bean.FolderBean
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class PhotoSelectorActivity : BaseActivity() {
    override fun getContentViewId(): Int = R.layout.activity_photo_selector

    override fun doSome() {
        initToolbar()
        checkData()
        initViews()
        initData()
    }

    private fun initToolbar() {
        toolbar?.title = "请选择图片"
        toolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
        toolbar?.inflateMenu(R.menu.menu_photo_selector)
        toolbar?.menu?.findItem(R.id.menu_refresh_photos)?.setOnMenuItemClickListener {
            findPhotoDirs()
            true
        }
        toolbar?.menu?.findItem(R.id.menu_clear_selected_photos)?.setOnMenuItemClickListener {
            val selectedPhotosSize = selectorPhotoAdapter.getSelectedPhotoSize()
            if (selectedPhotosSize > 0) {
                selectorPhotoAdapter.clearSelectedPhotos()
                Toast.makeText(this, "您清空了选中的" + selectedPhotosSize + "张图片", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "您还没有选中的图片", Toast.LENGTH_SHORT).show()
            }
            buttonOK.visibility = View.GONE
            true
        }
    }


    private lateinit var behavior: BottomSheetBehavior<View>

    private lateinit var selectorPhotoAdapter: SelectorPhotoAdapter
    private lateinit var dirAdapter: DirAdapter
    private var photoSizeLimit: Int = Int.MAX_VALUE
    private var sourceActivity: String? = null
    private var preSelectedPhotos: ArrayList<String>? = null

    private var disposable: Disposable? = null

    private fun checkData() {
        preSelectedPhotos = intent.getStringArrayListExtra(SELECTED_STRING_ARRAY_LIST_PHOTOS)
        photoSizeLimit = intent.getIntExtra(LIMIT_SELECT_PHOTO_COUNT, Int.MAX_VALUE)
        sourceActivity = intent.getStringExtra(SOURCE_ACTIVITY)
        if (sourceActivity.isNullOrBlank() || sourceActivity.isNullOrEmpty()) {
            Toast.makeText(this, "检查数据错误...请重试", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_CODE_FOR_OK) {
                preSelectedPhotos = data.getStringArrayListExtra(SELECTED_STRING_ARRAY_LIST_PHOTOS)
                selectorPhotoAdapter.resetSelectedPhotos(preSelectedPhotos!!)
                checkSelectedPhotosSize(preSelectedPhotos!!.size)
            } else if (resultCode == RESULT_CODE_FOR_BACK) {
                selectorPhotoAdapter.clearSelectedPhotos()
                checkSelectedPhotosSize(0)
            }
        }
    }


    private fun initData() {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            Toast.makeText(this, "当前存储卡不可用", Toast.LENGTH_SHORT).show()
            return
        }
        findPhotoDirs()
    }

    private var progressDialog: ProgressDialog? = null

    private fun findPhotoDirs() {
        selectorPhotoAdapter.clearSelectedPhotos()
        progressDialog = ProgressDialog.show(this, null, "正在加载中...")
        disposable = Single.just(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                .map { uri ->
                    this@PhotoSelectorActivity.contentResolver.query(uri, null,
                            MediaStore.Images.Media.MIME_TYPE + " = ? or "
                                    + MediaStore.Images.Media.MIME_TYPE + " = ? or "
                                    + MediaStore.Images.Media.MIME_TYPE + " = ? or "
                                    + MediaStore.Images.Media.MIME_TYPE + " = ? or "
                                    + MediaStore.Images.Media.MIME_TYPE + " = ? or "
                                    + MediaStore.Images.Media.MIME_TYPE + " = ?",
                            arrayOf("image/jpg", "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"),
                            MediaStore.Images.Media.DATE_MODIFIED)
                }
                .map { cursor ->
                    val folders = ArrayList<FolderBean>()
                    val dirPaths = HashSet<String>()
                    while (cursor.moveToNext()) {
                        val path = cursor.getString(
                                cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                        )
                        val parentFile = File(path).parentFile ?: continue
                        val dirPath = parentFile.absolutePath
                        val folderBean: FolderBean
                        if (dirPaths.contains(dirPath)) {
                            continue
                        } else {
                            dirPaths.add(dirPath)
                            folderBean = FolderBean()
                            folderBean.dir = dirPath
                            folderBean.firstImgPath = path
                        }

                        if (parentFile.list() == null) {
                            continue
                        }
                        val picSize = parentFile.list { _, name ->
                            val real = name.toLowerCase()
                            (real.endsWith(".jpg")
                                    || real.endsWith(".jpeg")
                                    || real.endsWith(".png")
                                    || real.endsWith(".bmp")
                                    || real.endsWith("gif")
                                    || real.endsWith(".webp"))
                        }.size
                        folderBean.count = picSize
                        folders.add(folderBean)
                    }
                    cursor.close()
                    Collections.sort<FolderBean>(folders, { arg0, arg1 ->
                        val a0 = arg0.count
                        val a1 = arg1.count
                        when {
                            a0 < a1 -> 1
                            a0 == a1 -> 0
                            else -> -1
                        }
                    })
                    folders
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { dirs ->
                            progressDialog?.dismiss()
                            if (dirs.isEmpty()) return@subscribe
                            bindFolders(dirs)
                            bindPhotos(dirs[0].dir!!)
                            if (preSelectedPhotos != null && preSelectedPhotos!!.size > 0) {
                                selectorPhotoAdapter.resetSelectedPhotos(preSelectedPhotos!!)
                                checkSelectedPhotosSize(preSelectedPhotos!!.size)
                            }
                        },
                        { error ->
                            Toast.makeText(this@PhotoSelectorActivity, "发生错误：${error.message}", Toast.LENGTH_SHORT).show()
                        }
                )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (disposable != null && !disposable!!.isDisposed) {
            disposable?.dispose()
        }
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }

    private fun initViews() {
        val layoutManager = GridLayoutManager(this, 3)
        selectorPhotoAdapter = SelectorPhotoAdapter(this, photoSizeLimit)
        recyclerViewPhotos?.layoutManager = layoutManager
        recyclerViewPhotos?.adapter = selectorPhotoAdapter
        selectorPhotoAdapter.setOnPhotoSelected(object : SelectorPhotoAdapter.OnPhotoSelected {
            override fun selected(size: Int) {
                checkSelectedPhotosSize(size)
            }
        })
        dirAdapter = DirAdapter()
        recyclerViewDirs?.setHasFixedSize(true)
        recyclerViewDirs?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewDirs?.adapter = dirAdapter
        dirAdapter.setOnDirSelectedListener(object : DirAdapter.OnDirSelectedListener {
            override fun selected(folder: FolderBean) {
                bindPhotos(folder.dir!!)
                exchangeBehavior()
            }
        })
        buttonOK?.setOnClickListener {
            val selectedPhotos = selectorPhotoAdapter.getSelectedPhotos()
            var intent: Intent? = null
            try {
                intent = Intent(this@PhotoSelectorActivity, Class.forName(sourceActivity))
            } catch (e: ClassNotFoundException) {
                Toast.makeText(this@PhotoSelectorActivity, "出现未知错误，请稍候重试", Toast.LENGTH_SHORT).show()
                finish()
            }
            intent?.putStringArrayListExtra(SELECTED_STRING_ARRAY_LIST_PHOTOS, ArrayList(selectedPhotos))
            setResult(RESULT_CODE_FOR_OK, intent)
            finish()
        }

        imageButtonPreview?.setColorFilter(ThemeHelper.getAccentColor(this))
        imageButtonPreview?.setOnClickListener {
            val photos = selectorPhotoAdapter.getSelectedPhotos()
            if (photos.isNotEmpty()) {
                val intent = Intent(this@PhotoSelectorActivity, PhotoSelectorPreviewActivity::class.java)
                intent.putExtra(SOURCE_ACTIVITY, PhotoSelectorActivity::class.java.canonicalName)
                intent.putStringArrayListExtra(SELECTED_STRING_ARRAY_LIST_PHOTOS, ArrayList(photos))
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                Toast.makeText(this, "您还没有选择图片，无法进行预览", Toast.LENGTH_SHORT).show()
            }
        }

        behavior = BottomSheetBehavior.from(linearLayoutBottomSheet)
        behavior.isHideable = false
        linearLayoutInfo?.setOnClickListener { exchangeBehavior() }
    }

    override fun onBackPressed() {
        try {
            val intent = Intent(this, Class.forName(sourceActivity))
            intent.putStringArrayListExtra(SELECTED_STRING_ARRAY_LIST_PHOTOS, preSelectedPhotos)
            setResult(RESULT_CODE_FOR_BACK, intent)
        } catch (e: Exception) {
            Toast.makeText(this, "返回出现错误，请重试", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    private fun bindFolders(folders: ArrayList<FolderBean>) {
        if (folders.size > 3) {
            val lp = recyclerViewDirs?.layoutParams
            lp?.height = resources.getDimensionPixelSize(R.dimen.v256dp)
            recyclerViewDirs?.layoutParams = lp
        }
        dirAdapter.update(folders)
    }

    private fun bindPhotos(currentDir: String) {
        val dir = File(currentDir)
        val photos = Arrays.asList(*dir.list { _, name ->
            val real = name.toLowerCase()
            (real.endsWith(".jpg")
                    || real.endsWith(".jpeg")
                    || real.endsWith(".png")
                    || real.endsWith(".bmp")
                    || real.endsWith("gif")
                    || real.endsWith(".webp"))
        })
        selectorPhotoAdapter.setDirAndPhotos(dir.absolutePath, photos)
        if (selectorPhotoAdapter.getSelectedPhotoSize() > 0) {
            buttonOK.visibility = View.VISIBLE
        }
        textViewPhotoCount?.text = String.format("%s张", photos.size.toString())
        textViewPhotoDir?.text = dir.name
    }

    private fun checkSelectedPhotosSize(size: Int) = if (size == 0) {
        toolbar?.title = "请选择图片"
        buttonOK?.visibility = View.GONE
    } else {
        if (photoSizeLimit == Int.MAX_VALUE) toolbar?.title = "已选中 $size 张"
        else toolbar?.title = "已选中 $size/$photoSizeLimit 张"
        buttonOK?.visibility = View.VISIBLE
    }

    private fun exchangeBehavior() = if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
    } else {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
    }

    companion object {
        const val REQUEST_CODE = 100
        const val RESULT_CODE_FOR_BACK = REQUEST_CODE + 1
        const val RESULT_CODE_FOR_OK = RESULT_CODE_FOR_BACK + 1
        const val LIMIT_SELECT_PHOTO_COUNT: String = "limit_select_photo_count"
        const val SOURCE_ACTIVITY: String = "source_activity"
        const val FIRST_PHOTO_INDEX: String = "first_photo_index"
        const val PHOTOS_CAN_BE_REMOVE: String = "photos_can_be_remove"
        const val SELECTED_STRING_ARRAY_LIST_PHOTOS: String = "selected_string_array_list_photos"
    }
}
