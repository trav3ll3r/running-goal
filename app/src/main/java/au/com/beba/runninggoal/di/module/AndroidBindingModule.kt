package au.com.beba.runninggoal.di.module

import au.com.beba.runninggoal.MainActivity
import au.com.beba.runninggoal.di.ActivityScope
import au.com.beba.runninggoal.di.ServiceScope
import au.com.beba.runninggoal.feature.progressSync.ApiSourceIntentService
import au.com.beba.runninggoal.feature.syncSources.SyncSourcesActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AndroidBindingModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun dataSourcesActivity(): SyncSourcesActivity

    @ServiceScope
    @ContributesAndroidInjector
    abstract fun apiSourceIntentService(): ApiSourceIntentService

//    @FragmentScope
//    @ContributesAndroidInjector
//    abstract fun mainFragment(): MainFragment
}