package au.com.beba.runninggoal

import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext

fun launchSilent(
        context: CoroutineContext = DefaultDispatcher,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        parent: Job? = null,
        block: suspend CoroutineScope.() -> Unit
) {
    launch(context, start, parent, block)
}