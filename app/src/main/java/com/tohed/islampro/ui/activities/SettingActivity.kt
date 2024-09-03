package com.tohed.islampro.ui.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tohed.islampro.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set click listeners
        binding.notifyll.setOnClickListener {
            openNotificationSettings()
        }

        binding.rateUS.setOnClickListener {
            openGooglePlayStore()
        }

        binding.privacyP.setOnClickListener {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        }
    }

    private fun openNotificationSettings() {
        try {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to open notification settings.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGooglePlayStore() {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=$packageName")
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback in case the Play Store is not available
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            startActivity(intent)
        }
    }

    /*private fun openPrivacyPolicy() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://tohed.com/privacy-policy/")
        startActivity(intent)
    }*/
}
