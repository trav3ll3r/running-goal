package au.com.beba.runninggoal.feature.navigation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.MainActivity
import au.com.beba.runninggoal.domain.event.Subscriber
import au.com.beba.runninggoal.domain.event.SubscriberEventCentre
import au.com.beba.runninggoal.domain.event.SubscriberPostbox
import au.com.beba.runninggoal.feature.goals.GoalViewHolder
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject


class NavigationViewModel
@Inject constructor(
        private val eventCentre: SubscriberEventCentre
) : ViewModel(), Subscriber {

    private val currentScreen: AppScreen = GoalsScreen()
    val navLiveData: MutableLiveData<AppScreen> = MutableLiveData()

    init {
        eventCentre.registerSubscriber(this, GoalViewHolder.GoalSelectedEvent::class)
        eventCentre.registerSubscriber(this, MainActivity.ManageSyncSourcesEvent::class)

        navLiveData.postValue(currentScreen)
    }

    override fun onCleared() {
        super.onCleared()
        eventCentre.unregisterSubscriber(this, GoalViewHolder.GoalSelectedEvent::class)
        eventCentre.unregisterSubscriber(this, MainActivity.ManageSyncSourcesEvent::class)
    }

    override fun newEvent(postbox: WeakReference<SubscriberPostbox>) {
        Timber.i("newEvent")
        Timber.d("newEvent | postbox=%s", postbox.get()?.describe())
        val pb = postbox.get()
        val event = pb?.takeLast()

        when (event) {
            is GoalViewHolder.GoalSelectedEvent -> navLiveData.postValue(GoalDetailsScreen(event.runningGoal, event.viewHolder))
            is MainActivity.ManageSyncSourcesEvent -> navLiveData.postValue(SyncSourcesScreen())
            else -> super.newEvent(postbox)
        }
    }
}