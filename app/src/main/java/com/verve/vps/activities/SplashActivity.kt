package com.verve.vps.activities

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.os.Handler

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.verve.vps.helpers.ApiServiceHelper
import com.verve.vps.helpers.NetworkHelper.connectedToFirebase
import com.verve.vps.helpers.NetworkHelper.playstoreVersionUpdated
import com.verve.vps.helpers.RetrofitHelper
import timber.log.Timber

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkGooglePlayVersion()
        // initialize the api service
        ApiServiceHelper.apiService = RetrofitHelper(this).getApiService()
        // go to login
        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
             finish()
        }, 2000)
    }


    private fun checkGooglePlayVersion() {
        val db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        db.firestoreSettings = settings
        db.collection("google-play-version")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    connectedToFirebase = true
                    playstoreVersionUpdated = document.data["updated"].toString().toBoolean()
                }
            }
            .addOnFailureListener { exception ->
                Timber.e(exception.cause)
                connectedToFirebase = false
            }
    }
}