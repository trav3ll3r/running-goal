package au.com.beba.runninggoal

import au.com.beba.runninggoal.di.component.DaggerAppComponent
import au.com.beba.runninggoal.repo.GoalRepo
import com.facebook.stetho.Stetho
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class App : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<App> {
        return DaggerAppComponent.builder().create(this)
    }

    override fun onCreate() {
        super.onCreate()

        applicationInjector().inject(this)

        GoalRepo.initialise(this)

        Stetho.initializeWithDefaults(this)
    }
}