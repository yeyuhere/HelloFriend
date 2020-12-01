package edu.cs371m.hellofriend

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng

class MainActivity
    : AppCompatActivity(),
    OnMapReadyCallback
{
    private lateinit var map: GoogleMap
    private lateinit var geocoder: Geocoder
    private val LOCATION_REQUEST_CODE = 101
    private var locationPermissionGranted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(toolbar)

        checkGooglePlayServices()
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.mapFrag) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        requestPermission(LOCATION_REQUEST_CODE)
        if (locationPermissionGranted) {
            // XXX Write me.
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
        }
        // Start the map at the Harry Ransom center
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(30.2843, -97.7412), 15.0f))
    }

    private fun checkGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode =
                googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode, 257).show()
            } else {
                Log.i(javaClass.simpleName,
                        "This device must install Google Play Services.")
                finish()
            }
        }
    }

    private fun requestPermission(requestCode: Int) {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    requestCode);
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            "Unable to show location - permission required",
                            Toast.LENGTH_LONG).show()
                } else {
                    locationPermissionGranted = true
                }
            }
        }
    }
}

