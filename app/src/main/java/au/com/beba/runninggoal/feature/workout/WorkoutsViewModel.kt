package au.com.beba.runninggoal.feature.workout

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.domain.workout.Workout
import javax.inject.Inject


class WorkoutsViewModel @Inject constructor(
        private val workoutFeature: WorkoutFeature
) : ViewModel()/*TODO:, CoroutineScope*/ {

    val workoutsLiveData: MutableLiveData<List<Workout>> = MutableLiveData()

    fun fetchWorkouts(goalId: Long) {
        workoutsLiveData.postValue(workoutFeature.getAllForGoal(goalId))
    }
}