package au.com.beba.runninggoal.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(tableName = "sync_source")
data class SyncSourceEntity(

        @PrimaryKey(autoGenerate = true)
        var uid: Int = 0,

        @ColumnInfo()
        var type: String = "",

        @ColumnInfo(name = "access_token")
        var accessToken: String = "",

        @ColumnInfo(name = "is_active")
        var isActive: Boolean = false,

        @ColumnInfo(name = "synced_at")
        var syncedAt: Long = 0
) {
    @Ignore constructor() : this(0)
}