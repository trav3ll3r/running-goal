package au.com.beba.runninggoal.feature.progressSync

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.domain.workout.sync.SyncSource
import au.com.beba.runninggoal.feature.base.ListListener


class SyncSourcesAdapter(private val items: MutableList<SyncSource>, private val listener: ListListener<SyncSource>) : RecyclerView.Adapter<SyncSourceViewHolder>() {

    // BUILD ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyncSourceViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(SyncSourceViewHolder.layoutId, parent, false)
        return SyncSourceViewHolder(v)
    }

    // BIND VALUES TO THE ViewHolder
    override fun onBindViewHolder(holder: SyncSourceViewHolder, position: Int) {
        holder.bindView(getItem(position), listener)
    }

    override fun getItemId(position: Int): Long {
        return items[position].id
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