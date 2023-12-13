package com.example.fitfinder.ui.calendar

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fitfinder.R
import com.example.fitfinder.data.repository.calendar.CalendarCreateEventRepository
import com.example.fitfinder.data.repository.messages.MatchesRepository
import com.example.fitfinder.data.repository.sportcategories.SportCategoriesRepository
import com.example.fitfinder.databinding.FragmentCalendarCreateEventBinding
import com.example.fitfinder.ui.MainActivity
import com.example.fitfinder.util.EventObserver
import com.example.fitfinder.util.SharedPreferencesUtil
import com.example.fitfinder.util.ToastyType
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.calendar.CalendarCreateEventViewModel
import com.example.fitfinder.viewmodel.messages.MatchesViewModel
import com.example.fitfinder.viewmodel.sportcategories.SportCategoriesViewModel
import com.zeeshan.material.multiselectionspinner.MultiSelectionSpinner
import es.dmoral.toasty.Toasty

class CalendarCreateEventFragment: Fragment(),
    CalendarEventDatePickerFragment.OnDateSetListener,
    CalendarEventTimePickerFragment.OnTimeSetListener{

    // Variables
    private lateinit var binding: FragmentCalendarCreateEventBinding

    // View-model
    private lateinit var sportCategoriesViewModel: SportCategoriesViewModel
    private lateinit var matchesViewModel: MatchesViewModel
    private lateinit var calendarCreateEventViewModel: CalendarCreateEventViewModel

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
        calendarCreateEventViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(CalendarCreateEventRepository()))[CalendarCreateEventViewModel::class.java]

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

        // Observe toast message events
        calendarCreateEventViewModel.toastMessageEvent.observe(viewLifecycleOwner, EventObserver { (message, type) ->
            when (type) {
                ToastyType.SUCCESS -> Toasty.success(requireContext(), message, Toasty.LENGTH_SHORT, true).show()
                ToastyType.ERROR -> Toasty.error(requireContext(), message, Toasty.LENGTH_SHORT, true).show()
                else -> {    // Handle any unexpected cases or log them for debugging
                    Log.e(ContentValues.TAG, "Unexpected ToastyType: $type")
                }
            }
        })

        // Fetch matches and users
        val userId = SharedPreferencesUtil.getUserId(requireContext()).toString()
        matchesViewModel.fetchMatchesAndUsersForUser(userId)

        matchesViewModel.matchesWithUsers.observe(viewLifecycleOwner) { matchesWithUsers ->
            val partnerItems = matchesWithUsers.map {
                PartnerItem(it.second.userId, it.second.firstName + " " + it.second.lastName)
            }
            val partnersAdapter = ArrayAdapter(
                requireContext(),
                R.layout.dropdown_item,
                partnerItems
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

        binding.btnCreate.setOnClickListener {
            if (areAllFiltersFilled()) {
                val selectedSportCategory = binding.autoCompleteTextViewSportCategory.text.toString()
                val selectedExercises = binding.multiSelectionExercises.selectedItems.map { it as String }
                val selectedPartnerName = binding.autoCompleteTextViewPartner.text.toString()

                // Fetch the selected partner's ID using the selected name
                val selectedPartner = matchesViewModel.matchesWithUsers.value?.find {
                    it.second.firstName + " " + it.second.lastName == selectedPartnerName
                }?.second

                val selectedPartnerId = selectedPartner?.userId
                val selectedDate = binding.btnDate.text.toString()
                val selectedTime = binding.btnTime.text.toString()
                val selectedLocation = binding.etLocation.text.toString()
                val selectedAdditionalEquipment = binding.etEquipment.text.toString()

                calendarCreateEventViewModel.createTrainingInvite(
                    senderId = userId,
                    receiverId = selectedPartnerId,
                    sportCategory = selectedSportCategory,
                    exercises = selectedExercises,
                    dateStr = selectedDate,
                    timeStr = selectedTime,
                    location = selectedLocation,
                    additionalEquipment = selectedAdditionalEquipment
                )
            } else {
                Toasty.warning(requireContext(), "Please fill all the fields to proceed.", Toasty.LENGTH_SHORT).show()
            }
        }
    }

    data class PartnerItem(val id: String, val name: String) {
        // This will be used by the ArrayAdapter for the display text
        override fun toString(): String {
            return name
        }
    }


    private fun areAllFiltersFilled(): Boolean {
        return binding.autoCompleteTextViewSportCategory.text.isNotEmpty() &&
                (binding.multiSelectionExercises.selectedItems?.isNotEmpty() == true) &&
                binding.autoCompleteTextViewPartner.text.isNotEmpty() &&
                binding.btnDate.text.isNotEmpty() &&
                binding.btnTime.text.isNotEmpty() &&
                binding.etLocation.text.isNotEmpty()
    }

    private fun updateExercisesSpinner(exercises: List<String>) {
        (binding.multiSelectionExercises as? MultiSelectionSpinner)?.let { spinner ->
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