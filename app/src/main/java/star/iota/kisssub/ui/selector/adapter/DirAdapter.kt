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

package star.iota.kisssub.ui.selector.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.item_photo_selector_dir.view.*
import star.iota.kisssub.R
import star.iota.kisssub.glide.GlideApp
import star.iota.kisssub.ui.selector.bean.FolderBean


class DirAdapter : RecyclerView.Adapter<DirAdapter.ViewHolder>() {

    private var folders: ArrayList<FolderBean> = ArrayList()

    private var onDirSelectedListener: OnDirSelectedListener? = null

    interface OnDirSelectedListener {
        fun selected(folder: FolderBean)
    }

    fun setOnDirSelectedListener(onDirSelectedListener: OnDirSelectedListener) {
        this.onDirSelectedListener = onDirSelectedListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_photo_selector_dir, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = folders[position]
        holder.itemView?.apply {
            val multi = MultiTransformation(
                    BlurTransformation(25),
                    CropCircleTransformation())
            GlideApp.with(this)
                    .load(folder.firstImgPath)
                    .apply(bitmapTransform(multi))
                    .into(image_view_photo_cover)
            text_view_dir_name?.text = folder.dir
            text_view_photo_count?.text = folder.count.toString()
            linear_layout_item_root?.setOnClickListener {
                onDirSelectedListener?.selected(folder)
            }
        }

    }

    override fun getItemCount(): Int = folders.size

    fun update(folders: ArrayList<FolderBean>) {
        this.folders = folders
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
