package au.com.beba.runninggoal.persistence

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "running_goal")
data class RunningGoalEntity(

        // REF https://developer.android.com/reference/android/arch/persistence/room/PrimaryKey#autoGenerate()
        @PrimaryKey(autoGenerate = true)
        var uid: Long = 0L,   // IF SET AS 0 AUTO-INCREMENT WILL ASSIGN THE VALUE

        @ColumnInfo(name = "goal_name")
        var goalName: String = "",

        @ColumnInfo(name = "target_distance")
        var targetDistance: Float = 0.0f,

        @ColumnInfo(name = "current_distance")
        var currentDistance: Float = 0.0f,

        @ColumnInfo(name = "start_date")
        var startDate: Long = 0,

        @ColumnInfo(name = "end_date")
        var endDate: Long = 0,

        @ColumnInfo(name = "view_type")
        var viewType: Int = 0
) {
    @Ignore constructor() : this(0)
}