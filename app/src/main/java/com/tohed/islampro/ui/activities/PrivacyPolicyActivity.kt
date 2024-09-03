package com.tohed.islampro.ui.activities

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.tohed.islampro.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val webView = binding.webView
        webView.settings.javaScriptEnabled = true // Enable JavaScript if required
        webView.webViewClient = WebViewClient() // Ensure links open within the WebView

        // Load the privacy policy URL
        webView.loadUrl("https://tohed.com/privacy-policy/")
    }
}
