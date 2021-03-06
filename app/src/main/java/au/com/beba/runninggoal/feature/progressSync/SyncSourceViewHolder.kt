package au.com.beba.runninggoal.feature.progressSync

import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.domain.workout.sync.SyncSource
import au.com.beba.runninggoal.feature.base.ListListener
import org.jetbrains.anko.find


class SyncSourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        const val layoutId = R.layout.list_item_sync_source
    }

    private val lblNickname: TextView = itemView.find(R.id.sync_source_nickname)
    private val lblSyncedAt: TextView = itemView.find(R.id.sync_source_item_synced_at)
    private val statusStatus: TextView = itemView.find(R.id.sync_source_item_status)

    fun bindView(syncSource: SyncSource, listener: ListListener<SyncSource>) {
        lblNickname.text = syncSource.nickname
        lblSyncedAt.text = syncSource.syncedAt.toString()

        statusStatus.visibility = View.GONE

        if (syncSource.accessToken.isEmpty()) {
            applyStatusStyle(statusStatus, "Not configured", R.color.status_not_started)
        } else {
            if (syncSource.isDefault) {
                applyStatusStyle(statusStatus, "Default", R.color.status_ongoing)
            } else {
                applyStatusStyle(statusStatus, "Inactive", R.color.status_not_started)

            }
        }

        itemView.setOnClickListener {
            listener.onItemClick(syncSource)
        }
    }

    private fun applyStatusStyle(statusView: TextView, caption: String, @ColorRes color: Int) {
        statusView.text = caption
        statusView.backgroundTintList = this.itemView.context.getColorStateList(color)
        statusView.visibility = View.VISIBLE
    }
}