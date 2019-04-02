package com.verve.vps.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View

import com.google.gson.Gson
import com.tbruyelle.rxpermissions2.RxPermissions
import com.verve.vps.R
import com.verve.vps.fragments.HomeFragment
import com.verve.vps.fragments.FirstTimeLoginFragment
import com.verve.vps.helpers.Constants
import com.verve.vps.helpers.Constants.LOGIN_USER_DATA
import com.verve.vps.helpers.MapsHelper.mapsPermissionsGranted
import com.verve.vps.helpers.NetworkHelper.connectedToFirebase
import com.verve.vps.helpers.NetworkHelper.playstoreVersionUpdated
import com.verve.vps.helpers.PreferencesHelper.sharedPreferences
import com.verve.vps.models.UserData
import com.verve.vps.utils.Utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*

import timber.log.Timber


class MainActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {

    private val permissionsDisposable = CompositeDisposable()
    private lateinit var rxPermissions: RxPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(connectedToFirebase) {
            if(!playstoreVersionUpdated ){
                play_services_txt.text = "Google Play services out of date. Requires 13400000 but found 13280022, please rebuild the app with the updated Google Play services"
                play_services_txt.visibility = View.VISIBLE
            }
            else {
                sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
                supportFragmentManager.addOnBackStackChangedListener(this)
                // check the user setup status
                checkUserSetupStatus()
                // check the app permissions
                rxPermissions = RxPermissions(this)
                setupAppPermissions()
            }
        } else {
            play_services_txt.text = "Unable to load police station details, please check your internet connection and reload the app again!!"
            play_services_txt.visibility = View.VISIBLE
        }



    }

    private fun checkUserSetupStatus() {
        val userDataStr = sharedPreferences.getString(LOGIN_USER_DATA,"")
        if (!userDataStr.equals("", ignoreCase = true)) {
            val userData = Gson().fromJson<UserData>(userDataStr, UserData::class.java)

            if (userData.passwordStatus == "0"){
                initFirstTimeLoginFragment()
            }
            if (userData.passwordStatus == "1"){
                initHomeFragment()
            }
        }
        else {
            initHomeFragment()
        }

    }

    // set up the apps permissions
    private fun setupAppPermissions(){

        rxPermissions
        .requestEach(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE)
        .subscribe {
            when {
                it.granted -> {
                    if(it.name == "android.permission.ACCESS_FINE_LOCATION") {
                        mapsPermissionsGranted = true
                        Timber.d("Permission granted is %s", "fine location")
                    }
                    if(it.name == "android.permission.ACCESS_COARSE_LOCATION") {
                        mapsPermissionsGranted = true
                        Timber.d("Permission granted is %s", "coarse location")
                    }

                    if(it.name == "android.permission.READ_EXTERNAL_STORAGE") {

                        Timber.d("Permission granted is %s", "read external storage")
                    }
                }

            }

        }
        .addTo(permissionsDisposable)

    }

    // initialize the fragments of the app
    private fun initFirstTimeLoginFragment() {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, FirstTimeLoginFragment())
            commit()
        }
    }
    private fun initHomeFragment() {
        shouldDisplayHomeUp()
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, HomeFragment())
            commit()
        }

    }

    override fun onBackStackChanged() {
        shouldDisplayHomeUp()
    }

    private fun shouldDisplayHomeUp() {
        //Enable Up button only  if there are entries in the back stack
        supportActionBar!!.setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount > 0)
    }

    override fun onSupportNavigateUp(): Boolean {
        //This method is called when the up button is pressed. Just the pop back stack.
        supportFragmentManager.popBackStack()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.home -> {
                this.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                true
            }
            R.id.logout -> {
                // clear out the cached fields in the app
                Utils.clearCachedFields()

                // go to the login page
                this.startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        permissionsDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        permissionsDisposable.clear()
    }

}