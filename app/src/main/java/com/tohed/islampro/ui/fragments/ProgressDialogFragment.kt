package com.tohed.islampro.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.tohed.islampro.R

class ProgressDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_progress, container, false)
    }

    override fun onStart() {
        super.onStart()
        // Make dialog non-cancelable
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)

        // Set the width and height of the dialog
        dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Set the dialog's background to transparent
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
