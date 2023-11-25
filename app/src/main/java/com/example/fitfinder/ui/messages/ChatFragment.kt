package com.example.fitfinder.ui.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.fitfinder.databinding.FragmentChatBinding
import com.example.fitfinder.ui.MainActivity
import com.example.fitfinder.viewmodel.messages.MatchesViewModel

class ChatFragment : Fragment() {

    // Variables
    private lateinit var binding: FragmentChatBinding
    private lateinit var matchesViewModel: MatchesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Hide the ActionBar and Bottom Navigation
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        (activity as? MainActivity)?.setBottomNavigationVisibility(false)

        // Initialize view models
        matchesViewModel = ViewModelProvider(requireActivity())[MatchesViewModel::class.java]

        // Observe the selected match and user
        matchesViewModel.selectedMatchWithUser.observe(viewLifecycleOwner) { matchWithUser ->
            val match = matchWithUser.first
            val potentialUser = matchWithUser.second

            // Set the name of the user in the TextView
            binding.tvUserName.text = potentialUser.firstName

            // Load the user's profile picture into the ImageView
            Glide.with(this)
                .load(potentialUser.profilePictureUrl)
                .into(binding.ivProfilePicture)
        }

        // Set the custom back button logic
        binding.backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Show the ActionBar again when the fragment is destroyed
        (activity as? AppCompatActivity)?.supportActionBar?.show()

        // Show the bottom navigation bar again when the fragment is destroyed
        (activity as? MainActivity)?.setBottomNavigationVisibility(true)
    }
}

