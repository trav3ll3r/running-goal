package au.com.beba.runninggoal.networking.source

import android.util.Log
import au.com.beba.runninggoal.networking.model.ApiSourceProfile
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.time.LocalDate
import kotlin.coroutines.experimental.CoroutineContext

class StravaApiSource
constructor(private val networkingContext: CoroutineContext = DefaultDispatcher,
            private val sourceProfile: ApiSourceProfile) : ApiSource {

    companion object {
        private val TAG = StravaApiSource::class.java.simpleName
    }

    private val client: OkHttpClient = OkHttpClient()

    override suspend fun getDistanceForDateRange(start: LocalDate, end: LocalDate): Float = withContext(networkingContext) {
        val url = "https://www.strava.com/api/v3/athlete"

        val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer %s".format(sourceProfile.accessToken))
                .addHeader("accept", "application/json")
                .build()

        val responseDef = executeRequest(request)

        val response = responseDef.await()
        Log.v(TAG, "code=%s".format(response?.code() ?: "UNKNOWN"))
        Log.v(TAG, "body=%s".format(response?.body()?.string() ?: "NULL BODY"))

        if (response?.body() != null) 999f else -1f
    }

    private fun executeRequest(request: Request): Deferred<Response?> = async(networkingContext) {
        client.newCall(request).execute()
    }
}