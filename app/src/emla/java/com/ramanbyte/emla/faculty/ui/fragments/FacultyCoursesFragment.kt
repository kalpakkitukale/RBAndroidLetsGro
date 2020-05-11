package com.ramanbyte.emla.faculty.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentFacultyCoursesBinding
import com.ramanbyte.emla.adapters.CoursesAdapter
import com.ramanbyte.emla.faculty.adapter.FacultyCoursesAdapter
import com.ramanbyte.emla.faculty.view_model.FacultyCoursesViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.ProgressLoader
import com.ramanbyte.utilities.displayMetrics

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 08/05/2020
 */
class FacultyCoursesFragment : BaseFragment<FragmentFacultyCoursesBinding,FacultyCoursesViewModel>() {

    var mContext : Context? = null
    private var coursesAdapter: FacultyCoursesAdapter? = null

    override val viewModelClass: Class<FacultyCoursesViewModel> = FacultyCoursesViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_faculty_courses

    override fun initiate() {

        setToolbarTitle(BindingUtils.string(R.string.courses))

        ProgressLoader(mContext!!, viewModel)
        AlertDialog(mContext!!, viewModel)

        layoutBinding.apply {
            lifecycleOwner = this@FacultyCoursesFragment
            facultyCoursesViewModel = viewModel
            noInternet.viewModel = viewModel
            noData.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel

            setAdapter()
            setViewModelOp()
        }
    }

    private fun setAdapter() {
        layoutBinding.apply {
            rvCoursesFragment.apply {
                coursesAdapter = FacultyCoursesAdapter()
                layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                adapter = coursesAdapter?.apply {
                    this.context = mContext
                    this.coursesViewModel = viewModel
                }
            }
        }
    }

    private fun setViewModelOp() {
        viewModel.apply {
            initPaginationResponseHandler()
            coursesPagedList()?.observe(this@FacultyCoursesFragment, androidx.lifecycle.Observer {
                it?.let { coursesAdapter?.apply { submitList(it) } }
            })
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

}
