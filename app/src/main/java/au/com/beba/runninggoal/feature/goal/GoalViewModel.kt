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
import au.com.beba.runninggoal.feature.sync.SyncFeature
import au.com.beba.runninggoal.repo.workout.WorkoutRepository
import kotlinx.coroutines.experimental.launch
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject


class GoalViewModel @Inject constructor(
        private val goalFeature: GoalFeature,
        private val workoutRepository: WorkoutRepository,
        private val syncFeature: SyncFeature,
        private val eventCentre: SubscriberEventCentre
) : ViewModel(), Subscriber {

    private var currentGoalId: Long = -1
    fun getObservableGoal(goalId: Long): MutableLiveData<RunningGoal?> {
        currentGoalId = goalId
        return goalLiveData
    }

    private val goalLiveData: MutableLiveData<RunningGoal?> = MutableLiveData()
    val workoutsLiveData: MutableLiveData<List<Workout>> = MutableLiveData()

    init {
        eventCentre.registerSubscriber(this, GoalChangeEvent::class)
        eventCentre.registerSubscriber(this, WorkoutSyncEvent::class)
    }

    override fun onCleared() {
        super.onCleared()
        eventCentre.unregisterSubscriber(this, GoalChangeEvent::class)
        eventCentre.unregisterSubscriber(this, WorkoutSyncEvent::class)
    }

    fun fetchGoal() {
        goalLiveData.postValue(goalFeature.getById(currentGoalId))
    }

    fun fetchWorkouts() {
        launch {
            workoutsLiveData.postValue(workoutRepository.getAllForGoal(currentGoalId))
        }
    }

    fun syncWorkouts(context: Context?, runningGoal: RunningGoal?, jobId: Int = 1001) {
        Timber.i("syncGoals")
        if (context != null) {
            syncFeature.syncNow(context, runningGoal?.id, jobId)
        }
    }

    override fun newEvent(postbox: WeakReference<SubscriberPostbox>) {
        Timber.i("newEvent")
        Timber.d("newEvent | postbox=%s", postbox.get()?.describe())
        val pb = postbox.get()
        val event = pb?.takeLast()

        when (event) {
            is GoalChangeEvent -> fetchGoal()

            is WorkoutSyncEvent -> {
                if (event.goalId == currentGoalId) {
                    notifyView(event.isUpdating)
                    if (!event.isUpdating) {
                        fetchWorkouts()
                    }
                }
            }
            else -> super.newEvent(postbox)
        }
    }

    private fun notifyView(isBusy: Boolean) {
        val goal = goalLiveData.value
        if (isBusy) {
            goal?.view?.updating = isBusy
            goalLiveData.postValue(goal)
        } else {
            if (currentGoalId > 0) {
                fetchGoal()
            }
        }
    }
}