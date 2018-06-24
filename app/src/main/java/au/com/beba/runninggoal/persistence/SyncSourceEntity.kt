package au.com.beba.runninggoal.persistence

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "sync_source")
data class SyncSourceEntity(

        @PrimaryKey(autoGenerate = true)
        var uid: Int = 0,

        @ColumnInfo()
        var type: String = "",

        @ColumnInfo(name = "access_token")
        var accessToken: String = "",

        @ColumnInfo(name = "synced_at")
        var syncedAt: Long = 0
)