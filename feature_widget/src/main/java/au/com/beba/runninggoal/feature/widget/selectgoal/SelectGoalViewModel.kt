package au.com.beba.runninggoal.feature.widget.selectgoal

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.repo.goal.GoalRepo
import au.com.beba.runninggoal.repo.goal.GoalRepository
import kotlinx.coroutines.experimental.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class SelectGoalViewModel
    : ViewModel() {

    companion object {
        private val TAG = SelectGoalViewModel::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    private var goalRepository: GoalRepository? = null
    val goalsLiveData: MutableLiveData<List<RunningGoal>> = MutableLiveData()

    fun fetchGoals(context: Context) {
        logger.info("fetchGoals")
        if (goalRepository == null) {
            goalRepository = GoalRepo(context)
        }

        val goals = runBlocking { goalRepository!!.fetchGoals() }

        logger.debug("fetchGoals | goals count = %s", goals.size)
        goalsLiveData.postValue(goals)
    }
}