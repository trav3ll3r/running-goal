package au.com.beba.runninggoal.feature.syncSources

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.domain.workout.sync.SourceType
import org.jetbrains.anko.find


class SourceTypesAdapter(context: Context, private val items: List<SourceType>) : ArrayAdapter<SourceType>(context, R.layout.list_item_source_type, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
                ?: LayoutInflater.from(context).inflate(R.layout.list_item_source_type, parent, false)

        bindView(view, position)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
                ?: LayoutInflater.from(context).inflate(R.layout.list_item_source_type, parent, false)
        bindView(view, position)
        return view
    }

    private fun bindView(view: View, position: Int) {
        val image = view.find<ImageView>(R.id.image)
        val name = view.find<TextView>(R.id.text)

        val source = items[position]

        image.setImageResource(source.iconRes)
        name.setText(source.nameRes)
    }
}