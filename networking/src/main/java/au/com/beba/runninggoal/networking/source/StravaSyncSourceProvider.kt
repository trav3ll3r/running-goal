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
import kotlin.coroutines.experimental.CoroutineContext


/**
 * Simple model to map JSON payload into AthleteActivity model
 */
data class AthleteActivity(val name: String, val distance: Float, val private: Boolean)


class StravaSyncSourceProvider
constructor(private val networkingContext: CoroutineContext = DefaultDispatcher) : CommonSyncSourceProvider(networkingContext) {

    companion object {
        private val TAG = StravaSyncSourceProvider::class.java.simpleName
    }

    private var sourceProfile: ApiSourceProfile? = null

    override fun setSyncSourceProfile(apiSourceProfile: ApiSourceProfile) {
        sourceProfile = apiSourceProfile
    }

    override suspend fun getDistanceForDateRange(startTime: Long, endTime: Long): Float = withContext(networkingContext) {
        val allActivities = mutableListOf<AthleteActivity>()

        Log.i(TAG, "getDistanceForDateRange | startTime=%s".format(startTime))
        Log.i(TAG, "getDistanceForDateRange | endTime=%s".format(endTime))
        val url = HttpUrl.Builder()
                .scheme("https")
                .host("www.strava.com")
                .addPathSegments("api/v3")
                .addPathSegments("athlete/activities")
                .setQueryParameter("after", "%s".format(startTime))
                .addQueryParameter("before", "%s".format(endTime))
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


        val activities = extractActivities(responseDef.await())
        Log.v(TAG, "distanceForActivities | page | activities=%s".format(activities.size))

        activities.forEach {
            Log.v(TAG, "distanceForActivities | run {name=%s distance=%s private=%b}".format(it.name, it.distance, it.private))
        }


        Log.v(TAG, "distanceForActivities | page | distanceMetre=%s".format(activities.map { it.distance }.sum()))
        return activities
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
//        Log.i(TAG, "logBody")
//        Log.v(TAG, "logBody | body=%s".format(body))
    }
}