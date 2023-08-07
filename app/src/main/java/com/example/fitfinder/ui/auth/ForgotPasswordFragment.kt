package com.example.fitfinder.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.fitfinder.R
import com.example.fitfinder.data.repository.auth.AuthRepository
import com.example.fitfinder.databinding.FragmentForgotPasswordBinding
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.auth.ForgotPasswordViewModel
import es.dmoral.toasty.Toasty

class ForgotPasswordFragment : DialogFragment() {
    // binding
    private lateinit var binding: FragmentForgotPasswordBinding

    // view-model
    private lateinit var viewModel: ForgotPasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the dialog to be non-cancelable.
        isCancelable = false

        // Create an instance of AuthRepository (which is a type of BaseRepository)
        val authRepository = AuthRepository()
        // Create an instance of AuthViewModel and pass the repository to it
        val viewModelFactory = ViewModelFactory(authRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[ForgotPasswordViewModel::class.java]

        binding.btnReset.setOnClickListener {
            val email = binding.etEmail.text.toString()

            if(isEmailValid(email))
                viewModel.sendPasswordResetEmail(email)
            else return@setOnClickListener
        }

        // Set up observer for resetPasswordResult
        viewModel.resetPasswordResult.observe(viewLifecycleOwner) { resetPasswordResult ->
            when (resetPasswordResult) {
                is AuthRepository.ResetPasswordResult.Success -> {
                    context?.let { Toasty.success(it, "Reset password link sent!", Toast.LENGTH_LONG, true).show() }
                }
                is AuthRepository.ResetPasswordResult.Error -> {
                    context?.let { Toasty.error(it, resetPasswordResult.message, Toast.LENGTH_LONG, true).show() }
                }
            }
        }

        // Handle the close button click
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun getTheme(): Int {
        return R.style.TransparentDialogTheme
    }

    /**
     * Validates the given email.
     * @param email The email to be validated.
     * @return True if the email is valid, false otherwise.
     */
    private fun isEmailValid(email : String): Boolean {
        if (email.isEmpty() || email.isBlank()) {
            binding.etEmail.error = "Email is required"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Invalid email format"
            return false
        }
        return true
    }
}

