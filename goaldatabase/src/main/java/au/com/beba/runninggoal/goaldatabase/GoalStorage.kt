package au.com.beba.runninggoal.goaldatabase

import au.com.beba.runninggoal.domain.RunningGoal


interface GoalStorage {

    suspend fun all(): List<RunningGoal>

    suspend fun getById(goalId: Long): RunningGoal?

    /**
     * Inserts or Updates the RunningGoal
     *
     * @return Unique ID of the RunningGoal (Primary Key)
     */
    suspend fun save(goal: RunningGoal): Long

    suspend fun delete(goal: RunningGoal): Int
}