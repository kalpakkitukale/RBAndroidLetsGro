package com.ramanbyte.emla.ui.fragments

import android.os.Bundle
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCompanyJobDetailBinding
import com.ramanbyte.emla.view_model.JobsViewModel
import com.ramanbyte.utilities.KEY_JOB_DETAILS
import com.ramanbyte.utilities.ProgressLoader

class CompanyJobDetailFragment :
    BaseFragment<FragmentCompanyJobDetailBinding, JobsViewModel>(hasOptionsMenu = false, useParent = true) {

    override val viewModelClass: Class<JobsViewModel> = JobsViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_company_job_detail

    var jobDetails: String = ""

    override fun initiate() {
        ProgressLoader(requireContext(), viewModel)

        arguments?.let {
            jobDetails = it.getString(KEY_JOB_DETAILS)!!
        }

        layoutBinding.apply {
            lifecycleOwner = this@CompanyJobDetailFragment
            jobsViewModel = viewModel
            webViewJobDescription.settings.apply {
                javaScriptEnabled = true
                displayZoomControls = false
            }
            jobDetail = jobDetails
        }
    }

    companion object {
        fun getInstance(jobDetail: String): CompanyJobDetailFragment {
            val companyJobDetailFragment = CompanyJobDetailFragment()
            val bundle = Bundle()
            bundle.putString(KEY_JOB_DETAILS, jobDetail)
            companyJobDetailFragment.arguments = bundle
            return companyJobDetailFragment
        }
    }

}