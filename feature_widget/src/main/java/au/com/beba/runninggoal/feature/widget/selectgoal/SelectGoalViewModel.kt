package au.com.beba.runninggoal.feature.widget.selectgoal

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.repo.goal.GoalRepo
import au.com.beba.runninggoal.repo.goal.GoalRepository
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class SelectGoalViewModel
    : ViewModel() {

    companion object {
        private val TAG = SelectGoalViewModel::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    private lateinit var goalRepository: GoalRepository
    val goalsLiveData: MutableLiveData<List<RunningGoal>> = MutableLiveData()

    fun fetchGoals(context: Context) {

        goalRepository = GoalRepo(context)

        logger.info("fetchGoals")
        launch {
            val goals = withContext(Dispatchers.Default) {
                goalRepository.fetchGoals()
            }
            logger.debug("fetchGoals | goals count = %s", goals.size)
            goalsLiveData.postValue(goals)
        }
    }
}