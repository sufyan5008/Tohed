package com.tohed.islampro.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tohed.islampro.R
import com.tohed.islampro.repository.PostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AboutUs : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var contentWebView: WebView
    private lateinit var progressBar: View

    private lateinit var repository: PostRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        // Initialize UI elements
        titleTextView = findViewById(R.id.titleTextView)
        contentWebView = findViewById(R.id.contentWebView)
        progressBar = findViewById(R.id.progressBar)

        // Initialize the repository here where context is fully available
        repository = PostRepository(this)

        // Fetch page content
        fetchPageContent(16024) // Example page ID
    }

    private fun fetchPageContent(pageId: Long) {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.Main).launch {
            val page = withContext(Dispatchers.IO) {
                repository.getPageDetails(pageId)
            }

            progressBar.visibility = View.GONE

            page?.let {
                titleTextView.text = it.title.rendered

                // Apply CSS styling to the content
                val styledContent = wrapHtmlInCss(it.content.rendered)

                contentWebView.settings.javaScriptEnabled = true
                contentWebView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
                contentWebView.webViewClient = WebViewClient()
                contentWebView.loadDataWithBaseURL(null, styledContent, "text/html", "UTF-8", null)
            } ?: run {
                // Handle error: page could not be loaded
                titleTextView.text = getString(R.string.error_loading_page)
            }
        }
    }

    private fun wrapHtmlInCss(htmlContent: String): String {
        val cssStyles = """
            <style>
                @font-face {
                    font-family: 'Mehr Urdu';
                    src: url('file:///android_asset/fonts/mehr_urdu_font.ttf');
                }
                @font-face {
                    font-family: 'Uthaman Arabic';
                    src: url('file:///android_asset/fonts/uthaman_arabic.ttf');
                }
                body {
                    font-family: 'Mehr Urdu', serif;
                    font-size: 14px;
                    direction: rtl; 
                    text-align: right;
                }
                .btahreer {
                    font-family: 'Mehr Urdu', serif;
                    color: #A52A2A; /* Brown color for Urdu text */
                }
                .uarabic1w {
                    font-family: 'Uthaman Arabic', serif;
                    color: #0000FF; /* Blue color for Arabic text */
                }
                .blue1 {
                    color: #0000FF; /* Blue color */
                }
                .ref {
                    font-family: 'Uthaman Arabic', serif;
                    color: #FF0033; /* Red color for reference text */
                }
            </style>
        """.trimIndent()

        val htmlTemplate = """
            <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    $cssStyles
                </head>
                <body>
                    $htmlContent
                </body>
            </html>
        """.trimIndent()

        return htmlTemplate
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, AboutUs::class.java)
        }
    }
}
