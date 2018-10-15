package au.com.beba.runninggoal.goaldatabase

import au.com.beba.runninggoal.domain.workout.Workout


interface WorkoutStorage {

    suspend fun insertAll(goalId: Long, workouts: List<Workout>)

    suspend fun allForGoal(goalId: Long): List<Workout>

    suspend fun deleteAllForGoal(goalId: Long): Int
}