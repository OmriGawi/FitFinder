package com.example.fitfinder.ui.profile


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.fitfinder.R
import com.example.fitfinder.data.model.UserType
import com.example.fitfinder.data.model.WorkoutTime
import com.example.fitfinder.data.repository.profile.UserProfileRepository
import com.example.fitfinder.databinding.FragmentProfileBinding
import com.example.fitfinder.util.SharedPreferencesUtil
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.profile.UserProfileViewModel

class ProfileFragment : Fragment(){

    // bindings
    private lateinit var binding: FragmentProfileBinding

    // view-model
    private lateinit var userProfileViewModel: UserProfileViewModel

    // variables
    private lateinit var userId: String

    // adapters
    private val sportCategoriesAdapter = SportCategoriesAdapter(mutableListOf())


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize view models
        userProfileViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(UserProfileRepository()))[UserProfileViewModel::class.java]

        // Fetch the userProfile
        userId = SharedPreferencesUtil.getUserId(requireContext()).toString()
        userProfileViewModel.fetchUserProfile(userId)

        // Setup RecyclerView
        setupRecyclerView()

        binding.rvSportCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSportCategories.adapter = sportCategoriesAdapter

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

                // Set the sportCategories RecyclerView
                sportCategoriesAdapter.setData(profile.sportCategories)

                // Set the About Me (description)
                profile.description?.let { description ->
                    if (description.isNotEmpty()) {
                        binding.etDescription.setText(description)
                    }
                }

            }
        }

        binding.ivAdd.setOnClickListener {
            val dialog = SportCategoryDialogFragment()
            dialog.show(parentFragmentManager, "SportCategoryDialog")
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

    private fun setupRecyclerView() {
        binding.rvSportCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSportCategories.adapter = sportCategoriesAdapter
    }

}


