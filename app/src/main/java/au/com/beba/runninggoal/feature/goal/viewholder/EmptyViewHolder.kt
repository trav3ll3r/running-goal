package au.com.beba.runninggoal.feature.goal.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.component.NumericWithLabel
import au.com.beba.runninggoal.domain.GoalProgress
import au.com.beba.runninggoal.domain.core.display
import org.jetbrains.anko.find


class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        const val layoutId = R.layout.list_item_empty_with_label
    }

    private val label: TextView = itemView.find(R.id.label)

    fun bindView(labelText: CharSequence) {
        label.text = labelText
    }
}