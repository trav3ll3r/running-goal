package au.com.beba.runninggoal.di.component

import au.com.beba.runninggoal.App
import au.com.beba.runninggoal.di.module.AndroidBindingModule
import au.com.beba.runninggoal.di.module.AppModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, AndroidSupportInjectionModule::class , AndroidBindingModule::class))
interface AppComponent : AndroidInjector<App> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()
}