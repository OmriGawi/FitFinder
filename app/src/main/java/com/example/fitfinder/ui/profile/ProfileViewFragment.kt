package com.example.fitfinder.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitfinder.R
import com.example.fitfinder.data.model.SkillLevel
import com.example.fitfinder.data.model.SportCategory
import com.example.fitfinder.databinding.FragmentProfileViewBinding
import com.example.fitfinder.ui.MainActivity

class ProfileViewFragment: Fragment() {
    // binding
    private lateinit var binding: FragmentProfileViewBinding

    private lateinit var screenName: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the arguments
        val args = arguments

        screenName = args?.getString("screen").toString()

        // Retrieve and set user fields
        val firstName = args?.getString("firstName", "")
        val lastName = args?.getString("lastName", "")
        binding.tvFullName.text = "${firstName} ${lastName}"

        val profilePictureUrl = args?.getString("profilePictureUrl", "")
        context?.let {
            Glide.with(it)
                .load(profilePictureUrl)
                .into(binding.ivProfile)
        }

        val userType = args?.getString("userType", "")
        binding.tvUserType.text = userType

        val workoutTimes = args?.getString("workoutTimes", "")
        binding.tvWorkoutTimes.text = workoutTimes


        // Retrieve and parse sportCategories
        val sportCategoriesStrList = args?.getStringArrayList("sportCategories") ?: arrayListOf()
        val sportCategories = sportCategoriesStrList.map { categoryStr ->
            val parts = categoryStr.split("|")
            if (parts.size == 2) {
                // Assuming SkillLevel is an enum or has a method to parse from string
                SportCategory(name = parts[0], skillLevel = SkillLevel.valueOf(parts[1]))
            } else {
                null // or some default SportCategory
            }
        }.filterNotNull() // Remove any nulls that resulted from parsing errors
        when (sportCategories.size) {
            0 -> {
            // No sport categories or more than 3, handle as needed
            // Maybe hide all or show a message
            binding.tvSportCategory1.visibility = View.GONE
            binding.tvSkillLevel1.visibility = View.GONE
            binding.tvSportCategory2.visibility = View.GONE
            binding.tvSkillLevel2.visibility = View.GONE
            binding.tvSportCategory3.visibility = View.GONE
            binding.tvSkillLevel3.visibility = View.GONE
            }
            1 -> {
                // Only one sport category, display it in the middle
                binding.tvSportCategory2.text = sportCategories[0].name
                binding.tvSkillLevel2.text = sportCategories[0].skillLevel.toString()

                // Hide the other views
                binding.tvSportCategory1.visibility = View.GONE
                binding.tvSkillLevel1.visibility = View.GONE
                binding.tvSportCategory3.visibility = View.GONE
                binding.tvSkillLevel3.visibility = View.GONE
            }
            2 -> {
                // Two sport categories, display them on the sides
                binding.tvSportCategory1.text = sportCategories[0].name
                binding.tvSkillLevel1.text = sportCategories[0].skillLevel.toString()
                binding.tvSportCategory3.text = sportCategories[1].name
                binding.tvSkillLevel3.text = sportCategories[1].skillLevel.toString()

                // Hide the middle views
                binding.tvSportCategory2.visibility = View.GONE
                binding.tvSkillLevel2.visibility = View.GONE
            }
            else -> {
                // Three sport categories, display all
                binding.tvSportCategory1.text = sportCategories[0].name
                binding.tvSkillLevel1.text = sportCategories[0].skillLevel.toString()
                binding.tvSportCategory2.text = sportCategories[1].name
                binding.tvSkillLevel2.text = sportCategories[1].skillLevel.toString()
                binding.tvSportCategory3.text = sportCategories[2].name
                binding.tvSkillLevel3.text = sportCategories[2].skillLevel.toString()
            }
        }

        val additionalPictures = args?.getStringArrayList("additionalPictures") ?: arrayListOf()

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_additionalPictures)
        recyclerView.layoutManager = GridLayoutManager(context, 2) // 2 columns in the grid
        recyclerView.adapter = ProfileViewAdditionalPicturesAdapter(additionalPictures)


    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).enableBackButton(true)
        (activity as MainActivity).setTitle("Profile View")
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).enableBackButton(false)
        (activity as MainActivity).setTitle(screenName)
    }
}