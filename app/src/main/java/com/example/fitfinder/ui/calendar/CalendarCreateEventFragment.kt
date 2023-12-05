package com.example.fitfinder.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fitfinder.R
import com.example.fitfinder.data.repository.messages.MatchesRepository
import com.example.fitfinder.data.repository.sportcategories.SportCategoriesRepository
import com.example.fitfinder.databinding.FragmentCalendarCreateEventBinding
import com.example.fitfinder.ui.MainActivity
import com.example.fitfinder.util.SharedPreferencesUtil
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.messages.MatchesViewModel
import com.example.fitfinder.viewmodel.sportcategories.SportCategoriesViewModel
import com.zeeshan.material.multiselectionspinner.MultiSelectionSpinner

class CalendarCreateEventFragment: Fragment(),
    CalendarEventDatePickerFragment.OnDateSetListener,
    CalendarEventTimePickerFragment.OnTimeSetListener{

    // Variables
    private lateinit var binding: FragmentCalendarCreateEventBinding

    // View-model
    private lateinit var sportCategoriesViewModel: SportCategoriesViewModel
    private lateinit var matchesViewModel: MatchesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCalendarCreateEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).enableBackButton(true)
        (activity as MainActivity).setTitle("Create a training session")

        // Retrieve the passed selected date and set it to the Date button
        arguments?.getString("selectedDate")?.let { selectedDate ->
            binding.btnDate.text = selectedDate
        }

        // Initialize view-models
        sportCategoriesViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(SportCategoriesRepository()))[SportCategoriesViewModel::class.java]
        matchesViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(MatchesRepository()))[MatchesViewModel::class.java]

        // Initialize sport categories dropdown
        sportCategoriesViewModel.fetchSportCategories()
        sportCategoriesViewModel.sportCategories.observe(viewLifecycleOwner) { categories ->
            val categoriesAdapter = ArrayAdapter(
                requireContext(),
                R.layout.dropdown_item,
                categories
            )
            binding.autoCompleteTextViewSportCategory.setAdapter(categoriesAdapter)
        }

        // Observe the exercises LiveData and update the multi-selection spinner
        sportCategoriesViewModel.exercises.observe(viewLifecycleOwner) { exercises ->
            updateExercisesSpinner(exercises)
        }

        // Fetch matches and users
        val userId = SharedPreferencesUtil.getUserId(requireContext()).toString()
        matchesViewModel.fetchMatchesAndUsersForUser(userId)

        matchesViewModel.matchesWithUsers.observe(viewLifecycleOwner) { matchesWithUsers ->
            val partnerNames = matchesWithUsers.map { it.second.firstName + " " + it.second.lastName }
            val partnersAdapter = ArrayAdapter(
                requireContext(),
                R.layout.dropdown_item,
                partnerNames
            )
            binding.autoCompleteTextViewPartner.setAdapter(partnersAdapter)
        }


        // Listeners
        binding.autoCompleteTextViewSportCategory.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedCategory = adapterView.getItemAtPosition(position) as String
            sportCategoriesViewModel.fetchExercisesForCategory(selectedCategory)
        }

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

    private fun updateExercisesSpinner(exercises: List<String>) {
        (binding.multiSelectionWorkoutTimes as? MultiSelectionSpinner)?.let { spinner ->
            spinner.items = exercises
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).enableBackButton(false)
        (activity as MainActivity).setTitle("Calendar")
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).enableBackButton(true)
    }

    override fun onDateSelected(date: String) {
        binding.btnDate.text = date
    }

    override fun onTimeSelected(time: String) {
        binding.btnTime.text = time
    }


}