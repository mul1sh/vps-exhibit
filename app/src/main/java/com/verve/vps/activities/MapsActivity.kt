package com.verve.vps.activities

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.provider.Settings.SettingNotFoundException
import android.os.Build
import android.os.Build.VERSION_CODES.KITKAT
import android.os.Bundle

import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

import com.verve.vps.R
import com.verve.vps.utils.Utils
import com.verve.vps.helpers.Constants.OCCURRENCE_LATITUDE
import com.verve.vps.helpers.Constants.OCCURRENCE_LONGITUDE
import com.verve.vps.helpers.Constants.OCCURRENCE_PLACE_NAME
import com.verve.vps.helpers.PreferencesHelper.sharedPreferences

import java.util.*

import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity:AppCompatActivity(),OnMapReadyCallback{

    private var mMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var occurrencePlaceName: String
    private var zoomDistance  = 18f
    private val locationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    private var receivedUserLocation: Boolean = false
    private lateinit var currentMarker: Marker
    private lateinit var placesClient: PlacesClient




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Initialize Places.
        Places.initialize(this, getString(R.string.google_maps_key))

        // Create a new Places client instance.
        placesClient = Places.createClient(this)

        // set the title
        supportActionBar!!.title = "Occurrence Map"

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    if(!receivedUserLocation) {
                        latitude = location.latitude
                        longitude = location.longitude
                        receivedUserLocation = true
                        moveMap(false)
                        latitude = null
                        longitude = null
                    }
                }
            }
        }

        // the auto-complete fragment
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setHint(getString(R.string.search_oc_location))

        sharedPreferences.apply {
            val occurrenceArea = getString(OCCURRENCE_PLACE_NAME, "")
            if (!occurrenceArea.equals(other = "", ignoreCase = true)) {
                autocompleteFragment.setText(occurrenceArea)
            }
        }
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // remove any old marker first
                removeOldMarker()

                // then set the newly acquired co-ordinates
                latitude = place.latLng!!.latitude
                longitude = place.latLng!!.longitude
                occurrencePlaceName = place.name.toString()
                // save the occurrence place
                sharedPreferences.edit().apply {
                    putString(OCCURRENCE_LATITUDE, latitude.toString())
                    putString(OCCURRENCE_LONGITUDE, longitude.toString())
                    putString(OCCURRENCE_PLACE_NAME,  occurrencePlaceName)
                    apply()
                }

                moveMap(true)
            }

            override fun onError(status: Status) {
                Utils.showSnackbar(this@MapsActivity, status.statusMessage!!, R.drawable.ic_error_red)
            }
        })

        // init the map buttons
        initButtons()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // set the initial location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            AlertDialog
            .Builder(this)
            .setMessage(getString(R.string.vps_map_permission_settings))
            .setPositiveButton(getString(R.string.msg_ok)){_,_ -> finish()}
            .create()
            .show()

            return
        } else {
            // first of all init the map settings
            mMap!!.uiSettings.isZoomControlsEnabled = true
            mMap!!.uiSettings.isCompassEnabled = true
            mMap!!.uiSettings.isMyLocationButtonEnabled = true
            mMap!!.isMyLocationEnabled = true

            // then reload any older chosen occurrence location
            val occurrenceLatitude =  sharedPreferences.getString(OCCURRENCE_LATITUDE,"")!!.trim()
            val occurrenceLongitude = sharedPreferences.getString(OCCURRENCE_LONGITUDE,"")!!.trim()

            if(occurrenceLatitude != "" && occurrenceLongitude != "") {
                latitude = occurrenceLatitude.toDouble()
                longitude = occurrenceLongitude.toDouble()
                moveMap(true)
            } else {
                // start polling for location updates
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            }
        }

    }

    private fun enableLocationSetting() {
        // notify user
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage(getString(R.string.occurrence_map_prompt))
        dialog.setPositiveButton(getString(R.string.turn_on_device_location)) { _, _ -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
        dialog.setNegativeButton(getString(android.R.string.cancel)) { _, _ -> finish() }
        dialog.show()
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationMode: Int
        val locationProviders: String

        if (Build.VERSION.SDK_INT >= KITKAT) {
            try {
                @Suppress("DEPRECATION")
                locationMode = Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)

            } catch (e: SettingNotFoundException) {
                e.printStackTrace()
                return false
            }

            @Suppress("DEPRECATION")
            return locationMode != Settings.Secure.LOCATION_MODE_OFF

        } else {
            @Suppress("DEPRECATION")
            locationProviders = Settings.Secure.getString(context.contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
            return !TextUtils.isEmpty(locationProviders)
        }

    }

    private fun moveMap( showMarker: Boolean) {
        val latLng = LatLng(latitude!!, longitude!!)
        if(showMarker) {
            currentMarker = mMap!!.addMarker(MarkerOptions().apply {
                position(latLng)
                title(getString(R.string.occurrence_location))
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            })
        }

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomDistance)
        mMap!!.animateCamera(cameraUpdate)

        if(receivedUserLocation) stopLocationUpdates()

    }

    override fun onResume() {
        super.onResume()

        if (!isLocationEnabled(this)) {
            enableLocationSetting()
            return
        }

    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        fusedLocationClient.flushLocations()
    }

    private fun initButtons() {
        btnSaveOccurrenceLocation.setOnClickListener {
            checkIfOccurrenceLocationChosen()
        }
    }

    private fun checkIfOccurrenceLocationChosen() {
        when {
            latitude == null || longitude == null -> {
                Utils.showSnackbar(this, "Please search for occurrence location!!", R.drawable.ic_error_red)
                return
            }
            else -> {
                Utils.makeReporterStartingForm(false)
                finish()
            }
        }
    }

    private fun removeOldMarker()  {
        if(::currentMarker.isInitialized) {
            currentMarker.remove()
        }
    }

}