package au.com.beba.runninggoal.feature.progressSync

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.feature.base.AdapterListener
import au.com.beba.runninggoal.models.SyncSource
import org.jetbrains.anko.find


class SyncSourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val lblType: TextView = itemView.find(R.id.sync_source_item_type)
    private val lblSyncedAt: TextView = itemView.find(R.id.sync_source_item_synced_at)
    private val checkIsActive: CheckBox = itemView.find(R.id.sync_source_item_is_active)

    fun bindView(syncSource: SyncSource, listener: AdapterListener<SyncSource>) {
        lblType.text = syncSource.type
        lblSyncedAt.text = syncSource.syncedAt.toString()
        checkIsActive.isChecked = syncSource.isActive

        itemView.setOnClickListener {
            listener.onItemClick(syncSource)
        }
    }
}