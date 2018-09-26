package au.com.beba.runninggoal.feature.goals

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.feature.base.AdapterListener
import au.com.beba.runninggoal.models.RunningGoal
import au.com.beba.runninggoal.repo.GoalRepository
import com.google.android.material.button.MaterialButton
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_running_goals.*
import org.jetbrains.anko.support.v4.find
import javax.inject.Inject


class RunningGoalsFragment : Fragment() {

    interface RunningGoalListener {
        fun onAddRunningGoal()
        fun onRunningGoalClicked(runningGoal: RunningGoal)
    }

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    @Inject
    lateinit var goalRepository: GoalRepository

    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, factory).get(RunningGoalViewModel::class.java)
    }

    companion object {
        private val TAG = RunningGoalsFragment::class.java.simpleName
    }

    private lateinit var fab: MaterialButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RunningGoalsAdapter

    private var listener: RunningGoalListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel.fetchGoals().observe(this, Observer {
            it?.let { updateList(it) }
        })
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
        viewModel.fetchGoals()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun initFAB() {
        fab = find(R.id.fab_sync)
        fab.setOnClickListener { listener?.onAddRunningGoal() }
    }

    private fun initRecyclerView() {
        recyclerView = recycler_view
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

    private fun updateList(items: List<RunningGoal>) {
        Log.i(TAG, "updateList")
        recyclerAdapter.setItems(items)
        recyclerAdapter.notifyDataSetChanged()
    }

}
