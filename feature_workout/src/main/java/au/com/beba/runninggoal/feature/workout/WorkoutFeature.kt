package au.com.beba.runninggoal.feature.workout

import au.com.beba.feature.base.AndroidFeature
import au.com.beba.runninggoal.domain.workout.Workout


interface WorkoutFeature : AndroidFeature {

    fun getAllForGoal(goalId: Long): List<Workout>?

    fun deleteAllForGoal(goalId: Long)
}