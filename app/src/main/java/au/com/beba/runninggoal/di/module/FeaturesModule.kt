package au.com.beba.runninggoal.di.module

import au.com.beba.runninggoal.App
import au.com.beba.runninggoal.feature.goal.GoalFeature
import au.com.beba.runninggoal.feature.goal.GoalFeatureImpl
import au.com.beba.runninggoal.feature.sync.SyncFeature
import au.com.beba.runninggoal.feature.sync.SyncFeatureImpl
import au.com.beba.runninggoal.feature.widget.WidgetFeature
import au.com.beba.runninggoal.feature.widget.WidgetFeatureImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class FeaturesModule {

    @Provides
    @Singleton
    fun goalFeature(application: App): GoalFeature {
        val feature = GoalFeatureImpl
        feature.bootstrap(application)
        return feature
    }

    @Provides
    @Singleton
    fun syncFeature(application: App): SyncFeature {
        val feature = SyncFeatureImpl
        feature.bootstrap(application)
        return feature
    }

    @Provides
    @Singleton
    fun widgetFeature(application: App): WidgetFeature {
        val feature = WidgetFeatureImpl
        feature.bootstrap(application)
        return feature
    }
}