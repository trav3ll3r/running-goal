package au.com.beba.runninggoal.networking.source

import au.com.beba.runninggoal.networking.model.ApiSourceProfile
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import kotlin.coroutines.experimental.CoroutineContext


abstract class CommonSyncSourceProvider
constructor(private val networkingContext: CoroutineContext = DefaultDispatcher)
    : SyncSourceProvider {

    companion object {
        private val TAG = CommonSyncSourceProvider::class.java.simpleName
    }

    private val client: OkHttpClient = OkHttpClient()

    protected fun executeRequest(request: Request): Deferred<Response?> = async(networkingContext) {
        var r: Response? = null
        if (networkAvailable()) {
            r = client.newCall(request).execute()
        }
        r
    }

    private fun networkAvailable(): Boolean {
        return true
    }

    override fun setSyncSourceProfile(apiSourceProfile: ApiSourceProfile) { /*DO NOTHING*/ }
}