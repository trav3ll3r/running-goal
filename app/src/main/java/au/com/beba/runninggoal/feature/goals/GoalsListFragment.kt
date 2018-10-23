package au.com.beba.runninggoal.feature.goals

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.feature.base.ListListener
import au.com.beba.runninggoal.feature.goal.GoalActionListener
import au.com.beba.runninggoal.feature.router.NavigationInteractor
import com.google.android.material.button.MaterialButton
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_running_goals.*
import org.jetbrains.anko.support.v4.find
import timber.log.Timber
import javax.inject.Inject


class GoalsListFragment : Fragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, factory).get(RunningGoalsViewModel::class.java)
    }

    private lateinit var fab: MaterialButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RunningGoalsAdapter

    private var goalActionListener: GoalActionListener? = null
    private var listItemListener: ListListener<RunningGoal>? = null
    private var navigationInteractor: NavigationInteractor? = null

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GoalActionListener) {
            goalActionListener = context
        } else {
            Timber.i("%s does not implement %s", context.toString(), GoalActionListener::class.java.simpleName)
        }

        if (context is NavigationInteractor) {
            navigationInteractor = context
        } else {
            Timber.i("%s does not implement %s", context.toString(), NavigationInteractor::class.java.simpleName)
        }

        if (context is ListListener<*>) {
            listItemListener = context as ListListener<RunningGoal>
        } else {
            Timber.i("%s does not implement %s", context.toString(), ListListener::class.java.simpleName)
        }
    }

    override fun onDetach() {
        super.onDetach()
        goalActionListener = null
        listItemListener = null
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
        fab = find(R.id.fab_sync)
//        fab.setOnClickListener { listener?.onAddRunningGoal() }
    }

    private fun initRecyclerView() {
        recyclerView = recycler_view
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 1)

        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)

        recyclerAdapter = RunningGoalsAdapter(mutableListOf(), object : ListListener<GoalViewHolder> {
            override fun onItemClick(item: GoalViewHolder) {
                goalActionListener?.showGoalDetails(item.itemView.tag as RunningGoal, item)
                listItemListener?.onItemClick(item.itemView.tag as RunningGoal)
            }
        })
        recyclerView.adapter = recyclerAdapter
    }

    private fun initLiveData() {
        Timber.i("initLiveData")
        viewModel.goalsLiveData.observe(this, Observer {
            Timber.i("initLiveData | goalsLiveData | observed")
            if (it != null) {
                Timber.d("initLiveData | goalsLiveData | observed | goals=%s", it.size)
                updateList(it)
            }
        })
        viewModel.fetchGoals()
    }

    /* ******* */
    /* ACTIONS */
    /* ******* */
    //EMPTY

    /* ********* */
    /* REACTIONS */
    /* ********* */
    private fun handleToolbarOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_sync_all -> {
                //syncAllGoals()
                return true
            }
            R.id.action_create_goal -> {
                goalActionListener?.createGoal()
                return true
            }
            R.id.action_manage_sync_sources -> {
                navigationInteractor?.onSyncSourcesRequested()
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
}
