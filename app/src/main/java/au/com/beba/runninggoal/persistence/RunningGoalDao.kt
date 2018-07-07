package au.com.beba.runninggoal.persistence

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update


@Dao
interface RunningGoalDao {

    @Query("SELECT * FROM running_goal")
    fun getAll(): List<RunningGoalEntity>

    @Query("SELECT * FROM running_goal WHERE widget_id = :widgetId")
    fun getById(widgetId: Int): RunningGoalEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(runningGoal: RunningGoalEntity): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(runningGoal: RunningGoalEntity)

    @Delete
    fun delete(runningGoal: RunningGoalEntity): Int
}