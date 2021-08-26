package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
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
                layoutManager = GridLayoutManager(mContext, 3)
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
        layoutBinding.apply {
            edtSkillSearch.apply {
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
                        val searchStr = edtSkillSearch.text.toString()
                        if (searchStr.isNotEmpty()) {
                            viewModel.apply {
                                viewModel.searchQuery.postValue(searchStr)
                            }
                        }
                        true
                    } else false
                }

            }

            imageViewSearchClear.apply {
                setOnClickListener(View.OnClickListener {
                    edtSkillSearch.setText(KEY_BLANK)
                    viewModel.apply {
                        searchQuery.postValue(KEY_BLANK)
                    }
                })

            }

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}