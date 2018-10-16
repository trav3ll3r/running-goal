package au.com.beba.runninggoal.goaldatabase.widget

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


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