package star.iota.kisssub.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "record")
class Record {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var type: Int = 0
    var category: String? = null
    var title: String? = null
    var date: String? = null
    var cover: String? = null
    var sub: String? = null
    var size: String? = null
    var url: String? = null
    var desc: String? = null
    var magnet: String? = null


    companion object {
        val NO_IMAGE = 0
        val WITH_IMAGE = 1
        val FAN = 2
    }
}