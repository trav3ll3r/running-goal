package au.com.beba.runninggoal.feature.widget.selectgoal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
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
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.feature.widget.R
import au.com.beba.runninggoal.repo.goal.GoalRepo
import au.com.beba.runninggoal.repo.goal.GoalRepository
import kotlinx.android.synthetic.main.fragment_select_goal.*
import org.jetbrains.anko.support.v4.find
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class SelectGoalFragment : Fragment() {

    companion object {
        private val TAG = SelectGoalFragment::class.java.simpleName
    }

    private val logger: Logger = LoggerFactory.getLogger(TAG)

    //TODO: INJECT
    private var factory: ViewModelProvider.Factory? = null
    //TODO: INJECT
    private var goalRepository: GoalRepository? = null

    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, factory).get(SelectGoalViewModel::class.java)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RunningGoalsAdapter

    private var listItemListener: ListListener<RunningGoal>? = null

    private fun resolveDependencies() {
        if (context != null) {
            goalRepository = GoalRepo(context!!)
        }
    }

    /* ********* */
    /* LIFECYCLE */
    /* ********* */
    override fun onCreate(savedInstanceState: Bundle?) {
        resolveDependencies()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_goal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initRecyclerView()
        initLiveData()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ListListener<*>) {
            listItemListener = context as ListListener<RunningGoal>
        } else {
            logger.info("%s does not implement %s", context.toString(), ListListener::class.java.simpleName)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listItemListener = null
    }

    /* ************ */
    /* INITIALISERS */
    /* ************ */
    private fun initToolbar() {
        find<Toolbar>(R.id.toolbar).apply {
            setTitle(R.string.select_goal)
        }
    }

    private fun initRecyclerView() {
        recyclerView = recycler_view
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 1)

        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)

        recyclerAdapter = RunningGoalsAdapter(mutableListOf(), object : ListListener<GoalViewHolder> {
            override fun onItemClick(item: GoalViewHolder) {
                listItemListener?.onItemClick(item.itemView.tag as RunningGoal)
            }
        })
        recyclerView.adapter = recyclerAdapter
    }

    private fun initLiveData() {
        logger.info("initLiveData")
        viewModel.goalsLiveData.observe(this, Observer {
            logger.info("initLiveData | goalsLiveData | observed")
            if (it != null) {
                logger.debug("initLiveData | goalsLiveData | observed | goals=%s", it.size)
                updateList(it)
            }
        })
        viewModel.fetchGoals(context!!)
    }

    /* ******* */
    /* ACTIONS */
    /* ******* */
    //EMPTY

    /* ********* */
    /* REACTIONS */
    /* ********* */
    private fun updateList(items: List<RunningGoal>) {
        logger.info("updateList")
        recyclerAdapter.setItems(items)
        recyclerAdapter.notifyDataSetChanged()
    }
}
