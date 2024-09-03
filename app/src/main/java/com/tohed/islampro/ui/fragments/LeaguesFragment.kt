package com.tohed.islampro.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.tohed.OnItemClickListener
import com.tohed.islampro.R
import com.tohed.islampro.adapters.TextViewAdapter
import com.tohed.islampro.databinding.FragmentLeaguesBinding
import com.tohed.islampro.viewModel.PostViewModel

class LeaguesFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentLeaguesBinding
    private val postViewModel: PostViewModel by viewModels()
    private var progressDialog: ProgressDialogFragment? = null
    private var hasNavigatedToExcerpt: Boolean = false

    private val texts = arrayOf(
        "وسیلہ",
        "اوقات نماز،اذان،اقامت کا بیان",
        "فضائل و احکام درود",
        "تقدیر",
        "جمع بین الصلاتین",
        "حج و عمرہ سے متعلق احکام",
        "سماع موتیٰ",
        "رفع یدین",
        "صدقات و زکوۃ کا بیان",
        "عذاب قبر برحق",
        "رفع سبابہ، جلسہ استراحت ، تشہد",
        "احکام و مسائل رمضان المبارک",
        "عقائد کا بیان",
        "طہارت، وضو، غسل کا بیان",
        "میت، جنائز کے احکامات",
        "علم غیب",
        "نماز تراویح",
        "طہارت",
        "قبروں سے متعلق",
        "مسح",
        "جادو، جنات، شیاطین سے متعلق",
        "ختم نبوت",
        "نماز سے متعلق متفرق احکامات",
        "عشرہ ذوالحجہ، قربانی، عقیقہ",
        "اہل جاہلیت کے عقائد",
        "وتر",
        "خرید و فروخت سے متعلق",
        "بدعت سے متعلق",
        "ادب کا بیان",
        "حیض و نفاس کا بیان",
        "تقلید و رد تقلید",
        "حرام و مباح امور",
        "طلاق، عدت، سوگ کے احکام",
        "حجیت حدیث",
        "لباس و زینت",
        "میاں بیوی کے تعلق سے چند احکام"
    )

    private val backgrounds = arrayOf(
        R.drawable.rounded_top_corners_1,
        R.drawable.rounded_top_corners_2,
        R.drawable.rounded_top_corners_3,
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeaguesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        hasNavigatedToExcerpt = false
        progressDialog?.dismiss()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerView.adapter = TextViewAdapter(requireContext(), texts, backgrounds, this)
    }

    private fun setupObservers() {
        postViewModel.postsLiveData.observe(viewLifecycleOwner) { posts ->
            if (postViewModel.loadingLiveData.value == true || hasNavigatedToExcerpt) {
                return@observe
            }

            progressDialog?.dismiss()

            if (posts.isNullOrEmpty()) {
                //Toast.makeText(context, "No posts found", Toast.LENGTH_SHORT).show()
            } else {
                val bundle = Bundle().apply {
                    putParcelableArray("posts", posts.toTypedArray())
                    putString("categoryTitle", postViewModel.currentCategoryTitle)
                }
                hasNavigatedToExcerpt = true
                findNavController().navigate(R.id.action_leaguesFragment_to_excerptFragment, bundle)
            }
        }

        postViewModel.loadingLiveData.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading && !hasNavigatedToExcerpt) {
                progressDialog = ProgressDialogFragment()
                progressDialog?.show(childFragmentManager, "loadingDialog")
            } else {
                progressDialog?.dismiss()
            }
        }

        postViewModel.errorLiveData.observe(viewLifecycleOwner) { error ->
            progressDialog?.dismiss()
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemClick(position: Int) {
        val selectedCategory = texts[position]
        val categoryId = getCategoryID(position)
        if (categoryId != -1) {
            postViewModel.currentCategoryTitle = selectedCategory
            postViewModel.fetchPostsByCategory(categoryId)
        } else {
            Toast.makeText(context, "Invalid category ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCategoryID(position: Int): Int {
        return when (position) {
            0 -> 408
            1 -> 430
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
            16 -> 434
            17 -> 474
            18 -> 456
            19 -> 445
            20 -> 439
            21 -> 480
            22 -> 447
            23 -> 452
            24 -> 419
            25 -> 459
            26 -> 469
            27 -> 457
            28 -> 420
            29 -> 422
            30 -> 449
            31 -> 427
            32 -> 429
            33 -> 440
            34 -> 453
            35 -> 432
            // Add other IDs
            else -> -1
        }
    }
}



/*
package com.tohed.islampro.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.tohed.OnItemClickListener
import com.tohed.islampro.R
import com.tohed.islampro.adapters.TextViewAdapter
import com.tohed.islampro.databinding.FragmentLeaguesBinding
import com.tohed.islampro.datamodel.Post
import com.tohed.islampro.viewModel.PostViewModel

class LeaguesFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentLeaguesBinding
    private val postViewModel: PostViewModel by viewModels()
    private var progressDialog: ProgressDialogFragment? = null


    private val texts = arrayOf(
        "وسیلہ",
        "اوقات نماز،اذان،اقامت کا بیان",
        "فضائل و احکام درود",
        "تقدیر",
        "جمع بین الصلاتین",
        "حج و عمرہ سے متعلق احکام",
        "سماع موتیٰ",
        "رفع یدین",
        "صدقات و زکوۃ کا بیان",
        "عذاب قبر برحق",
        "رفع سبابہ، جلسہ استراحت ، تشہد",
        "احکام و مسائل رمضان المبارک",
        "عقائد کا بیان",
        "طہارت، وضو، غسل کا بیان",
        "میت، جنائز کے احکامات",
        "علم غیب",
        "نماز تراویح",
        "طہارت",
        "قبروں سے متعلق",
        "مسح",
        "جادو، جنات، شیاطین سے متعلق",
        "ختم نبوت",
        "نماز سے متعلق متفرق احکامات",
        "عشرہ ذوالحجہ، قربانی، عقیقہ",
        "اہل جاہلیت کے عقائد",
        "وتر",
        "خرید و فروخت سے متعلق",
        "بدعت سے متعلق",
        "ادب کا بیان",
        "حیض و نفاس کا بیان",
        "تقلید و رد تقلید",
        "حرام و مباح امور",
        "طلاق، عدت، سوگ کے احکام",
        "حجیت حدیث",
        "لباس و زینت",
        "میاں بیوی کے تعلق سے چند احکام"
    )

    private val backgrounds = arrayOf(
        R.drawable.rounded_top_corners_1,
        R.drawable.rounded_top_corners_2,
        R.drawable.rounded_top_corners_3,
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeaguesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // postViewModel.resetPosts()
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerView.adapter = TextViewAdapter(requireContext(), texts, backgrounds, this)

        // Observe the postsLiveData
        */
/*postViewModel.postsLiveData.observe(viewLifecycleOwner) { posts ->
            progressDialog?.dismiss()
            if (posts.isNullOrEmpty()) {
                Toast.makeText(context, "No posts found", Toast.LENGTH_SHORT).show()
            } else {
                val bundle = Bundle().apply {
                    putParcelableArray("posts", posts.toTypedArray())
                    putString("categoryTitle", postViewModel.currentCategoryTitle) // Pass the title
                }
                findNavController().navigate(
                    R.id.action_leaguesFragment_to_excerptFragment, bundle
                )
            }
        }

        // Observe errors
        postViewModel.errorLiveData.observe(viewLifecycleOwner) { error ->
            progressDialog?.dismiss()
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }*//*

        // Observe the postsLiveData
        postViewModel.postsLiveData.observe(viewLifecycleOwner) { posts ->
            if (postViewModel.loadingLiveData.value == true) {
                return@observe // Ignore this update if we're still loading
            }
            progressDialog?.dismiss()

            if (posts.isNullOrEmpty()) {
                Toast.makeText(context, "No posts found", Toast.LENGTH_SHORT).show()
            } else {
                val bundle = Bundle().apply {
                    putParcelableArray("posts", posts.toTypedArray())
                    putString("categoryTitle", postViewModel.currentCategoryTitle) // Pass the title
                }
                findNavController().navigate(
                    R.id.action_leaguesFragment_to_excerptFragment, bundle
                )
            }
        }

        // Observe loading state
        postViewModel.loadingLiveData.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressDialog = ProgressDialogFragment()
                progressDialog?.show(childFragmentManager, "loadingDialog")
            } else {
                progressDialog?.dismiss()
            }
        }

        // Observe errors
        postViewModel.errorLiveData.observe(viewLifecycleOwner) { error ->
            progressDialog?.dismiss()
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemClick(position: Int) {
        val selectedCategory = texts[position]

        val categoryId = when (position) {
            0 -> 408 // ID for وسیلہ
            1 -> 430 // ID for اوقات نماز،اذان،اقامت کا بیان
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
            16 -> 434
            17 -> 474
            18 -> 456
            19 -> 445
            20 -> 439
            21 -> 480
            22 -> 447
            23 -> 452
            24 -> 419
            25 -> 459
            26 -> 469
            27 -> 457
            28 -> 420
            29 -> 422
            30 -> 449
            31 -> 427
            32 -> 429
            33 -> 440
            34 -> 453
            35 -> 432

            else -> -1
        }
        if (categoryId != -1) {
            postViewModel.currentCategoryTitle = selectedCategory

            */
/*progressDialog = ProgressDialogFragment()
            progressDialog?.show(childFragmentManager, "loadingDialog")*//*

           // postViewModel.resetPosts()

            postViewModel.fetchPostsByCategory(categoryId)
        } else {
            Toast.makeText(context, "Invalid category ID", Toast.LENGTH_SHORT).show()
        }
    }
}*/
