package com.ramanbyte.emla.faculty.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentStudentAskedQuestionsBinding
import com.ramanbyte.emla.faculty.adapter.FacultyCoursesAdapter
import com.ramanbyte.emla.faculty.adapter.StudentAskedQuestionsAdapter
import com.ramanbyte.emla.faculty.models.FacultyCoursesModel
import com.ramanbyte.emla.faculty.view_model.StudentAskedQuestionsViewModel
import com.ramanbyte.utilities.*

/**
 * A simple [Fragment] subclass.
 */
class StudentAskedQuestionsFragment :
    BaseFragment<FragmentStudentAskedQuestionsBinding, StudentAskedQuestionsViewModel>() {

    var mContext: Context? = null
    private var courseModel: FacultyCoursesModel? = null
    private var studentAskedQuestionsAdapter: StudentAskedQuestionsAdapter? = null

    override val viewModelClass: Class<StudentAskedQuestionsViewModel> =
        StudentAskedQuestionsViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_student_asked_questions

    override fun initiate() {

        arguments?.apply {
            courseModel = getParcelable(KEY_COURSE_MODEL)!!
            AppLog.infoLog("courseModel ${courseModel?.courseId}")
        }

        setToolbarTitle(BindingUtils.string(R.string.questions))

        ProgressLoader(mContext!!, viewModel)
        AlertDialog(mContext!!, viewModel)

        layoutBinding.apply {
            lifecycleOwner = this@StudentAskedQuestionsFragment
            studentAskedQuestionsViewModel = viewModel
            noInternet.viewModel = viewModel
            noData.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel
        }
        setAdapter()
        setViewModelOp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setAdapter() {
        layoutBinding.apply {
            rvAskQuestion.apply {
                studentAskedQuestionsAdapter = StudentAskedQuestionsAdapter()
                layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                adapter = studentAskedQuestionsAdapter?.apply {
                    this.context = mContext
                    this.studentAskedQuestionsViewModel = viewModel
                }
            }
        }
    }

    private fun setViewModelOp() {
        viewModel.apply {
            initPaginationResponseHandler()
            coursesPagedList()?.observe(this@StudentAskedQuestionsFragment, androidx.lifecycle.Observer {
                it?.let { studentAskedQuestionsAdapter?.apply { submitList(it) } }
            })
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

}
