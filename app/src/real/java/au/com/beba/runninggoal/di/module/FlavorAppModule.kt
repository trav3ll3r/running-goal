package au.com.beba.runninggoal.di.module

import au.com.beba.runninggoal.repo.sync.providers.StravaSyncSourceProvider
import au.com.beba.runninggoal.repo.sync.providers.SyncSourceProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class FlavorAppModule {

    @Provides
    @Singleton
    fun apiSource(): SyncSourceProvider = StravaSyncSourceProvider()

}