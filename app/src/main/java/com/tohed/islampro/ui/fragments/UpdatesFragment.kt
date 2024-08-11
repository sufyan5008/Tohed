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
import com.tohed.islampro.R
import com.tohed.islampro.adapters.UpdatesAdapter
import com.tohed.islampro.api.PostApiService
import com.tohed.islampro.databinding.FragmentUpdatesBinding
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.utils.NetworkReceiver
import com.tohed.islampro.viewModel.UpdatesViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdatesFragment : Fragment() {

    private lateinit var binding: FragmentUpdatesBinding
    private lateinit var apiService: PostApiService
    private lateinit var updatesViewModel: UpdatesViewModel
    private lateinit var updatesAdapter: UpdatesAdapter
    private lateinit var networkReceiver: NetworkReceiver


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentUpdatesBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updatesViewModel = ViewModelProvider(this).get(UpdatesViewModel::class.java)
        updatesAdapter = UpdatesAdapter(emptyList()) { clickedPost ->
            handlePostItemClick(clickedPost)
        }

        binding.updatesList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = updatesAdapter
        }

        updatesViewModel.postsLiveData.observe(viewLifecycleOwner) { posts ->
            binding.progressBar.visibility = View.GONE
            updatesAdapter.updatePosts(posts)
        }

        /*apiService = PostApiService.getService()

        // Fetch posts from category ID 444
        fetchPostsByCategory(414)*/
        //updatesViewModel.fetchPostsByCategory(categoryId)

        updatesViewModel.fetchPostsByCategory(504)

        networkReceiver = NetworkReceiver { isConnected ->
            if (isConnected) {
                binding.progressBar.visibility = View.VISIBLE
                updatesViewModel.syncPostsByCategory(504) // Sync with the server if connected
            }
        }

        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireContext().registerReceiver(networkReceiver, intentFilter)
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
        val args = Bundle()
        args.putLong("postId", postId)
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(R.id.action_updatesFragment_to_postDetailsFragment, args)
    }
}


/*private fun fetchPostsByCategory(categoryId: Int) {
    apiService.getPostsByCategory(categoryId, 1).enqueue(object : Callback<List<Post>> {
        override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
            if (response.isSuccessful) {
                val posts = response.body()
                posts?.let {
                    // Update RecyclerView with fetched posts
                    val adapter = UpdatesAdapter(it) { post ->
                        handlePostItemClick(post)
                    }
                    binding.updatesList.layoutManager = LinearLayoutManager(requireContext())
                    binding.updatesList.adapter = adapter
                }
            } else {
                // Handle error
            }
        }

        override fun onFailure(call: Call<List<Post>>, t: Throwable) {
            // Handle failure
        }
    })
}*/
/*private fun handlePostItemClick(post: Post) {
    val postId = post.id.toLong()
    navigateToPostDetails(postId)
}
private fun navigateToPostDetails(postId: Long) {
    val args = Bundle()
    args.putLong("postId", postId)
    val navController = NavHostFragment.findNavController(this)
    navController.navigate(R.id.action_updatesFragment_to_postDetailsFragment, args)
}
}*/