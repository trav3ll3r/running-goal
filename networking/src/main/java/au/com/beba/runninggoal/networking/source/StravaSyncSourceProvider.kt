package au.com.beba.runninggoal.networking.source

import android.util.Log
import au.com.beba.runninggoal.networking.model.ApiSourceProfile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.coroutines.experimental.CoroutineContext


class StravaSyncSourceProvider
constructor(private val networkingContext: CoroutineContext = DefaultDispatcher) : CommonSyncSourceProvider(networkingContext) {

    companion object {
        private val TAG = StravaSyncSourceProvider::class.java.simpleName
    }

    private var sourceProfile: ApiSourceProfile? = null

    override fun setSyncSourceProfile(apiSourceProfile: ApiSourceProfile) {
        sourceProfile = apiSourceProfile
    }

    override suspend fun getDistanceForDateRange(start: LocalDate, end: LocalDate): Float = withContext(networkingContext) {
        val startTime = start.atTime(0, 0, 0).toEpochSecond(ZoneOffset.UTC)
        val endTime = end.atTime(23, 59, 59).toEpochSecond(ZoneOffset.UTC)

        val url = HttpUrl.Builder()
                .scheme("https")
                .host("www.strava.com")
                .addPathSegments("api/v3")
                .addPathSegments("athlete/activities")
                .setQueryParameter("after", startTime.toString())
                .addQueryParameter("before", endTime.toString())
                .build()

        val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer %s".format(sourceProfile?.accessToken
                        ?: "NOT SET"))
                .addHeader("accept", "application/json")
                .build()

        Log.v(TAG, "request url=%s".format(request.url()))

        val responseDef = executeRequest(request)

        processResponse(responseDef.await())
    }

    private fun processResponse(response: Response?): Float {
        Log.v(TAG, "processResponse")
        Log.v(TAG, "processResponse | code=%s".format(response?.code() ?: "UNKNOWN"))
        val responseBody = response?.body()?.string()
        Log.v(TAG, "processResponse | body=%s".format(responseBody ?: "NULL BODY"))

        var distanceMetre = -1f
        if (response != null) {
            val listType = object : TypeToken<ArrayList<AthleteActivity>>() {}.type
            val activities: List<AthleteActivity> = Gson().fromJson(responseBody ?: "", listType)
            distanceMetre = activities.map { it.distance }.sum()
        }
        Log.v(TAG, "processResponse | distanceMetre=$distanceMetre")
        return distanceMetre
    }
}

data class AthleteActivity(val distance: Float)