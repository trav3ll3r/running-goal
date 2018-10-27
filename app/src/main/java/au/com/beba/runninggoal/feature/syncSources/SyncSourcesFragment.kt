package au.com.beba.runninggoal.feature.syncSources

import android.os.Bundle
import android.view.LayoutInflater
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
import au.com.beba.runninggoal.domain.workout.sync.SyncSource
import au.com.beba.runninggoal.feature.base.ListListener
import au.com.beba.runninggoal.feature.progressSync.SyncSourcesAdapter
import com.google.android.material.button.MaterialButton
import dagger.android.support.AndroidSupportInjection
import org.jetbrains.anko.support.v4.find
import timber.log.Timber
import javax.inject.Inject


class SyncSourcesFragment : Fragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val syncSourcesViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, factory).get(SyncSourcesViewModel::class.java)
    }
    private val fabViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, factory).get(SyncSourcesFabViewModel::class.java)
    }

    private lateinit var fab: MaterialButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: SyncSourcesAdapter

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
        initToolbar()
        initFAB()
        initRecyclerView()
        initLiveData()
    }

    override fun onResume() {
        super.onResume()
        syncSourcesViewModel.fetchSyncSources()
        fabViewModel.update()
    }

    /* ************ */
    /* INITIALISERS */
    /* ************ */
    private fun initToolbar() {
        find<Toolbar>(R.id.toolbar).apply {
            //inflateMenu(R.menu.main_menu)
            setTitle(R.string.sync_sources)
//            toolbar.setOnMenuItemClickListener {
//                handleToolbarOptionsItemSelected(it)
//            }
        }
    }

    private fun initFAB() {
        fab = find(R.id.fab)
        fab.setOnClickListener { onFabClick() }
    }

    private fun initRecyclerView() {
        recyclerView = find(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 1)

        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(decoration)

        recyclerAdapter = SyncSourcesAdapter(mutableListOf(), object : ListListener<SyncSource> {
            override fun onItemClick(item: SyncSource) {
                //TODO: REPLACE WITH publish(Event)
                syncSourcesViewModel.editSyncSource(item.id)
            }
        })
        recyclerView.adapter = recyclerAdapter
    }

    private fun initLiveData() {
        syncSourcesViewModel.syncSourcesLiveData.observe(this, Observer {
            Timber.d("initLiveData | syncSourcesViewModel | observed | count=%s", it.size)
            updateList(it)
        })
        fabViewModel.fabLiveData.observe(this, Observer {
            Timber.d("initLiveData | fabViewModel | observed")
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
    private fun updateList(items: List<SyncSource>) {
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
                FabActionType.CREATE_SYNC_SOURCE -> getString(R.string.add)
                FabActionType.NONE -> ""
            }
            this.icon = ContextCompat.getDrawable(context,
                    when (model.actionType) {
                        FabActionType.CREATE_SYNC_SOURCE -> R.drawable.ic_medal_24
                        FabActionType.NONE -> R.drawable.ic_medal_24 // FIXME
                    }
            )
        }
    }
}