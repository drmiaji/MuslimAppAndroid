package com.asiradnan.muslimapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class TaskDetailFragment : Fragment(R.layout.fragment_task_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = arguments?.getString("title")
        val detail = arguments?.getString("detail")
        val header: TextView? = view?.findViewById(R.id.task_detail_header)
        val detailview: TextView? = view?.findViewById(R.id.task_detail_detail)
        header?.text = title
        detailview?.text = detail
    }
}