package com.example.fitfinder.ui.profile

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.fitfinder.R
import com.example.fitfinder.data.model.SkillLevel
import com.example.fitfinder.data.model.SportCategory
import com.example.fitfinder.data.model.UserType
import com.example.fitfinder.data.model.WorkoutTime
import com.example.fitfinder.data.repository.profile.UserProfileRepository
import com.example.fitfinder.data.repository.sportcategories.SportCategoriesRepository
import com.example.fitfinder.databinding.FragmentProfileBinding
import com.example.fitfinder.util.SharedPreferencesUtil
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.profile.UserProfileViewModel
import com.example.fitfinder.viewmodel.sportcategories.SportCategoriesViewModel

class ProfileFragment : Fragment() {

    // bindings
    private lateinit var binding: FragmentProfileBinding

    // view-model
    private lateinit var userProfileViewModel: UserProfileViewModel
    private lateinit var sportCategoriesViewModel: SportCategoriesViewModel

    // variables
    private lateinit var userId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize view models
        userProfileViewModel = ViewModelProvider(this, ViewModelFactory(UserProfileRepository()))[UserProfileViewModel::class.java]
        sportCategoriesViewModel = ViewModelProvider(this, ViewModelFactory(SportCategoriesRepository()))[SportCategoriesViewModel::class.java]

        // Fetch the sport categories from Firestore
        sportCategoriesViewModel.fetchSportCategories()

        // Fetch the userProfile
        userId = SharedPreferencesUtil.getUserId(requireContext()).toString()
        userProfileViewModel.fetchUserProfile(userId)
        userProfileViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            userProfile?.let { profile ->
                // Set the Profile Picture
                profile.profilePictureUrl?.let { url ->
                    if (url.isNotEmpty()) {
                        Glide.with(requireContext())
                            .load(url)
                            .into(binding.ivProfile)
                    }
                }

                // Set the UserType in the Dropdown
                binding.autoCompleteTextViewUserType.setText(profile.userType.toString(), false)

                // Set the Workout Times in MultiSelectionSpinner
                val workoutTimesText = profile.workoutTimes.joinToString(", ") { it.name }
                binding.multiSelectionWorkoutTimes.setText(workoutTimesText)

                // Set the About Me (description)
                profile.description?.let { description ->
                    if (description.isNotEmpty()) {
                        binding.etDescription.setText(description)
                    }
                }

                // Pass the user's sport categories to the setupRecyclerView method
                setupRecyclerView(profile.sportCategories)
            }
        }

        binding.ivAdd.setOnClickListener {
        }
    }

    override fun onResume() {
        super.onResume()

        // Adapter for User Type
        val userTypes = UserType.values().map { it.name }.toTypedArray()
        val userTypesAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, userTypes)
        binding.autoCompleteTextViewUserType.setAdapter(userTypesAdapter)

        // Adapter for Workout Times
        val workoutTimes = WorkoutTime.values().map { it.name }
        binding.multiSelectionWorkoutTimes.items = workoutTimes
    }

    private fun setupRecyclerView(userCategories: MutableList<SportCategory>) {
        val adapter = SportCategoriesAdapter(userCategories)
        binding.rvSportCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSportCategories.adapter = adapter
    }
}


