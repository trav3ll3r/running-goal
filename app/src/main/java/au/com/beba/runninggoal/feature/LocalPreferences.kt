package au.com.beba.runninggoal.feature

interface LocalPreferences {
    fun getValueByKey(key: String): String
    fun setValueForKey(key: String, value: String?)
}
