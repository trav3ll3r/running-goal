package au.com.beba.runninggoal.feature.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.domain.event.PublisherEventCentre
import au.com.beba.runninggoal.feature.navigation.ShowEditGoalEvent
import au.com.beba.runninggoal.feature.navigation.ShowSyncSourcesEvent
import com.google.android.material.button.MaterialButton
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_running_goals.*
import org.jetbrains.anko.support.v4.find
import timber.log.Timber
import javax.inject.Inject


class GoalsListFragment : Fragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    @Inject
    lateinit var eventCentre: PublisherEventCentre

    private val goalsViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, factory).get(GoalsViewModel::class.java)
    }
    private val fabViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, factory).get(GoalsFabViewModel::class.java)
    }

    private lateinit var fab: MaterialButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RunningGoalsAdapter

    /* ********* */
    /* LIFECYCLE */
    /* ********* */
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_running_goals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initFAB()
        initRecyclerView()
        initLiveData()
    }

    override fun onResume() {
        super.onResume()
        goalsViewModel.fetchGoals()
        fabViewModel.update()
    }

    /* ************ */
    /* INITIALISERS */
    /* ************ */
    private fun initToolbar() {
        find<Toolbar>(R.id.toolbar).apply {
            inflateMenu(R.menu.main_menu)
            setTitle(R.string.running_goals)
            toolbar.setOnMenuItemClickListener {
                handleToolbarOptionsItemSelected(it)
            }
        }
    }

    private fun initFAB() {
        fab = find(R.id.fab)
        fab.setOnClickListener { onFabClick() }
    }

    private fun initRecyclerView() {
        recyclerView = recycler_view
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 1)

        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)

        recyclerAdapter = RunningGoalsAdapter()
        recyclerView.adapter = recyclerAdapter
    }

    private fun initLiveData() {
        Timber.i("initLiveData")
        goalsViewModel.goalsLiveData.observe(this, Observer {
            Timber.d("initLiveData | goalsLiveData | observed | count=%s", it.size)
            updateList(it)
        })

        fabViewModel.fabLiveData.observe(this, Observer {
            Timber.i("initLiveData | fabViewModel | observed")
            updateFab(it)
        })
    }

    /* ******* */
    /* ACTIONS */
    /* ******* */
    private fun onFabClick() {
        fabViewModel.fabAction(context)
    }

    /* ********* */
    /* REACTIONS */
    /* ********* */
    private fun handleToolbarOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_create_goal -> {
                eventCentre.publish(ShowEditGoalEvent(null))
                return true
            }
            R.id.action_manage_sync_sources -> {
                eventCentre.publish(ShowSyncSourcesEvent())
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateList(items: List<RunningGoal>) {
        Timber.i("updateList")
        recyclerAdapter.setItems(items)
        recyclerAdapter.notifyDataSetChanged()
    }

    private fun updateFab(model: FabModel) {
        Timber.i("updateFab")
        Timber.d("updateFab | type=%s".format(model.actionType))
        fab.apply {
            this.visibility = when (model.actionType) { FabActionType.NONE -> View.GONE
                else -> View.VISIBLE
            }
            this.text = when (model.actionType) {
                FabActionType.CREATE_GOAL -> getString(R.string.set_goal)
                FabActionType.SYNC_ALL -> getString(R.string.sync_all)
                FabActionType.MANAGE_SYNC_SOURCES -> getString(R.string.action_manage_sync_sources)
                FabActionType.NONE -> ""
            }
            this.icon = ContextCompat.getDrawable(context,
                    when (model.actionType) {
                        FabActionType.CREATE_GOAL -> R.drawable.ic_medal_24
                        FabActionType.SYNC_ALL -> R.drawable.ic_sync_24dp
                        FabActionType.MANAGE_SYNC_SOURCES -> R.drawable.ic_sync_24dp
                        FabActionType.NONE -> R.drawable.ic_medal_24 // FIXME
                    }
            )
        }
    }
}
