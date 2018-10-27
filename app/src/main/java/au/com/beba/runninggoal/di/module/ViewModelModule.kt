package au.com.beba.runninggoal.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import au.com.beba.runninggoal.feature.base.AlertViewModel
import au.com.beba.runninggoal.feature.goal.GoalDetailsViewModel
import au.com.beba.runninggoal.feature.goals.GoalsFabViewModel
import au.com.beba.runninggoal.feature.goals.GoalsViewModel
import au.com.beba.runninggoal.feature.navigation.NavigationViewModel
import au.com.beba.runninggoal.feature.syncSources.SyncSourcesFabViewModel
import au.com.beba.runninggoal.feature.syncSources.SyncSourcesViewModel
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
    @ViewModelKey(GoalsViewModel::class)
    internal abstract fun goalsViewModel(viewModel: GoalsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GoalsFabViewModel::class)
    internal abstract fun goalsFabViewModel(viewModel: GoalsFabViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GoalDetailsViewModel::class)
    internal abstract fun goalDetailsViewModel(viewModel: GoalDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SyncSourcesViewModel::class)
    internal abstract fun syncSourcesViewModel(viewModel: SyncSourcesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SyncSourcesFabViewModel::class)
    internal abstract fun syncSourcesFabViewModel(viewModel: SyncSourcesFabViewModel): ViewModel
}