package au.com.beba.runninggoal.di.module

import au.com.beba.runninggoal.App
import au.com.beba.runninggoal.domain.event.EventCentre
import au.com.beba.runninggoal.domain.event.PublisherEventCentre
import au.com.beba.runninggoal.domain.event.SubscriberEventCentre
import au.com.beba.runninggoal.feature.LocalPreferences
import au.com.beba.runninggoal.feature.LocalPreferencesImpl
import au.com.beba.runninggoal.feature.widget.GoalWidgetUpdater
import au.com.beba.runninggoal.repo.goal.GoalRepo
import au.com.beba.runninggoal.repo.goal.GoalRepository
import au.com.beba.runninggoal.repo.sync.SyncSourceRepo
import au.com.beba.runninggoal.repo.sync.SyncSourceRepository
import au.com.beba.runninggoal.repo.widget.WidgetRepo
import au.com.beba.runninggoal.repo.widget.WidgetRepository
import au.com.beba.runninggoal.repo.workout.WorkoutRepo
import au.com.beba.runninggoal.repo.workout.WorkoutRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class CommonAppModule {

    @Provides
    @Singleton
    fun localPreferences(application: App): LocalPreferences =
            LocalPreferencesImpl(application.applicationContext)

    @Provides
    @Singleton
    fun subscriberEventCentre(): SubscriberEventCentre = EventCentre

    @Provides
    @Singleton
    fun publisherEventCentre(): PublisherEventCentre = EventCentre

    @Provides
    @Singleton
    fun goalRepository(application: App): GoalRepository =
            GoalRepo(application)

    @Provides
    @Singleton
    fun workoutRepository(application: App): WorkoutRepository =
            WorkoutRepo(application)

    @Provides
    @Singleton
    fun syncSourceRepository(application: App): SyncSourceRepository =
            SyncSourceRepo(application)

    @Provides
    @Singleton
    fun widgetRepository(application: App): WidgetRepository =
            WidgetRepo(application)

    @Provides
    @Singleton
    fun goalWidgetUpdater(goalRepo: GoalRepository, widgetRepo: WidgetRepository): GoalWidgetUpdater =
            GoalWidgetUpdater(goalRepo, widgetRepo)
}