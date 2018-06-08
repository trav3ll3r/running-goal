package au.com.beba.runninggoal.persistence

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "running_goal")
data class RunningGoalEntity(

    // REF https://developer.android.com/reference/android/arch/persistence/room/PrimaryKey#autoGenerate()
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0,   // IF SET AS 0 AUTO-INCREMENT WILL ASSIGN THE VALUE

    @ColumnInfo(name = "goal_name")
    var goalName: String = "",

    @ColumnInfo(name = "target_distance")
    var targetDistance: Int = 0,

    @ColumnInfo(name = "current_distance")
    var currentDistance: Double = 0.00,

    @ColumnInfo(name = "start_date")
    var startDate: Long = 0,

    @ColumnInfo(name = "end_date")
    var endDate: Long = 0,

    // TODO: CHANGE TO ONE-TO-MANY RELATIONSHIP
    @ColumnInfo(name = "widget_id")
    var widgetId: Int = 0
)