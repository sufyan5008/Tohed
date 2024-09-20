package com.tohed.islampro

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tohed.islampro.databinding.FragmentPostDetailsBinding
import com.tohed.islampro.viewModel.PostViewModel
import org.jsoup.Jsoup

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

        postViewModel = ViewModelProvider(requireActivity()).get(PostViewModel::class.java)

        binding.contentWebView.settings.apply {
            javaScriptEnabled = true
            defaultTextEncodingName = "utf-8"
            domStorageEnabled = true // Enable DOM storage API
            loadWithOverviewMode = true // Load content to fit the view
            useWideViewPort = true // Enable wider view port
            textZoom = 150 // Increase text size for better readability
        }
        binding.ll1.visibility = View.GONE
        binding.contentWebView.viewTreeObserver.addOnScrollChangedListener {
            if (isWebViewScrolledToBottom()) {
                binding.ll1.visibility = View.VISIBLE // Show fabCopy when scrolled to bottom
            } else {
                binding.ll1.visibility = View.GONE // Hide fabCopy otherwise
            }
        }
        postViewModel.postDetailsLiveData.observe(viewLifecycleOwner) { postDetails ->
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

                binding.fabCopy.setOnClickListener {
                    val plainTextContent = Jsoup.parse(postDetails.content.rendered).text()
                    copyToClipboard(postDetails.title.rendered, plainTextContent)
                }
            }
        }
        fetchPostDetails(postId)
    }

    private fun isWebViewScrolledToBottom(): Boolean {
        val webViewHeight = binding.contentWebView.contentHeight * binding.contentWebView.scale
        val currentScrollPosition = binding.contentWebView.scrollY + binding.contentWebView.height
        return currentScrollPosition >= webViewHeight - 10
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
                    color: #A52A2A; 
                }
                .uarabic1w {
                    font-family: 'Uthaman Arabic', serif;
                    color: #0000FF; 
                }
                .blue1 {
                    color: #0000FF; 
                }
                h1 {
                    font-size: 18px;
                }
                h2 {
                    font-size: 16px;
                }
                   h3 {
                    font-size: 14px;
                }
                .ref {
                    font-family: 'Uthaman Arabic', serif;
                    color: #FF0033;
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

    private fun copyToClipboard(title: String, content: String) {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val textToCopy = "Title: $title\n\n$content"
        val clip = ClipData.newPlainText("Post Details", textToCopy)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(
            requireContext(),
            "مضمون کا مکمل ٹیکسٹ کاپی ہو چکا ہے، اب آپ اسے کہیں بھی پیسٹ کر سکتے ہیں",
            Toast.LENGTH_SHORT
        ).show()
    }
}
