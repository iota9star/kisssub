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

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.item_photo_selector_photo.view.*
import star.iota.kisssub.R
import star.iota.kisssub.glide.GlideApp
import star.iota.kisssub.helper.ThemeHelper


class SelectorPhotoAdapter(val context: Context, private val limit: Int) : RecyclerView.Adapter<SelectorPhotoAdapter.ViewHolder>() {

    private var dirPath: String? = null
    private var photos: MutableList<String> = ArrayList()

    fun setDirAndPhotos(dirPath: String, photos: MutableList<String>) {
        this.dirPath = dirPath
        this.photos = photos
        notifyDataSetChanged()
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(inflater.inflate(R.layout.item_photo_selector_photo, parent, false))

    private var onPhotoSelected: OnPhotoSelected? = null

    interface OnPhotoSelected {
        fun selected(size: Int)
    }

    fun setOnPhotoSelected(onPhotoSelected: OnPhotoSelected) {
        this.onPhotoSelected = onPhotoSelected
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filePath = dirPath + "/" + photos[position]
        holder.itemView?.apply {
            GlideApp.with(this)
                    .load(filePath)
                    .error(R.mipmap.ic_launcher)
                    .placeholder(R.mipmap.ic_launcher)
                    .fallback(R.mipmap.ic_launcher)
                    .into(image_view_photo)
            image_view_photo_mask?.visibility = View.GONE
            image_view_select_button?.visibility = View.GONE
            image_view_select_button?.setColorFilter(ThemeHelper.getAccentColor(context))
            image_view_photo?.setOnClickListener {
                if (selectedPhotos.contains(filePath)) {
                    selectedPhotos.remove(filePath)
                    image_view_photo_mask?.visibility = View.GONE
                    image_view_select_button?.visibility = View.GONE
                } else {
                    if (selectedPhotos.size >= limit) {
                        Toast.makeText(
                                context,
                                "您最多选择" + limit + "张图片",
                                Toast.LENGTH_SHORT)
                                .show()
                    } else {
                        selectedPhotos.add(filePath)
                        image_view_photo_mask?.visibility = View.VISIBLE
                        image_view_select_button?.visibility = View.VISIBLE
                    }
                }
                onPhotoSelected?.selected(selectedPhotos.size)
            }
            if (selectedPhotos.contains(filePath)) {
                image_view_photo_mask?.visibility = View.VISIBLE
                image_view_select_button?.visibility = View.VISIBLE
            } else {
                image_view_photo_mask?.visibility = View.GONE
                image_view_select_button?.visibility = View.GONE
            }
            onPhotoSelected?.selected(selectedPhotos.size)
        }
    }

    fun clearSelectedPhotos() {
        selectedPhotos.clear()
        notifyDataSetChanged()
    }

    fun getSelectedPhotos(): Set<String> = selectedPhotos
    fun getSelectedPhotoSize(): Int = selectedPhotos.size


    fun resetSelectedPhotos(paths: ArrayList<String>) {
        selectedPhotos.clear()
        selectedPhotos.addAll(paths)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = photos.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private val selectedPhotos = HashSet<String>()
    }
}
