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

package star.iota.kisssub.room

import android.arch.persistence.room.Room
import android.content.Context

class AppDatabaseHelper constructor(context: Context) {

    private val database = Room.databaseBuilder(context, AppDatabase::class.java, "kisssub").build()

    companion object {
        @Volatile
        private var INSTANCE: AppDatabaseHelper? = null

        fun getInstance(context: Context): AppDatabaseHelper {
            if (INSTANCE == null) {
                synchronized(AppDatabaseHelper::class) {
                    if (INSTANCE == null) {
                        INSTANCE = AppDatabaseHelper(context.applicationContext)
                    }
                }
            }
            return INSTANCE!!
        }
    }

    fun allRecord(): List<Record> = database.recordDao().all()
    fun addRecord(record: Record) = database.recordDao().add(record)
    fun deleteRecord(record: Record) {
        database.recordDao().delete(record)
    }

    fun allRssTag(): List<RssTag> = database.rssTagDao().all()
    fun addRssTag(rssTag: RssTag) = database.rssTagDao().add(rssTag)
    fun deleteRssTag(rssTag: RssTag) {
        database.rssTagDao().delete(rssTag)
    }
}