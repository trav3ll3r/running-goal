package au.com.beba.runninggoal.di.module

import au.com.beba.runninggoal.App
import au.com.beba.runninggoal.feature.LocalPreferences
import au.com.beba.runninggoal.feature.LocalPreferencesImpl
import au.com.beba.runninggoal.networking.model.ApiSourceProfile
import au.com.beba.runninggoal.networking.source.ApiSource
import au.com.beba.runninggoal.networking.source.StravaApiSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides @Singleton
    fun localPreferences(application: App): LocalPreferences = LocalPreferencesImpl(application.applicationContext)

    @Provides @Singleton
    fun apiSource(localStorage: LocalPreferences): ApiSource {
        val stravaAccessToken = localStorage.getValueByKey("ACCESS_TOKEN")

        return  StravaApiSource(sourceProfile = ApiSourceProfile(stravaAccessToken))
    }
}