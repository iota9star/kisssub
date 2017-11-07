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

    fun all(): List<Record> = database.recordDao().all()
    fun add(record: Record): Long = database.recordDao().add(record)
    fun delete(record: Record) {
        database.recordDao().delete(record)
    }


}