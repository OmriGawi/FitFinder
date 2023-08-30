package com.example.fitfinder.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.fitfinder.R
import com.example.fitfinder.data.model.SkillLevel
import com.example.fitfinder.data.model.WorkoutTime
import com.example.fitfinder.data.repository.sportcategories.SportCategoriesRepository
import com.example.fitfinder.databinding.FragmentSearchBinding
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.search.SearchViewModel
import com.example.fitfinder.viewmodel.sportcategories.SportCategoriesViewModel

class SearchFragment : Fragment() {

    // binding
    private lateinit var binding: FragmentSearchBinding

    // View-model
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var sportCategoriesViewModel: SportCategoriesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init ViewModels
        sportCategoriesViewModel = ViewModelProvider(this, ViewModelFactory(SportCategoriesRepository()))[SportCategoriesViewModel::class.java]

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

    }

    override fun onResume() {
        super.onResume()
        // Adapter for skill levels
        val skillLevels = SkillLevel.values().map { it.name }
        val skillLevelsAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, skillLevels)
        binding.acSkillLevel.setAdapter(skillLevelsAdapter)

        // Adapter for Workout Times
        val workoutTimes = WorkoutTime.values().map { it.name }
        binding.multiSelectionWorkoutTimes.items = workoutTimes
    }

}