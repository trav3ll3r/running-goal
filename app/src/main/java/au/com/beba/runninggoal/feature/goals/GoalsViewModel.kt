package au.com.beba.runninggoal.feature.goals

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.feature.goal.GoalFeature
import timber.log.Timber
import javax.inject.Inject


class GoalsViewModel
@Inject constructor(
        private val goalFeature: GoalFeature
) : ViewModel() {

    val goalsLiveData: MutableLiveData<List<RunningGoal>> = MutableLiveData()

    fun fetchGoals() {
        Timber.i("fetchGoals")
        val goals = goalFeature.fetchGoals()
        Timber.d("fetchGoals | goals count = %s", goals.size)
        goalsLiveData.postValue(goals)
    }
}