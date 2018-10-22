package au.com.beba.runninggoal.di.module

import au.com.beba.runninggoal.MainActivity
import au.com.beba.runninggoal.di.ActivityScope
import au.com.beba.runninggoal.di.FragmentScope
import au.com.beba.runninggoal.feature.goal.GoalDetailsFragment
import au.com.beba.runninggoal.feature.goals.GoalActivity
import au.com.beba.runninggoal.feature.goals.GoalsListFragment
import au.com.beba.runninggoal.feature.syncSources.EditSyncSourceActivity
import au.com.beba.runninggoal.feature.syncSources.SyncSourcesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class AndroidBindingModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun goalActivity(): GoalActivity

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun runningGoalsFragment(): GoalsListFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun goalDetailsFragment(): GoalDetailsFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun syncSourcesFragment(): SyncSourcesFragment

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun editSyncSourceActivity(): EditSyncSourceActivity

//    @BroadcastScope
//    @ContributesAndroidInjector
//    abstract fun runningGoalWidgetProvider(): RunningGoalWidgetProvider
}