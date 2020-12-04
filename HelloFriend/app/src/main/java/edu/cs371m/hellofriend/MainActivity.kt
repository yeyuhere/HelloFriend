package edu.cs371m.hellofriend

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.viewModels
import kotlinx.android.synthetic.main.content_main.*

// From FC9_GoogleMaps
class MainActivity
    : AppCompatActivity(),
    OnMapReadyCallback
//    View.OnClickListener
{
    private lateinit var map: GoogleMap
    private lateinit var geocoder: Geocoder
    private val LOCATION_REQUEST_CODE = 101
    private var locationPermissionGranted = false
    private lateinit var auth: FirebaseAuth
    private lateinit var fromH: String
    private lateinit var fromM: String
    private lateinit var toH: String
    private lateinit var toM: String
    private lateinit var ageString: String
    private lateinit var location: String
    private lateinit var marker: String
    val viewModel: MainViewModel by viewModels()

    private val ages: Array<String> by lazy {
        resources.getStringArray(R.array.age)
    }

    private val hour: Array<String> by lazy {
        resources.getStringArray(R.array.hour)
    }

    private val minute: Array<String> by lazy {
        resources.getStringArray(R.array.minute)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(toolbar)

        val authInitIntent = Intent(this, AuthInitActivity::class.java)
        startActivity(authInitIntent)

        checkGooglePlayServices()
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.mapFrag) as SupportMapFragment
        mapFragment.getMapAsync(this)

        geocoder = Geocoder(applicationContext)

//        val ageSpinner: Spinner = findViewById(R.id.ageSpinner)
//        val timeFromHourSpinner: Spinner = findViewById(R.id.timeFromHourSpinner)
//        val timeFromMinSpinner: Spinner = findViewById(R.id.timeFromMinSpinner)
//        val timeToHourSpinner: Spinner = findViewById(R.id.timeToHourSpinner)
//        val timeToMinSpinner: Spinner = findViewById(R.id.timeToMinSpinner)

        val ageAdapter = ArrayAdapter.createFromResource(this,
            R.array.age,
            android.R.layout.simple_spinner_item)
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ageSpinner.adapter = ageAdapter

        val timeFromHourAdapter = ArrayAdapter.createFromResource(this,
            R.array.hour,
            android.R.layout.simple_spinner_item)
        timeFromHourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeFromHourSpinner.adapter = timeFromHourAdapter

        val timeToHourAdapter = ArrayAdapter.createFromResource(this,
            R.array.hour,
            android.R.layout.simple_spinner_item)
        timeToHourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeToHourSpinner.adapter = timeToHourAdapter

        val timeFromMinAdapter = ArrayAdapter.createFromResource(this,
            R.array.minute,
            android.R.layout.simple_spinner_item)
        timeFromMinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeFromMinSpinner.adapter = timeFromMinAdapter

        val timeToMinAdapter = ArrayAdapter.createFromResource(this,
            R.array.minute,
            android.R.layout.simple_spinner_item)
        timeToMinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeToMinSpinner.adapter = timeToMinAdapter


//        timeFromHourSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                fromH = hour[p2]
//                Toast.makeText(this@MainActivity, fromH, Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//                Toast.makeText(this@MainActivity, "Nothing selected", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        timeFromMinSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                fromM = minute[p2]
//                Toast.makeText(this@MainActivity, fromM, Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//                Toast.makeText(this@MainActivity, "Nothing selected", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        timeToHourSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                toH = hour[p2]
//                Toast.makeText(this@MainActivity, toH, Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//                Toast.makeText(this@MainActivity, "Nothing selected", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        timeToMinSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                toM = hour[p2]
//                Toast.makeText(this@MainActivity, toM, Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//                Toast.makeText(this@MainActivity, "Nothing selected", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        ageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                ageString = ages[p2]
//                Toast.makeText(this@MainActivity, ageString, Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//                Toast.makeText(this@MainActivity, "Nothing selected", Toast.LENGTH_SHORT).show()
//            }
//        }
//        if (ageString != null && fromH != null && fromM != null && toH != null && toM != null)
//            marker = ageString + "yr: " + fromH + ":" + fromM + " - " + toH + ":" + toM

//        val locationET: EditText = findViewById(R.id.locationET)
        locationET.setOnEditorActionListener { _, actionId, event ->
            if ((event != null
                            &&(event.action == KeyEvent.ACTION_DOWN)
                            &&(event.keyCode == KeyEvent.KEYCODE_ENTER))
                    || (actionId == EditorInfo.IME_ACTION_DONE)) {
                hideKeyboard()
                var latLng = geocoder.getFromLocationName(locationET.text.toString(), 1)
                Log.d("xxx", "ETtext: ${locationET.text}; latLng: $latLng")
                if (!latLng.isNullOrEmpty()) {
                    Log.d("xxx", "${latLng[0].latitude}")
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latLng[0].latitude, latLng[0].longitude), 15.0f))
                    map.addMarker(MarkerOptions().position(LatLng(latLng[0].latitude, latLng[0].longitude)).title(marker))
                    Log.d("xxx", "fistmarker$marker")
                }
                locationET.clearComposingText()
            }
            false
        }

        initSaveScheduleBut()
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

//    private fun setSpinner(spinner: Spinner, array: Array<String>, item: String) {
//
//    }

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

    // https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    // https://stackoverflow.com/questions/7789514/how-to-get-activitys-windowtoken-without-view
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0);
    }

//    override fun onClick(v: View) {
//        val ageSpinner: Spinner = findViewById(R.id.ageSpinner)
//        val timeFromHourSpinner: Spinner = findViewById(R.id.timeFromHourSpinner)
//        val timeFromMinSpinner: Spinner = findViewById(R.id.timeFromMinSpinner)
//        val timeToHourSpinner: Spinner = findViewById(R.id.timeToHourSpinner)
//        val timeToMinSpinner: Spinner = findViewById(R.id.timeToMinSpinner)
//        val agePose = ageSpinner.selectedItemPosition
//        val fromHourPos = timeFromHourSpinner.selectedItemPosition
//        val fromMinPos = timeFromMinSpinner.selectedItemPosition
//        val toHourPos = timeToHourSpinner.selectedItemPosition
//        val toMinPos = timeToMinSpinner.selectedItemPosition
//        Log.d("xxx", "toMinPos: $toMinPos")
//        if (agePose != 0 && fromHourPos != 0 && fromMinPos != 0 && toHourPos != 0 && toMinPos != 0) {
//            marker = age[agePose] + "yr: " + hour[fromHourPos] + ":" + minute[fromMinPos] + " - " + hour[toHourPos] + ":" + minute[toMinPos]
//            Log.d("xxx", "$marker")
//        }
//    }

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
                    marker = ages[agePos] + "yr: " + hour[fromHPos] + ":" + minute[fromMPos] + " - " + hour[toHPos] + ":" + minute[toMPos]
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latLng[0].latitude, latLng[0].longitude), 15.0f))
                    map.addMarker(MarkerOptions().position(LatLng(latLng[0].latitude, latLng[0].longitude)).title(marker))
                    //clear locationET
                    locationET.text = null

                } else {
                    Toast.makeText(this, "Location does not exist", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all the required information", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

