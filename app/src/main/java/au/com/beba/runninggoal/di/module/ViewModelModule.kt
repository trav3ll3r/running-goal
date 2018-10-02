package au.com.beba.runninggoal.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import au.com.beba.runninggoal.feature.goal.GoalViewModel
import au.com.beba.runninggoal.feature.goals.RunningGoalsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(RunningGoalsViewModel::class)
    internal abstract fun runningGoalViewModel(viewModel: RunningGoalsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GoalViewModel::class)
    internal abstract fun goalViewModel(viewModel: GoalViewModel): ViewModel

    //Add more ViewModels here
}