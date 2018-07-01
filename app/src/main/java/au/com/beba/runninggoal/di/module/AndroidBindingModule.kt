package au.com.beba.runninggoal.di.module

import au.com.beba.runninggoal.MainActivity
import au.com.beba.runninggoal.di.ActivityScope
import au.com.beba.runninggoal.di.FragmentScope
import au.com.beba.runninggoal.di.ServiceScope
import au.com.beba.runninggoal.feature.goals.RunningGoalsFragment
import au.com.beba.runninggoal.feature.progressSync.ApiSourceIntentService
import au.com.beba.runninggoal.feature.syncSources.EditSyncSourceActivity
import au.com.beba.runninggoal.feature.syncSources.SyncSourcesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class AndroidBindingModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun runningGoalsFragment(): RunningGoalsFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun syncSourcesFragment(): SyncSourcesFragment

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun editSyncSourceActivity(): EditSyncSourceActivity

    @ServiceScope
    @ContributesAndroidInjector
    abstract fun apiSourceIntentService(): ApiSourceIntentService
}