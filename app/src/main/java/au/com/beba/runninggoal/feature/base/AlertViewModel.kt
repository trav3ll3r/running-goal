package au.com.beba.runninggoal.feature.base

import androidx.lifecycle.ViewModel
import au.com.beba.runninggoal.domain.event.Subscriber
import au.com.beba.runninggoal.domain.event.SubscriberEventCentre
import au.com.beba.runninggoal.domain.event.SubscriberPostbox
import au.com.beba.runninggoal.feature.appevents.NoDefaultSyncSource
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject


class AlertViewModel @Inject constructor(
        private val eventCentre: SubscriberEventCentre
) : ViewModel(), Subscriber {

    init {
        eventCentre.registerSubscriber(this, NoDefaultSyncSource::class)
    }

    override fun onCleared() {
        super.onCleared()
        eventCentre.unregisterSubscriber(this, NoDefaultSyncSource::class)
    }

    override fun newEvent(postbox: WeakReference<SubscriberPostbox>) {
        Timber.i("newEvent")
        Timber.d("newEvent | postbox=%s", postbox.get()?.describe())
        val pb = postbox.get()
        val event = pb?.takeLast()

        when (event) {
            is NoDefaultSyncSource -> alertUser()
            else -> super.newEvent(postbox)
        }
    }

    /**
     * NOTIFY USER ABOUT MISSING DEFAULT SYNC SOURCE
     */
    private fun alertUser() {
//                    val dialog = AlertDialog.Builder(ctx)
//                            .setCancelable(true)
//                            .setTitle(R.string.sync_now_error)
//                            .setMessage(R.string.message_no_default_sync_sources)
//                            .setPositiveButton(android.R.string.ok, null)
//                            .create()
//
//                    if (!isFinishing) {
//                        dialog.show()
//                    }
    }
}