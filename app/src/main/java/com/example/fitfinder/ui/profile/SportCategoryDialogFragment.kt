package com.example.fitfinder.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.fitfinder.R
import com.example.fitfinder.data.model.SkillLevel
import com.example.fitfinder.data.model.SportCategory
import com.example.fitfinder.data.repository.profile.UserProfileRepository
import com.example.fitfinder.data.repository.sportcategories.SportCategoriesRepository
import com.example.fitfinder.databinding.DialogAddNewSportCategoryBinding
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.profile.UserProfileViewModel
import com.example.fitfinder.viewmodel.sportcategories.SportCategoriesViewModel
import es.dmoral.toasty.Toasty

class SportCategoryDialogFragment : DialogFragment() {

    // binding
    private lateinit var binding : DialogAddNewSportCategoryBinding

    // view-model
    private lateinit var sportCategoriesViewModel: SportCategoriesViewModel
    private lateinit var userProfileViewModel: UserProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddNewSportCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the dialog to be non-cancelable.
        isCancelable = false

        // Init ViewModels
        sportCategoriesViewModel = ViewModelProvider(this, ViewModelFactory(SportCategoriesRepository()))[SportCategoriesViewModel::class.java]
        userProfileViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(UserProfileRepository()))[UserProfileViewModel::class.java]

        // Fetch the sport categories from Firestore
        sportCategoriesViewModel.fetchSportCategories()

        // Observe the sport categories from ViewModel
        sportCategoriesViewModel.sportCategories.observe(viewLifecycleOwner) { categories ->
            val categoriesAdapter = ArrayAdapter(
                requireContext(),
                R.layout.dropdown_item,
                categories
            )
            binding.acSportCategory.setAdapter(categoriesAdapter)
        }

        // Setting up skill levels
        val skillLevels = SkillLevel.values().map { it.name }
        val skillLevelsAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, skillLevels)
        binding.acSkillLevel.setAdapter(skillLevelsAdapter)

        // Handle the cancel button
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        // Handle the add button
        binding.btnAdd.setOnClickListener {
            val selectedCategory = binding.acSportCategory.text.toString()
            val selectedSkillLevelString = binding.acSkillLevel.text.toString()

            if (selectedCategory.isEmpty() || selectedSkillLevelString.isEmpty()) {
                // Show Toasty message if either dropdown is not selected
                Toasty.warning(requireContext(), "Please select both category and skill level.", Toasty.LENGTH_SHORT, true).show()

            } else {
                // If both are selected, proceed as before
                val selectedSkillLevel = SkillLevel.valueOf(selectedSkillLevelString)

                val sportCategory = SportCategory(selectedCategory, selectedSkillLevel)
                userProfileViewModel.addSportCategory(sportCategory)
                Toasty.success(requireContext(), "Sport category added successfully!.", Toasty.LENGTH_SHORT, true).show()

                dismiss()
            }
        }

    }

    override fun getTheme(): Int {
        return R.style.TransparentDialogTheme
    }
}
