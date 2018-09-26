package au.com.beba.runninggoal.component

import android.app.Dialog
import android.content.Context
import androidx.appcompat.widget.AppCompatButton
import au.com.beba.runninggoal.R
import au.com.beba.runninggoal.models.Distance


class DistancePickerDialog(private val context: Context, private val listener: DistancePickerDialog.OnDistanceSetListener) {

    fun show(initValue: Distance) {
        val dialog = Dialog(context)

        //setting custom layout to dialog
        dialog.setContentView(R.layout.decimal_number_dialog)
        dialog.setCanceledOnTouchOutside(false)

        // set initial value
        val picker: DistancePicker = dialog.findViewById(R.id.decimal_number_picker)
        picker.setValue(initValue)

        //add button click event
        val dismissButton: AppCompatButton = dialog.findViewById(R.id.button_select)
        dismissButton.setOnClickListener {
            listener.onSetValue(picker.getValue())
            dialog.dismiss()
        }

        dialog.show()
    }

    interface OnDistanceSetListener {
        fun onSetValue(distance: Distance) {}
    }
}