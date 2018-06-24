package au.com.beba.runninggoal.feature.progressSync

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.models.SyncSource
import org.jetbrains.anko.find


class SyncSourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val lblType: TextView
    private val lblSyncedAt: TextView

    init {
        lblType = itemView.find(R.id.sync_source_item_type)
        lblSyncedAt = itemView.find(R.id.sync_source_item_synced_at)
    }

    fun bindView(syncSource: SyncSource) {
        lblType.text = syncSource.type
        lblSyncedAt.text = syncSource.syncedAt.toString()
    }
}