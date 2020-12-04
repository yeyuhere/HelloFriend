package edu.cs371m.hellofriend

import android.app.Application
import android.location.Geocoder
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_schedule.*

class ScheduleFragment: Fragment(R.layout.fragment_schedule), OnMapReadyCallback {
    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var map: GoogleMap
    private lateinit var geocoder: Geocoder
    private lateinit var marker: String
    private lateinit var homeFragment: HomeFragment

    private val ages: Array<String> by lazy {
        resources.getStringArray(R.array.age)
    }

    private val hour: Array<String> by lazy {
        resources.getStringArray(R.array.hour)
    }

    private val minute: Array<String> by lazy {
        resources.getStringArray(R.array.minute)
    }


    companion object {
        fun newInstance(): ScheduleFragment {
            return ScheduleFragment()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFrag) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        geocoder = Geocoder(activity)

        val ageAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.age,
            android.R.layout.simple_spinner_item)
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ageSpinner.adapter = ageAdapter

        val timeFromHourAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.hour,
            android.R.layout.simple_spinner_item)
        timeFromHourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeFromHourSpinner.adapter = timeFromHourAdapter

        val timeToHourAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.hour,
            android.R.layout.simple_spinner_item)
        timeToHourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeToHourSpinner.adapter = timeToHourAdapter

        val timeFromMinAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.minute,
            android.R.layout.simple_spinner_item)
        timeFromMinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeFromMinSpinner.adapter = timeFromMinAdapter

        val timeToMinAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.minute,
            android.R.layout.simple_spinner_item)
        timeToMinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeToMinSpinner.adapter = timeToMinAdapter

        locationET.setOnEditorActionListener { _, actionId, event ->
            if ((event != null
                        &&(event.action == KeyEvent.ACTION_DOWN)
                        &&(event.keyCode == KeyEvent.KEYCODE_ENTER))
                || (actionId == EditorInfo.IME_ACTION_DONE)) {
//                hideKeyboard()
            }
            false
        }

        initSaveScheduleBut()

        homeFragment = HomeFragment.newInstance()

        initReturnBut()

    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

//        requestPermission(LOCATION_REQUEST_CODE)
//        if (locationPermissionGranted) {
//            // XXX Write me.
//            map.isMyLocationEnabled = true
//            map.uiSettings.isMyLocationButtonEnabled = true
//        }
        // Start the map at the Harry Ransom center
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(30.2843, -97.7412), 15.0f))
    }


    private fun initSaveScheduleBut() {
        saveSchedule.setOnClickListener {
            var agePos = ageSpinner.selectedItemPosition
            var fromHPos = timeFromHourSpinner.selectedItemPosition
            var fromMPos = timeFromMinSpinner.selectedItemPosition
            var toHPos = timeToHourSpinner.selectedItemPosition
            var toMPos = timeToMinSpinner.selectedItemPosition
            var latLng = geocoder.getFromLocationName(locationET.text.toString(), 1)

            if (locationET.text.isNotEmpty() && agePos != 0 && fromHPos != 0 && fromMPos != 0 && toHPos != 0 && toMPos != 0) {
                if (!latLng.isNullOrEmpty()) {
                    val schedule = Schedule().apply {
                        location = locationET.text.toString()
                        age = ages[agePos]
                        fromH = hour[fromHPos]
                        fromM = minute[fromMPos]
                        toH = hour[toHPos]
                        toM = minute[toMPos]
                    }
                    viewModel.saveSchedule(schedule)
                    marker = ages[agePos] + "YO: " + hour[fromHPos] + ":" + minute[fromMPos] + " - " + hour[toHPos] + ":" + minute[toMPos]
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latLng[0].latitude, latLng[0].longitude), 15.0f))
                    map.addMarker(MarkerOptions().position(LatLng(latLng[0].latitude, latLng[0].longitude)).title(marker))
                    //clear content
                    locationET.text = null
                    ageSpinner.setSelection(0)
                    timeFromHourSpinner.setSelection(0)
                    timeFromMinSpinner.setSelection(0)
                    timeToHourSpinner.setSelection(0)
                    timeToMinSpinner.setSelection(0)

//                    hideKeyboard()

                } else {
                    Toast.makeText(requireContext(), "Location does not exist", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill in all the required information", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initReturnBut() {
        returnBut.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment, homeFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        }
    }

}