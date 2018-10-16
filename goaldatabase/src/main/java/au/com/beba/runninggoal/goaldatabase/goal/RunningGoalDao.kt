package au.com.beba.runninggoal.goaldatabase.goal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface RunningGoalDao {

    @Query("SELECT * FROM running_goal")
    fun getAll(): List<RunningGoalEntity>

    @Query("SELECT * FROM running_goal WHERE uid = :goalId")
    fun getById(goalId: Long): RunningGoalEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(runningGoal: RunningGoalEntity): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(runningGoal: RunningGoalEntity)

    @Delete
    fun delete(runningGoal: RunningGoalEntity): Int
}