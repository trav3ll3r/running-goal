package au.com.beba.runninggoal.feature.goal

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.domain.event.GoalChangeEvent
import au.com.beba.runninggoal.domain.event.PublisherEventCentre
import au.com.beba.runninggoal.domain.event.Subscriber
import au.com.beba.runninggoal.domain.event.SubscriberEventCentre
import au.com.beba.runninggoal.domain.event.SubscriberPostbox
import au.com.beba.runninggoal.domain.event.WorkoutSyncEvent
import au.com.beba.runninggoal.domain.workout.Workout
import au.com.beba.runninggoal.feature.navigation.ShowEditGoalEvent
import au.com.beba.runninggoal.feature.sync.SyncFeature
import au.com.beba.runninggoal.feature.workout.WorkoutFeature
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject


class GoalDetailsViewModel @Inject constructor(
        private val goalFeature: GoalFeature,
        private val workoutFeature: WorkoutFeature,
        private val syncFeature: SyncFeature,
        private val subscriberEventCentre: SubscriberEventCentre,
        private val publisherEventCentre: PublisherEventCentre
) : ViewModel(), Subscriber {

    private var currentGoalId: Long = -1
    fun getObservableGoal(goalId: Long): MutableLiveData<RunningGoal?> {
        currentGoalId = goalId
        return goalLiveData
    }

    private val goalLiveData: MutableLiveData<RunningGoal?> = MutableLiveData()
    val workoutsLiveData: MutableLiveData<List<Workout>> = MutableLiveData()

    init {
        subscriberEventCentre.registerSubscriber(this, GoalChangeEvent::class)
        subscriberEventCentre.registerSubscriber(this, WorkoutSyncEvent::class)
    }

    override fun onCleared() {
        super.onCleared()
        subscriberEventCentre.unregisterSubscriber(this, GoalChangeEvent::class)
        subscriberEventCentre.unregisterSubscriber(this, WorkoutSyncEvent::class)
    }

    fun fetchGoal() {
        goalLiveData.postValue(goalFeature.getById(currentGoalId))
    }

    fun fetchWorkouts() {
        workoutsLiveData.postValue(workoutFeature.getAllForGoal(currentGoalId))
    }

    fun syncWorkouts(context: Context?, runningGoal: RunningGoal?) {
        Timber.i("syncGoals")
        if (context != null) {
            syncFeature.syncNow(context, runningGoal?.id)
        }
    }

    fun editGoal(goalId: Long) {
        publisherEventCentre.publish(ShowEditGoalEvent(goalId))
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