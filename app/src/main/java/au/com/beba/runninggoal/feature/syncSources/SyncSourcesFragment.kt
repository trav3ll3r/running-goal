package au.com.beba.runninggoal.feature.syncSources

import android.content.Context
import android.os.Bundle
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
import au.com.beba.runninggoal.feature.progressSync.SyncSourcesAdapter
import au.com.beba.runninggoal.models.SyncSource
import au.com.beba.runninggoal.repo.GoalRepository
import au.com.beba.runninggoal.repo.SyncSourceRepository
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.support.v4.find
import javax.inject.Inject


class SyncSourcesFragment : Fragment() {

    interface SyncSourceListener {
        fun onSyncSourceClicked(syncSource: SyncSource)
    }

    @Inject
    lateinit var goalRepository: GoalRepository
    @Inject
    lateinit var syncSourceRepository: SyncSourceRepository

    companion object {
        private val TAG = SyncSourcesFragment::class.java.simpleName
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: SyncSourcesAdapter
    private var listener: SyncSourceListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sync_sources, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SyncSourceListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement %s".format(SyncSourceListener::class.java.simpleName))
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

    private fun initRecyclerView() {
        recyclerView = find(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 1)

        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)

        recyclerAdapter = SyncSourcesAdapter(mutableListOf(), object : AdapterListener<SyncSource> {
            override fun onItemClick(item: SyncSource) {
                listener?.onSyncSourceClicked(item)
            }
        })
        recyclerView.adapter = recyclerAdapter
    }

    private fun refreshList() {
        Log.i(TAG, "refreshList")
        launch(UI) {
            val syncSources = syncSourceRepository.getSyncSources()
            recyclerAdapter.setItems(syncSources)
            recyclerAdapter.notifyDataSetChanged()
        }
    }
}