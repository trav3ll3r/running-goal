package au.com.beba.runninggoal.di.component

import au.com.beba.runninggoal.App
import au.com.beba.runninggoal.di.module.AndroidBindingModule
import au.com.beba.runninggoal.di.module.AppModule
import au.com.beba.runninggoal.di.module.ViewModelModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, AndroidBindingModule::class, AppModule::class, ViewModelModule::class])
interface AppComponent : AndroidInjector<App> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()
}