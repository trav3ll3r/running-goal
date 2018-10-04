package au.com.beba.runninggoal.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface AthleteActivityDao {

    @Query("SELECT * FROM athlete_activity WHERE goal_id = :goalId")
    fun getAllForGoal(goalId: Long): List<AthleteActivityEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(athleteActivities: List<AthleteActivityEntity>): List<Long>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(athleteActivity: AthleteActivityEntity)

    @Query("DELETE FROM athlete_activity WHERE goal_id = :goalId")
    fun deleteAllForGoal(goalId: Long): Int
}