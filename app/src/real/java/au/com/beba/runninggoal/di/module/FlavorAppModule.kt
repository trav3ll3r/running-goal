package au.com.beba.runninggoal.di.module

import au.com.beba.runninggoal.https.HttpClient
import au.com.beba.runninggoal.https.HttpClientImpl
import au.com.beba.runninggoal.sync.StravaSyncSourceProvider
import au.com.beba.runninggoal.sync.SyncSourceProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class FlavorAppModule {

    @Provides
    @Singleton
    fun httpClient(): HttpClient = HttpClientImpl()

    @Provides
    @Singleton
    fun apiSource(httpClient: HttpClient): SyncSourceProvider = StravaSyncSourceProvider(httpClient)

}