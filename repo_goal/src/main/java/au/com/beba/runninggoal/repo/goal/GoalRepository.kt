package au.com.beba.runninggoal.repo.goal

import au.com.beba.runninggoal.domain.RunningGoal


interface GoalRepository {

    suspend fun fetchGoals(): List<RunningGoal>

    suspend fun getById(goalId: Long): RunningGoal?

    /**
     * Inserts or Updates the RunningGoal
     *
     * @return Unique ID of the RunningGoal (Primary Key)
     */
    suspend fun save(goal: RunningGoal): Long

    suspend fun delete(goal: RunningGoal): Int
}