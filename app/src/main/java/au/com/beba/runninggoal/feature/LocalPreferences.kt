package au.com.beba.runninggoal.feature

interface LocalPreferences {
    fun getValueByKey(key: String): String
}
