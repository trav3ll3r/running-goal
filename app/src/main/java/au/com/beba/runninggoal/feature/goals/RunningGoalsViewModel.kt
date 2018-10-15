package au.com.beba.runninggoal.feature.goals

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.repo.goal.GoalRepository
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import timber.log.Timber
import javax.inject.Inject


class RunningGoalsViewModel
@Inject constructor(
        private val goalRepository: GoalRepository)
    : ViewModel() {

    val goalsLiveData: MutableLiveData<List<RunningGoal>> = MutableLiveData()

    fun fetchGoals() {
        Timber.i("fetchGoals")
        launch {
            val goals = withContext(DefaultDispatcher) {
                goalRepository.fetchGoals()
            }
            Timber.d("fetchGoals | goals count = %s", goals.size)
            goalsLiveData.postValue(goals)
        }
    }
}