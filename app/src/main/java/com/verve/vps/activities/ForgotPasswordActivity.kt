package com.verve.vps.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

import com.verve.vps.R
import com.verve.vps.helpers.AccessTokenHelper.getAccessToken
import com.verve.vps.helpers.ApiServiceHelper
import com.verve.vps.utils.Utils

import kotlinx.android.synthetic.main.activity_forgot_password.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber

class ForgotPasswordActivity:  AppCompatActivity(){

    private val mCompositeDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        supportActionBar!!.title = ""


        // handle button click
        btnChangePassword.setOnClickListener { validateForm() }
    }

    private fun validateForm() {
        // show the loading button
        btnChangePassword.startAnimation()
        // Reset errors.
        forgot_password_question.error = null
        forgot_password_answer.error = null
        forgot_password_new_password.error = null
        forgot_password_confirm.error = null

        // Store values at the time of the login attempt.
        val fpQuestion = forgot_password_question.text.toString()
        val fpAnswer = forgot_password_answer.text.toString()
        val fpNewPassword = forgot_password_new_password.text.toString()
        val fpPasswordConfirm = forgot_password_confirm.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid question
        if (TextUtils.isEmpty(fpQuestion)) {
            forgot_password_question.error = "Security question is required!"
            focusView = forgot_password_question
            cancel = true
        }

        // Check for a valid answer
        if (TextUtils.isEmpty(fpAnswer)) {
            forgot_password_answer.error = "The answer to your security question is required!"
            focusView = forgot_password_answer
            cancel = true
        }

        // Check for a valid password
        if (TextUtils.isEmpty(fpNewPassword) || !isPasswordValid(fpNewPassword)) {
            forgot_password_new_password.error = "Please enter a valid password, that's at least 4 characters long!"
            focusView = forgot_password_new_password
            cancel = true
        }

        // Check for a valid confirm password
        if (TextUtils.isEmpty(fpPasswordConfirm) || !isConfirmPasswordValid(fpPasswordConfirm, fpPasswordConfirm)) {
            forgot_password_confirm.error = "Please make sure both passwords match!"
            focusView = forgot_password_confirm
            cancel = true
        }



        if (cancel) {
            // There was an error so focus on the first
            // form field with an error.
            focusView?.requestFocus()
            showButtonLoadingError()
        } else {
            // change the user password
            changePassword(fpQuestion,fpAnswer,fpNewPassword)
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

    private fun isConfirmPasswordValid(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    private fun changePassword(question: String, answer: String, newPassword: String) {
        ApiServiceHelper.apiService!!
            .changePassword(getAccessToken(), question, answer, newPassword)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({response -> handleApiResult(response)}, {error ->handleApiError(error) })
            .addTo(mCompositeDisposable)
    }

    private fun handleApiResult(response: Response<ResponseBody>) {
        val status = JSONObject(response.body()?.string().toString()).getBoolean("status")
        if( status) {
            showLoadingButtonSuccess()
            Utils.showSnackbar(this, "Password changed successfully", R.drawable.ic_check_circle)
            startActivity( Intent(this, LoginActivity::class.java))
            finish()
        } else {
            showButtonLoadingError()
            Utils.showSnackbar(this,"Unable to change your password, please try again later", R.drawable.ic_error_red)
        }

    }

    private fun handleApiError(e: Throwable) {
        Timber.e(e)
        showButtonLoadingError()
        Utils.showSnackbar(this, "Password change failed, please check your internet connection and try again later", R.drawable.ic_error_red)
    }

    private fun showLoadingButtonSuccess() {
        btnChangePassword.doneLoadingAnimation(R.color.btnGreen, Utils.getBitmapFromVectorDrawable(this,R.drawable.ic_check_circle))
        Handler().postDelayed({  btnChangePassword.revertAnimation() }, 2000)
    }


    private fun showButtonLoadingError() {
        btnChangePassword.doneLoadingAnimation(R.color.colorAccent, Utils.getBitmapFromVectorDrawable(this,R.drawable.ic_error_red))
        Handler().postDelayed({  btnChangePassword.revertAnimation() }, 2000)
    }

    override fun onPause() {
        super.onPause()
        mCompositeDisposable.clear()
    }


    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }


}