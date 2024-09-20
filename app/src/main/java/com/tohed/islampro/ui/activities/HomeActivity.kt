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
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.tohed.islampro.R
import com.tohed.islampro.api.PrayerService
import com.tohed.islampro.databinding.ActivityHomeBinding
import com.tohed.islampro.databinding.DialogFeedbackBinding
import com.yarolegovich.slidingrootnav.SlideGravity
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        fun getIntent(activity: AppCompatActivity): Intent {
            return Intent(activity, HomeActivity::class.java)
        }

        private const val STORAGE_PERMISSION_CODE = 1002
        private const val IMAGE_PICK_CODE = 1000
    //       private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
//        private const val LOCATION_SETTINGS_REQUEST_CODE = 123
    }

    private lateinit var searchView: SearchView


    private var isHome = false
    private var selectedScreenshotUri: Uri? = null
    private var onImagePickedCallback: ((Uri?) -> Unit)? = null
    private lateinit var binding: ActivityHomeBinding
    private lateinit var slidingRootNav: SlidingRootNav
    private lateinit var navController: NavController

    /*private lateinit var prayerService: PrayerService
    private lateinit var tvScreenshotName: TextView
    private lateinit var screenshotContainer: LinearLayout
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
    private var longitude: Double = 0.0*/

    private lateinit var feedbackDialogBinding: DialogFeedbackBinding
    private lateinit var feedbackDialog: AlertDialog

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

        //sharedPreferences = getSharedPreferences("PrayerTimesPrefs", Context.MODE_PRIVATE)

        // fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //requestLocationPermission()


        setUpSearchView()

        setUpNavigationControllerGraph()
        setSlidingRootNav(savedInstanceState)
        setOnClickListeners()

        /* prayerNameTextView = findViewById(R.id.tvNimazName)
         prayerTimeTextView = findViewById(R.id.tvNimazTime)
         hijriDateTextView = findViewById(R.id.islam_hijri)
         gregorianDateTextView = findViewById(R.id.date)
         upcomingPrayerTimeTextView = findViewById(R.id.upcomingPrayerTimeTv)
         arabicMonthNamee = findViewById(R.id.islam_date)*/

        binding.homeBtn.setOnClickListener(this)
        binding.searchBtn.setOnClickListener(this)
        binding.premiumBtn.setOnClickListener(this)
        binding.mutafariqM.setOnClickListener(this)
        binding.createBtn.setOnClickListener(this)

        //  loadPrayerTimingsFromPrefs()
    }

    override fun onResume() {
        super.onResume()/*  if (!settingsDialogShown) {
              refreshData()
          }*/
    }

    private fun setUpSearchView() {
        // Access EditText and ImageView from the layout
        val searchEditText = binding.searchEditText  // EditText for search input
        val searchIcon = binding.searchIcon          // ImageView for search icon

        // Handle search when clicking on the ImageView
        searchIcon.setOnClickListener {
            val query = searchEditText.text.toString()  // Get the input text
            if (query.isNotEmpty()) {
                // Trigger the search
                launchSearchResultsActivity(query)
            } else {
                Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show()
            }
        }

        // Optionally, handle "enter" key press in the EditText
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString()
                if (query.isNotEmpty()) {
                    // Trigger the search
                    launchSearchResultsActivity(query)
                } else {
                    Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }
    }

    private fun launchSearchResultsActivity(query: String) {
        val intent = Intent(this, SearchResultsActivity::class.java)
        intent.putExtra("query", query)
        startActivity(intent)
    }



    /*private fun requestLocationPermission() {
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
    }*/

    /* private fun refreshData() {
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
     }*/

    /*private fun fetchAndDisplayPrayerTimings(latitude: Double, longitude: Double) {
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
    }*/

    /*private fun savePrayerTimingsToPrefs(response: PrayerTimesResponse) {
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
    }*/

    /*private fun loadPrayerTimingsFromPrefs() {
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
    }*/

    /*private fun showLocationSettingsDialog() {
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
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /*if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
            if (isLocationEnabled()) {
                settingsDialogShown = false
                fetchLocation()
            } else {
                showToast("Location services are still disabled.")
            }
        }*/

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val selectedImage = data?.data
            onImagePickedCallback?.invoke(selectedImage)

        }
    }

    /* private fun isLocationEnabled(): Boolean {
         val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
         return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
             LocationManager.NETWORK_PROVIDER
         )
     }*/


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Storage permission granted
                    pickImage { uri ->
                        selectedScreenshotUri = uri
                        uri?.let {
                            val fileName = getFileName(it)/*val tvScreenshotName = findViewById<TextView>(R.id.tvScreenshotName)
                            val screenshotContainer = findViewById<LinearLayout>(R.id.screenshotContainer)*/
                            if (::feedbackDialogBinding.isInitialized) {
                                feedbackDialogBinding.tvScreenshotName.text = fileName
                                feedbackDialogBinding.screenshotContainer.visibility = View.VISIBLE
                            }
                        }
                    }
                } else {
                    // Storage permission denied
                    showToast("Storage permission denied.")
                }
            }

            /*LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation()
                } else {
                    showToast("Location permission denied.")
                }
            }*/
        }
    }


    /*private fun displayPrayerTimings(response: PrayerTimesResponse) {
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
    }*/

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
        slidingRootNav.layout.findViewById<CardView>(R.id.share).setOnClickListener {
            shareApp()
        }
        slidingRootNav.layout.findViewById<CardView>(R.id.follow).setOnClickListener {
            showFollowUsDialog()
        }
        slidingRootNav.layout.findViewById<CardView>(R.id.notification).setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

    }

    private fun showFollowUsDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_follow_us)
        dialog.setCancelable(true)
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),  // 90% of the screen width
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        // Find views in the dialog layout
        val ivFacebook = dialog.findViewById<ImageView>(R.id.ivFacebook)
        val ivLinkedIn = dialog.findViewById<ImageView>(R.id.ivLinkedIn)
        val ivTwitter = dialog.findViewById<ImageView>(R.id.ivTwitter)
        val ivWhatsApp = dialog.findViewById<ImageView>(R.id.ivWhatsApp)

        ivFacebook.setOnClickListener {
            openSocialMedia("com.facebook.katana", "https://www.facebook.com/tohedcom")
        }

        ivLinkedIn.setOnClickListener {
            openSocialMedia(
                "com.linkedin.android", "https://www.linkedin.com/in/tohed-dot-com-b3726a293/"
            )
        }

        ivTwitter.setOnClickListener {
            openSocialMedia("com.twitter.android", "https://x.com/Tohed_Com")
        }

        ivWhatsApp.setOnClickListener {
            openSocialMedia("com.whatsapp", "https://wa.me/message/GDBNOLUGYROZC1")
        }

        dialog.show()
    }

    private fun openSocialMedia(packageName: String, url: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            startActivity(intent)
        } else {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(webIntent)
        }
    }


    private fun shareApp() {
        val appPackageName = packageName
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out this app!")
            putExtra(
                Intent.EXTRA_TEXT,
                "Hey, check out this amazing app: https://play.google.com/store/apps/details?id=$appPackageName"
            )
        }
        try {
            startActivity(Intent.createChooser(shareIntent, "Share app via"))
        } catch (e: ActivityNotFoundException) {
            showShareToast("No app available to share the link.")
        }
    }

    private fun showShareToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
                navController.navigate(R.id.homeFragment)
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
        feedbackDialogBinding = DialogFeedbackBinding.inflate(layoutInflater)
        dialog.setContentView(R.layout.dialog_feedback)
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),  // 90% of the screen width
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
                        tvScreenshotName?.text = fileName
                        screenshotContainer?.visibility = View.VISIBLE
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
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
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