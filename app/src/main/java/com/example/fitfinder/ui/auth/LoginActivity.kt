package com.example.fitfinder.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.fitfinder.data.repository.auth.AuthRepository
import com.example.fitfinder.databinding.ActivityLoginBinding
import com.example.fitfinder.util.Constants
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.auth.LoginViewModel
import es.dmoral.toasty.Toasty

class LoginActivity : AppCompatActivity() {

    // binding
    private lateinit var binding: ActivityLoginBinding

    // view-model
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Create an instance of AuthRepository (which is a type of BaseRepository)
        val authRepository = AuthRepository()

        // Create an instance of AuthViewModel and pass the repository to it
        val viewModelFactory = ViewModelFactory(authRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if(isEmailValid(email) && isPasswordValid(password))
                viewModel.login(email, password)
            else return@setOnClickListener
        }

        viewModel.result.observe(this) { result ->
            when (result) {
                is AuthRepository.LoginResult.Success -> {
                    // Handle successful login
                    Toasty.success(applicationContext, "Login Successful!", Toast.LENGTH_LONG, true).show()
                }
                is AuthRepository.LoginResult.Error -> {
                    // Handle error
                    Toasty.error(applicationContext, result.message, Toast.LENGTH_LONG, true).show()
                }
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            val fragment = ForgotPasswordFragment()
            fragment.show(supportFragmentManager, "forgotPasswordDialog")
        }

        //Navigate to SignupActivity
        binding.tvRegister.setOnClickListener {
            // Start the SignupActivity when the "Register" textView is clicked
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
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
