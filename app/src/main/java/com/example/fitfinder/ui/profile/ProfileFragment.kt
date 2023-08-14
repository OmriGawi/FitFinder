package com.example.fitfinder.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.fitfinder.R
import com.example.fitfinder.data.model.SkillLevel
import com.example.fitfinder.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    // binding
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val skillLevels = SkillLevel.values().map { it.name }.toTypedArray()
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, skillLevels)
        binding.autoCompleteTextViewUserType.setAdapter(adapter)
    }
}