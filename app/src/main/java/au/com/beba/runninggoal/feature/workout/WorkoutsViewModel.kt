package au.com.beba.runninggoal.feature.workout

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.domain.workout.Workout
import au.com.beba.runninggoal.repo.workout.WorkoutRepository
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject


class WorkoutsViewModel @Inject constructor(
        private val workoutRepository: WorkoutRepository
) : ViewModel()/*TODO:, CoroutineScope*/ {

    val workoutsLiveData: MutableLiveData<List<Workout>> = MutableLiveData()

    init {
    }

    fun fetchWorkouts(goalId: Long) {
        launch {
            workoutsLiveData.postValue(workoutRepository.getAllForGoal(goalId))
        }
    }
}