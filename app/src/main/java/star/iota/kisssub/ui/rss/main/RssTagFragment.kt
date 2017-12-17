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

package star.iota.kisssub.ui.rss.main

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.fragment_rss.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseFragment
import star.iota.kisssub.ext.addFragmentToActivity
import star.iota.kisssub.helper.ThemeHelper
import star.iota.kisssub.room.AppDatabaseHelper
import star.iota.kisssub.room.RssTag
import star.iota.kisssub.ui.rss.data.RssFragment
import star.iota.kisssub.ui.rss.tag.RssTagManageContract
import star.iota.kisssub.ui.rss.tag.RssTagManageFragment
import star.iota.kisssub.ui.rss.tag.RssTagManagePresenter

class RssTagFragment : BaseFragment(), RssTagManageContract.View {
    override fun success(tags: ArrayList<RssTag>) {
        val titles = ArrayList<String>()
        val fragments = ArrayList<Fragment>()
        tags.forEach {
            if (!it.tag.isNullOrBlank()) {
                titles.add(it.tag!!)
                fragments.add(RssFragment.newInstance(it.tag!!))
            }
        }
        pagerAdapter.addAll(titles, fragments)
        viewPager.offscreenPageLimit = pagerAdapter.count
    }

    override fun success(rssTag: RssTag) {
    }

    override fun error(e: String?) {
        println("run here rss tag fragment error")
    }

    override fun noData() {
        println("run here rss tag fragment nodata")
    }

    companion object {
        fun newInstance(): RssTagFragment = RssTagFragment()
    }

    override fun getContainerViewId(): Int = R.layout.fragment_rss

    private lateinit var pagerAdapter: RssPagerAdapter
    override fun doSome() {
        setToolbarTitle(getString(R.string.menu_rss))
        initPagerAdapter()
        initViewPager()
        initActionView()
        initPresenter()
    }

    private lateinit var presenter: RssTagManagePresenter
    private fun initPresenter() {
        presenter = RssTagManagePresenter(this)
        presenter.get(AppDatabaseHelper.getInstance(context!!))
    }

    private fun initActionView() {
        if (ThemeHelper.isDark(context!!)) {
            imageViewAdd.setColorFilter(ThemeHelper.getSecondaryTextColorDark(context!!))
        } else {
            imageViewAdd.setColorFilter(ThemeHelper.getSecondaryTextColor(context!!))
        }
        imageViewAdd.setOnClickListener {
            (activity!! as AppCompatActivity).addFragmentToActivity(RssTagManageFragment.newInstance(), R.id.frameLayoutContainer)
        }
    }

    private fun initPagerAdapter() {
        val titles = ArrayList<String>()
        val fragments = ArrayList<Fragment>()
        titles.add("最新")
        fragments.add(RssFragment.newInstance(null))
        pagerAdapter = RssPagerAdapter(childFragmentManager, titles, fragments)
    }

    private fun initViewPager() {
        tabLayout.setupWithViewPager(viewPager)
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = pagerAdapter.count
    }

    override fun getBackgroundView(): ImageView? = null

    override fun getMaskView(): View? = null

    override fun onDestroy() {
        super.onDestroy()
        presenter.unsubscribe()
    }

}
