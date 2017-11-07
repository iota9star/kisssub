package star.iota.kisssub.room

import android.arch.persistence.room.*

@Dao
interface RecordDao {
    @Query("SELECT * FROM record where id = :arg0 LIMIT 1")
    fun find(id: Int): Record

    @Query("SELECT * FROM record order by id desc")
    fun all(): List<Record>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(record: Record): Long

    @Delete
    fun delete(record: Record)


}