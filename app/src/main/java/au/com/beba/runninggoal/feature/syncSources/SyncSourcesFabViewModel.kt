package au.com.beba.runninggoal.feature.syncSources

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.domain.event.PublisherEventCentre
import au.com.beba.runninggoal.domain.event.Subscriber
import au.com.beba.runninggoal.domain.event.SubscriberPostbox
import au.com.beba.runninggoal.domain.event.SyncSourceChange
import au.com.beba.runninggoal.domain.event.SyncSourceDelete
import au.com.beba.runninggoal.feature.navigation.ShowEditSyncSourceEvent
import au.com.beba.runninggoal.feature.sync.SyncFeature
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject


class SyncSourcesFabViewModel
@Inject constructor(
        private val syncFeature: SyncFeature,
        private val pubEventCentre: PublisherEventCentre
) : ViewModel(), Subscriber {

    val fabLiveData: MutableLiveData<FabModel> = MutableLiveData()

    init {
        update()
//        subEventCentre.registerSubscriber(this, SyncSourceChange::class)
//        subEventCentre.registerSubscriber(this, SyncSourceDelete::class)
    }

    override fun onCleared() {
        super.onCleared()
//        subEventCentre.unregisterSubscriber(this, SyncSourceChange::class)
//        subEventCentre.unregisterSubscriber(this, SyncSourceDelete::class)
    }

    private var currentAction: FabActionType = FabActionType.NONE
        get() {
            Timber.i("currentAction | get")
            val result = when {
                !syncFeature.isSuspended -> FabActionType.CREATE_SYNC_SOURCE
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
                FabActionType.CREATE_SYNC_SOURCE -> pubEventCentre.publish(ShowEditSyncSourceEvent(null))
                FabActionType.NONE -> Unit
            }
        }
    }
}

data class FabModel(val actionType: FabActionType)

enum class FabActionType {
    NONE,
    CREATE_SYNC_SOURCE
}