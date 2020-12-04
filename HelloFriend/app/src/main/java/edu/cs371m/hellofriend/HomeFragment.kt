package edu.cs371m.hellofriend

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.concurrent.fixedRateTimer

class HomeFragment: Fragment(), OnMapReadyCallback {
    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var scheduleFragment: ScheduleFragment
    private lateinit var map: GoogleMap
    private lateinit var geocoder: Geocoder
    private val LOCATION_REQUEST_CODE = 101
    private var locationPermissionGranted = false

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        scheduleFragment = ScheduleFragment.newInstance()

        checkGooglePlayServices()
        //
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.home_mapFrag) as SupportMapFragment
        mapFragment.getMapAsync(this)

        geocoder = Geocoder(requireContext())

        initCountInBut()
    }

    private fun initCountInBut() {
        countInBut.setOnClickListener {
            fragmentManager
                ?.beginTransaction()
                ?.replace(R.id.main_fragment, scheduleFragment)
                ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                ?.commit()
        }

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
            googleApiAvailability.isGooglePlayServicesAvailable(requireContext())
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(requireActivity(), resultCode, 257).show()
            } else {
                Log.i(javaClass.simpleName,
                    "This device must install Google Play Services.")
//                finish()
            }
        }
    }

    private fun requestPermission(requestCode: Int) {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
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
                    Toast.makeText(requireContext(),
                        "Unable to show location - permission required",
                        Toast.LENGTH_LONG).show()
                } else {
                    locationPermissionGranted = true
                }
            }
        }
    }

}