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

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_theme.view.*
import star.iota.kisssub.R

class ThemeAdapter(private val themes: ArrayList<ThemeBean>) : RecyclerView.Adapter<ThemeAdapter.ViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_theme, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val theme = themes[position]
        holder.itemView?.apply {
            if (theme.isSelected) {
                imageViewPoint?.setImageResource(R.drawable.ic_item_theme_checked)
            } else {
                imageViewPoint?.setImageResource(R.drawable.ic_item_theme_uncheck)
            }
            imageViewPoint?.setColorFilter(theme.color)
            textViewDesc?.text = theme.description
            linearLayoutContainer?.setOnClickListener {
                for (bean in themes) {
                    bean.isSelected = false
                }
                theme.isSelected = true
                onItemClickListener?.onClick(theme)
                notifyDataSetChanged()
            }
        }

    }

    override fun getItemCount(): Int = themes.size

    fun add(themes: List<ThemeBean>) {
        val size = this.themes.size
        this.themes.addAll(themes)
        notifyItemRangeInserted(size, themes.size)
    }

    fun removeSelectedStatus() {
        themes.forEach {
            it.isSelected = false
        }
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onClick(theme: ThemeBean)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
