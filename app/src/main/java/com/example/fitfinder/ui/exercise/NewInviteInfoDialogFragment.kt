package com.example.fitfinder.ui.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.fitfinder.R
import com.example.fitfinder.data.model.TrainingInvite
import com.example.fitfinder.data.repository.exercise.NewInvitesRepository
import com.example.fitfinder.databinding.DialogNewInviteInfoBinding
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.exercise.NewInvitesViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class NewInviteInfoDialogFragment : DialogFragment() {

    private var _binding: DialogNewInviteInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var newInvitesViewModel: NewInvitesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogNewInviteInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize view models
        newInvitesViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(NewInvitesRepository()))[NewInvitesViewModel::class.java]

        // Observe the selected invite from the ViewModel
        newInvitesViewModel.selectedInvite.observe(viewLifecycleOwner) { invite ->
            displayInviteDetails(invite)
        }

    }

    private fun displayInviteDetails(invite: TrainingInvite) {
        // Update UI elements with invite details
        binding.tvTime.text = formatDateAndTime(invite.dateTime)
        binding.tvLocation.text = invite.location
        binding.tvSportCategory.text = invite.sportCategory
        binding.tvExercises.text = invite.exercises.joinToString(", ")
        binding.tvAdditionalEquipment.text = invite.additionalEquipment.ifBlank {
            "None"
        }
    }

    private fun formatDateAndTime(timestamp: Timestamp): String {
        // Format the Timestamp to a readable date and time string
        val sdfDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val sdfTime = SimpleDateFormat("h:mm a", Locale.getDefault())
        val dateStr = sdfDate.format(timestamp.toDate())
        val timeStr = sdfTime.format(timestamp.toDate())

        return "$dateStr\n$timeStr"
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getTheme(): Int {
        return R.style.TransparentDialogTheme
    }
}
