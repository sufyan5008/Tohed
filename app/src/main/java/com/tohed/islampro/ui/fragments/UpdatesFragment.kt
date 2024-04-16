package com.tohed.islampro.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tohed.islampro.adapters.UpdatesAdapter
import com.tohed.islampro.databinding.FragmentUpdatesBinding

class UpdatesFragment : Fragment() {

    private lateinit var binding: FragmentUpdatesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentUpdatesBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding.dates.adapter = TimingAdapter()
        binding.updatesList.adapter = UpdatesAdapter()
    }

}