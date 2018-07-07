package au.com.beba.runninggoal.feature.goals

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.repo.GoalRepository
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import javax.inject.Inject


class RunningGoalViewModel @Inject constructor(private val goalRepository: GoalRepository) : ViewModel() {

    fun fetchGoals(): LiveData<List<RunningGoal>> {
        launch {
            withContext(DefaultDispatcher) { goalRepository.fetchGoals() }
        }
        return goalRepository.getGoals()
    }
}