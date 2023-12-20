package com.example.fitfinder.ui.exercise

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.fitfinder.data.model.TrainingInvite
import com.example.fitfinder.data.repository.exercise.NewInvitesRepository
import com.example.fitfinder.databinding.FragmentNewInvitesBinding
import com.example.fitfinder.ui.MainActivity
import com.example.fitfinder.util.EventObserver
import com.example.fitfinder.util.SharedPreferencesUtil
import com.example.fitfinder.util.ToastyType
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.exercise.NewInvitesViewModel
import es.dmoral.toasty.Toasty

class NewInvitesFragment : Fragment() {

    // Variables
    private lateinit var binding: FragmentNewInvitesBinding
    private lateinit var newInvitesViewModel: NewInvitesViewModel
    private lateinit var invitesAdapter: NewInvitesAdapter
    private lateinit var userId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNewInvitesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize view models
        newInvitesViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(NewInvitesRepository()))[NewInvitesViewModel::class.java]

        userId = SharedPreferencesUtil.getUserId(requireContext()).toString()

        setupRecyclerView()

        // Observe to the ViewModel's LiveData containing invites with user details
        newInvitesViewModel.invitesWithDetails.observe(viewLifecycleOwner) { invitesWithDetails ->
            invitesAdapter.updateData(invitesWithDetails)
        }

        newInvitesViewModel.fetchNewInvites(userId)

        // Observe toast message events
        newInvitesViewModel.toastMessageEvent.observe(viewLifecycleOwner, EventObserver { (message, type) ->
            when (type) {
                ToastyType.SUCCESS -> Toasty.success(requireContext(), message, Toasty.LENGTH_SHORT, true).show()
                ToastyType.ERROR -> Toasty.error(requireContext(), message, Toasty.LENGTH_SHORT, true).show()
                else -> {    // Handle any unexpected cases or log them for debugging
                    Log.e(ContentValues.TAG, "Unexpected ToastyType: $type")
                }
            }
        })
    }

    private fun setupRecyclerView() {
        invitesAdapter = NewInvitesAdapter(emptyList<Pair<TrainingInvite, Map<String, Any?>>>()) { inviteId ->
            // Call ViewModel method to decline the invite
            newInvitesViewModel.declineInvite(inviteId, userId)
        }
        binding.rvNewInvites.adapter = invitesAdapter
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