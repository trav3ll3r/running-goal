package au.com.beba.runninggoal.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import au.com.beba.runninggoal.feature.goals.RunningGoalViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(RunningGoalViewModel::class)
    internal abstract fun runningGoalViewModel(viewModel: RunningGoalViewModel): ViewModel

    //Add more ViewModels here
}