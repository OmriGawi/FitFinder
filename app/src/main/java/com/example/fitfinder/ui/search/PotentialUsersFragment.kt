package com.example.fitfinder.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.lifecycle.ViewModelProvider
import com.example.fitfinder.R
import com.example.fitfinder.data.model.PotentialUser
import com.example.fitfinder.data.repository.search.SearchRepository
import com.example.fitfinder.databinding.FragmentPotentialUsersBinding
import com.example.fitfinder.ui.MainActivity
import com.example.fitfinder.ui.profile.ProfileViewFragment
import com.example.fitfinder.util.SharedPreferencesUtil
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.search.SearchViewModel
import com.yuyakaido.android.cardstackview.*

class PotentialUsersFragment :
    Fragment(),
    PotentialUsersAdapter.CardActionCallback,
    CardStackListener {

    // binding
    private lateinit var binding: FragmentPotentialUsersBinding

    // view-models
    private lateinit var searchViewModel: SearchViewModel

    // variables
    private lateinit var cardStackLayoutManager: CardStackLayoutManager
    private lateinit var userId : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPotentialUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch userId
        userId = SharedPreferencesUtil.getUserId(requireContext()).toString()

        // Initialize ViewModels
        searchViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(SearchRepository()))[SearchViewModel::class.java]

        val adapter = PotentialUsersAdapter(emptyList(), this)
        cardStackLayoutManager = CardStackLayoutManager(context,this)
        binding.cardStackView.layoutManager = cardStackLayoutManager
        binding.cardStackView.adapter = adapter

        // Use the data from searchViewModel directly
        searchViewModel.potentialUsers.observe(viewLifecycleOwner) { users ->
            adapter.setUsers(users)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).enableBackButton(true)
    }

    override fun onLikeClicked() {
        val setting = SwipeAnimationSetting.Builder()
            .setDirection(Direction.Right)
            .setDuration(Duration.Normal.duration)
            .setInterpolator(AccelerateInterpolator())
            .build()
        cardStackLayoutManager.setSwipeAnimationSetting(setting)
        binding.cardStackView.swipe()
    }

    override fun onDislikeClicked() {
        val setting = SwipeAnimationSetting.Builder()
            .setDirection(Direction.Left)
            .setDuration(Duration.Normal.duration)
            .setInterpolator(AccelerateInterpolator())
            .build()
        cardStackLayoutManager.setSwipeAnimationSetting(setting)
        binding.cardStackView.swipe()
    }

    override fun onInfoClicked(user: PotentialUser) {
        val bundle = createUserProfileBundle(user) // Create a bundle with user details
        val profileViewFragment = ProfileViewFragment()
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

    override fun onCardSwiped(direction: Direction) {
        when (direction) {
            Direction.Right -> acceptUser()
            Direction.Left -> rejectUser()
            else -> {} // Handle other directions if necessary.
        }
    }

    private fun acceptUser() {
        val potentialUser = getCurrentPotentialUser()
        searchViewModel.acceptUser(userId, potentialUser.userId)
    }

    private fun rejectUser() {
        val potentialUser = getCurrentPotentialUser()
        searchViewModel.rejectUser(userId, potentialUser.userId)
    }

    // Get the currently displayed user
    private fun getCurrentPotentialUser(): PotentialUser {
        val position = cardStackLayoutManager.topPosition - 1
        return (binding.cardStackView.adapter as PotentialUsersAdapter).getUserAtPosition(position)
    }

    // The rest of the CardStackListener methods
    override fun onCardDragging(direction: Direction?, ratio: Float) {}
    override fun onCardRewound() {}
    override fun onCardCanceled() {}
    override fun onCardAppeared(view: View?, position: Int) {}
    override fun onCardDisappeared(view: View?, position: Int) {}

}