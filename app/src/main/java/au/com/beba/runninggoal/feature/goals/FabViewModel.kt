package au.com.beba.runninggoal.feature.goals

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.MainActivity
import au.com.beba.runninggoal.domain.event.PublisherEventCentre
import au.com.beba.runninggoal.domain.event.Subscriber
import au.com.beba.runninggoal.domain.event.SubscriberEventCentre
import au.com.beba.runninggoal.domain.event.SubscriberPostbox
import au.com.beba.runninggoal.domain.event.SyncSourceChange
import au.com.beba.runninggoal.domain.event.SyncSourceDelete
import au.com.beba.runninggoal.feature.goal.GoalFeature
import au.com.beba.runninggoal.feature.sync.SyncFeature
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject


class FabViewModel
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
            val result = when (syncFeature.isReady) {
                true -> FabActionType.SYNC_ALL
                else -> FabActionType.MANAGE_SYNC_SOURCES
            }
            Timber.i("currentAction | return %s".format(result))
            return result
        }

    fun update() {
        val visible = goalFeature.isReady
        fabLiveData.postValue(FabModel(currentAction, visible))
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
                FabActionType.SYNC_ALL -> syncFeature.syncNow(context, null)
                FabActionType.MANAGE_SYNC_SOURCES -> pubEventCentre.publish(MainActivity.ManageSyncSourcesEvent())
            }
        }
    }
}

data class FabModel(val actionType: FabActionType, val visible: Boolean = true)

enum class FabActionType {
    MANAGE_SYNC_SOURCES,
    SYNC_ALL
}