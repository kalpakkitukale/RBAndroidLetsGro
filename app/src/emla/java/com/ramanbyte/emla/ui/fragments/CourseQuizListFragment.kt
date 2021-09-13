package com.ramanbyte.emla.ui.fragments

import android.content.Context
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseParentFragment
import com.ramanbyte.databinding.CourseQuizListFragmentBinding
import com.ramanbyte.emla.adapters.CourseQuizListAdapter
import com.ramanbyte.emla.view_model.CourseQuizListViewModel
import com.ramanbyte.emla.view_model.CoursesDetailViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.ProgressLoader

class CourseQuizListFragment :
    BaseParentFragment<CourseQuizListFragmentBinding, CourseQuizListViewModel, CoursesDetailViewModel>(
        hasOptionsMenu = false
    ) {

    companion object {
        fun newInstance() = CourseQuizListFragment()
    }

    var mContext: Context? = null

    private var courseQuizListAdapter: CourseQuizListAdapter? = null

    override val viewModelClass: Class<CourseQuizListViewModel> =
        CourseQuizListViewModel::class.java

    override val parentViewModelClass: Class<CoursesDetailViewModel> =
        CoursesDetailViewModel::class.java

    override fun layoutId(): Int = R.layout.course_quiz_list_fragment

    override fun initiateView() {

        viewModel.courseModel = parentViewModel.coursesModelLiveData.value

        ProgressLoader(context!!, viewModel)
        AlertDialog(context!!, viewModel)

        layoutBinding?.apply {

            lifecycleOwner = this@CourseQuizListFragment
            courseDetailViewModel = parentViewModel
            courseQuizViewModel = viewModel
            noData.viewModel = viewModel
            noInternet.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel

        }

        setAdapter()
        viewModelOps()
    }

    private fun setAdapter() {
        layoutBinding.apply {

            rvCourseQuiz.apply {
                courseQuizListAdapter = CourseQuizListAdapter()
                adapter = courseQuizListAdapter.apply {
                    this!!.context = mContext
                    this.courseQuizListViewModel = viewModel
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onResume() {
        super.onResume()
        viewModel.apply {
            setToolbarTitle(courseModel?.courseName!!)
        }
    }

    private fun viewModelOps() {
        viewModel.apply {
            getCourseQuizList()

            quizListForCoursePagedList()!!.observe(this@CourseQuizListFragment, Observer {
                courseQuizListAdapter?.submitList(it)
            })
        }
    }


}