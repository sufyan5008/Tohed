package com.tohed.islampro.ui.fragments

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tohed.islampro.R
import com.tohed.islampro.adapters.UpdatesAdapter
import com.tohed.islampro.api.PostApiService
import com.tohed.islampro.databinding.FragmentUpdatesBinding
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.utils.NetworkReceiver
import com.tohed.islampro.viewModel.UpdatesViewModel

class UpdatesFragment : Fragment() {

    private lateinit var binding: FragmentUpdatesBinding
    private lateinit var apiService: PostApiService
    private lateinit var updatesViewModel: UpdatesViewModel
    private lateinit var updatesAdapter: UpdatesAdapter
    private lateinit var networkReceiver: NetworkReceiver
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updatesViewModel = ViewModelProvider(this).get(UpdatesViewModel::class.java)
        updatesAdapter = UpdatesAdapter(emptyList()) { clickedPost ->
            handlePostItemClick(clickedPost)
        }

        networkReceiver = NetworkReceiver { isConnected ->
            if (isConnected) {
                binding.progressBar.visibility = View.VISIBLE
                updatesViewModel.syncPostsByCategory(504) // Sync with the server if connected
            }
        }

        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireContext().registerReceiver(networkReceiver, intentFilter)


        binding.updatesList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = updatesAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount = layoutManager?.childCount ?: 0
                    val totalItemCount = layoutManager?.itemCount ?: 0
                    val firstVisibleItemPosition = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    // Check if the user is scrolling up
                    if (dy < 0) {
                        binding.seeAll.visibility = View.GONE
                    }

                    // Check if the user is scrolling down and is at the end of the list
                    if (dy > 0 && (firstVisibleItemPosition + visibleItemCount) >= totalItemCount) {
                        binding.seeAll.visibility = View.VISIBLE
                    }
                }
            })
        }

        updatesViewModel.postsLiveData.observe(viewLifecycleOwner) { posts ->
            binding.progressBar.visibility = View.GONE
            updatesAdapter.updatePosts(posts)
            binding.seeAll.visibility = View.GONE
            isLoading = false
        }

        binding.seeAll.setOnClickListener {
            fetchMorePosts()
        }

        fetchMorePosts() // Initial fetch
    }

    private fun fetchMorePosts() {
        if (isLoading) return

        binding.progressBar.visibility = View.VISIBLE
        isLoading = true
        updatesViewModel.fetchPostsByCategory(504) // Adjust the category ID as necessary
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(networkReceiver)
    }

    private fun handlePostItemClick(post: Post) {
        val postId = post.id.toLong()
        navigateToPostDetails(postId)
    }

    private fun navigateToPostDetails(postId: Long) {
        val args = Bundle().apply {
            putLong("postId", postId)
        }
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(R.id.action_updatesFragment_to_postDetailsFragment, args)
    }
}
