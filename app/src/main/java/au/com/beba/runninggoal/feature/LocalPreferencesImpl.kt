package au.com.beba.runninggoal.feature

import android.content.Context
import android.content.SharedPreferences

private const val sharedPrefId: String = "RUNNING_GOAL_SHARED_PREFS"

class LocalPreferencesImpl(private val context: Context) : LocalPreferences {

    override fun getValueByKey(key: String): String {
        return preferences.getString(key, "")
    }

    override fun setValueForKey(key: String, value: String?) {
        preferences
                .edit()
                .putString(key, "")
                .apply()
    }

    private val preferences: SharedPreferences
        get() {
            return context.getSharedPreferences(sharedPrefId, Context.MODE_PRIVATE)
        }

}