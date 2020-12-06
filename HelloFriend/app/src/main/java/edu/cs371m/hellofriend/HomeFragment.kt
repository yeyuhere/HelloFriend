package edu.cs371m.hellofriend

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_schedule.*

class HomeFragment: Fragment(), OnMapReadyCallback {
    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var scheduleFragment: ScheduleFragment
    private lateinit var map: GoogleMap
    private lateinit var geocoder: Geocoder
    private val LOCATION_REQUEST_CODE = 101
    private var locationPermissionGranted = false
    private var currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

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

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.home_mapFrag) as SupportMapFragment
        mapFragment.getMapAsync(this)

        geocoder = Geocoder(requireContext())
        initCountInBut()
        initSearchET()
        initMyPostBut()

    }

    override fun onResume() {
        super.onResume()
        viewModel.getSchedule()
    }

    private fun initCountInBut() {
        countInBut.setOnClickListener {
            fragmentManager
                ?.beginTransaction()
                ?.add(R.id.main_fragment, scheduleFragment)
                ?.addToBackStack(null)
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

        viewModel.observeSchedules().observe(viewLifecycleOwner, Observer {
            it.forEach{
                var marker = it.age + "YO: " + it.fromH + ":" + it.fromM + " - " + it.toH + ":" + it.toM
                map.addMarker(MarkerOptions().position(LatLng(it.latitude!!, it.longitude!!)).title(marker))
            }
        })
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

    private fun initSearchET() {
        searchET.setOnEditorActionListener { _, actionId, event ->
            if ((event != null
                        &&(event.action == KeyEvent.ACTION_DOWN)
                        &&(event.keyCode == KeyEvent.KEYCODE_ENTER))
                || (actionId == EditorInfo.IME_ACTION_DONE)) {
                (activity as MainActivity).hideKeyboard()
                var latLng = geocoder.getFromLocationName(searchET.text.toString(), 1)
                if (!latLng.isNullOrEmpty()) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latLng[0].latitude, latLng[0].longitude), 18.0f))
                }
                searchET.text = null
            }
            false
        }
    }

    private fun initMyPostBut() {
        myPostBut.setOnClickListener {
            fragmentManager
                    ?.beginTransaction()
                    ?.add(R.id.main_fragment, MyPostFragment.newInstance())
                    ?.addToBackStack(null)
                    ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    ?.commit()

        }
    }


}