package com.example.fitfinder.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fitfinder.R
import com.example.fitfinder.data.repository.auth.AuthRepository
import com.example.fitfinder.databinding.ActivityMainBinding
import com.example.fitfinder.ui.auth.LoginActivity
import com.example.fitfinder.ui.calendar.CalendarFragment
import com.example.fitfinder.ui.exercise.ExerciseFragment
import com.example.fitfinder.ui.messages.MessagesFragment
import com.example.fitfinder.ui.profile.ProfileFragment
import com.example.fitfinder.ui.search.SearchFragment
import com.example.fitfinder.viewmodel.ViewModelFactory
import com.example.fitfinder.viewmodel.auth.LogoutViewModel
import es.dmoral.toasty.Toasty

class MainActivity : AppCompatActivity() {

    // binding
    private lateinit var binding: ActivityMainBinding

    // view-model
    private lateinit var logoutViewModel: LogoutViewModel

    // Fragments
    private val profileFragment by lazy { ProfileFragment() }
    private val messagesFragment by lazy { MessagesFragment() }
    private val calendarFragment by lazy { CalendarFragment() }
    private val searchFragment by lazy { SearchFragment() }
    private val exerciseFragment by lazy { ExerciseFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Set the initial title for the ActionBar
        supportActionBar?.title = getString(R.string.profile)
        // Set the first fragment to be displayed
        replaceFragment(profileFragment)

        // Create an instance of AuthRepository (which is a type of BaseRepository)
        val authRepository = AuthRepository()
        // Create an instance of AuthViewModel and pass the repository to it
        val viewModelFactory = ViewModelFactory(authRepository)
        logoutViewModel = ViewModelProvider(this, viewModelFactory)[LogoutViewModel::class.java]

        binding.bottomNavigationView.setOnItemSelectedListener {
            val title: String = when(it.itemId) {
                R.id.bottom_nav_profile -> {
                    replaceFragment(profileFragment)
                    getString(R.string.profile)
                }
                R.id.bottom_nav_messages -> {
                    replaceFragment(messagesFragment)
                    getString(R.string.messages)
                }
                R.id.bottom_nav_calendar -> {
                    replaceFragment(calendarFragment)
                    getString(R.string.calendar)
                }
                R.id.bottom_nav_search -> {
                    replaceFragment(searchFragment)
                    getString(R.string.search)
                }
                R.id.bottom_nav_exercise -> {
                    replaceFragment(exerciseFragment)
                    getString(R.string.exercise)
                }
                else -> ""
            }
            supportActionBar?.title = title
            true
        }

        logoutViewModel.result.observe(this) { result ->
            when (result) {
                is AuthRepository.LogoutResult.Success -> {
                    // Handle successful Logout
                    Toasty.success(applicationContext, "Logout Successful!", Toast.LENGTH_LONG, true).show()
                    // Start the LoginActivity
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is AuthRepository.LogoutResult.Error -> {
                    // Handle error
                    Toasty.error(applicationContext, result.message, Toast.LENGTH_LONG, true).show()
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_top_overflow, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.overflow_item_logout -> {
                logoutViewModel.logout()
            }
            // Handle other menu items if needed
        }
        return super.onOptionsItemSelected(item)
    }


    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}