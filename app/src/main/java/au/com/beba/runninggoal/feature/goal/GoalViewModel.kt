package au.com.beba.runninggoal.feature.goal

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.feature.progressSync.SyncSourceIntentService
import au.com.beba.runninggoal.models.AthleteActivity
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.repo.AthleteActivityRepository
import au.com.beba.runninggoal.repo.GoalRepository
import au.com.beba.runninggoal.repo.SyncSourceRepository
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import javax.inject.Inject
import kotlin.coroutines.experimental.buildSequence


class GoalViewModel @Inject constructor(
        private val goalRepository: GoalRepository,
        private val athleteActivityRepository: AthleteActivityRepository,
        private val syncSourceRepository: SyncSourceRepository
) : ViewModel() {

    companion object {
        private val TAG = GoalViewModel::class.java.simpleName
    }

    val goalLiveData: MutableLiveData<RunningGoal?> = MutableLiveData()
    val activitiesLiveData: MutableLiveData<List<AthleteActivity>> = MutableLiveData()

    fun fetchGoal(goalId: Long) {
        async {
            goalLiveData.postValue(goalRepository.getById(goalId))
        }
    }

    fun fetchActivities(goalId: Long) {
        async {
            val list: MutableList<AthleteActivity> = mutableListOf()
            list.addAll(athleteActivityRepository.getAllForGoal(goalId))
            activitiesLiveData.postValue(list)
        }
    }

    fun syncWorkouts(context: Context?, runningGoal: RunningGoal?, jobId: Int = 1001) {
        Log.i(TAG, "syncGoals")
        if (context != null) {

            launch {
                val syncSource = syncSourceRepository.getDefaultSyncSource()
                if (syncSource.isDefault) {
                    // Enqueues new JobIntentService
                    SyncSourceIntentService.enqueueWork(
                            context,
                            SyncSourceIntentService.buildIntent(runningGoal),
                            jobId)
                }
            }
        }
    }
}