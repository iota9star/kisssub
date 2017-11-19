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

package star.iota.kisssub.ui.about

import com.google.gson.annotations.SerializedName

class InfoBean(
        @SerializedName("type")
        val type: Int,
        @SerializedName("versionCode")
        val versionCode: Int,

        @SerializedName("versionName")
        val versionName: String?,

        @SerializedName("date")
        val date: String?,

        @SerializedName("changeLog")
        val changeLog: String?,

        @SerializedName("dynamic")
        val dynamic: String?,

        @SerializedName("content")
        val content: String?,

        @SerializedName("url")
        val url: String?,
        @SerializedName("star")
        val star: Int,

        @SerializedName("msg")
        val msg: String?
) {
    companion object {
        val TYPE_UPDATE = 0
        val TYPE_INFO = 1
    }
}