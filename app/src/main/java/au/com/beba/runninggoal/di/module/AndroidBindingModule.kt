package au.com.beba.runninggoal.di.module

import au.com.beba.runninggoal.MainActivity
import au.com.beba.runninggoal.di.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AndroidBindingModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity

//    @FragmentScope
//    @ContributesAndroidInjector
//    abstract fun mainFragment(): MainFragment
}