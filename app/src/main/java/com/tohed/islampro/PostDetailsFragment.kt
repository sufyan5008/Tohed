package com.tohed.islampro

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tohed.islampro.databinding.FragmentPostDetailsBinding
import com.tohed.islampro.viewModel.PostViewModel

class PostDetailsFragment : Fragment() {

    private var postId: Long = 0
    private lateinit var binding: FragmentPostDetailsBinding
    private lateinit var postViewModel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postId = it.getLong("postId", 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        postViewModel = ViewModelProvider(requireActivity()).get(PostViewModel::class.java)

        // WebView settings
        binding.contentWebView.settings.apply {
            javaScriptEnabled = true
            defaultTextEncodingName = "utf-8"
            domStorageEnabled = true // Enable DOM storage API
            loadWithOverviewMode = true // Load content to fit the view
            useWideViewPort = true // Enable wider view port
            textZoom = 150 // Increase text size for better readability
        }

        // Observe post details LiveData
        postViewModel.postDetailsLiveData.observe(viewLifecycleOwner) { postDetails ->
            // Update UI with post details (e.g., title, content)
            binding.progressBar.visibility = View.GONE
            postDetails?.let { post ->
                binding.titleTextView.text = postDetails.title.rendered
                binding.contentWebView.loadDataWithBaseURL(
                    null,
                    wrapHtmlInCss(postDetails.content.rendered),
                    "text/html; charset=utf-8",
                    "UTF-8",
                    null
                )
            }
        }
        fetchPostDetails(postId)
    }

    private fun fetchPostDetails(postId: Long) {
        binding.progressBar.visibility = View.VISIBLE
        postViewModel.fetchPostDetails(postId)
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
                    color: #000000; /* Black color for Urdu text */
                }
                .uarabic1w {
                    font-family: 'Uthaman Arabic', serif;
                    color: #0000FF; /* Blue color for Arabic text */
                }
                .blue1 {
                    color: #0000FF; /* Blue color */
                }
                .ref {
                    color: #FF0000; /* Red color for reference text */
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
}



/*
package com.tohed.islampro

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tohed.islampro.databinding.FragmentPostDetailsBinding
import com.tohed.islampro.viewModel.PostViewModel

class PostDetailsFragment : Fragment() {

    private var postId: Long = 0
    private lateinit var binding: FragmentPostDetailsBinding
    private lateinit var postViewModel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postId = it.getLong("postId", 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        postViewModel = ViewModelProvider(requireActivity()).get(PostViewModel::class.java)

        binding.contentWebView.settings.javaScriptEnabled = true
        binding.contentWebView.settings.defaultTextEncodingName = "utf-8" // Set encoding for Urdu text

        // Observe post details LiveData
        postViewModel.postDetailsLiveData.observe(viewLifecycleOwner) { postDetails ->
            // Update UI with post details (e.g., title, content)
            binding.progressBar.visibility = View.GONE
            postDetails?.let { post ->
                binding.titleTextView.text = postDetails.title.rendered
                binding.contentWebView.loadDataWithBaseURL(
                    null,
                    wrapHtmlInCss(postDetails.content.rendered),
                    "text/html; charset=utf-8",
                    "UTF-8",
                    null
                )
            }
        }
        fetchPostDetails(postId)
    }

    private fun fetchPostDetails(postId: Long) {
        binding.progressBar.visibility = View.VISIBLE
        postViewModel.fetchPostDetails(postId)
    }

    private fun wrapHtmlInCss(htmlContent: String): String {
        val cssStyle = "<style>body { direction: rtl; text-align: right; }</style>"
        return "$cssStyle$htmlContent"
    }
}
*/
