package au.com.beba.runninggoal.feature.goal

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.domain.event.GoalChangeEvent
import au.com.beba.runninggoal.domain.event.Subscriber
import au.com.beba.runninggoal.domain.event.SubscriberEventCentre
import au.com.beba.runninggoal.domain.event.SubscriberPostbox
import au.com.beba.runninggoal.domain.event.WorkoutSyncEvent
import au.com.beba.runninggoal.domain.workout.Workout
import au.com.beba.runninggoal.feature.progressSync.SyncSourceIntentService
import au.com.beba.runninggoal.repo.goal.GoalRepository
import au.com.beba.runninggoal.repo.sync.SyncSourceRepository
import au.com.beba.runninggoal.repo.workout.WorkoutRepository
import kotlinx.coroutines.experimental.launch
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject


class GoalViewModel @Inject constructor(
        private val goalRepository: GoalRepository,
        private val workoutRepository: WorkoutRepository,
        private val syncSourceRepository: SyncSourceRepository,
        eventCentre: SubscriberEventCentre
) : ViewModel(), Subscriber {

    val goalLiveData: MutableLiveData<RunningGoal?> = MutableLiveData()
    val workoutsLiveData: MutableLiveData<List<Workout>> = MutableLiveData()

    init {
        eventCentre.registerSubscriber(this, GoalChangeEvent::class.java.canonicalName!!)
        eventCentre.registerSubscriber(this, WorkoutSyncEvent::class.java.canonicalName!!)
    }

    fun fetchGoal(goalId: Long) {
        launch {
            goalLiveData.postValue(goalRepository.getById(goalId))
        }
    }

    fun fetchWorkouts(goalId: Long) {
        launch {
            workoutsLiveData.postValue(workoutRepository.getAllForGoal(goalId))
        }
    }

    fun syncWorkouts(context: Context?, runningGoal: RunningGoal?, jobId: Int = 1001) {
        Timber.i("syncGoals")
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

    override fun newEvent(postbox: WeakReference<SubscriberPostbox>) {
        Timber.i("newEvent")
        val pb = postbox.get()
        val event = pb?.takeLast()

        when (event) {
            is WorkoutSyncEvent -> notifyView(event.isUpdating)
            is GoalChangeEvent -> fetchGoal(event.goalId)
            else -> super.newEvent(postbox)
        }
    }

    private fun notifyView(isBusy: Boolean) {
        val goal = goalLiveData.value
        if (isBusy) {
            goal?.view?.updating = isBusy
            goalLiveData.postValue(goal)
        } else {
            if (goal?.id != null) {
                fetchGoal(goal.id)
            }
        }
    }
}