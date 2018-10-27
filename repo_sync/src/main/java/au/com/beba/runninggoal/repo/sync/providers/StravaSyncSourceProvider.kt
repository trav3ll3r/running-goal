package au.com.beba.runninggoal.repo.sync.providers

import au.com.beba.runninggoal.domain.workout.Workout
import au.com.beba.runninggoal.domain.workout.sync.SyncSource
import au.com.beba.runninggoal.https.HttpHeaders
import au.com.beba.runninggoal.https.HttpRequest
import au.com.beba.runninggoal.https.HttpResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.InputStreamReader


class StravaSyncSourceProvider : CommonSyncSourceProvider() {

    companion object {
        private val TAG = StravaSyncSourceProvider::class.java.simpleName

        private const val ATHLETE_ACTIVITIES_URL_TEMPLATE = "https://www.strava.com/api/v3/athlete/activities?after=%s&before=%s&per_page=%s"
        private val ATHLETE_ACTIVITIES_HEADERS = HttpHeaders()
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    private var syncSource: SyncSource? = null

    override fun setSyncSourceProfile(syncSource: SyncSource) {
        this.syncSource = syncSource
    }

    override suspend fun getWorkoutsForDateRange(startTime: Long, endTime: Long): List<Workout> {
        val allActivities = mutableListOf<Workout>()

        // page DEFAULT=30; MAX=200
        val activitiesUrl = ATHLETE_ACTIVITIES_URL_TEMPLATE.format(startTime, endTime, "100")

        /*
         * Official StravaAPI guideline is to keep requesting
         * page after page until an empty page is returned.
         * Only then it's guaranteed all AthleteActivities for
         * before:after range have been retrieved.
         */
        var page = 0
        do {
            page += 1
            val url = activitiesUrl + "&page=%s".format(page)
            val activities = getActivitiesForUrl(url)
            allActivities.addAll(activities)
        } while (activities.isNotEmpty())

        return allActivities
    }

    private suspend fun getActivitiesForUrl(url: String): List<Workout> {
        logger.info("getActivitiesForUrl")
        ATHLETE_ACTIVITIES_HEADERS.addHeader("accept", "application/json")
        ATHLETE_ACTIVITIES_HEADERS.addHeader("Authorization", "Bearer %s".format(syncSource?.accessToken
                ?: "NOT SET"))
        val request = HttpRequest("GET", url, ATHLETE_ACTIVITIES_HEADERS)

        return marshalResponse(executeRequest(request).await())
    }

    private fun marshalResponse(response: HttpResponse): List<Workout> {
        logger.info("marshalResponse")
        val result = if (response.hasBody) {
            val listType = object : TypeToken<ArrayList<Workout>>() {}.type
            val reader = InputStreamReader(response.body, Charsets.UTF_8)
            val result: List<Workout> = gson.fromJson(reader, listType)
            logger.info("marshalResponse | has body")
            result
        } else {
            logger.info("marshalResponse | empty body")
            emptyList()
        }

        logger.info("marshalResponse | return %s Workouts".format(result.size))
        return result
    }

    private val gson: Gson
        get() {
            val gb = GsonBuilder()
            return gb.create()
        }
}