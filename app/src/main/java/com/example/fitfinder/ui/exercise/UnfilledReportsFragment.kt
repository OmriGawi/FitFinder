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
import com.example.fitfinder.databinding.FragmentUnfilledReportsBinding
import com.example.fitfinder.ui.MainActivity
import com.example.fitfinder.util.SharedPreferencesUtil
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.exercise.UnfilledReportsViewModel

class UnfilledReportsFragment : Fragment() {

    // Variables
    private lateinit var binding: FragmentUnfilledReportsBinding
    private lateinit var unfilledReportsViewModel: UnfilledReportsViewModel
    private lateinit var unfilledReportsAdapter: UnfilledReportsAdapter
    private lateinit var userId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUnfilledReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize view models
        unfilledReportsViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(UnfilledReportsRepository()))[UnfilledReportsViewModel::class.java]

        userId = SharedPreferencesUtil.getUserId(requireContext()).toString()

        setupRecyclerView()

        unfilledReportsViewModel.fetchUnfilledReports(userId)

        // Observe unfilled reports
        unfilledReportsViewModel.unfilledReportsWithDetails.observe(viewLifecycleOwner) { unfilledReportsWithDetails ->
            unfilledReportsAdapter.updateData(unfilledReportsWithDetails)
        }

    }

    private fun setupRecyclerView() {
        unfilledReportsAdapter = UnfilledReportsAdapter(emptyList(), ::handleFillReportClicked)
        binding.rvUnfilledReports.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = unfilledReportsAdapter
        }
    }

    private fun handleFillReportClicked(trainingSession: TrainingSession) {
        // TODO: Navigate to the fill report screen and pass the TrainingSession object
        // You can use a DialogFragment or navigate to another Fragment/Activity based on your app's flow
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).enableBackButton(true)
        (activity as MainActivity).setTitle("Unfilled Reports")
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).enableBackButton(false)
        (activity as MainActivity).setTitle("Exercise")
    }
}