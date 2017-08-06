package au.com.beba.cleanproject.di.module

import au.com.beba.cleanproject.MainActivity
import au.com.beba.cleanproject.di.ActivityScope
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