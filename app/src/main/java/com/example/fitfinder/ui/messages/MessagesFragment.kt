package com.example.fitfinder.ui.messages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.fitfinder.R
import com.example.fitfinder.data.repository.messages.MatchesRepository
import com.example.fitfinder.databinding.FragmentMessagesBinding
import com.example.fitfinder.util.SharedPreferencesUtil
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.messages.MatchesViewModel

class MessagesFragment : Fragment() {

    // Variables
    private lateinit var binding: FragmentMessagesBinding
    private lateinit var matchesViewModel: MatchesViewModel
    private lateinit var matchesAdapter: MatchesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize view models
        matchesViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(MatchesRepository()))[MatchesViewModel::class.java]

        // Initialize adapters
        matchesAdapter = MatchesAdapter(emptyList())

        // Setup RecyclerView
        setupRecyclerView()

        // Fetch matches and users
        val userId = SharedPreferencesUtil.getUserId(requireContext()).toString()
        matchesViewModel.fetchMatchesAndUsersForUser(userId)

        matchesViewModel.matchesWithUsers.observe(viewLifecycleOwner) { matchesWithUsers ->
            matchesAdapter.updateData(matchesWithUsers)
        }

        matchesAdapter.onMatchClickListener = { match, potentialUser ->
            matchesViewModel.selectMatchWithUser(match, potentialUser)
            navigateToChat()
        }

        // Set up Listeners
        matchesViewModel.observeMatchesUpdates(userId)
    }

    private fun setupRecyclerView() {
        binding.rvChats.adapter = matchesAdapter
    }

    private fun navigateToChat() {
        val chatFragment = ChatFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, chatFragment)
            .addToBackStack(null)
            .commit()
    }
}
