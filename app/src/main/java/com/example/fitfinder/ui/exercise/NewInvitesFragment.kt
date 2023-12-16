package com.example.fitfinder.ui.exercise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitfinder.data.repository.exercise.NewInvitesRepository
import com.example.fitfinder.databinding.FragmentNewInvitesBinding
import com.example.fitfinder.ui.MainActivity
import com.example.fitfinder.util.SharedPreferencesUtil
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.exercise.NewInvitesViewModel

class NewInvitesFragment : Fragment() {

    // Variables
    private lateinit var binding: FragmentNewInvitesBinding
    private lateinit var newInvitesViewModel: NewInvitesViewModel
    private lateinit var invitesAdapter: NewInvitesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNewInvitesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize view models
        newInvitesViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(NewInvitesRepository()))[NewInvitesViewModel::class.java]

        val userId = SharedPreferencesUtil.getUserId(requireContext()).toString()

        setupRecyclerView()

        newInvitesViewModel.fetchNewInvites(userId)

        // Inside NewInvitesFragment onViewCreated or a similar lifecycle method
        newInvitesViewModel.invites.observe(viewLifecycleOwner) { invites ->
            invitesAdapter.updateData(invites)
        }


    }

    private fun setupRecyclerView() {
        invitesAdapter = NewInvitesAdapter(emptyList())
        binding.rvNewInvites.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = invitesAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).enableBackButton(true)
        (activity as MainActivity).setTitle("New Invites")
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).enableBackButton(false)
        (activity as MainActivity).setTitle("Exercise")
    }

}