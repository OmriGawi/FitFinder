package com.example.fitfinder.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.fitfinder.data.repository.auth.AuthRepository
import com.example.fitfinder.databinding.ActivitySignupBinding
import com.example.fitfinder.util.Constants
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.auth.SignupViewModel
import es.dmoral.toasty.Toasty

class SignupActivity : AppCompatActivity(), DatePickerFragment.OnDateOfBirthSetListener {

    // binding
    private lateinit var binding: ActivitySignupBinding

    // view-model
    private lateinit var viewModel: SignupViewModel

    // Variables
    private var birthDate : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Create an instance of AuthRepository (which is a type of BaseRepository)
        val authRepository = AuthRepository()

        // Create an instance of AuthViewModel and pass the repository to it
        val viewModelFactory = ViewModelFactory(authRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[SignupViewModel::class.java]

        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if(isFullNameValid(fullName) && isEmailValid(email) && isBirthDateValid(birthDate) && isPasswordValid(password))
                viewModel.register(fullName, birthDate, email, password)
            else return@setOnClickListener
        }

        viewModel.result.observe(this) { result ->
            when (result) {
                is AuthRepository.RegistrationResult.Success -> {
                    Toasty.success(applicationContext, "Success!", Toast.LENGTH_LONG, true).show()
                }
                is AuthRepository.RegistrationResult.Error -> {
                    // Handle specific error
                    Toasty.error(applicationContext, result.message, Toast.LENGTH_LONG, true).show()
                }
            }
        }


        binding.btnDate.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(supportFragmentManager, "datePicker")
        }

        // Navigate to LoginActivity
        binding.ivBack.setOnClickListener {
            // Start the LoginActivity when the "Back" imageView is clicked
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        binding.tvLogin.setOnClickListener {
            // Start the LoginActivity when the "Login" textView is clicked
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    override fun onDateOfBirthSelected(dateOfBirth: String) {
        // Set the selected date on the button
        binding.btnDate.text = dateOfBirth

        // Store the selected date in the global variable
        birthDate = dateOfBirth
    }

    /**
     * Validates the given fullName.
     * @param fullName The fullName to be validated.
     * @return True if the fullName is valid, false otherwise.
     */
    private fun isFullNameValid(fullName: String): Boolean {
        if (fullName.isEmpty() || fullName.isBlank()) {
            binding.etFullName.error = "Full name is required"
            return false
        }
        val nameParts = fullName.split(" ")
        if (nameParts.size != 2) {
            binding.etFullName.error = "First name and last name are required"
            return false
        }
        return true
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

    private fun isBirthDateValid(birthDate: String): Boolean {
        if (birthDate.isEmpty() || birthDate.isBlank()) {
            binding.btnDate.error = "Date of birth is required"
            return false
        }
        return true
    }

    /**
     * Validates the given password.
     * @param password The password to be validated.
     * @return True if the password is valid, false otherwise.
     */
    private fun isPasswordValid(password : String): Boolean{
        if (password.isEmpty() || password.isBlank()) {
            binding.etPassword.error = "Password is required"
            return false
        }
        if (password.length < Constants.MIN_PASSWORD_LENGTH) {
            binding.etPassword.error = "Password need to be 6 or more chars"
            return false
        }
        return true
    }

}






