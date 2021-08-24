package com.ramanbyte.emla.ui.fragments

import android.content.Context
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentSkillListBinding
import com.ramanbyte.emla.adapters.SkillsListAdapter
import com.ramanbyte.emla.view_model.JobSkillsViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.ProgressLoader

class SkillListFragment :
    BaseFragment<FragmentSkillListBinding, JobSkillsViewModel>(hasOptionsMenu = true) {
    override val viewModelClass: Class<JobSkillsViewModel> = JobSkillsViewModel::class.java

    private var skillsListAdapter: SkillsListAdapter? = null

    var mContext: Context? = null

    override fun layoutId(): Int =
        R.layout.fragment_skill_list


    override fun initiate() {
        ProgressLoader(context!!, viewModel)
        AlertDialog(context!!, viewModel)

        layoutBinding.apply {
            setToolbarTitle("")
            lifecycleOwner = this@SkillListFragment
            skillsViewModel = viewModel
            noData.viewModel = viewModel
            noInternet.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel
        }

        setAdapter()
        viewModelOps()
    }

    private fun setAdapter() {
        layoutBinding.apply {

            rvSkill.apply {
                skillsListAdapter = SkillsListAdapter()
                adapter = skillsListAdapter.apply {
                    this!!.context = mContext
                    this.jobSkillsViewModel = viewModel
                }
            }
        }
    }

    private fun viewModelOps() {
        viewModel.apply {
            getSkillsList(KEY_BLANK)

            getSkillsList()!!.observe(this@SkillListFragment, Observer {
                skillsListAdapter?.submitList(it)
            })
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}