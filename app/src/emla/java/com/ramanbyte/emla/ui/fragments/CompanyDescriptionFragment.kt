package com.ramanbyte.emla.ui.fragments

import android.view.View
import android.content.Intent
import androidx.databinding.ObservableInt
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCompanyDescriptionBinding
import com.ramanbyte.emla.adapters.ViewPagerAdapter
import com.ramanbyte.emla.view_model.JobsViewModel
import com.ramanbyte.utilities.*
import kotlinx.android.synthetic.emla.fragment_company_description.*

class CompanyDescriptionFragment :
    BaseFragment<FragmentCompanyDescriptionBinding, JobsViewModel>() {

    var jobId: Int? = null
    var position: Int? = null

    var isJobApplied: ObservableInt = ObservableInt(0)

    private var viewPagerAdapter: ViewPagerAdapter? = null

    override val viewModelClass: Class<JobsViewModel> = JobsViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_company_description

    override fun initiate() {
        ProgressLoader(requireContext(), viewModel)
        AlertDialog(requireContext(), viewModel)

        if (arguments != null) {
            jobId = arguments?.getInt(KEY_JOB_ID)
            position = arguments?.getInt(KEY_UPDATE_POSITION)
            isJobApplied.set(arguments?.getInt(KEY_IS_JOB_APPLIED) ?: 0)

        }

        layoutBinding.apply {
            lifecycleOwner = this@CompanyDescriptionFragment
            jobsViewModel = viewModel
            isJobApplied = this@CompanyDescriptionFragment.isJobApplied
        }

        viewModelOps()

        viewModel.toggleLayoutVisibility(View.GONE, View.GONE, View.GONE, "", View.GONE)

        jobId?.let { safeId ->
            viewModel.getJobDetails(safeId)
        }

    }

    private fun viewModelOps() {
        viewModel.apply {

            companyDescriptionLiveData.observe(viewLifecycleOwner, Observer { jobModel ->

                jobModel?.apply {
                    isJobApplied = isJobApplied
                    companyImageURL =
                        StaticMethodUtilitiesKtx.getS3DynamicURL(
                            companyLogo ?: KEY_BLANK,
                            context!!
                        )
                    setUpViewPager()
                }
            })

            applyJobResponseModelLiveData.observe(
                viewLifecycleOwner,
                Observer { applyJobResponseModel ->

                    if (applyJobResponseModel != null) {

                        val applyJobFlag = applyJobResponseModel.flag

                        setAlertDialogResourceModelMutableLiveData(
                            message = applyJobResponseModel.message,
                            alertDrawableResource = BindingUtils.drawable(R.drawable.ic_success),
                            isInfoAlert = true,
                            positiveButtonText = BindingUtils.string(R.string.strOk),
                            positiveButtonClickFunctionality = {
                                isAlertDialogShown.postValue(false)
                                isJobApplied.set(applyJobFlag)
                                val intent = Intent(KEY_ACTION_UPDATE_CARD)
                                intent.putExtra(KEY_UPDATE_POSITION, position)
                                activity!!.sendBroadcast(intent)
                                /*setFragmentResult("requestKey", Bundle().apply {
                                    putString("bundleKey", "sucesss")
                                })*/
                            })
                        isAlertDialogShown.postValue(true)

                    } else {
                        setAlertDialogResourceModelMutableLiveData(
                            message = BindingUtils.string(R.string.some_thing_went_wrong),
                            alertDrawableResource = BindingUtils.drawable(R.drawable.ic_fail),
                            isInfoAlert = true,
                            positiveButtonText = BindingUtils.string(R.string.strOk),
                            positiveButtonClickFunctionality = {
                                isAlertDialogShown.postValue(false)
                            })
                        isAlertDialogShown.postValue(true)
                    }

                })

        }

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

    override fun onResume() {
        super.onResume()
        AppLog.infoLog("CompanyDescriptionFragment onResume")
    }

    override fun onStop() {
        super.onStop()
        AppLog.infoLog("CompanyDescriptionFragment onStop")
    }

    override fun onStart() {
        super.onStart()
        AppLog.infoLog("CompanyDescriptionFragment onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        AppLog.infoLog("CompanyDescriptionFragment onDestroy")
    }

}