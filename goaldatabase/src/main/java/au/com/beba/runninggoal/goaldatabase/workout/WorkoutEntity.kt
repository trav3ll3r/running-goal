package au.com.beba.runninggoal.goaldatabase.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(tableName = "workout")
data class WorkoutEntity(

        // ForeignKey to RunningGoalEntity
        @ColumnInfo(name = "goal_id")
        var goalId: Long = 0L,

        @ColumnInfo(name = "name")
        var name: String = "",

        @ColumnInfo(name = "description")
        var description: String = "",

        @ColumnInfo(name = "distance_in_metres")
        var distanceInMetres: Long = 0L,

        @ColumnInfo(name = "date_time")
        var dateTime: Long = 0,

        // REF https://developer.android.com/reference/android/arch/persistence/room/PrimaryKey#autoGenerate()
        @PrimaryKey(autoGenerate = true)
        var uid: Long = 0L   // IF SET AS 0 AUTO-INCREMENT WILL ASSIGN THE VALUE

) {
    @Ignore constructor() : this(uid = 0L)
}