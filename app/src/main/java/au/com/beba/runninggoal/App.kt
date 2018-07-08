package au.com.beba.runninggoal

import au.com.beba.runninggoal.di.component.DaggerMainComponent
import com.facebook.stetho.Stetho
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class App : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<App> {
        return DaggerMainComponent.builder().create(this)
    }

    override fun onCreate() {
        super.onCreate()

        applicationInjector().inject(this)

        Stetho.initializeWithDefaults(this)
    }
}