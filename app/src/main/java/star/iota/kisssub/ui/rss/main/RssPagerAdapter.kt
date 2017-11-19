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
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter

class RssPagerAdapter(fm: FragmentManager, private val titles: ArrayList<String>, private val fragments: ArrayList<Fragment>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE

    override fun getPageTitle(position: Int): CharSequence? = titles[position]

    override fun getItemId(position: Int): Long = fragments[position].hashCode().toLong()

    fun addAll(titles: ArrayList<String>, fragments: ArrayList<Fragment>) {
        this.titles.addAll(titles)
        this.fragments.addAll(fragments)
        notifyDataSetChanged()
    }
}