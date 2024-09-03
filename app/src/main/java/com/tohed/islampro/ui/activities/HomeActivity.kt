package com.tohed.islampro.ui.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import com.tohed.islampro.api.fetchPrayerTimings
import com.tohed.islampro.api.PrayerService
import com.tohed.islampro.databinding.ActivityHomeBinding
import com.tohed.islampro.datamodel.PrayerTimesResponse
import com.yarolegovich.slidingrootnav.SlideGravity
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        fun getIntent(activity: AppCompatActivity): Intent {
            return Intent(activity, HomeActivity::class.java)
        }

        private const val STORAGE_PERMISSION_CODE = 1002
        private const val IMAGE_PICK_CODE = 1000
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val LOCATION_SETTINGS_REQUEST_CODE = 123
    }

    private var isHome = false
    private var selectedScreenshotUri: Uri? = null
    private var onImagePickedCallback: ((Uri?) -> Unit)? = null
    private lateinit var binding: ActivityHomeBinding
    private lateinit var slidingRootNav: SlidingRootNav
    private lateinit var navController: NavController
    private lateinit var prayerService: PrayerService

    private lateinit var prayerTimeTextView: TextView
    private lateinit var prayerNameTextView: TextView
    private lateinit var hijriDateTextView: TextView
    private lateinit var islamicYearTextView: TextView
    private lateinit var gregorianDateTextView: TextView
    private lateinit var upcomingPrayerTimeTextView: TextView
    private lateinit var arabicMonthNamee: TextView
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

        prayerNameTextView = findViewById(R.id.tvNimazName)
        prayerTimeTextView = findViewById(R.id.tvNimazTime)
        hijriDateTextView = findViewById(R.id.islam_hijri)
        gregorianDateTextView = findViewById(R.id.date)
        upcomingPrayerTimeTextView = findViewById(R.id.upcomingPrayerTimeTv)
        arabicMonthNamee = findViewById(R.id.islam_date)

        binding.homeBtn.setOnClickListener(this)
        binding.searchBtn.setOnClickListener(this)
        binding.premiumBtn.setOnClickListener(this)
        binding.mutafariqM.setOnClickListener(this)
        binding.createBtn.setOnClickListener(this)

        loadPrayerTimingsFromPrefs()  // Load prayer timings on start
    }

    override fun onResume() {
        super.onResume()
        if (!settingsDialogShown) {
            refreshData()
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
            fetchLocation() // Directly fetch location if permission is already granted
        }
    }

    private fun refreshData() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            checkLocationEnabledAndFetch()
        } else {
            requestLocationPermission()
        }
    }

    private fun checkLocationEnabledAndFetch() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        ) {
            fetchLocation()
        } else {
            showLocationSettingsDialog()
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                latitude = it.latitude
                longitude = it.longitude
                fetchAndDisplayPrayerTimings(latitude, longitude)
            } ?: run {
                showToast("Failed to get phone location. Please try again.")
            }
        }.addOnFailureListener { e ->
            e.printStackTrace()
            showToast("Failed to get phone location. Please try again.")
        }
    }

    private fun fetchAndDisplayPrayerTimings(latitude: Double, longitude: Double) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = com.tohed.islampro.api.fetchPrayerTimings(latitude, longitude)
                withContext(Dispatchers.Main) {
                    savePrayerTimingsToPrefs(response)
                    displayPrayerTimings(response)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showToast("Failed to fetch prayer timings. Please check your internet connection.")
                }
            }
        }
    }

    private fun savePrayerTimingsToPrefs(response: PrayerTimesResponse) {
        val editor = sharedPreferences.edit()
        editor.putString("gregorianDate", response.data.date.gregorian.date)
        editor.putString("gregorianWeekday", response.data.date.gregorian.weekday.en)
        editor.putString("islamicDate", response.data.date.hijri.date)
        editor.putString("hijriDay", response.data.date.hijri.day)
        editor.putString("islamicYear", response.data.date.hijri.year)
        editor.putString("islamicMonth", response.data.date.hijri.month.en)

        response.data.timings.forEach { (prayerName, prayerTime) ->
            editor.putString(prayerName, prayerTime)
        }
        editor.apply()
    }

    private fun loadPrayerTimingsFromPrefs() {
        val gregorianDate = sharedPreferences.getString("gregorianDate", "")
        val gregorianWeekday = sharedPreferences.getString("gregorianWeekday", "")
        val islamicDate = sharedPreferences.getString("islamicDate", "")
        val islamicYear = sharedPreferences.getString("islamicYear", "")
        val hijriDay = sharedPreferences.getString("hijriDay", "")
        val islamicMonth = sharedPreferences.getString("islamicMonth", "")

        val islamicYearAndDay = " $islamicMonth, $islamicYear"
        val formattedGregorianDate = "$gregorianWeekday, $gregorianDate"

        hijriDateTextView.text = islamicYearAndDay
        gregorianDateTextView.text = formattedGregorianDate
        arabicMonthNamee.text = hijriDay

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
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
            if (isLocationEnabled()) {
                settingsDialogShown = false
                fetchLocation()
            } else {
                showToast("Location services are still disabled.")
            }
        }

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val selectedImage = data?.data
            onImagePickedCallback?.invoke(selectedImage)

            //val dialog = getActiveDialog() // Implement this to return the current open dialog
           /* val tvScreenshotName = findViewById<TextView>(R.id.tvScreenshotName)
            val screenshotContainer = findViewById<LinearLayout>(R.id.screenshotContainer)

            selectedImage?.let {
                val fileName = getFileName(it)

                // Ensure the TextView is not null before setting the text
                if (tvScreenshotName != null) {
                    tvScreenshotName.text = fileName
                    screenshotContainer.visibility = View.VISIBLE
                } else {
                    // Handle the case where the TextView is not found
                    showToast("Error: Could not find TextView to display file name.")
                }
            }*/
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    /*override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            } else {
                showToast("Location permission denied.")
            }
        }
    }*/

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation()
                } else {
                    showToast("Location permission denied.")
                }
            }

            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Storage permission granted
                    pickImage { uri ->
                        selectedScreenshotUri = uri
                        uri?.let {
                            val fileName = getFileName(it)
                            val tvScreenshotName = findViewById<TextView>(R.id.tvScreenshotName)
                            val screenshotContainer = findViewById<LinearLayout>(R.id.screenshotContainer)
                            tvScreenshotName.text = fileName
                            screenshotContainer.visibility = View.VISIBLE
                        }
                    }
                } else {
                    // Storage permission denied
                    showToast("Storage permission denied.")
                }
            }
        }
    }


    private fun displayPrayerTimings(response: PrayerTimesResponse) {
        val data = response.data

        val islamicYear = data.date.hijri.year
        val hijriDay = data.date.hijri.day
        val islamicMonth = data.date.hijri.month.en
        val islamicYearAndDay = "$islamicYear ,$islamicMonth"

        hijriDateTextView.text = islamicYearAndDay

        val gregorianDate = data.date.gregorian.date
        val gregorianWeekday = data.date.gregorian.weekday.en
        val formattedGregorianDate = "$gregorianWeekday, $gregorianDate"

        gregorianDateTextView.text = formattedGregorianDate

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        var currentPrayerName = ""
        var currentPrayerTime = ""
        var upcomingPrayerName = ""

        for ((prayerName, prayerTime) in data.timings) {
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
        arabicMonthNamee.text = hijriDay
        upcomingPrayerTimeTextView.text = upcomingPrayerName
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
        slidingRootNav.layout.findViewById<CardView>(R.id.aboutUs).setOnClickListener {
            startActivity(AboutUs.getIntent(this))
        }
        slidingRootNav.layout.findViewById<CardView>(R.id.WA_Support).setOnClickListener {
            val whatsappUrl = "https://wa.me/message/GDBNOLUGYROZC1"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setPackage("com.whatsapp")
            intent.data = Uri.parse(whatsappUrl)
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                intent.setPackage(null)
                startActivity(intent)
            }
        }
        slidingRootNav.layout.findViewById<CardView>(R.id.feedback).setOnClickListener {
            showFeedbackDialog()
        }
    }

    private fun setActiveColor(imageView: ImageView, textView: TextView) {
        textView.setTextColor(getColor(R.color.white))
        imageView.setColorFilter(
            ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN
        )
    }

    private fun setDeactiveColor(imageView: ImageView, textView: TextView) {
        textView.setTextColor(getColor(R.color.icon_tint))
        imageView.setColorFilter(
            ContextCompat.getColor(this, R.color.icon_tint), PorterDuff.Mode.SRC_IN
        )
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.home_btn -> {
                navController.popBackStack()
                navController.navigate(R.id.gamesFragment)
                isHome = true
                resetButtonStates()

                setActiveColor(binding.homeImg, binding.homeTxt)
                binding.homeBtn.background.setTintList(null)
                binding.searchBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.premiumBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.mutafariqM.background.setTint(resources.getColor(R.color.transparent))
                binding.createBtn.background.setTint(resources.getColor(R.color.transparent))
            }

            R.id.search_btn -> {
                navController.popBackStack()
                navController.navigate(R.id.updatesFragment)
                isHome = false
                resetButtonStates()

                setActiveColor(binding.greetingImg, binding.greetingTv)
                binding.searchBtn.background.setTintList(null)
                binding.homeBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.premiumBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.mutafariqM.background.setTint(resources.getColor(R.color.transparent))
                binding.createBtn.background.setTint(resources.getColor(R.color.transparent))
            }

            R.id.mutafariq_M -> {
                isHome = false
                navController.popBackStack()
                navController.navigate(R.id.favouritesFragment)
                resetButtonStates()

                setActiveColor(binding.mutImg, binding.mutTv)
                binding.mutafariqM.background.setTintList(null)
                binding.homeBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.searchBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.premiumBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.createBtn.background.setTint(resources.getColor(R.color.transparent))
            }

            R.id.create_btn -> {
                isHome = false
                navController.popBackStack()
                resetButtonStates()

                setActiveColor(binding.createImg, binding.createTxt)
                binding.createBtn.background.setTintList(null)
                binding.premiumBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.homeBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.searchBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.mutafariqM.background.setTint(resources.getColor(R.color.transparent))
            }

            R.id.premium_btn -> {
                isHome = false
                navController.popBackStack()
                navController.navigate(R.id.leaguesFragment)
                resetButtonStates()

                setActiveColor(binding.brandingImg, binding.brandingTv)
                binding.premiumBtn.background.setTintList(null)
                binding.homeBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.searchBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.mutafariqM.background.setTint(resources.getColor(R.color.transparent))
                binding.createBtn.background.setTint(resources.getColor(R.color.transparent))
            }
        }
    }

    private fun showFeedbackDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_feedback)
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
        val etFeedback = dialog.findViewById<EditText>(R.id.etFeedback)
        val ivAddScreenshot = dialog.findViewById<ImageView>(R.id.ivAddScreenshot)
        val screenshotContainer = dialog.findViewById<LinearLayout>(R.id.screenshotContainer)
        val tvScreenshotName = dialog.findViewById<TextView>(R.id.tvScreenshotName)
        val ivRemoveScreenshot = dialog.findViewById<ImageView>(R.id.ivRemoveScreenshot)
        val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmit)

        val resetDialog = {
            selectedScreenshotUri = null
            screenshotContainer.visibility = View.GONE
            etFeedback.text.clear()
        }

        ivClose.setOnClickListener {
            resetDialog()
            dialog.dismiss()
        }

        /*ivAddScreenshot.setOnClickListener {
            pickImage { uri ->
                selectedScreenshotUri = uri
                uri?.let {
                    val fileName = getFileName(it)
                    tvScreenshotName.text = fileName
                    screenshotContainer.visibility = View.VISIBLE
                }
            }
        }*/

        ivAddScreenshot.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, getStoragePermission()
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission granted, pick the image
                pickImage { uri ->
                    selectedScreenshotUri = uri
                    uri?.let {
                        val fileName = getFileName(it)
                        tvScreenshotName.text = fileName
                        screenshotContainer.visibility = View.VISIBLE
                    }
                }
            } else {
                // Request storage permission
                ActivityCompat.requestPermissions(
                    this, arrayOf(getStoragePermission()), STORAGE_PERMISSION_CODE
                )
            }
        }



        ivRemoveScreenshot.setOnClickListener {
            selectedScreenshotUri = null
            screenshotContainer.visibility = View.GONE
        }

        btnSubmit.setOnClickListener {
            val feedback = etFeedback.text.toString().trim()
            if (feedback.isEmpty()) {
                etFeedback.error = "Please provide feedback"
                return@setOnClickListener
            }

            sendFeedbackEmail(feedback, selectedScreenshotUri)
            resetDialog()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getStoragePermission(): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES // Android 13+
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE // Below Android 13
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "Unnamed"
    }

    private fun pickImage(onImagePicked: (Uri?) -> Unit) {
        onImagePickedCallback = onImagePicked

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun sendFeedbackEmail(feedback: String, screenshotUri: Uri?) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("usmanahmed7290@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "User Feedback")
            putExtra(Intent.EXTRA_TEXT, feedback)
            if (screenshotUri != null) {
                putExtra(Intent.EXTRA_STREAM, screenshotUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }

        try {
            val packageManager = packageManager
            val activities = packageManager.queryIntentActivities(intent, 0)
            val isIntentSafe = activities.isNotEmpty()

            if (isIntentSafe) {
                startActivity(Intent.createChooser(intent, "Send feedback via email"))
            } else {
                showToast("No email clients installed.")
            }
        } catch (ex: ActivityNotFoundException) {
            showToast("No email clients installed.")
        }
    }

    private fun resetButtonStates() {
        setDeactiveColor(binding.homeImg, binding.homeTxt)
        setDeactiveColor(binding.greetingImg, binding.greetingTv)
        setDeactiveColor(binding.mutImg, binding.mutTv)
        setDeactiveColor(binding.brandingImg, binding.brandingTv)
        setDeactiveColor(binding.createImg, binding.createTxt)
    }
}




/*
package com.tohed.islampro.ui.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        fun getIntent(activity: AppCompatActivity): Intent {
            return Intent(activity, HomeActivity::class.java)
        }
        private const val IMAGE_PICK_CODE = 1000

    }

    private var isHome = false
    private var selectedScreenshotUri: Uri? = null
    private var onImagePickedCallback: ((Uri?) -> Unit)? = null
    private lateinit var binding: ActivityHomeBinding
    private lateinit var slidingRootNav: SlidingRootNav
    private lateinit var navController: NavController
    private lateinit var prayerService: PrayerService

    private lateinit var prayerTimeTextView: TextView
    private lateinit var prayerNameTextView: TextView
    private lateinit var hijriDateTextView: TextView
    private lateinit var islamicYearTextView: TextView
    private lateinit var gregorianDateTextView: TextView
    private lateinit var upcomingPrayerTimeTextView: TextView
    private lateinit var arabicMonthNamee: TextView
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

        //islamicYearTextView = findViewById(R.id.islamicYearTextView)
        prayerNameTextView = findViewById(R.id.tvNimazName)
        prayerTimeTextView = findViewById(R.id.tvNimazTime)
        hijriDateTextView = findViewById(R.id.islam_hijri)
        gregorianDateTextView = findViewById(R.id.date)
        upcomingPrayerTimeTextView = findViewById(R.id.upcomingPrayerTimeTv)
        arabicMonthNamee = findViewById(R.id.islam_date)
        //       dayImageView = findViewById(R.id.ivday)
//        nightImageView = findViewById(R.id.ivnight)

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
        binding.createBtn.setOnClickListener(this)
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
        // Save individual components for accurate retrieval
        editor.putString("gregorianDate", response.data.date.gregorian.date)
        editor.putString("gregorianWeekday", response.data.date.gregorian.weekday.en)
        editor.putString("islamicDate", response.data.date.hijri.date)
        editor.putString("hijriDay", response.data.date.hijri.day)
        editor.putString("islamicYear", response.data.date.hijri.year)
        editor.putString("islamicMonth", response.data.date.hijri.month.en)

        // Save prayer timings
        response.data.timings.forEach { (prayerName, prayerTime) ->
            editor.putString(prayerName, prayerTime)
        }
        editor.apply() // Apply changes
    }


    private fun loadPrayerTimingsFromPrefs() {
        val gregorianDate = sharedPreferences.getString("gregorianDate", "")
        val gregorianWeekday = sharedPreferences.getString("gregorianWeekday", "")
        val islamicDate = sharedPreferences.getString("islamicDate", "")
        val islamicYear = sharedPreferences.getString("islamicYear", "")
        val hijriDay = sharedPreferences.getString("hijriDay", "")
        val islamicMonth = sharedPreferences.getString("islamicMonth", "")

        val islamicYearAndDay = " $islamicMonth, $islamicYear"
        val formattedGregorianDate = "$gregorianWeekday, $gregorianDate"

        // Display the dates
        hijriDateTextView.text = islamicYearAndDay
        gregorianDateTextView.text = formattedGregorianDate
        arabicMonthNamee.text = hijriDay

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
        upcomingPrayerTimeTextView.text = upcomingPrayerName*/
/*arabicMonthNamee.text = islamicMonth
        gregorianDateTextView.text = gregorianDate
        hijriDateTextView.text = islamicDate*//*

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

        // Handle location settings result
        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
            if (isLocationEnabled()) {
                settingsDialogShown = false
                getLocation()
            } else {
                showToast("Location services are still disabled.")
            }
        }

        // Handle image picking result
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val selectedImage = data?.data
            // Invoke the callback with the selected image URI
            onImagePickedCallback?.invoke(selectedImage)
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
                // showToast("Location permission denied.")
            }
        }
    }

    private fun displayPrayerTimings(response: PrayerTimesResponse) {
        val data = response.data

        // Extract Islamic year and Hijri day
        val islamicYear = data.date.hijri.year
        val hijriDay = data.date.hijri.day
        val islamicMonth = data.date.hijri.month.en
        val islamicYearAndDay = "$islamicYear ,$islamicMonth"

        // Set the combined string to the appropriate TextView
        hijriDateTextView.text = islamicYearAndDay

        // Extract Gregorian date and format with weekday
        val gregorianDate = data.date.gregorian.date
        val gregorianWeekday = data.date.gregorian.weekday.en
        val formattedGregorianDate = "$gregorianWeekday, $gregorianDate"

        // Display the extracted data
        //islamicYearTextView.text = islamicMonth // You might want to check this line
        gregorianDateTextView.text = formattedGregorianDate

        // Update prayer timings if necessary
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        var currentPrayerName = ""
        var currentPrayerTime = ""
        var upcomingPrayerName = ""

        for ((prayerName, prayerTime) in data.timings) {
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
        arabicMonthNamee.text = hijriDay
        upcomingPrayerTimeTextView.text = upcomingPrayerName
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
        slidingRootNav.layout.findViewById<CardView>(R.id.aboutUs).setOnClickListener {
            startActivity(AboutUs.getIntent(this))
        }
        slidingRootNav.layout.findViewById<CardView>(R.id.WA_Support).setOnClickListener {
            val whatsappUrl = "https://wa.me/message/GDBNOLUGYROZC1"
            val intent = Intent(Intent.ACTION_VIEW)

            // Check if WhatsApp is installed
            intent.setPackage("com.whatsapp")

            intent.data = Uri.parse(whatsappUrl)
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // WhatsApp not installed, open in web browser
                intent.setPackage(null)
                startActivity(intent)
            }
        }
        slidingRootNav.layout.findViewById<CardView>(R.id.feedback).setOnClickListener {
            showFeedbackDialog()
        }

    }

    private fun setActiveColor(imageView: ImageView, textView: TextView) {
        textView.setTextColor(getColor(R.color.white))
        imageView.setColorFilter(
            ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN
        )
    }

    private fun setDeactiveColor(imageView: ImageView, textView: TextView) {
        textView.setTextColor(getColor(R.color.icon_tint))
        imageView.setColorFilter(
            ContextCompat.getColor(this, R.color.icon_tint), PorterDuff.Mode.SRC_IN
        )
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.home_btn -> {
                navController.popBackStack()
                navController.navigate(R.id.gamesFragment)
                isHome = true
                resetButtonStates()

                //findViewById<ImageView>(R.id.roundedBackground).setImageResource(R.drawable.round_gray)

                setActiveColor(binding.homeImg, binding.homeTxt)
                binding.homeBtn.background.setTintList(null)
                binding.searchBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.premiumBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.mutafariqM.background.setTint(resources.getColor(R.color.transparent))
                binding.createBtn.background.setTint(resources.getColor(R.color.transparent))
            }

            R.id.search_btn -> {
                navController.popBackStack()
                navController.navigate(R.id.updatesFragment)
                isHome = false
                resetButtonStates()
                //findViewById<ImageView>(R.id.roundedBackground).setImageResource(R.drawable.round_gray)

                setActiveColor(binding.greetingImg, binding.greetingTv)
                binding.searchBtn.background.setTintList(null)
                binding.homeBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.premiumBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.mutafariqM.background.setTint(resources.getColor(R.color.transparent))
                binding.createBtn.background.setTint(resources.getColor(R.color.transparent))


            }

            R.id.mutafariq_M -> {
                isHome = false
                navController.popBackStack()
                navController.navigate(R.id.favouritesFragment)

                resetButtonStates()
                //findViewById<ImageView>(R.id.roundedBackground).setImageResource(R.drawable.round_gray)

                setActiveColor(binding.mutImg, binding.mutTv)
                binding.mutafariqM.background.setTintList(null)
                binding.homeBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.searchBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.premiumBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.createBtn.background.setTint(resources.getColor(R.color.transparent))

            }

            R.id.create_btn -> {
                isHome = false
                navController.popBackStack()
                resetButtonStates()

                //findViewById<ImageView>(R.id.roundedBackground).setImageResource(R.drawable.round_selected)
                setActiveColor(binding.createImg, binding.createTxt)
                binding.createBtn.background.setTintList(null)
                binding.premiumBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.homeBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.searchBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.mutafariqM.background.setTint(resources.getColor(R.color.transparent))
            }

            R.id.premium_btn -> {
                isHome = false
                navController.popBackStack()
                navController.navigate(R.id.leaguesFragment)

                resetButtonStates()

                //findViewById<ImageView>(R.id.roundedBackground).setImageResource(R.drawable.round_gray)

                setActiveColor(binding.brandingImg, binding.brandingTv)
                binding.premiumBtn.background.setTintList(null)
                binding.homeBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.searchBtn.background.setTint(resources.getColor(R.color.transparent))
                binding.mutafariqM.background.setTint(resources.getColor(R.color.transparent))
                binding.createBtn.background.setTint(resources.getColor(R.color.transparent))

            }
        }
    }

    private fun showFeedbackDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_feedback)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
        val etFeedback = dialog.findViewById<EditText>(R.id.etFeedback)
        val ivAddScreenshot = dialog.findViewById<ImageView>(R.id.ivAddScreenshot)
        val screenshotContainer = dialog.findViewById<LinearLayout>(R.id.screenshotContainer)
        val tvScreenshotName = dialog.findViewById<TextView>(R.id.tvScreenshotName)
        val ivRemoveScreenshot = dialog.findViewById<ImageView>(R.id.ivRemoveScreenshot)
        val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmit)


        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        ivAddScreenshot.setOnClickListener {
            pickImage { uri ->
                selectedScreenshotUri = uri
                uri?.let {
                    // Show the screenshot name and make the container visible
                    val fileName = getFileName(it)
                    tvScreenshotName.text = fileName
                    screenshotContainer.visibility = View.VISIBLE
                }
            }
        }

        ivRemoveScreenshot.setOnClickListener {
            // Clear the screenshot selection
            selectedScreenshotUri = null
            screenshotContainer.visibility = View.GONE
        }

        btnSubmit.setOnClickListener {
            val feedback = etFeedback.text.toString().trim()
            if (feedback.isEmpty()) {
                etFeedback.error = "Please provide feedback"
                return@setOnClickListener
            }

            sendFeedbackEmail(feedback, selectedScreenshotUri)
            dialog.dismiss()
        }

        dialog.show()
    }

   */
/* private fun showFeedbackDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_feedback)

        val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
        val etFeedback = dialog.findViewById<EditText>(R.id.etFeedback)
        val ivAddScreenshot = dialog.findViewById<ImageView>(R.id.ivAddScreenshot)
        val screenshotContainer = dialog.findViewById<LinearLayout>(R.id.screenshotContainer)
        val tvScreenshotName = dialog.findViewById<TextView>(R.id.tvScreenshotName)
        val ivRemoveScreenshot = dialog.findViewById<ImageView>(R.id.ivRemoveScreenshot)
        val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmit)

        var screenshotUri: Uri? = null

        // Reset the UI when the dialog is closed
        val resetDialog = {
            screenshotUri = null
            screenshotContainer.visibility = View.GONE
            etFeedback.text.clear()
        }

        ivClose.setOnClickListener {
            resetDialog()
            dialog.dismiss()
        }

        ivAddScreenshot.setOnClickListener {
            pickImage { uri ->
                screenshotUri = uri
                uri?.let {
                    // Show the screenshot name and make the container visible
                    val fileName = getFileName(it)
                    tvScreenshotName.text = fileName
                    screenshotContainer.visibility = View.VISIBLE
                }
            }
        }

        ivRemoveScreenshot.setOnClickListener {
            // Clear the screenshot selection
            screenshotUri = null
            screenshotContainer.visibility = View.GONE
        }

        btnSubmit.setOnClickListener {
            val feedback = etFeedback.text.toString().trim()
            if (feedback.isEmpty()) {
                etFeedback.error = "Please provide feedback"
                return@setOnClickListener
            }

            sendFeedbackEmail(feedback, screenshotUri)

            // Clear the screenshot after sending feedback
            resetDialog()
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            resetDialog()
        }

        dialog.show()
    }*//*



    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "Unnamed"
    }


    private fun pickImage(onImagePicked: (Uri?) -> Unit) {
        onImagePickedCallback = onImagePicked

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }



    private fun sendFeedbackEmail(feedback: String, screenshotUri: Uri?) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("usmanahmed7290@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "User Feedback")
            putExtra(Intent.EXTRA_TEXT, feedback)

            // Attach the screenshot if one is selected
            if (screenshotUri != null) {
                putExtra(Intent.EXTRA_STREAM, screenshotUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant permission for the attachment
            }
        }

        try {
            // Check if there's an email client installed
            val packageManager = packageManager
            val activities = packageManager.queryIntentActivities(intent, 0)
            val isIntentSafe = activities.isNotEmpty()

            if (isIntentSafe) {
                // Directly open the chooser with email apps
                startActivity(Intent.createChooser(intent, "Send feedback via email"))
            } else {
                Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show()
            }
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun resetButtonStates() {
        setDeactiveColor(binding.homeImg, binding.homeTxt)
        setDeactiveColor(binding.greetingImg, binding.greetingTv)
        setDeactiveColor(binding.mutImg, binding.mutTv)
        setDeactiveColor(binding.brandingImg, binding.brandingTv)
        setDeactiveColor(binding.createImg, binding.createTxt) // Resetting the create button
    }
}*/
