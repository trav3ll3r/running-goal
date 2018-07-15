package au.com.beba.runninggoal.persistence

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "widget")
data class WidgetEntity(

        @PrimaryKey(autoGenerate = false)
        var uid: Int = 0,

        @ColumnInfo(name = "goal_id")
        var goalId: Long = 0L,

        @ColumnInfo(name = "view_type")
        var viewType: Int = 0
) {
    @Ignore constructor() : this(0)
}