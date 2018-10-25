package au.com.beba.runninggoal.di.component

import au.com.beba.runninggoal.App
import au.com.beba.runninggoal.di.module.AndroidBindingModule
import au.com.beba.runninggoal.di.module.CommonAppModule
import au.com.beba.runninggoal.di.module.FeaturesModule
import au.com.beba.runninggoal.di.module.ViewModelModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AndroidBindingModule::class,
    CommonAppModule::class,
    ViewModelModule::class,
    FeaturesModule::class
])
interface MainComponent : AndroidInjector<App> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()
}