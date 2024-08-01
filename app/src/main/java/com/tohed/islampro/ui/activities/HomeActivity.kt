package com.tohed.islampro.ui.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
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
import com.tohed.islampro.R
import com.tohed.islampro.api.PrayerService
import com.tohed.islampro.api.createPrayerService
import com.tohed.islampro.api.fetchPrayerTimings
import com.tohed.islampro.databinding.ActivityHomeBinding
import com.tohed.islampro.datamodel.PrayerTimesResponse
import com.yarolegovich.slidingrootnav.SlideGravity
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*
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
    private var settingsDialogShown = false
    private lateinit var sharedPreferences: SharedPreferences



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

        sharedPreferences = getSharedPreferences("PrayerTimesPrefs", Context.MODE_PRIVATE)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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

        lifecycleScope.launch(Dispatchers.IO) {
            while (coroutineContext.isActive) {
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
        }

    }

    override fun onResume() {
        super.onResume()
        if (!settingsDialogShown) {
            refreshData()
        }
    }

    private fun refreshData() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
                )
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        latitude = it.latitude
                        longitude = it.longitude

                        fetchPrayerTimings()
                    } ?: run {
                        showToast("Failed to get phone location. Please try again.")
                    }
                }.addOnFailureListener { e ->
                    e.printStackTrace()
                    showToast("Failed to get phone location. Please try again.")
                }
            } else {
                showLocationSettingsDialog()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
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
            getLocation()
        }
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
                )
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        latitude = it.latitude
                        longitude = it.longitude

                        fetchPrayerTimings()
                    } ?: run {}
                }.addOnFailureListener { e ->
                    e.printStackTrace()
                    showToast("Failed to get phone location. Please try again.")
                }
            } else {
                showLocationSettingsDialog()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun fetchPrayerTimings() {
        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val service = createPrayerService(latitude, longitude)
        GlobalScope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    val response = service.getPrayerTimings(currentDate, latitude, longitude, 2)
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
    }

    private fun showLocationSettingsDialog() {
        settingsDialogShown = true
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
        builder.setOnCancelListener {}
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
            if (isLocationEnabled()) {
                settingsDialogShown = false
                getLocation()
            } else {
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


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
        bottomNav.setOnItemSelectedListener { menuItem ->
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
*/

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        fun getIntent(activity: AppCompatActivity): Intent {
            return Intent(activity, HomeActivity::class.java)
        }
    }
    private var isHome = false

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
    private var settingsDialogShown = false
    private lateinit var sharedPreferences: SharedPreferences

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

        sharedPreferences = getSharedPreferences("PrayerTimesPrefs", Context.MODE_PRIVATE)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()
        setUpNavigationControllerGraph()
        //setSlidingRootNav(savedInstanceState)
        //setOnClickListeners()

        prayerNameTextView = findViewById(R.id.tvNimazName)
        prayerTimeTextView = findViewById(R.id.tvNimazTime)
        hijriDateTextView = findViewById(R.id.islam_hijri)
        gregorianDateTextView = findViewById(R.id.date)
        upcomingPrayerTimeTextView = findViewById(R.id.upcomingPrayerTimeTv)
        arabicMonthNamee = findViewById(R.id.islam_date)
        dayImageView = findViewById(R.id.ivday)
        nightImageView = findViewById(R.id.ivnight)

        lifecycleScope.launch(Dispatchers.IO) {
            while (coroutineContext.isActive) {
                if (isNetworkAvailable()) {
                    try {
                        val response = fetchPrayerTimings(latitude, longitude)
                        withContext(Dispatchers.Main) {
                            savePrayerTimingsToPrefs(response)
                            displayPrayerTimings(response)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                   // showToast("No internet connection!")
                    withContext(Dispatchers.Main) {
                        loadPrayerTimingsFromPrefs()
                    }
                }
                delay(3000) // Refresh every 3 seconds
            }
        }

        binding.homeBtn.setOnClickListener(this)
        binding.searchBtn.setOnClickListener(this)
        binding.premiumBtn.setOnClickListener(this)
        binding.mutafariqM.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (!settingsDialogShown) {
            refreshData()
        }
    }

    private fun refreshData() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
                )
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        latitude = it.latitude
                        longitude = it.longitude

                        fetchPrayerTimings()
                    } ?: run {
                        showToast("Failed to get phone location. Please try again.")
                    }
                }.addOnFailureListener { e ->
                    e.printStackTrace()
                    showToast("Failed to get phone location. Please try again.")
                }
            } else {
                showLocationSettingsDialog()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
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
            getLocation()
        }
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
                )
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        latitude = it.latitude
                        longitude = it.longitude

                        fetchPrayerTimings()
                    } ?: run {}
                }.addOnFailureListener { e ->
                    e.printStackTrace()
                    showToast("Failed to get phone location. Please try again.")
                }
            } else {
                showLocationSettingsDialog()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun fetchPrayerTimings() {
        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val service = createPrayerService(latitude, longitude)
        GlobalScope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    val response = service.getPrayerTimings(currentDate, latitude, longitude, 2)
                    withContext(Dispatchers.Main) {
                        savePrayerTimingsToPrefs(response)
                        displayPrayerTimings(response)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        //showToast("Failed to fetch prayer timings. Please check your internet connection.")
                    }
                }
                delay(3000) // Refresh every 3 seconds
            }
        }
    }

    private fun savePrayerTimingsToPrefs(response: PrayerTimesResponse) {
        val editor = sharedPreferences.edit()
        editor.putString("gregorianDate", response.data.date.gregorian.date)
        editor.putString("islamicDate", response.data.date.hijri.date)
        editor.putString("islamicMonth", response.data.date.hijri.month.ar)
        response.data.timings.forEach { (prayerName, prayerTime) ->
            editor.putString(prayerName, prayerTime)
        }
        editor.apply()
    }

    private fun loadPrayerTimingsFromPrefs() {
        val gregorianDate = sharedPreferences.getString("gregorianDate", "")
        val islamicDate = sharedPreferences.getString("islamicDate", "")
        val islamicMonth = sharedPreferences.getString("islamicMonth", "")
        val prayerTimingsMap = mapOf(
            "Fajr" to sharedPreferences.getString("Fajr", ""),
            "Dhuhr" to sharedPreferences.getString("Dhuhr", ""),
            "Asr" to sharedPreferences.getString("Asr", ""),
            "Maghrib" to sharedPreferences.getString("Maghrib", ""),
            "Isha" to sharedPreferences.getString("Isha", ""),
            "Sunrise" to sharedPreferences.getString("Sunrise", ""),
            "Sunset" to sharedPreferences.getString("Sunset", "")
        )

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        var currentPrayerName = ""
        var currentPrayerTime = ""
        var upcomingPrayerName = ""

        for ((prayerName, prayerTime) in prayerTimingsMap) {
            if (prayerTime != null && currentTime < prayerTime) {
                upcomingPrayerName = prayerName
                currentPrayerTime = prayerTime
                break
            } else {
                currentPrayerName = prayerName
            }
        }

        prayerNameTextView.text = currentPrayerName
        prayerTimeTextView.text = currentPrayerTime
        upcomingPrayerTimeTextView.text = upcomingPrayerName
        arabicMonthNamee.text = islamicMonth
        gregorianDateTextView.text = gregorianDate
        hijriDateTextView.text = islamicDate
    }

    private fun showLocationSettingsDialog() {
        settingsDialogShown = true
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
        builder.setOnCancelListener {}
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
            if (isLocationEnabled()) {
                settingsDialogShown = false
                getLocation()
            } else {
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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

    /*private fun setOnClickListeners() {
        binding.drawerOpenClose.setOnClickListener {
            if (slidingRootNav.isMenuClosed) {
                slidingRootNav.openMenu()
            } else {
                slidingRootNav.closeMenu()
            }
        }
    }*/

   /* private fun setSlidingRootNav(savedInstanceState: Bundle?) {
        slidingRootNav =
            SlidingRootNavBuilder(this).withMenuOpened(false).withSavedState(savedInstanceState)
                .withGravity(SlideGravity.RIGHT).withMenuLayout(R.layout.drawer_content).inject()

        slidingRootNav.layout.findViewById<CardView>(R.id.setting).setOnClickListener {
            startActivity(SettingActivity.getIntent(this))
        }
    }*/

    private fun setActiveColor(imageView: ImageView, textView: TextView) {
        textView.setTextColor(getColor(R.color.white))
        imageView.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN)
    }

    private fun setDeactiveColor(imageView: ImageView, textView: TextView) {
        textView.setTextColor(getColor(R.color.icon_tint))
        imageView.setColorFilter(ContextCompat.getColor(this, R.color.icon_tint), PorterDuff.Mode.SRC_IN)
    }
    /*private fun setUpBottomNavigation() {
        val bottomNav = binding.bottomBar
        //bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
        bottomNav.setOnItemSelectedListener { menuItem ->
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
    }*/

    override fun onClick(view: View) {
        when (view.id) {
            R.id.home_btn -> {
                navController.popBackStack()
                navController.navigate(R.id.gamesFragment)
                isHome = true
                binding.homeBtn.background.setTintList(null)
                binding.searchBtn.background.setTint(getResources().getColor(R.color.transparent))
                binding.premiumBtn.background.setTint(getResources().getColor(R.color.transparent))
                binding.mutafariqM.background.setTint(getResources().getColor(R.color.transparent))


                setActiveColor(binding.homeImg, binding.homeTxt)
                setDeactiveColor(binding.greetingImg, binding.greetingTv)
                setDeactiveColor(binding.brandingImg, binding.brandingTv)
                setDeactiveColor(binding.mutImg, binding.mutTv)
            }

            R.id.search_btn -> {
                navController.popBackStack()
                navController.navigate(R.id.updatesFragment)
                isHome = false
                binding.homeBtn.background.setTint(getResources().getColor(R.color.transparent))
                binding.searchBtn.background.setTintList(null)
                binding.premiumBtn.background.setTint(getResources().getColor(R.color.transparent))
                binding.mutafariqM.background.setTint(getResources().getColor(R.color.transparent))


                setDeactiveColor(binding.homeImg, binding.homeTxt)
                setActiveColor(binding.greetingImg, binding.greetingTv)
                setDeactiveColor(binding.brandingImg, binding.brandingTv)
                setDeactiveColor(binding.mutImg, binding.mutTv)

            }

            R.id.mutafariq_M -> {
                isHome = false
                navController.popBackStack()
                navController.navigate(R.id.favouritesFragment)

                binding.homeBtn.background.setTint(getResources().getColor(R.color.transparent))
                binding.searchBtn.background.setTint(getResources().getColor(R.color.transparent))
                binding.mutafariqM.background.setTintList(null)
                binding.premiumBtn.background.setTint(getResources().getColor(R.color.transparent))

                setDeactiveColor(binding.homeImg, binding.homeTxt)
                setDeactiveColor(binding.greetingImg, binding.greetingTv)
                setActiveColor(binding.mutImg, binding.mutTv)
                setDeactiveColor(binding.brandingImg, binding.brandingTv)
            }

            R.id.premium_btn -> {
                isHome = false
                navController.popBackStack()
                navController.navigate(R.id.leaguesFragment)

                binding.homeBtn.background.setTint(getResources().getColor(R.color.transparent))
                binding.searchBtn.background.setTint(getResources().getColor(R.color.transparent))
                binding.premiumBtn.background.setTintList(null)
                binding.mutafariqM.background.setTint(getResources().getColor(R.color.transparent))


                setDeactiveColor(binding.homeImg, binding.homeTxt)
                setDeactiveColor(binding.greetingImg, binding.greetingTv)
                setActiveColor(binding.brandingImg, binding.brandingTv)
                setDeactiveColor(binding.mutImg, binding.mutTv)
            }
        }
    }
}