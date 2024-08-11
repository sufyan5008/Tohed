package com.tohed.islampro.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.navigation.fragment.findNavController
import com.tohed.OnItemClickListener
import com.tohed.islampro.R
import com.tohed.islampro.adapters.TextViewAdapter
import com.tohed.islampro.databinding.FragmentLeaguesBinding
import com.tohed.islampro.viewModel.PostViewModel

class LeaguesFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentLeaguesBinding
    private val postViewModel: PostViewModel by viewModels()
    private var progressDialog: ProgressDialogFragment? = null


    private val texts = arrayOf(
        "وسیلہ", "اوقات نماز،اذان،اقامت کا بیان", "فضائل و احکام درود", "تقدیر", "جمع بین الصلاتین", "حج و عمرہ سے متعلق احکام",
        "سماع موتیٰ", "رفع یدین", "صدقات و زکوۃ کا بیان", "عذاب قبر برحق", "رفع سبابہ، جلسہ استراحت ، تشہد", "احکام و مسائل رمضان المبارک",
        "عقائد کا بیان", "طہارت، وضو، غسل کا بیان", "میت، جنائز کے احکامات", "علم غیب", "نماز تراویح", "طہارت",
        "قبروں سے متعلق", "مسح", "جادو، جنات، شیاطین سے متعلق", "ختم نبوت", "نماز سے متعلق متفرق احکامات", "عشرہ ذوالحجہ، قربانی، عقیقہ",
        "اہل جاہلیت کے عقائد", "وتر", "خرید و فروخت سے متعلق", "ادب کا بیان", "حیض و نفاس کا بیان", "تقلید و رد تقلید",
        "حرام و مباح امور", "طلاق، عدت، سوگ کے احکام", "حجیت حدیث", "لباس و زینت", "میاں بیوی کے تعلق سے چند احکام", "بدعت سے متعلق"
    )

    private val backgrounds = arrayOf(
        R.drawable.rounded_top_corners_1,
        R.drawable.rounded_top_corners_2,
        R.drawable.rounded_top_corners_3,
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeaguesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerView.adapter = TextViewAdapter(requireContext(), texts, backgrounds, this)

        // Observe the postsLiveData to get updates
       // observePosts()
    }

    override fun onItemClick(position: Int) {
        val selectedCategory = texts[position]

        val categoryId = when (position) {
            0 -> 408 // ID for وسیلہ
            1 -> 409 // ID for اوقات نماز،اذان،اقامت کا بیان
            2 -> 418
            3 -> 454
            4 -> 446
            5 -> 416
            6 -> 450
            7 -> 460
            8 -> 431
            9 -> 448
            10 -> 442
            11 -> 415
            12 -> 438
            13 -> 421
            14 -> 428
            15 -> 423
            16 -> 424
            17 -> 425
            18 -> 434
            19 -> 427
            20 -> 428
            21 -> 429
            22 -> 430
            23 -> 431
            24 -> 432
            25 -> 433
            26 -> 434
            27 -> 435
            28 -> 436
            29 -> 437
            30 -> 438
            31 -> 439
            32 -> 440
            33 -> 441
            34 -> 442
            35 -> 443
            36 -> 444

            else -> -1
        }
        if (categoryId != -1) {

            progressDialog = ProgressDialogFragment()
            progressDialog?.show(childFragmentManager, "loadingDialog")
            // Fetch posts for the selected category
            postViewModel.fetchPostsByCategory(categoryId)
            postViewModel.postsLiveData.observe(viewLifecycleOwner) { posts ->
                progressDialog?.dismiss()
                if (posts.isNullOrEmpty()) {
                    Toast.makeText(context, "No posts found", Toast.LENGTH_SHORT).show()
                } else {
                    val bundle = Bundle().apply {
                        putParcelableArray("posts", posts.toTypedArray())
                        putString("categoryTitle", selectedCategory) // Pass the title
                    }
                    findNavController().navigate(R.id.action_leaguesFragment_to_excerptFragment, bundle)
                }
            }
        } else {
            Toast.makeText(context, "Invalid category ID", Toast.LENGTH_SHORT).show()
        }
    }

    /*private fun observePosts() {
        postViewModel.postsLiveData.observe(viewLifecycleOwner) { posts ->
            progressDialog?.dismiss()
            if (posts.isNullOrEmpty()) {
                Toast.makeText(context, "No posts found", Toast.LENGTH_SHORT).show()
            } else {
                // Create a bundle and pass the posts to the next fragment
                val bundle = Bundle().apply {
                    putParcelableArray("posts", posts.toTypedArray())

                }
                findNavController().navigate(R.id.action_leaguesFragment_to_excerptFragment, bundle)
            }
        }
    }*/
}


/*
package com.tohed.islampro.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.navigation.fragment.findNavController
import com.tohed.OnItemClickListener
import com.tohed.islampro.R
import com.tohed.islampro.adapters.TextViewAdapter
import com.tohed.islampro.databinding.FragmentLeaguesBinding
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.api.PostApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeaguesFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentLeaguesBinding
    private lateinit var apiService: PostApiService
    private val texts = arrayOf(
        "وسیلہ", "اوقات نماز،اذان،اقامت کا بیان", "فضائل و احکام درود", "تقدیر", "جمع بین الصلاتین", "حج و عمرہ سے متعلق احکام",
        "سماع موتیٰ", "رفع یدین", "صدقات و زکوۃ کا بیان", "عذاب قبر برحق", "رفع سبابہ، جلسہ استراحت ، تشہد", "احکام و مسائل رمضان المبارک",
        "عقائد کا بیان", "طہارت، وضو، غسل کا بیان", "میت، جنائز کے احکامات", "علم غیب", "نماز تراویح", "طہارت",
        "قبروں سے متعلق", "مسح", "جادو، جنات، شیاطین سے متعلق", "ختم نبوت", "نماز سے متعلق متفرق احکامات", "عشرہ ذوالحجہ، قربانی، عقیقہ",
        "اہل جاہلیت کے عقائد", "وتر", "خرید و فروخت سے متعلق", "ادب کا بیان", "حیض و نفاس کا بیان", "تقلید و رد تقلید",
        "حرام و مباح امور", "طلاق، عدت، سوگ کے احکام", "حجیت حدیث", "لباس و زینت", "میاں بیوی کے تعلق سے چند احکام", "بدعت سے متعلق"
    )
    private val backgrounds = arrayOf(
        R.drawable.rounded_top_corners_1,
        R.drawable.rounded_top_corners_2,
        R.drawable.rounded_top_corners_3,
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeaguesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiService = PostApiService.getService()

        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerView.adapter = TextViewAdapter(requireContext(), texts, backgrounds, this)
    }

    override fun onItemClick(position: Int) {
        val categoryId = when (position) {
            0 -> 408 // ID for وسیلہ
            1 -> 409 // ID for اوقات نماز،اذان،اقامت کا بیان
            // Add more cases for other positions as needed
            else -> -1
        }
        if (categoryId != -1) {
            fetchPostsByCategory(categoryId)
        } else {
            Toast.makeText(context, "Invalid category ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchPostsByCategory(categoryId: Int) {
        apiService.getPostsByCategory(categoryId, 1).enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    val posts = response.body()
                    if (!posts.isNullOrEmpty()) {
                        // Create a bundle and put the data in it
                        val bundle = Bundle().apply {
                            putParcelableArray("posts", posts.toTypedArray())
                        }
                        findNavController().navigate(R.id.action_leaguesFragment_to_excerptFragment, bundle)
                    } else {
                        Toast.makeText(context, "No posts found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch posts: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Toast.makeText(context, "Network request failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

*/
