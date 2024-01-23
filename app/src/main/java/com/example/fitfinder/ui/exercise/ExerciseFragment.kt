package com.example.fitfinder.ui.exercise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitfinder.R
import com.example.fitfinder.databinding.FragmentExerciseBinding

class ExerciseFragment : Fragment() {

    // binding
    private lateinit var binding: FragmentExerciseBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listeners for each CardView
        binding.cvNewInvites.setOnClickListener {
            navigateToNewInvites()
        }

        binding.cvProgress.setOnClickListener {
            navigateToProgress()
        }

        binding.cvTrainingSessions.setOnClickListener {
            navigateToTrainingSessions()
        }

        binding.cvUnfilledReports.setOnClickListener {
            navigateToUnfilledReports()
        }
    }

    private fun navigateToNewInvites() {
        val newInvitesFragment = NewInvitesFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, newInvitesFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToProgress() {
        // Replace with actual fragment class and ID
    }

    private fun navigateToTrainingSessions() {
        // Replace with actual fragment class and ID
    }

    private fun navigateToUnfilledReports() {
        val unfilledReportsFragment = UnfilledReportsFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, unfilledReportsFragment)
            .addToBackStack(null)
            .commit()
    }
}