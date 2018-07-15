package au.com.beba.runninggoal.di.module

import au.com.beba.runninggoal.MainActivity
import au.com.beba.runninggoal.di.ActivityScope
import au.com.beba.runninggoal.di.BroadcastScope
import au.com.beba.runninggoal.di.FragmentScope
import au.com.beba.runninggoal.di.ServiceScope
import au.com.beba.runninggoal.feature.GoalActivity
import au.com.beba.runninggoal.feature.goals.RunningGoalsFragment
import au.com.beba.runninggoal.feature.progressSync.SyncSourceIntentService
import au.com.beba.runninggoal.feature.syncSources.EditSyncSourceActivity
import au.com.beba.runninggoal.feature.syncSources.SyncSourcesFragment
import au.com.beba.runninggoal.feature.widget.RunningGoalWidgetProvider
import au.com.beba.runninggoal.feature.widget.SelectGoalActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class AndroidBindingModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun selectGoalActivity(): SelectGoalActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun goalActivity(): GoalActivity

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
    abstract fun syncSourceIntentService(): SyncSourceIntentService

    @BroadcastScope
    @ContributesAndroidInjector
    abstract fun runningGoalWidgetProvider(): RunningGoalWidgetProvider
}