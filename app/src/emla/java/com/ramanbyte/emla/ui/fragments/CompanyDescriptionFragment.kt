package com.ramanbyte.emla.ui.fragments

import android.content.Context
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCompanyDescriptionBinding
import com.ramanbyte.emla.adapters.ViewPagerAdapter
import com.ramanbyte.emla.view_model.JobsViewModel
import com.ramanbyte.utilities.ProgressLoader
import kotlinx.android.synthetic.emla.fragment_company_description.*

class CompanyDescriptionFragment: BaseFragment<FragmentCompanyDescriptionBinding, JobsViewModel>(useParent = true) {


    var mContext: Context? = null

    private var viewPagerAdapter: ViewPagerAdapter? = null

    override val viewModelClass: Class<JobsViewModel> = JobsViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_company_description

    override fun initiate() {
        ProgressLoader(context!!, viewModel)

       layoutBinding.apply{
            lifecycleOwner = this@CompanyDescriptionFragment
            jobsViewModel = viewModel
        }
    }

    private fun setUpViewPager(it: JobsViewModel){
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