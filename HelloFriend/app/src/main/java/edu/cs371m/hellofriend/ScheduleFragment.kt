package edu.cs371m.hellofriend

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_schedule.*

class ScheduleFragment: Fragment(R.layout.fragment_schedule), OnMapReadyCallback {
    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var map: GoogleMap
    private lateinit var geocoder: Geocoder
    private lateinit var marker: String
    private lateinit var homeFragment: HomeFragment
    private var currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

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
                (activity as MainActivity).hideKeyboard()
            }
            false
        }

        initSaveScheduleBut()
        homeFragment = HomeFragment.newInstance()
        initReturnBut()

//        (activity as AppCompatActivity).onBackPressedDispatcher.addCallback(viewLifecycleOwner){
////            (activity as AppCompatActivity).supportFragmentManager.popBackStack()
//            returnBut.callOnClick()
//        }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

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
            var latLngOri = geocoder.getFromLocationName(locationET.text.toString(), 1)

            if (locationET.text.isNotEmpty() && agePos != 0 && fromHPos != 0 && fromMPos != 0 && toHPos != 0 && toMPos != 0) {
                if (!latLngOri.isNullOrEmpty()) {
                    viewModel.observeSchedules().observe(viewLifecycleOwner, Observer {
                        it.forEach {
                            if (latLngOri[0].latitude == it.latitude && latLngOri[0].longitude == it.longitude) {
                                Log.d("xxx", "same location")
                                // https://stackoverflow.com/questions/20490654/more-than-one-marker-on-same-place-markerclusterer
                                latLngOri[0].latitude += (Math.random() -.5) / 3000
                                latLngOri[0].longitude += (Math.random() -.5) / 3000
                            }
                        }
                    })
                    val schedule = Schedule().apply {
                        latitude = latLngOri[0].latitude
                        longitude = latLngOri[0].longitude
                        age = ages[agePos]
                        fromH = hour[fromHPos]
                        fromM = minute[fromMPos]
                        toH = hour[toHPos]
                        toM = minute[toMPos]
                        ownerUid = currentUser?.uid
                    }
                    viewModel.saveSchedule(schedule)
                    marker = ages[agePos] + "YO: " + hour[fromHPos] + ":" + minute[fromMPos] + " - " + hour[toHPos] + ":" + minute[toMPos]
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latLngOri[0].latitude, latLngOri[0].longitude), 15.0f))
                    map.addMarker(MarkerOptions().position(LatLng(latLngOri[0].latitude, latLngOri[0].longitude)).title(marker))
                    //clear content
                    locationET.text = null
                    ageSpinner.setSelection(0)
                    timeFromHourSpinner.setSelection(0)
                    timeFromMinSpinner.setSelection(0)
                    timeToHourSpinner.setSelection(0)
                    timeToMinSpinner.setSelection(0)

                    (activity as MainActivity).hideKeyboard()

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