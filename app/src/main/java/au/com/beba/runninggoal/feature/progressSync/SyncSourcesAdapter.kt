package au.com.beba.runninggoal.feature.progressSync

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.feature.base.AdapterListener
import au.com.beba.runninggoal.models.SyncSource


class SyncSourcesAdapter(private val items: MutableList<SyncSource>, private val listener: AdapterListener<SyncSource>) : RecyclerView.Adapter<SyncSourceViewHolder>() {

    // BUILD ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyncSourceViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.sync_source_list_item, parent, false)
        return SyncSourceViewHolder(v)
    }

    // BIND VALUES TO THE ViewHolder
    override fun onBindViewHolder(holder: SyncSourceViewHolder, position: Int) {
        holder.bindView(getItem(position), listener)
    }

    override fun getItemId(position: Int): Long {
        return items[position].id.toLong()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getItem(position: Int): SyncSource {
        return items[position]
    }

    fun setItems(syncSources: List<SyncSource>) {
        items.clear()
        items.addAll(syncSources)
    }
}