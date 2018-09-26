package au.com.beba.runninggoal.repo

import androidx.lifecycle.LiveData
import au.com.beba.runninggoal.models.RunningGoal


interface GoalRepository {
    fun getGoals(): LiveData<List<RunningGoal>>

    suspend fun fetchGoals(): List<RunningGoal>

    suspend fun getById(goalId: Long): RunningGoal?

    suspend fun markGoalUpdateStatus(updating: Boolean, runningGoal: RunningGoal)

    /**
     * Inserts or Updates the RunningGoal
     *
     * @return Unique ID of the RunningGoal (Primary Key)
     */
    suspend fun save(goal: RunningGoal): Long

    suspend fun delete(goal: RunningGoal): Int
}