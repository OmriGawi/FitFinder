package com.example.fitfinder.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitfinder.R
import com.example.fitfinder.ui.MainActivity

class PotentialUsersFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_potential_users, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).enableBackButton(true)
    }

}