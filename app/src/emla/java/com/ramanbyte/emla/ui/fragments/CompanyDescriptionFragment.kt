package com.ramanbyte.emla.ui.fragments

import android.content.Context
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCompanyDescriptionBinding
import com.ramanbyte.emla.adapters.ViewPagerAdapter
import com.ramanbyte.emla.models.response.JobModel
import com.ramanbyte.emla.view_model.JobsViewModel
import com.ramanbyte.utilities.*
import kotlinx.android.synthetic.emla.fragment_company_description.*

class CompanyDescriptionFragment :
    BaseFragment<FragmentCompanyDescriptionBinding, JobsViewModel>() {

    var mContext: Context? = null
    var jobId: Int? = 0
    var isJobApplied: Int? = 0

    private var viewPagerAdapter: ViewPagerAdapter? = null

    override val viewModelClass: Class<JobsViewModel> = JobsViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_company_description

    override fun initiate() {
        ProgressLoader(context!!, viewModel)
        jobId = arguments?.getInt(KEY_JOB_ID) ?: 0
        isJobApplied = arguments?.getInt(KEY_IS_JOB_APPLIED) ?: 0
        layoutBinding.apply {
            lifecycleOwner = this@CompanyDescriptionFragment
            jobsViewModel = viewModel
        }

        viewModelOps()
    }

    private fun viewModelOps() {
        viewModel.apply {
            getJobDetails(jobId!!)
            companyDescriptionLiveData.observe(this@CompanyDescriptionFragment, Observer {
                if (it != null) {
                    layoutBinding.jobModel = it
                    setUpViewPager(it)
                }
            })
        }
    }

    private fun setUpViewPager(it: JobModel) {
        viewPagerAdapter = ViewPagerAdapter(
            childFragmentManager,
            FragmentStatePagerAdapter.POSITION_UNCHANGED
        )

        viewPagerAdapter?.addFragmentView(CompanyJobDetailFragment.getInstance(), "")
        viewPagerAdapter?.addFragmentView(AboutCompanyFragment(), "")
        companyViewPager.apply {
            //set View pager here
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}