package au.com.beba.runninggoal.persistence

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query


@Dao
interface RunningGoalDao {

    @Query("SELECT * FROM running_goal")
    fun getAll(): List<RunningGoalEntity>

    @Query("SELECT * FROM running_goal WHERE widget_id = :widgetId")
//    fun getById(widgetId: Int): LiveData<RunningGoalEntity>
    fun getById(widgetId: Int): RunningGoalEntity?

    @Insert
    fun insertAll(vararg runningGoals: RunningGoalEntity)

    @Delete
    fun delete(runningGoal: RunningGoalEntity)
}