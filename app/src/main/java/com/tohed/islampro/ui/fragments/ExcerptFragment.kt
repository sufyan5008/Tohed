package com.tohed.islampro.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tohed.islampro.R
import com.tohed.islampro.adapters.ExcerptAdapter
import com.tohed.islampro.databinding.FragmentExcerptBinding
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.viewModel.PostViewModel
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

class ExcerptFragment : Fragment() {

    private lateinit var binding: FragmentExcerptBinding
    private lateinit var excerptAdapter: ExcerptAdapter
    private val postViewModel: PostViewModel by viewModels()

    private var isLoading = false
    private var postsList: MutableList<Post> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExcerptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the posts and title from the arguments
        setupFragmentArguments()

        // Setup RecyclerView
        setupRecyclerView()

        // Setup ViewModel observers
        setupObservers()

        // Setup Load More Button
        setupLoadMoreButton()
    }

    private fun setupFragmentArguments() {
        arguments?.let {
            postsList.addAll(it.getParcelableArray("posts") as Array<Post>)
            val categoryTitle = it.getString("categoryTitle")
            binding.excerptTitleTV.text = categoryTitle ?: getString(R.string.app_name)

            // Set the current category ID in the ViewModel
            val categoryId = it.getInt("categoryId")
            postViewModel.currentCategoryId = categoryId

            // Fetch the posts for the specified category
            postViewModel.fetchPostsByCategory(categoryId)
        }
    }

    private fun setupRecyclerView() {
        excerptAdapter = ExcerptAdapter(postsList) { post ->
            navigateToPostDetails(post.id.toLong())
        }

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = excerptAdapter

            // Add scroll listener to detect when reaching the end of the list
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                    if (!isLoading && linearLayoutManager.findLastVisibleItemPosition() == postsList.size - 1) {
                        binding.seeAll.visibility = View.VISIBLE
                    }
                }
            })
        }
    }

    private fun setupObservers() {
        postViewModel.postsLiveData.observe(viewLifecycleOwner) { newPosts ->
            if (newPosts.isNullOrEmpty()) {
                // handle empty state or no more posts
            } else {
                binding.progressBar.visibility = View.GONE
                binding.seeAll.visibility = View.GONE
                postsList.addAll(newPosts)
                excerptAdapter.notifyItemRangeInserted(postsList.size - newPosts.size, newPosts.size)
                isLoading = false
            }
        }

        postViewModel.errorLiveData.observe(viewLifecycleOwner) { error ->
            // handle error (e.g., show a toast or a message on the screen)
            binding.seeAll.visibility = View.GONE
        }
    }

    private fun setupLoadMoreButton() {
        binding.seeAll.setOnClickListener {
            isLoading = true
            postViewModel.fetchPostsByCategory(postViewModel.currentCategoryId ?: -1)
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    private fun navigateToPostDetails(postId: Long) {
        findNavController().navigate(
            R.id.action_excerptFragment_to_postDetailsFragment,
            Bundle().apply { putLong("postId", postId) }
        )
    }
}





/*package com.tohed.islampro.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tohed.islampro.R
import com.tohed.islampro.adapters.ExcerptAdapter
import com.tohed.islampro.databinding.FragmentExcerptBinding
import com.tohed.islampro.datamodel.Post

class ExcerptFragment : Fragment() {

    private lateinit var binding: FragmentExcerptBinding
    private lateinit var posts: Array<Post>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExcerptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup the fragment with the received arguments
        setupFragmentArguments()

        // Setup RecyclerView
        setupRecyclerView()
    }

    private fun setupFragmentArguments() {
        arguments?.let {
            posts = it.getParcelableArray("posts") as Array<Post>
            val categoryTitle = it.getString("categoryTitle")
            binding.excerptTitleTV.text = categoryTitle ?: getString(R.string.app_name)
        }
    }

    private fun setupRecyclerView() {
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = ExcerptAdapter(posts.toList()) { post ->
                navigateToPostDetails(post.id.toLong())
            }
        }
    }

    private fun navigateToPostDetails(postId: Long) {
        findNavController().navigate(
            R.id.action_excerptFragment_to_postDetailsFragment,
            Bundle().apply { putLong("postId", postId) }
        )
    }
}*/
