package com.ramanbyte.emla.ui.fragments

import android.content.Context
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCompanyJobDetailBinding
import com.ramanbyte.emla.view_model.JobsViewModel
import com.ramanbyte.utilities.KEY_JOB_ID
import com.ramanbyte.utilities.ProgressLoader

class CompanyJobDetailFragment :
    BaseFragment<FragmentCompanyJobDetailBinding, JobsViewModel>(hasOptionsMenu = false) {

    var mContext: Context? = null

    var jobId: Int? = null

    override val viewModelClass: Class<JobsViewModel> = JobsViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_company_job_detail

    override fun initiate() {
        ProgressLoader(context!!, viewModel)

        if (arguments != null) {
            jobId = arguments?.getInt(KEY_JOB_ID)
        }

        layoutBinding.apply{
            lifecycleOwner = this@CompanyJobDetailFragment
            jobsViewModel = viewModel
        }
    }

    companion object {
        fun getInstance() = CompanyJobDetailFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}