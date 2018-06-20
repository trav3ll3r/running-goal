package au.com.beba.runninggoal.component

import android.app.Dialog
import android.content.Context
import android.support.v7.widget.AppCompatButton
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.models.Distance

class DistancePickerDialog(private val context: Context, private val listener: DistancePickerDialog.OnDistanceSetListener) {

    fun show(initValue: Distance) {
        val dialog = Dialog(context)

        //setting custom layout to dialog
        dialog.setContentView(R.layout.decimal_number_dialog)

        val picker: DistancePicker = dialog.findViewById(R.id.decimal_number_picker)

        picker.setValue(initValue)

//        dialog.setTitle("Custom Dialog")

        //adding text dynamically
//        val txt = dialog.findViewById(R.id.textView) as TextView
//        txt.text = "Put your dialog text here."
//
//        val image = dialog.findViewById(R.id.image) as ImageView
//        image.setImageDrawable(resources.getDrawable(android.R.drawable.ic_dialog_info))
//
//        //add button click event
        val dismissButton: AppCompatButton = dialog.findViewById(R.id.button_select)
        dismissButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    interface OnDistanceSetListener {
        fun onSetValue(distance: Distance) {}
    }
}