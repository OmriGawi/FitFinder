package com.example.fitfinder.ui.calendar

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*

class CalendarEventDatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    interface OnDateSetListener {
        fun onDateSelected(date: String)
    }

    var onDateSetListener: OnDateSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog with THEME_HOLO_LIGHT and return it
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            AlertDialog.THEME_HOLO_LIGHT,
            this,
            year,
            month,
            day
        )

        // Set the DatePicker's min date to today
        datePickerDialog.datePicker.minDate = c.timeInMillis

        return datePickerDialog
    }


    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Create a Calendar instance with the selected date
        val calendar = Calendar.getInstance().apply {
            set(year, month, day)
        }

        // Create a SimpleDateFormat instance with the desired format
        val dateFormat = SimpleDateFormat("d MMM, yyyy", Locale.getDefault())

        // Format the calendar's time into the selectedDate string
        val selectedDate = dateFormat.format(calendar.time)

        // Call the listener to pass the selected date back
        onDateSetListener?.onDateSelected(selectedDate)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDateSetListener) {
            onDateSetListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        onDateSetListener = null
    }
}