package au.com.beba.runninggoal.feature.goal

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.models.AthleteActivity
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.repo.GoalRepository
import kotlinx.coroutines.experimental.async
import javax.inject.Inject


class GoalViewModel @Inject constructor(private val goalRepository: GoalRepository) : ViewModel() {

    val goalLiveData: MutableLiveData<RunningGoal?> = MutableLiveData()
    val activitiesLiveData: MutableLiveData<List<AthleteActivity>> = MutableLiveData()

    fun fetchGoal(goalId: Long) {
        async {
            goalLiveData.postValue(goalRepository.getById(goalId))
        }
    }

    fun fetchActivities(goalId: Long) {
        async {
            //activitiesLiveData.postValue(activitiesRepository.getForGoal(goalId))
            val mockList: MutableList<AthleteActivity> = mutableListOf()
            (0..10).forEach { _ -> mockList.add(AthleteActivity()) }
            activitiesLiveData.postValue(mockList)
        }
    }
}