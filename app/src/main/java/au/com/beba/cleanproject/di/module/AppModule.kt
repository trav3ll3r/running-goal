package au.com.beba.cleanproject.di.module

import au.com.beba.cleanproject.App
import au.com.beba.cleanproject.feature.LocalPreferences
import au.com.beba.cleanproject.feature.LocalPreferencesImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides @Singleton
    fun localPreferences(application: App): LocalPreferences = LocalPreferencesImpl(application.applicationContext)
}