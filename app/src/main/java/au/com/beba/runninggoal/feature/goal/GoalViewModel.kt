package au.com.beba.runninggoal.feature.goal

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.domain.Workout
import au.com.beba.runninggoal.feature.progressSync.SyncSourceIntentService
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.repo.WorkoutRepository
import au.com.beba.runninggoal.repo.GoalRepository
import au.com.beba.runninggoal.repo.SyncSourceRepository
import kotlinx.coroutines.experimental.*
import javax.inject.Inject


class GoalViewModel @Inject constructor(
        private val goalRepository: GoalRepository,
        private val workoutRepository: WorkoutRepository,
        private val syncSourceRepository: SyncSourceRepository
) : ViewModel() {

    companion object {
        private val TAG = GoalViewModel::class.java.simpleName
    }

    val goalLiveData: MutableLiveData<RunningGoal?> = MutableLiveData()
    val activitiesLiveData: MutableLiveData<List<Workout>> = MutableLiveData()

    fun fetchGoal(goalId: Long) {
        async {
            goalLiveData.postValue(goalRepository.getById(goalId))
        }
    }

    fun fetchActivities(goalId: Long) {
        async {
            val list: MutableList<Workout> = mutableListOf()
            list.addAll(workoutRepository.getAllForGoal(goalId))
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