package com.ramanbyte.emla.ui.fragments

import android.content.Context
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentJobListBinding
import com.ramanbyte.emla.adapters.JobListAdapter
import com.ramanbyte.emla.view_model.JobsViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.KEY_SKILL_ID
import com.ramanbyte.utilities.ProgressLoader

class JobListFragment :
    BaseFragment<FragmentJobListBinding, JobsViewModel>(hasOptionsMenu = false) {
    override val viewModelClass: Class<JobsViewModel> = JobsViewModel::class.java

    private var jobListAdapter: JobListAdapter? = null

    var mContext: Context? = null

    override fun layoutId(): Int =
        R.layout.fragment_job_list

    var skillId: Int = 0

    override fun initiate() {
        skillId = arguments?.getInt(KEY_SKILL_ID) ?: 0

        ProgressLoader(context!!, viewModel)
        AlertDialog(context!!, viewModel)

        layoutBinding.apply {
            setToolbarTitle("")
            lifecycleOwner = this@JobListFragment
            jobsViewModel = viewModel
            noData.viewModel = viewModel
            noInternet.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel
        }

        setAdapter()
        viewModelOps()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun setAdapter() {
        layoutBinding.apply {

            rvSkill.apply {
                jobListAdapter = JobListAdapter()
                adapter = jobListAdapter.apply {
                    this!!.context = mContext
                    this.jobsViewModel = viewModel
                }
            }
        }
    }

    private fun viewModelOps() {
        viewModel.apply {
            loadJobsList(skillId)

            getJobsList()!!.observe(this@JobListFragment, Observer {
                jobListAdapter?.submitList(it)
            })
        }
    }

}