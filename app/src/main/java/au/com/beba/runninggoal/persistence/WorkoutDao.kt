package au.com.beba.runninggoal.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workout WHERE goal_id = :goalId")
    fun getAllForGoal(goalId: Long): List<WorkoutEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(workouts: List<WorkoutEntity>): List<Long>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(workout: WorkoutEntity)

    @Query("DELETE FROM workout WHERE goal_id = :goalId")
    fun deleteAllForGoal(goalId: Long): Int
}