package com.example.fitfinder.ui.search

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.fitfinder.R
import com.example.fitfinder.data.model.SkillLevel
import com.example.fitfinder.data.model.SportCategory
import com.example.fitfinder.data.model.WorkoutTime
import com.example.fitfinder.data.repository.search.SearchRepository
import com.example.fitfinder.data.repository.sportcategories.SportCategoriesRepository
import com.example.fitfinder.databinding.FragmentSearchBinding
import com.example.fitfinder.ui.MainActivity
import com.example.fitfinder.util.EventObserver
import com.example.fitfinder.util.SharedPreferencesUtil
import com.example.fitfinder.util.ToastyType
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.search.SearchViewModel
import com.example.fitfinder.viewmodel.sportcategories.SportCategoriesViewModel
import es.dmoral.toasty.Toasty

class SearchFragment : Fragment() {

    // binding
    private lateinit var binding: FragmentSearchBinding

    // View-model
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var sportCategoriesViewModel: SportCategoriesViewModel

    // Variables
    private lateinit var userId : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch userId
        userId = SharedPreferencesUtil.getUserId(requireContext()).toString()

        // Initialize ViewModels
        sportCategoriesViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(SportCategoriesRepository()))[SportCategoriesViewModel::class.java]
        searchViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(SearchRepository()))[SearchViewModel::class.java]

        // Initialize sport categories dropdown
        sportCategoriesViewModel.fetchSportCategories()
        sportCategoriesViewModel.sportCategories.observe(viewLifecycleOwner) { categories ->
            val categoriesAdapter = ArrayAdapter(
                requireContext(),
                R.layout.dropdown_item,
                categories
            )
            binding.acSportCategory.setAdapter(categoriesAdapter)
        }

        searchViewModel.toastMessageEvent.observe(viewLifecycleOwner, EventObserver { (message, type) ->
            when (type) {
                ToastyType.SUCCESS -> Toasty.success(requireContext(), message, Toasty.LENGTH_LONG, true).show()
                ToastyType.ERROR -> Toasty.error(requireContext(), message, Toasty.LENGTH_SHORT, true).show()
                ToastyType.INFO -> Toasty.info(requireContext(), message, Toasty.LENGTH_SHORT, true).show()
                else -> {    // Handle any unexpected cases or log them for debugging
                    Log.e(ContentValues.TAG, "Unexpected ToastyType: $type")
                }
            }
        })

        searchViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbSearch.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        searchViewModel.navigateToPotentialUsers.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                // Navigate to PotentialUsersFragment
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.frame_layout, PotentialUsersFragment())
                transaction.addToBackStack(null)
                transaction.commit()

                // Reset the LiveData to avoid future unwanted navigation
                searchViewModel.setNavigateToPotentialUsers(false)
            }
        }

        // Listeners
        binding.btnSearch.setOnClickListener {
            if (areAllFiltersFilled()) {
                val selectedSportCategoryName = binding.acSportCategory.text.toString()
                val selectedSkillLevel = SkillLevel.valueOf(binding.acSkillLevel.text.toString())
                val selectedSportCategory = SportCategory(selectedSportCategoryName, selectedSkillLevel)
                val selectedWorkoutTimes = binding.multiSelectionWorkoutTimes.selectedItems!!.map { WorkoutTime.valueOf(it as String) }
                val selectedRadius = binding.acRadius.text.toString()

                searchViewModel.searchPotentialUsers(userId, selectedSportCategory, selectedWorkoutTimes, selectedRadius)
            } else {
                Toasty.warning(requireContext(), "Please fill all the fields to proceed.", Toasty.LENGTH_SHORT).show()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).enableBackButton(false)

        // Adapter for skill levels
        val skillLevels = SkillLevel.values().map { it.name }
        val skillLevelsAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, skillLevels)
        binding.acSkillLevel.setAdapter(skillLevelsAdapter)

        // Adapter for radius
        val radiusValues = resources.getStringArray(R.array.radius_values).toList()
        val radiusAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, radiusValues)
        binding.acRadius.setAdapter(radiusAdapter)

        // Adapter for Workout Times
        binding.multiSelectionWorkoutTimes.items = WorkoutTime.values().map { it.name }
    }

    private fun areAllFiltersFilled(): Boolean {
        return binding.acSportCategory.text.isNotEmpty() &&
                binding.acSkillLevel.text.isNotEmpty() &&
                (binding.multiSelectionWorkoutTimes.selectedItems?.isNotEmpty() == true) &&
                binding.acRadius.text.isNotEmpty()
    }

}