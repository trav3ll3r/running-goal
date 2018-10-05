package au.com.beba.runninggoal.repo

import au.com.beba.runninggoal.domain.core.Workout


interface AthleteActivityRepository {

    suspend fun insertAll(goalId: Long, workouts: List<Workout>)

    suspend fun getAllForGoal(goalId: Long): List<Workout>

    suspend fun deleteAllForGoal(goalId: Long): Int
}