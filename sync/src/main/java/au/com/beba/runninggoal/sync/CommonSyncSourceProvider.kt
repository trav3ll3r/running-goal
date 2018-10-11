package au.com.beba.runninggoal.sync

import au.com.beba.runninggoal.https.HttpClient
import au.com.beba.runninggoal.https.HttpClientImpl
import au.com.beba.runninggoal.https.HttpRequest
import au.com.beba.runninggoal.https.HttpResponse
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

import kotlin.coroutines.experimental.CoroutineContext

abstract class CommonSyncSourceProvider
constructor(private val networkingContext: CoroutineContext = DefaultDispatcher)
    : SyncSourceProvider {

    private val httpClient: HttpClient

    init {
        httpClient = HttpClientImpl()
    }

    override fun setSyncSourceProfile(apiSourceProfile: ApiSourceProfile) {
        /*DO NOTHING*/
    }

    protected fun executeRequest(request: HttpRequest): Deferred<HttpResponse> = async(networkingContext) {
        val responseDef = httpClient.executeRequest(request)
        responseDef.await()
    }
}