package com.tohed.islampro.ui.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationBarView
import com.tohed.islampro.R
import com.tohed.islampro.api.PrayerService
import com.tohed.islampro.api.fetchPrayerTimings
import com.tohed.islampro.databinding.ActivityHomeBinding
import com.tohed.islampro.datamodel.PrayerTimesResponse
import com.yarolegovich.slidingrootnav.SlideGravity
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HomeActivity : AppCompatActivity() {

    companion object {
        fun getIntent(activity: AppCompatActivity): Intent {
            return Intent(activity, HomeActivity::class.java)

        }
    }


    private lateinit var binding: ActivityHomeBinding
    private lateinit var slidingRootNav: SlidingRootNav
    private lateinit var navController: NavController
    private lateinit var prayerService: PrayerService

    private lateinit var prayerTimeTextView: TextView
    private lateinit var prayerNameTextView: TextView
    private lateinit var hijriDateTextView: TextView
    private lateinit var gregorianDateTextView: TextView
    private lateinit var upcomingPrayerTimeTextView: TextView
    private lateinit var arabicMonthNamee: TextView
    private lateinit var dayImageView: ImageView
    private lateinit var nightImageView: ImageView
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val LOCATION_SETTINGS_REQUEST_CODE = 123


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Request location permission
        requestLocationPermission()

        setUpNavigationControllerGraph()
        setSlidingRootNav(savedInstanceState)
        setOnClickListeners()
        setUpBottomNavigation()

        prayerNameTextView = findViewById(R.id.tvNimazName)
        prayerTimeTextView = findViewById(R.id.tvNimazTime)
        hijriDateTextView = findViewById(R.id.islam_hijri)
        gregorianDateTextView = findViewById(R.id.date)
        upcomingPrayerTimeTextView = findViewById(R.id.upcomingPrayerTimeTv)
        arabicMonthNamee = findViewById(R.id.islam_date)
        dayImageView = findViewById(R.id.ivday)
        nightImageView = findViewById(R.id.ivnight)

        /*lifecycleScope.launch(Dispatchers.IO) {
            while (coroutineContext.isActive) { // Check if the coroutine is still active
                if (isNetworkAvailable()) {
                    try {
                        val response = fetchPrayerTimings(latitude, longitude)
                        withContext(Dispatchers.Main) {
                            displayPrayerTimings(response)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    showToast("No internet connection!")
                }
                delay(5000) // Refresh every minute
            }
        }*/
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted, proceed to get location
            getLocation()
        }
    }

    private fun getLocation() {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Check if location services are enabled
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
                )
            ) {
                // Location services are enabled, proceed to get location
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        latitude = it.latitude
                        longitude = it.longitude

                        // Once you have latitude and longitude, fetch prayer timings using coroutine scope
                        lifecycleScope.launch(Dispatchers.IO) {
                            while (isActive) { // Loop as long as the coroutine is active
                                try {
                                    val response = fetchPrayerTimings(latitude, longitude)
                                    withContext(Dispatchers.Main) {
                                        displayPrayerTimings(response)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    withContext(Dispatchers.Main) {
                                        showToast("Failed to fetch prayer timings. Please check your internet connection.")
                                    }
                                }
                                delay(3000) // Refresh every 3 seconds
                            }
                        }
                    } ?: run {
                        showToast("Failed to get location. Please try again.")
                    }
                }.addOnFailureListener { e ->
                    e.printStackTrace()
                    showToast("Failed to get location. Please try again.")
                }
            } else {
                // Location services are not enabled, show dialog to prompt user to enable them
                showLocationSettingsDialog()
            }
        } else {
            // Location permission is not granted, request permission from the user
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun showLocationSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Location Services Disabled")
        builder.setMessage("Please enable location services to use this app.")
        builder.setPositiveButton("Open Settings") { _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, LOCATION_SETTINGS_REQUEST_CODE)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
            // User returned from location settings screen
            if (isLocationEnabled()) {
                // Location services are now enabled, proceed to get location
                getLocation()
            } else {
                // Location services are still not enabled, handle accordingly
                showToast("Location services are still disabled.")
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    /*private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    latitude = it.latitude
                    longitude = it.longitude

                    // Once you have latitude and longitude, fetch prayer timings using coroutine scope
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val response = fetchPrayerTimings(latitude, longitude)
                            withContext(Dispatchers.Main) {
                                displayPrayerTimings(response)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        //delay(5000) // Refresh every 5 seconds
                    }
                }
            }.addOnFailureListener { e ->
                e.printStackTrace()
                showToast("Failed to get location.")
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }*/


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to get location
                getLocation()
            } else {
                showToast("Location permission denied.")
            }
        }
    }


    private fun displayPrayerTimings(response: PrayerTimesResponse) {
        val data = response.data
        val islamicMonth = data.date.hijri.month.ar
        val gregorianDate = data.date.gregorian.date
        val islamicDate = data.date.hijri.date
        val prayerTimingsMap = data.timings
        val sunriseTime = data.timings["Sunrise"]
        val sunsetTime = data.timings["Sunset"]
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        var currentPrayerName = ""
        var currentPrayerTime = ""
        var upcomingPrayerName = ""

        for ((prayerName, prayerTime) in prayerTimingsMap) {
            if (currentTime < prayerTime) {
                upcomingPrayerName = prayerName
                currentPrayerTime = prayerTime
                break
            } else {
                currentPrayerName = prayerName
            }
        }

        // Handle null cases for sunriseTime and sunsetTime
        //   val isDayTime = sunriseTime != null && sunsetTime != null && currentTime >= sunriseTime && currentTime < sunsetTime

        /*dayImageView.visibility = if (isDayTime) View.VISIBLE else View.GONE
        nightImageView.visibility = if (isDayTime) View.GONE else View.VISIBLE
*/
        prayerNameTextView.text = currentPrayerName
        prayerTimeTextView.text = currentPrayerTime
        upcomingPrayerTimeTextView.text = upcomingPrayerName
        arabicMonthNamee.text = islamicMonth
        gregorianDateTextView.text = gregorianDate
        hijriDateTextView.text = islamicDate
    }


    private fun setUpNavigationControllerGraph() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun setOnClickListeners() {
        binding.drawerOpenClose.setOnClickListener {
            if (slidingRootNav.isMenuClosed) {
                slidingRootNav.openMenu()
            } else {
                slidingRootNav.closeMenu()
            }
        }
    }

    private fun setSlidingRootNav(savedInstanceState: Bundle?) {
        slidingRootNav =
            SlidingRootNavBuilder(this).withMenuOpened(false).withSavedState(savedInstanceState)
                .withGravity(SlideGravity.RIGHT).withMenuLayout(R.layout.drawer_content).inject()

        slidingRootNav.layout.findViewById<CardView>(R.id.setting).setOnClickListener {
            startActivity(SettingActivity.getIntent(this))
        }
    }

    private fun setUpBottomNavigation() {
        val bottomNav = binding.bottomBar

        // Disable item shifting mode to ensure all text labels are always visible
        bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED

        // Set up the listener for item selection
        bottomNav.setOnItemSelectedListener { menuItem ->
            // Navigate to the corresponding destination when an item is selected
            when (menuItem.itemId) {
                R.id.gamesFragment -> {
                    navController.popBackStack()
                    navController.navigate(R.id.gamesFragment)
                    true
                }

                R.id.updatesFragment -> {
                    navController.popBackStack()
                    navController.navigate(R.id.updatesFragment)
                    true
                }

                R.id.favouritesFragment -> {
                    navController.popBackStack()
                    navController.navigate(R.id.favouritesFragment)
                    true
                }

                R.id.leaguesFragment -> {
                    navController.popBackStack()
                    navController.navigate(R.id.leaguesFragment)
                    true
                }

                else -> false
            }
        }
    }
}