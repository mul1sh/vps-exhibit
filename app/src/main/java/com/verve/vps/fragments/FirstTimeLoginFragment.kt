package com.verve.vps.fragments

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.JsonPrimitive

import com.verve.vps.R
import com.verve.vps.helpers.ApiServiceHelper
import com.verve.vps.utils.Utils
import com.verve.vps.helpers.AccessTokenHelper.getAccessToken

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.first_time_login.*
import retrofit2.Response
import timber.log.Timber

class FirstTimeLoginFragment: Fragment() {
    private lateinit var parentActivity: AppCompatActivity
    private lateinit var mContext: Context
    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.first_time_login, container, false)
    }

    override fun onResume() {
        super.onResume()

        parentActivity = activity as AppCompatActivity
        mContext = parentActivity.applicationContext
        parentActivity.supportActionBar!!.title = "First Time Login"

        btnFirstTimeLogin.setOnClickListener { validateForm() }

    }

    private fun validateForm() {
        // Reset errors.
        first_time_login_question.error = null
        first_time_login_answer.error = null
        first_time_login_password.error = null
        first_time_login_password_confirm.error = null

        // Store values at the time of the login attempt.
        val ftlQuestion = first_time_login_question.text.toString()
        val ftlAnswer = first_time_login_answer.text.toString()
        val ftlPassword = first_time_login_password.text.toString()
        val ftlPasswordConfirm = first_time_login_password_confirm.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid question
        if (TextUtils.isEmpty(ftlQuestion)) {
            first_time_login_question.error = "Security question is required!"
            focusView = first_time_login_question
            cancel = true
        }

        // Check for a valid answer
        if (TextUtils.isEmpty(ftlAnswer)) {
            first_time_login_answer.error = "The answer to your security question is required!"
            focusView = first_time_login_answer
            cancel = true
        }

        // Check for a valid password
        if (TextUtils.isEmpty(ftlPassword) || !isPasswordValid(ftlPassword)) {
            first_time_login_password.error = "Please enter a valid password, that's at least 4 characters long!"
            focusView = first_time_login_password
            cancel = true
        }

        // Check for a valid confirm password
        if (TextUtils.isEmpty(ftlPasswordConfirm) || !isConfirmPasswordValid(ftlPassword, ftlPasswordConfirm)) {
            first_time_login_password_confirm.error = "Please make sure both passwords match!"
            focusView = first_time_login_password_confirm
            cancel = true
        }



        if (cancel) {
            // There was an error so focus on the first
            // form field with an error.
            focusView?.requestFocus()
        } else {

            updateUi(true)
            // finish setting up the user
            finishSetup(ftlQuestion,ftlAnswer,ftlPassword)
        }
    }


    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

    private fun isConfirmPasswordValid(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    private fun finishSetup(question: String, answer: String, newPassword: String) {
        // setup the user
        ApiServiceHelper.apiService!!
        .firstTimeLogin(getAccessToken(), question, answer, newPassword)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe ({response -> handleApiResult(response)}, {error ->handleApiError(error) })
        .addTo(mCompositeDisposable)
    }

    private fun handleApiResult(res: Response<JsonPrimitive>) {
        updateUi(false)
        if( res.body().toString() == "true") {
            Utils.replaceFragment(HomeFragment(),parentActivity)
        } else {
            Timber.e("First time login api call not successful")
        }

    }

    private fun handleApiError(e: Throwable) {
        Timber.e(e)
        updateUi(false)
        Utils.showSnackbar(parentActivity, "Setup failed, please check your internet connection and try again later", R.drawable.ic_error_red)
    }

    override fun onPause() {
        super.onPause()
        mCompositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }

    private fun updateUi(setupInProgress: Boolean) {
        if(setupInProgress) {
            // hide the keyboard
            Utils.hideKeyboard(parentActivity.currentFocus,mContext)
            // show the progress bar
            setup_progressBar.visibility = View.VISIBLE
            // disable the button
            btnFirstTimeLogin.visibility = View.GONE
        } else {
            // hide the progress bar
            setup_progressBar.visibility = View.GONE
            // enable the button
            btnFirstTimeLogin.visibility = View.VISIBLE
        }
    }
}