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

// PostDetailsFragment.kt
class PostDetailsFragment : Fragment() {

    private var postId: Long = 0
    private lateinit var binding: FragmentPostDetailsBinding
    private lateinit var postViewModel: PostViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postId = it.getLong("postId")
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
            postDetails?.let { post ->

                binding.titleTextView.text = postDetails.title.rendered
                binding.contentWebView.loadDataWithBaseURL(
                    null,
                    wrapHtmlInCss(postDetails.content.rendered),
                    "text/html; charset=utf-8",
                    "UTF-8",
                    null
                )

                 //binding.contentTextView.text = postDetails.content.rendered
            }
        }

        // Fetch post details based on postId
        postViewModel.fetchPostDetails(postId)
    }
    private fun wrapHtmlInCss(htmlContent: String): String {
        val cssStyle = "<style>body { direction: rtl; text-align: right; }</style>"
        return "$cssStyle$htmlContent"
    }

}
