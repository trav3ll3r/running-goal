package au.com.beba.runninggoal.feature.syncSources

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.domain.event.PublisherEventCentre
import au.com.beba.runninggoal.domain.workout.sync.SyncSource
import au.com.beba.runninggoal.feature.navigation.ShowEditSyncSourceEvent
import au.com.beba.runninggoal.feature.sync.SyncFeature
import timber.log.Timber
import javax.inject.Inject


class SyncSourcesViewModel
@Inject constructor(
        private val syncFeature: SyncFeature,
        private val publisherEventCentre: PublisherEventCentre
) : ViewModel() {

    val syncSourcesLiveData: MutableLiveData<List<SyncSource>> = MutableLiveData()

    fun fetchSyncSources() {
        Timber.i("fetchSyncSources")
        val goals = syncFeature.getAllConfiguredSources()
        Timber.d("fetchSyncSources | sync sources count = %s", goals.size)
        syncSourcesLiveData.postValue(goals)
    }

    fun editSyncSource(syncSourceId: Long) {
        publisherEventCentre.publish(ShowEditSyncSourceEvent(syncSourceId))
    }

    fun createSyncSource() {
        publisherEventCentre.publish(ShowEditSyncSourceEvent(null))
    }
}