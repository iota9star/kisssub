package star.iota.kisssub.ui.selector

import android.content.Intent
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_photo_selector_preview.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseActivity
import star.iota.kisssub.ui.selector.adapter.PhotoAdapter
import star.iota.kisssub.ui.selector.adapter.PhotoPagerAdapter

class PhotoSelectorPreviewActivity : BaseActivity() {

    override fun getContentViewId(): Int = R.layout.activity_photo_selector_preview

    private lateinit var behavior: BottomSheetBehavior<View>
    private fun exchangeBehavior() = if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
    } else {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
    }

    private fun initView() {
        behavior = BottomSheetBehavior.from(linearLayoutBottomSheet)
        behavior.isHideable = false
        linearLayoutInfo.setOnClickListener { exchangeBehavior() }
    }

    private var paths: ArrayList<String>? = null
    private var index: Int = 0
    private var sourceActivity: String? = null
    private fun bind() {
        paths = intent.getStringArrayListExtra(PhotoSelectorActivity.SELECTED_STRING_ARRAY_LIST_PHOTOS)
        index = intent.getIntExtra(PhotoSelectorActivity.FIRST_PHOTO_INDEX, 0)
        sourceActivity = intent.getStringExtra(PhotoSelectorActivity.SOURCE_ACTIVITY)
        if (sourceActivity.isNullOrBlank() || sourceActivity.isNullOrEmpty()) {
            Toast.makeText(this, "检查数据错误...请重试", Toast.LENGTH_SHORT).show()
            finish()
        }
        if (paths == null || paths?.size == 0) {
            Toast.makeText(this, "没有获得预览的图片", Toast.LENGTH_LONG).show()
            return
        }
        textViewPhotoCount.text = (" / ${paths!!.size}")
        textViewPhotoIndex.text = (index + 1).toString()
        val fragments = ArrayList<Fragment>()
        paths!!.forEach { path -> fragments.add(PhotoFragment.newInstance(path)) }
        val pagerAdapter = PhotoPagerAdapter(supportFragmentManager, fragments)
        viewPager.adapter = pagerAdapter
        viewPager.currentItem = index
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                textViewPhotoIndex.text = (position + 1).toString()
            }
        })
        recyclerViewPreview.layoutManager = GridLayoutManager(this, 4)
        val photoAdapter = PhotoAdapter(this, paths!!)
        recyclerViewPreview.adapter = photoAdapter
        if (paths!!.size > 8) {
            val lp = recyclerViewPreview.layoutParams
            lp.height = resources.getDimensionPixelSize(R.dimen.v256dp)
            recyclerViewPreview.layoutParams = lp
        }
        photoAdapter.setOnPhotoSelected(object : PhotoAdapter.OnPhotoSelected {
            override fun selected(position: Int) {
                viewPager.currentItem = position
                textViewPhotoIndex.text = (position + 1).toString()
            }
        })
        buttonRemove.setOnClickListener {
            if (paths!!.size == 0) {
                onBackPressed()
                return@setOnClickListener
            }
            val currentItem = viewPager.currentItem
            paths!!.removeAt(currentItem)
            pagerAdapter.remove(currentItem)
            photoAdapter.notifyItemRemoved(currentItem)
            viewPager.currentItem = 0
            if (paths!!.size == 0) {
                textViewPhotoIndex.text = 0.toString()
                buttonRemove.text = "点击返回前页"
            } else {
                textViewPhotoIndex.text = 1.toString()
            }
            textViewPhotoCount.text = (" / ${paths!!.size}")
        }
    }

    override fun onBackPressed() {
        try {
            val intent = Intent(this, Class.forName(sourceActivity))
            intent.putStringArrayListExtra(PhotoSelectorActivity.SELECTED_STRING_ARRAY_LIST_PHOTOS, paths)
            if (paths!!.size > 0) {
                setResult(PhotoSelectorActivity.RESULT_CODE_FOR_OK, intent)
            } else {
                setResult(PhotoSelectorActivity.RESULT_CODE_FOR_BACK, intent)
            }
        } catch (e: Exception) {
        }
        finish()
    }

    override fun doSome() {
        initView()
        bind()
    }
}