package au.com.beba.runninggoal.di.module

import au.com.beba.runninggoal.App
import au.com.beba.runninggoal.domain.event.EventCentre
import au.com.beba.runninggoal.domain.event.PublisherEventCentre
import au.com.beba.runninggoal.domain.event.SubscriberEventCentre
import au.com.beba.runninggoal.feature.LocalPreferences
import au.com.beba.runninggoal.feature.LocalPreferencesImpl
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
    fun workoutRepository(application: App): WorkoutRepository =
            WorkoutRepo(application)
}