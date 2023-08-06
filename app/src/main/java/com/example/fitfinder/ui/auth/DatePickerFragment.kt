package com.example.fitfinder.ui.auth

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*


class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    interface OnDateOfBirthSetListener {
        fun onDateOfBirthSelected(dateOfBirth: String)
    }

    private var onDateOfBirthSetListener: OnDateOfBirthSetListener? = null

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

        // Set the DatePicker's maximum date to today
        datePickerDialog.datePicker.maxDate = c.timeInMillis

        return datePickerDialog
    }


    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Format the selected date as day/month/year
        val selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year)

        // Call the listener to pass the selected date back to the SignupActivity
        onDateOfBirthSetListener?.onDateOfBirthSelected(selectedDate)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDateOfBirthSetListener) {
            onDateOfBirthSetListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        onDateOfBirthSetListener = null
    }
}