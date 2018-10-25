package au.com.beba.runninggoal

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import au.com.beba.runninggoal.domain.workout.sync.SyncSource
import au.com.beba.runninggoal.feature.goal.GoalActionListener
import au.com.beba.runninggoal.feature.goal.GoalDetailsFragment
import au.com.beba.runninggoal.feature.goals.GoalEditActivity
import au.com.beba.runninggoal.feature.goals.GoalViewHolder
import au.com.beba.runninggoal.feature.goals.GoalsListFragment
import au.com.beba.runninggoal.feature.navigation.AppScreen
import au.com.beba.runninggoal.feature.navigation.GoalDetailsScreen
import au.com.beba.runninggoal.feature.navigation.GoalsScreen
import au.com.beba.runninggoal.feature.navigation.NavigationViewModel
import au.com.beba.runninggoal.feature.navigation.SyncSourcesScreen
import au.com.beba.runninggoal.feature.syncSources.EditSyncSourceActivity
import au.com.beba.runninggoal.feature.syncSources.SyncSourcesFragment
import dagger.android.AndroidInjection
import timber.log.Timber
import javax.inject.Inject


class MainActivity : AppCompatActivity(),
        SyncSourcesFragment.SyncSourceListener,
        GoalActionListener {

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_create_goal -> {
                createGoal()
                return true
            }
//            R.id.action_manage_sync_sources -> {
//                //showSyncSources()
//                return true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initLiveData() {
        Timber.i("initLiveData")
        navViewModel.navLiveData.observe(this, Observer {
            Timber.d("initLiveData | goalsLiveData | observed | screen=%s", it)
            handleNavigation(it)
        })
    }

    /* REACTIONS */
    private fun handleNavigation(screen: AppScreen) {
        when (screen) {
            is GoalsScreen -> showRunningGoals()
            is GoalDetailsScreen -> gotoGoalDetails(screen.runningGoal, screen.holder)
            is SyncSourcesScreen -> showSyncSources()
        }
    }

    override fun createGoal() {
        gotoCreateGoal()
    }

    override fun editGoal(runningGoal: RunningGoal) {
        gotoEditGoal(runningGoal)
    }

    override fun onSyncSourceClicked(syncSource: SyncSource) {
        editSyncSource(syncSource)
    }

    private fun showRunningGoals() {
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

    private fun showSyncSources() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.content_container, SyncSourcesFragment())
                .addToBackStack(null)
                .commit()
    }

    private fun gotoCreateGoal() {
        val intent = GoalEditActivity.buildIntent(this, null)
        startActivity(intent)
    }

    private fun gotoEditGoal(runningGoal: RunningGoal) {
        val intent = GoalEditActivity.buildIntent(this, runningGoal.id)
        startActivity(intent)
    }

    private fun editSyncSource(syncSource: SyncSource) {
        val intent = EditSyncSourceActivity.buildIntent(this, syncSource)
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

    class ManageSyncSourcesEvent : Event
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