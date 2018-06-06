package au.com.beba.runninggoal.di.module

import au.com.beba.runninggoal.App
import au.com.beba.runninggoal.feature.LocalPreferences
import au.com.beba.runninggoal.feature.LocalPreferencesImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides @Singleton
    fun localPreferences(application: App): LocalPreferences = LocalPreferencesImpl(application.applicationContext)
}