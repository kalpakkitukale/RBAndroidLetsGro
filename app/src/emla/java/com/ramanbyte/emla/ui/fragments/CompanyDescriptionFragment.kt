package com.ramanbyte.emla.ui.fragments

import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCompanyDescriptionBinding
import com.ramanbyte.emla.adapters.ViewPagerAdapter
import com.ramanbyte.emla.view_model.JobsViewModel
import com.ramanbyte.utilities.KEY_IS_JOB_APPLIED
import com.ramanbyte.utilities.KEY_JOB_ID
import com.ramanbyte.utilities.ProgressLoader
import kotlinx.android.synthetic.emla.fragment_company_description.*

class CompanyDescriptionFragment :
    BaseFragment<FragmentCompanyDescriptionBinding, JobsViewModel>() {

    var jobId: Int? = null

    var isJobApplied: Int? = 0

    private var viewPagerAdapter: ViewPagerAdapter? = null

    override val viewModelClass: Class<JobsViewModel> = JobsViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_company_description

    override fun initiate() {
        ProgressLoader(requireContext(), viewModel)

        if (arguments != null) {
            jobId = arguments?.getInt(KEY_JOB_ID)
            isJobApplied = arguments?.getInt(KEY_IS_JOB_APPLIED) ?: 0

        }

        layoutBinding.apply {
            lifecycleOwner = this@CompanyDescriptionFragment
            jobsViewModel = viewModel
            isJobApplied = this@CompanyDescriptionFragment.isJobApplied
        }

        viewModelOps()

        jobId?.let { safeId ->
            viewModel.getJobDetails(safeId)
            viewModel.applyJob(safeId)
        }

    }

    private fun viewModelOps() {
        viewModel.companyDescriptionLiveData.observe(viewLifecycleOwner, Observer { jobModel ->

            jobModel?.apply {
                jobModel.isJobApplied = isJobApplied
                setUpViewPager()
            }
        })
    }

    private fun setUpViewPager() {
        viewPagerAdapter = ViewPagerAdapter(
            childFragmentManager,
            FragmentStatePagerAdapter.POSITION_UNCHANGED
        )

        viewPagerAdapter?.addFragmentView(CompanyJobDetailFragment.getInstance(), "Job Details")
        viewPagerAdapter?.addFragmentView(AboutCompanyFragment(), "About Company")
        layoutBinding.companyViewPager.apply {
            //set View pager here
            adapter = viewPagerAdapter

            layoutBinding.tabCompanyDescription.setupWithViewPager(this)
        }
    }

}