package com.example.fitfinder.ui.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.fitfinder.R
import com.example.fitfinder.data.model.Message
import com.example.fitfinder.data.model.PotentialUser
import com.example.fitfinder.data.repository.messages.ChatRepository
import com.example.fitfinder.databinding.FragmentChatBinding
import com.example.fitfinder.ui.MainActivity
import com.example.fitfinder.ui.profile.ProfileViewFragment
import com.example.fitfinder.util.SharedPreferencesUtil
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.messages.ChatViewModel
import com.example.fitfinder.viewmodel.messages.MatchesViewModel

class ChatFragment : Fragment() {

    // Variables
    private lateinit var binding: FragmentChatBinding
    private lateinit var matchesViewModel: MatchesViewModel
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter

    private lateinit var potentialUser: PotentialUser

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
        chatViewModel = ViewModelProvider(this, ViewModelFactory(ChatRepository()))[ChatViewModel::class.java]

        // Initialize the chat adapter with an empty list and the current user's ID
        val currentUserId = SharedPreferencesUtil.getUserId(requireContext()).toString()
        chatAdapter = ChatAdapter(currentUserId)

        // Observe the selected match and user
        matchesViewModel.selectedMatchWithUser.observe(viewLifecycleOwner) { matchWithUser ->
            val match = matchWithUser.first
            potentialUser = matchWithUser.second

            // Set the name of the user in the TextView
            binding.tvUserName.text = potentialUser.firstName

            // Load the user's profile picture into the ImageView
            Glide.with(this)
                .load(potentialUser.profilePictureUrl)
                .into(binding.ivProfilePicture)

            // Load messages for the selected match
            chatViewModel.loadMessages(match.matchId)
        }

        // Setup the RecyclerView with the adapter
        binding.rvChat.apply {
            layoutManager = LinearLayoutManager(context).apply {
                reverseLayout = true
                stackFromEnd = true
            }
            adapter = chatAdapter
        }


        // Observe messages LiveData
        chatViewModel.messages.observe(viewLifecycleOwner) { messages ->
            // Update the adapter with new messages
            chatAdapter.setMessages(messages)
            binding.rvChat.scrollToPosition(0) // Scrolls to the bottom of the chat where new messages appear
        }

        // Set the custom back button logic
        binding.backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        binding.btnSend.setOnClickListener {
            val messageContent = binding.etMessage.text.toString()
            if (messageContent.isNotEmpty()) {
                val newMessage = Message(
                    messageId = "", // This will be generated by Firestore
                    sender = currentUserId,
                    content = messageContent,
                    timestamp = System.currentTimeMillis(), // Or use Firebase server timestamp
                    seen = false
                )
                val matchId = matchesViewModel.selectedMatchWithUser.value?.first?.matchId
                matchId?.let {
                    chatViewModel.sendMessage(it, newMessage)
                    binding.etMessage.text.clear()
                }
            }
        }

        binding.ivProfilePicture.setOnClickListener {
            navigateToViewProfile()
        }

        binding.tvUserName.setOnClickListener {
            navigateToViewProfile()
        }

    }

    private fun navigateToViewProfile() {
        val profileViewFragment = ProfileViewFragment()

        // Get Bundle from ViewModel and set as Fragment arguments
        val bundle = createUserProfileBundle(potentialUser)
        profileViewFragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, profileViewFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun createUserProfileBundle(user: PotentialUser): Bundle {
        val bundle = Bundle()
        bundle.putString("firstName", user.firstName)
        bundle.putString("lastName", user.lastName)
        bundle.putString("profilePictureUrl", user.profilePictureUrl)
        bundle.putString("userType", user.userType.toString())

        val workoutTimesStr = user.workoutTimes.joinToString(separator = ",") { it.name }
        bundle.putString("workoutTimes", workoutTimesStr)

        // Convert each SportCategory into a String representation
        val sportCategoriesStrList = user.sportCategories.map { "${it.name}|${it.skillLevel}" }
        bundle.putStringArrayList("sportCategories", ArrayList(sportCategoriesStrList))

        bundle.putStringArrayList("additionalPictures", ArrayList(user.additionalPictures))

        return bundle
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Show the ActionBar again when the fragment is destroyed
        (activity as? AppCompatActivity)?.supportActionBar?.show()

        // Show the bottom navigation bar again when the fragment is destroyed
        (activity as? MainActivity)?.setBottomNavigationVisibility(true)
    }
}

