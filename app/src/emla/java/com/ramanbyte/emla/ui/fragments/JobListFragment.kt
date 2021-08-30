package com.ramanbyte.emla.ui.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentJobListBinding
import com.ramanbyte.emla.adapters.JobListAdapter
import com.ramanbyte.emla.view_model.JobsViewModel
import com.ramanbyte.utilities.*

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

        /*setFragmentResultListener("requestKey") { key, bundle ->
            // We use a String here, but any type that can be put in a Bundle is supported
            val result = bundle.getString("bundleKey")
            // Do something with the result...
            AppLog.infoLog("result")
        }*/
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

        layoutBinding.apply {
            edtJobSearch.apply {
                addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(searckKey: Editable?) {
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(
                        searckKey: CharSequence?,
                        p1: Int,
                        p2: Int,
                        p3: Int
                    ) {
                        imageViewSearchClear.visibility = if (searckKey!!.isNotEmpty()) {
                            View.VISIBLE
                        } else {
                            View.INVISIBLE
                        }
                    }
                })

                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        val searchStr = edtJobSearch.text.toString()
                        if (searchStr.isNotEmpty()) {
                            viewModel.apply {
                                viewModel.searchJobQuery.postValue(searchStr)
                            }
                        }
                        true
                    } else false
                }

            }

            imageViewSearchClear.apply {
                setOnClickListener(View.OnClickListener {
                    edtJobSearch.setText(KEY_BLANK)
                    viewModel.apply {
                        searchJobQuery.postValue(KEY_BLANK)
                    }
                })

            }

        }
    }

    override fun onResume() {
        super.onResume()
        AppLog.infoLog("JobListFragment onResume")
        activity!!.registerReceiver(
            cardUpdateReceiver,
            IntentFilter().apply { addAction(KEY_ACTION_UPDATE_CARD) })
    }

    override fun onStop() {
        super.onStop()
        AppLog.infoLog("JobListFragment onStop")
    }

    override fun onStart() {
        super.onStart()
        AppLog.infoLog("JobListFragment onStart")
    }

    override fun onDestroy() {
        try {
            if (cardUpdateReceiver != null)
                activity!!.unregisterReceiver(cardUpdateReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
        super.onDestroy()
        AppLog.infoLog("JobListFragment onDestroy")
    }

    private var cardUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == KEY_ACTION_UPDATE_CARD) {
                val position = intent.getIntExtra(KEY_UPDATE_POSITION, 0)
                jobListAdapter!!.updateCardOnPosition(
                    position,
                    viewModel.updateCardLiveData.value?.apply { isJobApplied = 1 }!!
                )
            }
        }
    }
}