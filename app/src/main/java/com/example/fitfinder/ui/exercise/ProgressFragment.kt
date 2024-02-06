package com.example.fitfinder.ui.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitfinder.data.repository.exercise.ProgressRepository
import com.example.fitfinder.databinding.FragmentProgressBinding
import com.example.fitfinder.ui.MainActivity
import com.example.fitfinder.util.SharedPreferencesUtil
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.exercise.ProgressViewModel

class ProgressFragment: Fragment() {

    // variables
    private lateinit var binding: FragmentProgressBinding
    private lateinit var progressViewModel: ProgressViewModel
    private lateinit var userId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressAdapter = ProgressAdapter()

        binding.rvProgress.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = progressAdapter
        }

        // Initialize view models
        progressViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(ProgressRepository()))[ProgressViewModel::class.java]

        userId = SharedPreferencesUtil.getUserId(requireContext()).toString()

        progressViewModel.fetchProgressData(userId)

        progressViewModel.progressData.observe(viewLifecycleOwner) { progressList ->
            progressAdapter.updateData(progressList)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).enableBackButton(true)
        (activity as MainActivity).setTitle("Progress")
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).enableBackButton(false)
        (activity as MainActivity).setTitle("Exercise")
    }

}