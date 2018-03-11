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

package star.iota.kisssub.ui.item

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.fragment_default.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseAdapter
import star.iota.kisssub.base.StringContract
import star.iota.kisssub.base.StringListFragment
import star.iota.kisssub.base.StringPresenter
import star.iota.kisssub.room.Record

class ItemFragment : StringListFragment<StringPresenter<ArrayList<Record>>, ArrayList<Record>, Record>(), StringContract.View<ArrayList<Record>> {
    override fun getBackgroundView(): ImageView = imageViewContentBackground
    override fun getMaskView(): View = viewMask
    override fun getContainerViewId(): Int = R.layout.fragment_default
    override fun getRefreshLayout(): SmartRefreshLayout? = refreshLayout

    override fun getRecyclerView(): RecyclerView? = recyclerView

    private val baseAdapter: BaseAdapter<Record> by lazy {
        ItemAdapter()
    }

    override fun getAdapter(): BaseAdapter<Record> = baseAdapter

    override fun setupRefreshLayout(refreshLayout: SmartRefreshLayout?) {
        refreshLayout?.autoRefresh()
    }

    override fun success(result: ArrayList<Record>) {
        super.success(result)
        baseAdapter.addAll(result)
    }

    override fun getStringPresenter(): StringPresenter<ArrayList<Record>> = ItemPresenter(this)

    override fun doOther() {
    }

    companion object {
        fun newInstance(title: String, url: String): ItemFragment {
            val fragment = ItemFragment()
            val bundle = Bundle()
            bundle.putString(URL, url.replace("1.html", ""))
            bundle.putString(SUFFIX, ".html")
            bundle.putString(TITLE, title)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun loadDataEnd() {
        if (isRefresh()) {
            setPage(2)
            refreshLayout?.finishRefresh()
        } else {
            pagePlus()
            refreshLayout?.finishLoadMore()
        }
    }
}
