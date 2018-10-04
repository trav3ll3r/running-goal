package au.com.beba.runninggoal.networking.source

import android.util.Log
import au.com.beba.runninggoal.networking.model.ApiSourceProfile
import au.com.beba.runninggoal.networking.model.AthleteActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.experimental.DefaultDispatcher
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import kotlin.coroutines.experimental.CoroutineContext


class StravaSyncSourceProvider
constructor(networkingContext: CoroutineContext = DefaultDispatcher)
    : CommonSyncSourceProvider(networkingContext) {

    companion object {
        private val TAG = StravaSyncSourceProvider::class.java.simpleName
    }

    private var sourceProfile: ApiSourceProfile? = null

    override fun setSyncSourceProfile(apiSourceProfile: ApiSourceProfile) {
        sourceProfile = apiSourceProfile
    }

    override suspend fun getAthleteActivitiesForDateRange(startTime: Long, endTime: Long): List<AthleteActivity> {
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

        return allActivities
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
        val activities = marshalResponse(responseDef.await())
        Log.v(TAG, "distanceForActivities | page | activities=%s".format(activities.size))

        activities.forEach {
            Log.v(TAG, "distanceForActivities | run {name=%s distance=%s}".format(it.name, it.distanceInMetres))
        }

        Log.v(TAG, "distanceForActivities | page | distanceMetre=%s".format(activities.asSequence().map { it.distanceInMetres }.sum()))
        return activities
    }

    private fun marshalResponse(response: Response?): List<AthleteActivity> {
        Log.i(TAG, "marshalResponse")
        Log.v(TAG, "marshalResponse | code=%s".format(response?.code() ?: "UNKNOWN"))
        val responseBody = response?.body()?.string() ?: ""
        //logBody(responseBody)

        return if (response != null) {
            val listType = object : TypeToken<ArrayList<AthleteActivity>>() {}.type
            gson.fromJson(responseBody, listType)
        } else {
            emptyList()
        }
    }

//    private fun logBody(body: String) {
//        Log.i(TAG, "logBody")
//        Log.v(TAG, "logBody | body=%s".format(body))
//    }

    private val gson: Gson
        get() {
            val gb = GsonBuilder()
//            gb.registerTypeAdapter(Number::class.java, object : JsonSerializer<String> {
//                override fun serialize(src: String?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
//                    return if (src != null) {
//                        val f = src.toFloat()
//                        JsonPrimitive(f.toLong())
//                    } else {
//                        JsonPrimitive(0L)
//                    }
//                }
//            })
            return gb.create()
        }
}