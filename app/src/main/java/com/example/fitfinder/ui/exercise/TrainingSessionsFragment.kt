package com.example.fitfinder.ui.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitfinder.R
import com.example.fitfinder.data.repository.exercise.TrainingSessionsRepository
import com.example.fitfinder.databinding.FragmentTrainingSessionsBinding
import com.example.fitfinder.ui.MainActivity
import com.example.fitfinder.util.SharedPreferencesUtil
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.exercise.TrainingSessionsViewModel


class TrainingSessionsFragment : Fragment(){

    // variables
    private lateinit var binding: FragmentTrainingSessionsBinding
    private lateinit var userId: String
    private lateinit var trainingSessionsViewModel: TrainingSessionsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTrainingSessionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        val trainingSessionsAdapter = TrainingSessionsAdapter()
        binding.rvTrainingSessions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = trainingSessionsAdapter
        }

        // Setup Filter Dropdown
        val filterValues = resources.getStringArray(R.array.training_session_filter).toList()
        val filterAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, filterValues)
        binding.acFilter.setAdapter(filterAdapter)
        // Set the default value without triggering autocomplete suggestions
        binding.acFilter.setText(filterValues[1], false)

        // Initialize view models
        trainingSessionsViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(TrainingSessionsRepository()))[TrainingSessionsViewModel::class.java]

        // Fetch trainingSessions
        userId = SharedPreferencesUtil.getUserId(requireContext()).toString()
        trainingSessionsViewModel.fetchTrainingSessions(userId)



        // Handle dropdown item selection
        binding.acFilter.setOnItemClickListener { _, _, position, _ ->
            trainingSessionsViewModel.filterSessions(filterValues[position])
        }

        // Observe the filtered training sessions LiveData
        trainingSessionsViewModel.filteredTrainingSessions.observe(viewLifecycleOwner) { sessions ->
            (binding.rvTrainingSessions.adapter as TrainingSessionsAdapter).updateData(sessions)
        }

    }


    override fun onResume() {
        super.onResume()
        (activity as MainActivity).enableBackButton(true)
        (activity as MainActivity).setTitle("Training Sessions")

    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).enableBackButton(false)
        (activity as MainActivity).setTitle("Exercise")
    }

}