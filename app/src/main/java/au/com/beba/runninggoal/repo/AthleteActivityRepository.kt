package au.com.beba.runninggoal.repo

import au.com.beba.runninggoal.models.AthleteActivity


interface AthleteActivityRepository {

    suspend fun insertAll(goalId: Long, athleteActivities: List<AthleteActivity>)

    suspend fun getAllForGoal(goalId: Long): List<AthleteActivity>

    suspend fun deleteAllForGoal(goalId: Long): Int
}