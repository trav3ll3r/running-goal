package au.com.beba.runninggoal.feature.syncSources

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.domain.workout.sync.SyncSource
import au.com.beba.runninggoal.feature.base.ListListener
import au.com.beba.runninggoal.feature.progressSync.SyncSourcesAdapter
import au.com.beba.runninggoal.feature.sync.SyncFeature
import dagger.android.support.AndroidSupportInjection
import org.jetbrains.anko.support.v4.find
import timber.log.Timber
import javax.inject.Inject


class SyncSourcesFragment : Fragment() {

    interface SyncSourceListener {
        fun onSyncSourceClicked(syncSource: SyncSource)
    }

    @Inject
    lateinit var syncFeature: SyncFeature

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

        recyclerAdapter = SyncSourcesAdapter(mutableListOf(), object : ListListener<SyncSource> {
            override fun onItemClick(item: SyncSource) {
                listener?.onSyncSourceClicked(item)
            }
        })
        recyclerView.adapter = recyclerAdapter
    }

    private fun refreshList() {
        Timber.i("refreshList")
        val syncSources = syncFeature.getAllConfiguredSources()
        recyclerAdapter.setItems(syncSources)
        recyclerAdapter.notifyDataSetChanged()
    }
}