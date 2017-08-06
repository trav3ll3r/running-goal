package au.com.beba.cleanproject.di.component

import au.com.beba.cleanproject.App
import au.com.beba.cleanproject.di.module.AndroidBindingModule
import au.com.beba.cleanproject.di.module.AppModule
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