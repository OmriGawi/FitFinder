package com.example.fitfinder.ui.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitfinder.R
import com.example.fitfinder.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    // binding
    private lateinit var binding: FragmentCalendarBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the current date as default
        binding.tvSelectedDate.text = getCurrentFormattedDate()

        // Set a listener for date change on CalendarView
        binding.cvCalendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            binding.tvSelectedDate.text = formatDate(year, month, dayOfMonth)
        }

        // Set a listener for create a new event
        binding.btnCreateEvent.setOnClickListener{
            navigateToCalendarCreateEvent()
        }
    }

    // Function to get the current formatted date
    private fun getCurrentFormattedDate(): String {
        val currentDate = Calendar.getInstance()
        return formatDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH))
    }

    // Format the date into a readable string
    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
        val dateFormat = SimpleDateFormat("d MMM, yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun navigateToCalendarCreateEvent() {
        val selectedDate = binding.tvSelectedDate.text.toString()
        val calendarCreateEventFragment = CalendarCreateEventFragment().apply {
            arguments = Bundle().apply {
                putString("selectedDate", selectedDate)
            }
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, calendarCreateEventFragment)
            .addToBackStack(null)
            .commit()
    }


}