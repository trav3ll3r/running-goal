package au.com.beba.runninggoal.feature.workout

import android.os.Bundle
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
import au.com.beba.runninggoal.domain.RunningGoal
import au.com.beba.runninggoal.domain.workout.Workout
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_goal_details.*
import timber.log.Timber
import javax.inject.Inject


class WorkoutsFragment : Fragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory


    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, factory).get(WorkoutsViewModel::class.java)
    }

    companion object {
        private const val EXTRA_GOAL_ID = "EXTRA_GOAL_ID"

        fun newInstance(runningGoal: RunningGoal): WorkoutsFragment {

            val f = WorkoutsFragment()
            val extra = Bundle()
            extra.putLong(EXTRA_GOAL_ID, runningGoal.id)
            f.arguments = extra
            return f
        }
    }

    private val goalId: Long
        get() {
            return arguments?.getLong(EXTRA_GOAL_ID) ?: 0L
        }

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: WorkoutsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goal_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View is created so postpone the transition
        postponeEnterTransition()

        initLiveData()

        initRecyclerView()
    }

    /* ************ */
    /* INITIALISERS */
    /* ************ */
    private fun initLiveData() {
        Timber.i("initLiveData")

        viewModel.workoutsLiveData.observe(this, Observer {
            Timber.i("workoutsLiveData | observed")
            if (it != null) {
                Timber.d("workoutsLiveData | observed | workouts=%s", it.size)
                handleWorkoutsUpdate(it)
            }
        })
        viewModel.fetchWorkouts(goalId)
    }

    private fun initRecyclerView() {
        recyclerView = recycler_view
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 1)

        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)

        recyclerAdapter = WorkoutsAdapter(mutableListOf())
        recyclerView.adapter = recyclerAdapter
    }

    /* ******* */
    /* ACTIONS */
    /* ******* */


    /* ********* */
    /* REACTIONS */
    /* ********* */
    private fun handleWorkoutsUpdate(items: List<Workout>) {
        Timber.i("handleWorkoutsUpdate")
        recyclerAdapter.setItems(items)
        recyclerAdapter.notifyDataSetChanged()
    }
}
