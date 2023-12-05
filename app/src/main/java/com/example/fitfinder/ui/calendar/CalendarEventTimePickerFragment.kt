package com.example.fitfinder.ui.calendar

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import android.text.format.DateFormat
import java.util.*

class CalendarEventTimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    interface OnTimeSetListener {
        fun onTimeSelected(time: String)
    }

    var onTimeSetListener: OnTimeSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(requireContext(), this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Format the selected time as Hour:Minutes AM/PM
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }

        // Use SimpleDateFormat for formatting the time as well
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val selectedTime = timeFormat.format(calendar.time)

        // Pass the selected time back to the fragment
        onTimeSetListener?.onTimeSelected(selectedTime)
    }
}
