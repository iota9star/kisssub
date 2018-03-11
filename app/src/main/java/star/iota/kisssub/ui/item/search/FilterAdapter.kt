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

package star.iota.kisssub.ui.item.search

import android.view.LayoutInflater
import android.view.ViewGroup
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseAdapter
import star.iota.kisssub.base.BaseViewHolder
import star.iota.kisssub.utils.ViewContextUtils


class FilterAdapter : BaseAdapter<FilterBean>() {
    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<FilterBean> = if (viewType == FilterBean.HEADER) {
        HeaderViewHolder(LayoutInflater.from(ViewContextUtils.getAppCompatActivity(parent)).inflate(R.layout.item_filter_header, parent, false))
    } else {
        TagViewHolder(LayoutInflater.from(ViewContextUtils.getAppCompatActivity(parent)).inflate(R.layout.item_filter_tag, parent, false))
    }

    override fun getItemViewType(position: Int): Int = getDataList()[position].type
}
