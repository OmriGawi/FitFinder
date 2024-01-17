package com.example.fitfinder.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.fitfinder.R
import com.example.fitfinder.data.model.TrainingSession
import com.example.fitfinder.data.repository.calendar.CalendarRepository
import com.example.fitfinder.databinding.DialogTrainingSessionBinding
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.calendar.CalendarViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class CalendarTrainingSessionDialogFragment : DialogFragment(){
    private var _binding: DialogTrainingSessionBinding? = null
    private val binding get() = _binding!!

    private lateinit var calendarViewModel: CalendarViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogTrainingSessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize view models
        calendarViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(CalendarRepository()))[CalendarViewModel::class.java]

        // Observe the selected invite from the ViewModel
        calendarViewModel.selectedTrainingSession.observe(viewLifecycleOwner) { session ->
            displayTrainingSessionDetails(session)
        }

    }

    private fun displayTrainingSessionDetails(session: TrainingSession ) {
        // Update UI elements with invite details
        binding.tvTime.text = formatDateAndTime(session.dateTime)
        binding.tvLocation.text = session.location
        binding.tvSportCategory.text = session.sportCategory
        binding.tvExercises.text = session.exercises.joinToString(", ")
        binding.tvAdditionalEquipment.text = session.additionalEquipment.ifBlank {
            "None"
        }
    }

    private fun formatDateAndTime(timestamp: Timestamp): String {
        // Format the Timestamp to a readable date and time string
        val sdfDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val sdfTime = SimpleDateFormat("h:mm a", Locale.getDefault())
        val dateStr = sdfDate.format(timestamp.toDate())
        val timeStr = sdfTime.format(timestamp.toDate())

        return "$dateStr\n$timeStr"
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getTheme(): Int {
        return R.style.TransparentDialogTheme
    }
}