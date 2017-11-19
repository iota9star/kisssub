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

package star.iota.kisssub.ui.selector.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import jp.wasabeef.glide.transformations.CropSquareTransformation


class PhotoAdapter(val context: Context, private val photos: ArrayList<String>) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {


    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(inflater.inflate(R.layout.item_photo_selector_preview_photo, parent, false))

    private var onPhotoSelected: OnPhotoSelected? = null

    interface OnPhotoSelected {
        fun selected(position: Int)
    }

    fun setOnPhotoSelected(onPhotoSelected: OnPhotoSelected) {
        this.onPhotoSelected = onPhotoSelected
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filePath = photos[position]
        GlideApp.with(context)
                .load(filePath)
                .listener(GlidePalette.with(filePath)
                        .use(BitmapPalette.Profile.VIBRANT_LIGHT)
                        .intoBackground(holder.image, BitmapPalette.Swatch.RGB)
                        .crossfade(true))
                .apply(GlideOptions.bitmapTransform(CropSquareTransformation()))
                .into(holder.image)
        holder.image.setOnClickListener {
            onPhotoSelected?.selected(position)
        }

    }

    override fun getItemCount(): Int = photos.size

    class ViewHolder(
            itemView: View,
            val image: ImageView = itemView.findViewById(R.id.image_view_photo)
    ) : RecyclerView.ViewHolder(itemView)
}