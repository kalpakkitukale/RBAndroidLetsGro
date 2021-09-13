package com.ramanbyte.emla.ui.fragments

import android.os.Bundle
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentAboutCompanyBinding
import com.ramanbyte.emla.view_model.JobsViewModel
import com.ramanbyte.utilities.KEY_COMPANY_DETAILS
import com.ramanbyte.utilities.ProgressLoader

class AboutCompanyFragment :
    BaseFragment<FragmentAboutCompanyBinding, JobsViewModel>(hasOptionsMenu = false, useParent = true) {

    override val viewModelClass: Class<JobsViewModel> = JobsViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_about_company

    var companyDetails: String = ""

    override fun initiate() {
        ProgressLoader(requireContext(), viewModel)

        arguments?.let {
            companyDetails = it.getString(KEY_COMPANY_DETAILS)!!
        }

        layoutBinding.apply {
            lifecycleOwner = this@AboutCompanyFragment
            jobsViewModel = viewModel
            companyDetail = companyDetails
        }
    }

    companion object {
        fun getInstance(companyDetail: String): AboutCompanyFragment {
            val aboutCompanyFragment = AboutCompanyFragment()
            val bundle = Bundle()
            bundle.putString(KEY_COMPANY_DETAILS, companyDetail)
            aboutCompanyFragment.arguments = bundle
            return aboutCompanyFragment
        }
    }
}