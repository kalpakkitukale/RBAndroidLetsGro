package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCoursesBinding
import com.ramanbyte.emla.adapters.CoursesAdapter
import com.ramanbyte.emla.view_model.CoursesViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.ProgressLoader
import com.ramanbyte.utilities.displayMetrics

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */
class CoursesFragment : BaseFragment<FragmentCoursesBinding, CoursesViewModel>() {
    private lateinit var mContext: Context
    private var coursesAdapter: CoursesAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_courses, container, false)
    }

    override val viewModelClass: Class<CoursesViewModel>
        get() = CoursesViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_courses

    override fun initiate() {
        layoutBinding.apply {
            lifecycleOwner = this@CoursesFragment
            ProgressLoader(mContext, viewModel)
            AlertDialog(mContext, viewModel)
            coursesViewModel = viewModel
            noInternet.viewModel = viewModel
            noData.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel

        }
        setAdapter()
        setViewModelOp()
    }

    private fun setAdapter() {
        layoutBinding.apply {
            rvCoursesFragment.apply {
                coursesAdapter = CoursesAdapter((activity!!).displayMetrics())
                layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                adapter = coursesAdapter?.apply {
                    this.context = mContext
                    viewModel = this@CoursesFragment.viewModel
                }
            }
        }
    }

    private fun setViewModelOp() {
        viewModel.apply {
            initPaginationResponseHandler()

            coursesPagedList()?.observe(this@CoursesFragment, androidx.lifecycle.Observer {
                coursesAdapter?.apply { submitList(it) }
            })

            selectedCourseModelLiveData.observe(
                this@CoursesFragment, Observer {

                })
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}
