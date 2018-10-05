package au.com.beba.runninggoal.feature.goals

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.domain.core.RunningGoal
import au.com.beba.runninggoal.repo.GoalRepository
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import javax.inject.Inject


class RunningGoalsViewModel @Inject constructor(private val goalRepository: GoalRepository) : ViewModel() {

    val goals: LiveData<List<RunningGoal>>
        get() = goalRepository.getGoals()

    fun fetchGoals() {
        launch {
            withContext(DefaultDispatcher) { goalRepository.fetchGoals() }
        }
    }
}