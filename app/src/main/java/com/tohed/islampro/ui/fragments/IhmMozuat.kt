package com.tohed.islampro.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tohed.islampro.R
import com.tohed.islampro.databinding.FragmentCategoryBinding
import com.tohed.islampro.databinding.FragmentIhmMozuatBinding
import com.tohed.islampro.viewModel.IhmMozuatViewModel

class IhmMozuat : Fragment() {

    private lateinit var binding: FragmentIhmMozuatBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIhmMozuatBinding.inflate(inflater, container, false)
        return binding.root;
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}