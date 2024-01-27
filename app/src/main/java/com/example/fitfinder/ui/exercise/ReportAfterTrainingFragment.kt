package com.example.fitfinder.ui.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitfinder.data.model.TrainingSession
import com.example.fitfinder.data.repository.exercise.UnfilledReportsRepository
import com.example.fitfinder.databinding.FragmentReportAfterTrainingBinding
import com.example.fitfinder.ui.MainActivity
import com.example.fitfinder.util.EventObserver
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.exercise.UnfilledReportsViewModel
import java.text.SimpleDateFormat
import java.util.*

class ReportAfterTrainingFragment: Fragment() {

    // Variables
    private lateinit var binding: FragmentReportAfterTrainingBinding
    private lateinit var unfilledReportsViewModel: UnfilledReportsViewModel
    private val exerciseReportAdapter = ReportAfterTrainingAdapter(emptyList())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentReportAfterTrainingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize view models
        unfilledReportsViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(UnfilledReportsRepository()))[UnfilledReportsViewModel::class.java]

        // Set up RecyclerView
        binding.rvExercises.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = exerciseReportAdapter
        }

        unfilledReportsViewModel.selectedReport.observe(viewLifecycleOwner, EventObserver { (trainingSession, partnerDetails) ->
            displayTrainingSessionDetails(trainingSession, partnerDetails)
            exerciseReportAdapter.updateData(trainingSession.exercises)
        })

    }

    private fun displayTrainingSessionDetails(trainingSession: TrainingSession, partnerDetails: Map<String, Any?>) {
        binding.tvTime.text = formatDateTime(trainingSession.dateTime.toDate())
        binding.tvLocation.text = trainingSession.location
        binding.tvSportCategory.text = trainingSession.sportCategory
        binding.tvAdditionalEquipment.text = trainingSession.additionalEquipment.ifEmpty { "None" }
        binding.tvPartner.text = "${partnerDetails["firstName"]} ${partnerDetails["lastName"]}"
    }

    private fun formatDateTime(date: Date): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return dateFormat.format(date)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).enableBackButton(true)
        (activity as MainActivity).setTitle("Report After Training")
    }

    override fun onDestroy() {
        super.onDestroy()
        unfilledReportsViewModel.clearSelectedReport()
        (activity as MainActivity).enableBackButton(false)
        (activity as MainActivity).setTitle("Unfilled Reports")
    }
}