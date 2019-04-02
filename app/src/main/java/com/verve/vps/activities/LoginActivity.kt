package com.verve.vps.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.content.Context
import android.graphics.Paint
import android.animation.Animator
import android.os.Build
import android.os.Handler
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator

import com.google.gson.Gson
import com.verve.vps.R
import com.verve.vps.helpers.ApiServiceHelper
import com.verve.vps.helpers.Constants.AUTH_TOKEN
import com.verve.vps.helpers.Constants.LOGIN_STATUS
import com.verve.vps.helpers.Constants.LOGIN_TOKEN
import com.verve.vps.helpers.Constants.PREFS_NAME
import com.verve.vps.helpers.Constants.USER_LOGGED_IN
import com.verve.vps.helpers.Constants.LOGIN_USER_DATA
import com.verve.vps.helpers.Constants.OFFICER_NAME
import com.verve.vps.helpers.Constants.OFFICER_RANK
import com.verve.vps.helpers.Constants.OFFICER_SERVICE_NO
import com.verve.vps.helpers.NetworkHelper
import com.verve.vps.helpers.PreferencesHelper.sharedPreferences
import com.verve.vps.models.LoginResult
import com.verve.vps.utils.Utils

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

import okhttp3.Credentials

import kotlinx.android.synthetic.main.activity_login.*

import timber.log.Timber

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(){

    private val mCompositeDisposable = CompositeDisposable()
    private lateinit var basicLoginToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(PREFS_NAME , Context.MODE_PRIVATE)

        setContentView(R.layout.activity_login)
        // Set up the login form.
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })


        btnLogin.setOnClickListener { attemptLogin() }
        btnForgotPassword.setOnClickListener{ forgotPassword() }
        btnForgotPassword.paintFlags = btnForgotPassword.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        btnForgotPassword.visibility = View.GONE

        if (sharedPreferences.getBoolean(USER_LOGGED_IN,false)){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        // show loading
        btnLogin.startAnimation()
        // hide the keyboard
        Utils.hideKeyboard(btnLogin,this)

        // Reset errors.
        service_number.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val serviceNumber = service_number.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(serviceNumber)) {
            password.error = "Please enter a valid password!"
            focusView = password
            cancel = true
        }
        else if (!isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        // Check for a valid service number
        if (TextUtils.isEmpty(serviceNumber)) {
            service_number.error = "Service number is required"
            focusView = service_number
            cancel = true
        } else if (!isServiceNumberValid(serviceNumber)) {
            service_number.error = "Service number must have at least 6 characters!"
            focusView = service_number
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()

            // show the form error
            showButtonLoadingError()

        } else {

            // login in the user if the form is ok
            loginUser(serviceNumber,passwordStr)
        }
    }

    private fun showButtonLoadingError() {
        btnLogin.doneLoadingAnimation(R.color.colorAccent, Utils.getBitmapFromVectorDrawable(this,R.drawable.ic_error_red))
        Handler().postDelayed({  btnLogin.revertAnimation() }, 2000)
    }

    /**
     * Handles the forgot password action for a specific user
     */
    private fun forgotPassword(){
        // then show the main screen
        startActivity( Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
    }

    private fun isServiceNumberValid(username: String): Boolean {
        return username.length >= 6
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

    private fun loginUser(serviceNumber: String, password: String) {
        if(NetworkHelper.checkInternetConnectivity(this)) {
            // login via the api
            basicLoginToken = Credentials.basic(serviceNumber, password)
            ApiServiceHelper.apiService!!
            .loginUser(basicLoginToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (::handleLoginResult, ::handleApiError)
            .addTo(mCompositeDisposable)
        }
        else {
            // show the button error
            showButtonLoadingError()
            // show the error message
            Utils.showSnackbar(this, "Login failed, please make sure your internet connection is working well and try again", R.drawable.ic_error_red)
        }

    }

    private fun handleLoginResult(loginResult: LoginResult) {

        // save the login settings
       sharedPreferences.edit().apply{
           putBoolean(USER_LOGGED_IN, true)
           putString(AUTH_TOKEN, loginResult.access_token)
           putBoolean(LOGIN_STATUS, loginResult.status)
           putString(LOGIN_USER_DATA, Gson().toJson(loginResult.userData))
           putString(LOGIN_TOKEN, basicLoginToken)
           putString(OFFICER_NAME, "${loginResult.userData.firstname} ${loginResult.userData.lastname} ${loginResult.userData.surname}")
           putString(OFFICER_SERVICE_NO, loginResult.userData.serviceno)
           putString(OFFICER_RANK, loginResult.userData.role)
           apply()
       }
        // then login the user
        loginAnimation()


    }

    private fun handleApiError(e: Throwable) {
        Timber.e(e)
        // show the button error
        showButtonLoadingError()
        Utils.showSnackbar(this, "Login failed, please check your credentials and make sure they are correct", R.drawable.ic_error_red)
    }

    override fun onPause() {
        super.onPause()
        mCompositeDisposable.clear()
    }


    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }


    private fun loginAnimation() {

        // hide the login form before the animation
        login_card.visibility = View.GONE
        animate_view.visibility = View.VISIBLE

        val cx = (animate_view.left + animate_view.right) / 2
        val cy = (animate_view.top + animate_view.bottom) / 2
        val animator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewAnimationUtils.createCircularReveal(animate_view,cx,cy,0f, resources.displayMetrics.heightPixels * 1.2f)
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        };
        animator.duration = 500
        animator.interpolator = AccelerateInterpolator()

        animator.start()
        animator.addListener(object:Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {

                // then show the main screen
                startActivity( Intent(this@LoginActivity, MainActivity::class.java))
                finish()

                animate_view.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })

    }



}
