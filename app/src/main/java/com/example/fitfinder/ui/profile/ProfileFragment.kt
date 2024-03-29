package com.example.fitfinder.ui.profile


import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.fitfinder.R
import com.example.fitfinder.data.model.SportCategory
import com.example.fitfinder.data.model.UserType
import com.example.fitfinder.data.model.WorkoutTime
import com.example.fitfinder.data.repository.profile.UserProfileRepository
import com.example.fitfinder.databinding.FragmentProfileBinding
import com.example.fitfinder.util.EventObserver
import com.example.fitfinder.util.SharedPreferencesUtil
import com.example.fitfinder.util.ToastyType
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.profile.UserProfileViewModel
import com.zeeshan.material.multiselectionspinner.MultiSelectionSpinner
import es.dmoral.toasty.Toasty

class ProfileFragment : Fragment(), AdditionalPicturesAdapter.OnImageRemovedListener, SportCategoriesAdapter.OnSportCategoryRemovedListener{

    // Bindings
    private lateinit var binding: FragmentProfileBinding

    // View-model
    private lateinit var userProfileViewModel: UserProfileViewModel

    // Variables
    private lateinit var userId: String
    private lateinit var pickImageContractProfilePicture: ActivityResultLauncher<Intent>
    private lateinit var pickImageContractAdditionalPicture: ActivityResultLauncher<Intent>

    // Adapters
    private lateinit var additionalPicturesAdapter: AdditionalPicturesAdapter
    private lateinit var sportCategoriesAdapter: SportCategoriesAdapter


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

        // Initialize Adapters
        additionalPicturesAdapter = AdditionalPicturesAdapter(mutableListOf(), requireContext(), this)
        sportCategoriesAdapter = SportCategoriesAdapter(mutableListOf(),this)
        binding.multiSelectionWorkoutTimes.items = WorkoutTime.values().map { it.name }

        // Setup RecyclerView
        setupRecyclerView()

        // Setup pickImageContract
        pickImageContractProfilePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    userProfileViewModel.updateProfilePictureUrl(userId, uri)
                }
            }
        }
        pickImageContractAdditionalPicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    userProfileViewModel.addAdditionalPicture(userId, uri)
                }
            }
        }

        // Observe changes in ViewModel
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
                // Set the Additional Pictures RecyclerView
                additionalPicturesAdapter.setData(profile.additionalPictures)

                // Set the About Me (description)
                profile.description?.let { description ->
                    if (description.isNotEmpty()) {
                        binding.etDescription.setText(description)
                    }
                }
            }
        }

        userProfileViewModel.toastMessageEvent.observe(viewLifecycleOwner, EventObserver { (message, type) ->
            when (type) {
                ToastyType.SUCCESS -> Toasty.success(requireContext(), message, Toasty.LENGTH_LONG, true).show()
                ToastyType.ERROR -> Toasty.error(requireContext(), message, Toasty.LENGTH_SHORT, true).show()
                ToastyType.INFO -> Toasty.info(requireContext(), message, Toasty.LENGTH_SHORT, true).show()
                else -> {    // Handle any unexpected cases or log them for debugging
                    Log.e(TAG, "Unexpected ToastyType: $type")
                }
            }
        })

        userProfileViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbProfileUpload.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Listeners
        binding.tvEditProfilePicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageContractProfilePicture.launch(intent)
        }

        binding.autoCompleteTextViewUserType.setOnItemClickListener { _, _, position, _ ->
            val selectedUserType = UserType.values()[position]
            userProfileViewModel.updateUserType(selectedUserType)
        }

        binding.multiSelectionWorkoutTimes.setOnItemSelectedListener(object: MultiSelectionSpinner.OnItemSelectedListener {

            override fun onItemSelected(view: View, isSelected: Boolean, position: Int) {
                // Whenever an item is selected or deselected, get all currently selected items
                val selectedWorkoutTimes = binding.multiSelectionWorkoutTimes.selectedItems.map {
                    WorkoutTime.valueOf(it.toString())
                }.toMutableList()

                // Update the ViewModel with the new list
                userProfileViewModel.updateWorkoutTimes(selectedWorkoutTimes)
            }

            override fun onSelectionCleared() {
                // When all selections are cleared, send an empty list to the ViewModel
                userProfileViewModel.updateWorkoutTimes(mutableListOf())
            }
        })

        binding.tvViewProfile.setOnClickListener{
            navigateToViewProfile()
        }


        binding.ivAdd.setOnClickListener {
            val dialog = SportCategoryDialogFragment()
            dialog.show(parentFragmentManager, "SportCategoryDialog")
        }

        binding.etDescription.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                userProfileViewModel.updateUserDescription(binding.etDescription.text.toString())
            }
        }

        binding.ivAddAdditional.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageContractAdditionalPicture.launch(intent)
        }

        binding.ivEditAdditional.setOnClickListener {
            additionalPicturesAdapter.toggleEditMode()
        }

        binding.btnSave.setOnClickListener {
            userProfileViewModel.updateUserProfile(userId)
        }

    }

    private fun navigateToViewProfile() {
        val profileViewFragment = ProfileViewFragment()

        // Get Bundle from ViewModel and set as Fragment arguments
        val bundle = userProfileViewModel.createUserProfileBundle()
        profileViewFragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, profileViewFragment)
            .addToBackStack(null)
            .commit()
    }


    override fun onResume() {
        super.onResume()
        // Adapter for User Type
        val userTypes = UserType.values().map { it.name }.toTypedArray()
        val userTypesAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, userTypes)
        binding.autoCompleteTextViewUserType.setAdapter(userTypesAdapter)
    }

    override fun onImageRemoved(position: Int, imageUrl: String) {
        userProfileViewModel.removeAdditionalPicture(imageUrl)
    }

    override fun onSportCategoryRemoved(position: Int, sportCategory: SportCategory) {
        userProfileViewModel.removeSportCategory(sportCategory)
    }

    private fun setupRecyclerView() {
        binding.rvSportCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSportCategories.adapter = sportCategoriesAdapter

        binding.rvAdditionalPictures.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvAdditionalPictures.adapter = additionalPicturesAdapter
    }
}


