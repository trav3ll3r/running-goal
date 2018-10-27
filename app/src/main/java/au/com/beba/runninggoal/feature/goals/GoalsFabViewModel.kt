package au.com.beba.runninggoal.feature.goals

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.domain.event.PublisherEventCentre
import au.com.beba.runninggoal.domain.event.Subscriber
import au.com.beba.runninggoal.domain.event.SubscriberEventCentre
import au.com.beba.runninggoal.domain.event.SubscriberPostbox
import au.com.beba.runninggoal.domain.event.SyncSourceChange
import au.com.beba.runninggoal.domain.event.SyncSourceDelete
import au.com.beba.runninggoal.feature.goal.GoalFeature
import au.com.beba.runninggoal.feature.navigation.ShowEditGoalEvent
import au.com.beba.runninggoal.feature.navigation.ShowSyncSourcesEvent
import au.com.beba.runninggoal.feature.sync.SyncFeature
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject


class GoalsFabViewModel
@Inject constructor(
        private val goalFeature: GoalFeature,
        private val syncFeature: SyncFeature,
        private val subEventCentre: SubscriberEventCentre,
        private val pubEventCentre: PublisherEventCentre
) : ViewModel(), Subscriber {

    val fabLiveData: MutableLiveData<FabModel> = MutableLiveData()

    init {
        update()
        subEventCentre.registerSubscriber(this, SyncSourceChange::class)
        subEventCentre.registerSubscriber(this, SyncSourceDelete::class)
    }

    override fun onCleared() {
        super.onCleared()
        subEventCentre.unregisterSubscriber(this, SyncSourceChange::class)
        subEventCentre.unregisterSubscriber(this, SyncSourceDelete::class)
    }

    private var currentAction: FabActionType = FabActionType.MANAGE_SYNC_SOURCES
        get() {
            Timber.i("currentAction | get")
            val result = when {
                goalFeature.isReady && goalFeature.fetchGoals().isEmpty() -> FabActionType.CREATE_GOAL
                syncFeature.isReady -> FabActionType.SYNC_ALL
                !syncFeature.isReady -> FabActionType.MANAGE_SYNC_SOURCES
                else -> FabActionType.NONE
            }
            Timber.i("currentAction | return %s".format(result))
            return result
        }

    fun update() {
        fabLiveData.postValue(FabModel(currentAction))
    }

    override fun newEvent(postbox: WeakReference<SubscriberPostbox>) {
        Timber.i("newEvent")
        Timber.d("newEvent | postbox=%s", postbox.get()?.describe())
        val pb = postbox.get()
        val event = pb?.takeLast()

        when (event) {
            is SyncSourceChange,
            is SyncSourceDelete -> update()
            else -> super.newEvent(postbox)
        }
    }

    fun fabAction(context: Context?) {
        if (context != null) {
            when (currentAction) {
                FabActionType.CREATE_GOAL -> pubEventCentre.publish(ShowEditGoalEvent(null))
                FabActionType.SYNC_ALL -> syncFeature.syncNow(context, null)
                FabActionType.MANAGE_SYNC_SOURCES -> pubEventCentre.publish(ShowSyncSourcesEvent())
                FabActionType.NONE -> Unit
            }
        }
    }
}

data class FabModel(val actionType: FabActionType)

enum class FabActionType {
    NONE,
    CREATE_GOAL,
    MANAGE_SYNC_SOURCES,
    SYNC_ALL
}