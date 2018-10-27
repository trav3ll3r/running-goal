package au.com.beba.runninggoal

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.ChangeTransform
import androidx.transition.TransitionSet
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.domain.event.Event
import au.com.beba.runninggoal.feature.goal.GoalDetailsFragment
import au.com.beba.runninggoal.feature.goals.GoalEditActivity
import au.com.beba.runninggoal.feature.goals.GoalViewHolder
import au.com.beba.runninggoal.feature.goals.GoalsListFragment
import au.com.beba.runninggoal.feature.navigation.NavigationViewModel
import au.com.beba.runninggoal.feature.navigation.ShowEditGoalEvent
import au.com.beba.runninggoal.feature.navigation.ShowEditSyncSourceEvent
import au.com.beba.runninggoal.feature.navigation.ShowGoalDetailsEvent
import au.com.beba.runninggoal.feature.navigation.ShowGoalsEvent
import au.com.beba.runninggoal.feature.navigation.ShowSyncSourcesEvent
import au.com.beba.runninggoal.feature.syncSources.EditSyncSourceActivity
import au.com.beba.runninggoal.feature.syncSources.SyncSourcesFragment
import dagger.android.AndroidInjection
import timber.log.Timber
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val navViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, factory).get(NavigationViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initLiveData()
    }

    private fun initLiveData() {
        Timber.i("initLiveData")
        navViewModel.navLiveData.observe(this, Observer {
            Timber.d("initLiveData | goalsLiveData | observed | screen=%s", it)
            handleNavigation(it)
        })
    }

    /* ********* */
    /* REACTIONS */
    /* ********* */
    private fun handleNavigation(event: Event) {
        when (event) {
            is ShowGoalsEvent -> gotoRunningGoals()
            is ShowEditGoalEvent -> gotoEditGoal(event.goalId)
            is ShowGoalDetailsEvent -> gotoGoalDetails(event.runningGoal, event.viewHolder)
            is ShowSyncSourcesEvent -> gotoSyncSources()
            is ShowEditSyncSourceEvent -> editSyncSource(event.syncSourceId)
        }
    }

    private fun gotoRunningGoals() {
        supportFragmentManager.beginTransaction().replace(R.id.content_container, GoalsListFragment()).commit()
    }

    private fun gotoGoalDetails(runningGoal: RunningGoal, holder: GoalViewHolder) {
        supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.content_container, GoalDetailsFragment.newInstance(runningGoal).apply {
                    this.sharedElementEnterTransition = DetailsTransition()
                    this.enterTransition = DetailsTransition()  //ChangeBounds() //Fade()
                    exitTransition = DetailsTransition()    //Fade()
                    this.sharedElementReturnTransition = DetailsTransition()
                })
                .addToBackStack(null)
                .apply {
                    holder.getSharedViews().forEach {
                        addSharedElement(it, it.transitionName)
                    }
                }
                .commit()
    }

    private fun gotoSyncSources() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.content_container, SyncSourcesFragment())
                .addToBackStack(null)
                .commit()
    }

    private fun gotoEditGoal(goalId: Long?) {
        val intent = GoalEditActivity.buildIntent(this, goalId)
        startActivity(intent)
    }

    //TODO: TRIGGER VIA EVENT
    private fun editSyncSource(syncSourceId: Long?) {
        val intent = EditSyncSourceActivity.buildIntent(this, syncSourceId)
        startActivity(intent)
    }

    private fun alertUI() {
        // NOTIFY USER ABOUT MISSING DEFAULT SYNC SOURCE
        val dialog = AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(R.string.sync_now_error)
                .setMessage(R.string.message_no_default_sync_sources)
                .setPositiveButton(android.R.string.ok, null)
                .create()

        if (!isFinishing) {
            dialog.show()
        }
    }
}

class DetailsTransition : TransitionSet() {
    init {
        duration = 400
        ordering = ORDERING_TOGETHER
        addTransition(ChangeBounds())
                .addTransition(ChangeTransform())
                .addTransition(ChangeImageTransform())

//        ordering = ORDERING_SEQUENTIAL
//                addTransition(AutoTransition())

    }
}