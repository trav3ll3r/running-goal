package au.com.beba.runninggoal.di.module

import au.com.beba.runninggoal.networking.source.StravaSyncSourceProvider
import au.com.beba.runninggoal.networking.source.SyncSourceProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class FlavorAppModule {

    @Provides
    @Singleton
    fun apiSource(): SyncSourceProvider = StravaSyncSourceProvider()

}