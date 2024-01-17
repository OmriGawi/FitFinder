package com.example.fitfinder.ui.auth

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.fitfinder.R


@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    var activityRule: ActivityTestRule<LoginActivity> = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun clickLoginButtonWithoutEmail_showsEmailError() {
        // Try to click the login button without entering an email
        onView(withId(R.id.btn_login)).perform(click())

        // Check if the email field displays the correct error message
        onView(withId(R.id.et_email)).check(matches(hasErrorText("Email is required")))
    }

    @Test
    fun clickLoginButtonWithoutPassword_showsPasswordError() {
        // Enter a valid email but no password
        onView(withId(R.id.et_email)).perform(typeText("user@example.com"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())

        // Check if the password field displays the correct error message
        onView(withId(R.id.et_password)).check(matches(hasErrorText("Password is required")))
    }

    @Test
    fun enterInvalidEmail_showsInvalidEmailError() {
        // Enter an invalid email and try to perform a login
        onView(withId(R.id.et_email)).perform(typeText("invalid_email"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())

        // Check if the email field displays the correct error message for invalid email format
        onView(withId(R.id.et_email)).check(matches(hasErrorText("Invalid email format")))
    }

    @Test
    fun enterShortPassword_showsPasswordLengthError() {
        // Enter a valid email and a short password
        onView(withId(R.id.et_email)).perform(typeText("user@example.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("123"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())

        // Check if the password field displays the correct error message for short password
        onView(withId(R.id.et_password)).check(matches(hasErrorText("Password need to be 6 or more chars")))
    }

    @Test
    fun clickForgotPassword_showsForgotPasswordDialog() {
        // Perform a click on the forgot password text view
        onView(withId(R.id.tv_forgotPassword)).perform(click())

        // Check if the dialog is displayed by looking for a specific view within the dialog
        onView(withId(R.id.fragmentForgotPassword)).check(matches(isDisplayed()))
    }


}
