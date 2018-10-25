package au.com.beba.runninggoal

import au.com.beba.runninggoal.di.component.DaggerMainComponent
import au.com.beba.runninggoal.feature.widget.WidgetFeatureImpl
import com.facebook.stetho.Stetho
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber


class App : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<App> {
        return DaggerMainComponent.builder().create(this)
    }

    override fun onCreate() {
        super.onCreate()

        applicationInjector().inject(this)

        initLogging()

        Stetho.initializeWithDefaults(this)

        initFeatures()
    }

    private fun initLogging() {
        Timber.plant(Timber.DebugTree())
    }

    private fun initFeatures() {
        val feature = WidgetFeatureImpl
        feature.bootstrap(this)
    }
}