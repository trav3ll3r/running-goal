package au.com.beba.runninggoal.di.module

import au.com.beba.runninggoal.App
import au.com.beba.runninggoal.feature.LocalPreferences
import au.com.beba.runninggoal.feature.LocalPreferencesImpl
import au.com.beba.runninggoal.repo.GoalRepo
import au.com.beba.runninggoal.repo.GoalRepository
import au.com.beba.runninggoal.repo.SyncSourceRepo
import au.com.beba.runninggoal.repo.SyncSourceRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun localPreferences(application: App): LocalPreferences = LocalPreferencesImpl(application.applicationContext)

    @Provides
    @Singleton
    fun goalRepository(application: App): GoalRepository = GoalRepo.getInstance(application.applicationContext)

    @Provides
    @Singleton
    fun syncSourceRepository(application: App): SyncSourceRepository = SyncSourceRepo.getInstance(application.applicationContext)

    //TODO: USE
//    @Provides @Singleton
//    fun apiSource(localStorage: LocalPreferences): ApiSource {
//        val stravaAccessToken = localStorage.getValueByKey("ACCESS_TOKEN")
//
//        return StravaApiSource(sourceProfile = ApiSourceProfile(stravaAccessToken))
//    }
}