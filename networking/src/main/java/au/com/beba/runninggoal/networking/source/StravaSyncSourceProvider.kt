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


/**
 * Simple model to map JSON payload into AthleteActivity model
 */
data class AthleteActivity(val distance: Float)


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

        val allActivities = mutableListOf<AthleteActivity>()

        val url = HttpUrl.Builder()
                .scheme("https")
                .host("www.strava.com")
                .addPathSegments("api/v3")
                .addPathSegments("athlete/activities")
                .setQueryParameter("after", startTime.toString())
                .addQueryParameter("before", endTime.toString())
                .addQueryParameter("per_page", "100") // DEFAULT=30; MAX=200

        var page = 0
        do {
            page += 1
            url.setQueryParameter("page", page.toString())
            val activities = getActivitiesForUrl(url)
            allActivities.addAll(activities)
        } while (activities.isNotEmpty())

        distanceForAllActivities(allActivities)
    }

    private suspend fun getActivitiesForUrl(url: HttpUrl.Builder): List<AthleteActivity> {
        Log.i(TAG, "getActivitiesForUrl")
        val request = Request.Builder()
                .url(url.build())
                .addHeader("Authorization", "Bearer %s".format(sourceProfile?.accessToken
                        ?: "NOT SET"))
                .addHeader("accept", "application/json")
                .build()

        Log.v(TAG, "getActivitiesForUrl | request url=%s".format(request.url()))

        val responseDef = executeRequest(request)

        return extractActivities(responseDef.await())
    }

    private fun extractActivities(response: Response?): List<AthleteActivity> {
        Log.i(TAG, "extractActivities")
        Log.v(TAG, "extractActivities | code=%s".format(response?.code() ?: "UNKNOWN"))
        val responseBody = response?.body()?.string() ?: ""
        logBody(responseBody)

        return if (response != null) {
            val listType = object : TypeToken<ArrayList<AthleteActivity>>() {}.type
            Gson().fromJson(responseBody, listType)
        } else {
            emptyList()
        }
    }

    private fun distanceForAllActivities(activities: List<AthleteActivity>): Float {
        Log.v(TAG, "distanceForAllActivities")
        val distanceMetre: Float = activities.map { it.distance }.sum()
        Log.v(TAG, "distanceForAllActivities | distanceMetre=$distanceMetre")
        return distanceMetre
    }

    private fun logBody(body: String) {
        Log.i(TAG, "logBody")
        Log.v(TAG, "logBody | body=%s".format(body))
    }
}