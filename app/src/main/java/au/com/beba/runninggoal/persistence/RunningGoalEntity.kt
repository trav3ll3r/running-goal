package au.com.beba.runninggoal.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


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
        var endDate: Long = 0
) {
    @Ignore constructor() : this(0)
}