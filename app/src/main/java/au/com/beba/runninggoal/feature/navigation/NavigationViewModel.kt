package au.com.beba.runninggoal.feature.navigation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.domain.event.Event
import au.com.beba.runninggoal.domain.event.Subscriber
import au.com.beba.runninggoal.domain.event.SubscriberEventCentre
import au.com.beba.runninggoal.domain.event.SubscriberPostbox
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject


class NavigationViewModel
@Inject constructor(
        private val eventCentre: SubscriberEventCentre
) : ViewModel(), Subscriber {

    private val currentScreen: Event = ShowGoalsEvent()
    val navLiveData: MutableLiveData<Event> = MutableLiveData()

    init {
        eventCentre.registerSubscriber(this, ShowGoalDetailsEvent::class)
        eventCentre.registerSubscriber(this, ShowEditGoalEvent::class)
        eventCentre.registerSubscriber(this, ShowSyncSourcesEvent::class)
        eventCentre.registerSubscriber(this, ShowEditSyncSourceEvent::class)

        navLiveData.postValue(currentScreen)
    }

    override fun onCleared() {
        super.onCleared()
        eventCentre.unregisterSubscriber(this, ShowGoalDetailsEvent::class)
        eventCentre.unregisterSubscriber(this, ShowEditGoalEvent::class)
        eventCentre.unregisterSubscriber(this, ShowSyncSourcesEvent::class)
        eventCentre.unregisterSubscriber(this, ShowEditSyncSourceEvent::class)
    }

    override fun newEvent(postbox: WeakReference<SubscriberPostbox>) {
        Timber.i("newEvent")
        Timber.d("newEvent | postbox=%s", postbox.get()?.describe())
        val pb = postbox.get()
        val event = pb?.takeLast()

        when (event) {
            is ShowGoalDetailsEvent,
            is ShowEditGoalEvent,
            is ShowSyncSourcesEvent,
            is ShowEditSyncSourceEvent -> navLiveData.postValue(event)
            else -> super.newEvent(postbox)
        }
    }
}