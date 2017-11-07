package star.iota.kisssub.ui.selector.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter


class PhotoPagerAdapter(fm: FragmentManager, private val fragments: ArrayList<Fragment>) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE

    fun remove(pos: Int) {
        fragments.removeAt(pos)
        notifyDataSetChanged()
    }
}
