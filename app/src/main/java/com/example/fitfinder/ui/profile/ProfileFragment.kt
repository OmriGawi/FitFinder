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
    private lateinit var userId : String
    private val workoutTimes = WorkoutTime.values().map { it.name }.toMutableList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                val allWorkoutTimes = WorkoutTime.values().map { it.name }
                val selectedIndices = profile.workoutTimes.map { allWorkoutTimes.indexOf(it.name) }

                binding.multiSelectionWorkoutTimes.setSelection(selectedIndices.toIntArray())

                // Set the About Me (description)
                profile.description?.let { description ->
                    if (description.isNotEmpty()) {
                        binding.etDescription.setText(description)
                    }
                }

                // For Sport Categories:
                // First, fetch all available categories from the separate ViewModel
                // This part assumes you've already instantiated the sportCategoriesViewModel and called its fetchSportCategories function.
                sportCategoriesViewModel.sportCategories.observe(viewLifecycleOwner) { allCategories ->
                    // Set Sport Categories in RecyclerView
                    val adapter = SportCategoriesAdapter(profile.sportCategories, allCategories)
                    binding.rvSportCategories.adapter = adapter
                    binding.rvSportCategories.layoutManager = LinearLayoutManager(requireContext())
                }
            }
        }



        userProfileViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            if (userProfile != null) {
                // Pass the user's sport categories to the setupRecyclerView method
                setupRecyclerView(
                    userProfile.sportCategories,
                    emptyList()
                )  // At first, the allCategories list is empty
            }
        }

        binding.ivAdd.setOnClickListener {
            addNewSportCategory()
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


    private fun setupRecyclerView(userCategories: List<SportCategory>, allCategories: List<String>) {
        val adapter = SportCategoriesAdapter(userCategories, allCategories)
        binding.rvSportCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSportCategories.adapter = adapter
    }

    private fun addNewSportCategory() {
        val currentUserProfile = userProfileViewModel.userProfile.value
        if (currentUserProfile != null) {
            val updatedSportCategories = currentUserProfile.sportCategories.toMutableList()
            updatedSportCategories.add(SportCategory("", SkillLevel.Beginner))

            val updatedUserProfile = currentUserProfile.copy(sportCategories = updatedSportCategories)

            // Use the new method here
            userProfileViewModel.updateLocalUserProfile(updatedUserProfile)

            (binding.rvSportCategories.adapter as? SportCategoriesAdapter)?.notifyDataSetChanged()
        }
    }


}


