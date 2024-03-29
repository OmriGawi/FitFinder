package com.example.fitfinder.ui.auth

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.fitfinder.data.repository.auth.AuthRepository
import com.example.fitfinder.databinding.ActivityLoginBinding
import com.example.fitfinder.ui.MainActivity
import com.example.fitfinder.util.Constants
import com.example.fitfinder.util.SharedPreferencesUtil
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

        if (viewModel.isUserLoggedIn()) {
            navigateToMainActivity()
        }

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
                    // Save user ID to SharedPreferences
                    viewModel.getCurrentUserId()
                        ?.let { SharedPreferencesUtil.saveUserId(applicationContext, it) }

                    Toasty.success(applicationContext, "Login Successful!", Toast.LENGTH_SHORT, true).show()
                    navigateToMainActivity()
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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun navigateToMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
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
