package com.ramanbyte.emla.ui.fragments

import android.content.Context
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentAboutCompanyBinding
import com.ramanbyte.emla.view_model.JobsViewModel
import com.ramanbyte.utilities.ProgressLoader

class AboutCompanyFragment :
    BaseFragment<FragmentAboutCompanyBinding, JobsViewModel>(hasOptionsMenu = false) {


    var mContext: Context? = null

    override val viewModelClass: Class<JobsViewModel> = JobsViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_about_company

    override fun initiate() {
        ProgressLoader(context!!, viewModel)

        layoutBinding.apply{
            lifecycleOwner = this@AboutCompanyFragment
            jobsViewModel = viewModel
        }
    }

    companion object {
        fun getInstance() = AboutCompanyFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}