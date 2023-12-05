package com.example.fitfinder.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fitfinder.databinding.FragmentCalendarCreateEventBinding
import com.example.fitfinder.ui.MainActivity

class CalendarCreateEventFragment: Fragment(),
    CalendarEventDatePickerFragment.OnDateSetListener,
    CalendarEventTimePickerFragment.OnTimeSetListener{

    // Variables
    private lateinit var binding: FragmentCalendarCreateEventBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCalendarCreateEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).enableBackButton(true)

        // Retrieve the passed selected date and set it to the Date button
        arguments?.getString("selectedDate")?.let { selectedDate ->
            binding.btnDate.text = selectedDate
        }

        // Listeners
        binding.btnDate.setOnClickListener {
            val datePickerFragment = CalendarEventDatePickerFragment()
            datePickerFragment.onDateSetListener = this
            datePickerFragment.show(parentFragmentManager, "datePicker")
        }

        binding.btnTime.setOnClickListener {
            val timePickerFragment = CalendarEventTimePickerFragment()
            timePickerFragment.onTimeSetListener = this
            timePickerFragment.show(parentFragmentManager, "timePicker")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).enableBackButton(false)
    }

    override fun onDateSelected(date: String) {
        binding.btnDate.text = date
    }

    override fun onTimeSelected(time: String) {
        binding.btnTime.text = time
    }


}