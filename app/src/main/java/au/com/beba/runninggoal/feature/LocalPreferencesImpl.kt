package au.com.beba.runninggoal.feature

import android.content.Context

private const val sharedPrefId: String = "RUNNING_GOAL_SHARED_PREF"

class LocalPreferencesImpl(private val context: Context) : LocalPreferences {

    override fun getValueByKey(key: String): String {
        val sp = context.getSharedPreferences(sharedPrefId, Context.MODE_PRIVATE)
        return sp.getString(key, "")
    }
}