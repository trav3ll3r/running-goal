package au.com.beba.runninggoal.feature.goals

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.feature.base.AdapterListener
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.repo.GoalRepository
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.support.v4.find
import javax.inject.Inject


class RunningGoalsFragment : Fragment() {

    interface RunningGoalListener {
        fun onAddRunningGoal()
        fun onRunningGoalClicked(runningGoal: RunningGoal)
    }

    @Inject
    lateinit var goalRepository: GoalRepository

    companion object {
        private val TAG = RunningGoalsFragment::class.java.simpleName
    }

    private lateinit var fab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RunningGoalsAdapter

    private var listener: RunningGoalListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_running_goals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFAB()
        initRecyclerView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RunningGoalListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement %s".format(RunningGoalListener::class.java.simpleName))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun initFAB() {
        fab = find(R.id.fab)
        fab.setOnClickListener { listener?.onAddRunningGoal() }
    }

    private fun initRecyclerView() {
        recyclerView = find(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 1)

        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)

        recyclerAdapter = RunningGoalsAdapter(mutableListOf(), object : AdapterListener<RunningGoal> {
            override fun onItemClick(item: RunningGoal) {
                listener?.onRunningGoalClicked(item)
            }
        })
        recyclerView.adapter = recyclerAdapter
    }

    private fun refreshList() {
        Log.i(TAG, "refreshList")
        launch(UI) {
            val goals = goalRepository.getGoals()
            recyclerAdapter.setItems(goals)
            recyclerAdapter.notifyDataSetChanged()
        }
    }
}
