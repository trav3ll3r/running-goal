package au.com.beba.runninggoal.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import au.com.beba.runninggoal.feature.base.AlertViewModel
import au.com.beba.runninggoal.feature.goal.GoalViewModel
import au.com.beba.runninggoal.feature.goals.FabViewModel
import au.com.beba.runninggoal.feature.goals.RunningGoalsViewModel
import au.com.beba.runninggoal.feature.navigation.NavigationViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(AlertViewModel::class)
    internal abstract fun alertViewModel(viewModel: AlertViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NavigationViewModel::class)
    internal abstract fun navigationViewModel(viewModel: NavigationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RunningGoalsViewModel::class)
    internal abstract fun runningGoalViewModel(viewModel: RunningGoalsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FabViewModel::class)
    internal abstract fun goalSyncViewModel(viewModel: FabViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GoalViewModel::class)
    internal abstract fun goalViewModel(viewModel: GoalViewModel): ViewModel
}