package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentPreAssessmentTestBinding
import com.ramanbyte.emla.models.ChapterModel
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.view_model.ShowQuestionsViewModel
import com.ramanbyte.utilities.*

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 14/04/20
 */
class PreAssessmentTestFragment :
    BaseFragment<FragmentPreAssessmentTestBinding, ShowQuestionsViewModel>() {

    private var mContext: Context? = null
    private var courseModel: CoursesModel? = null
    private var chapterModel: ChapterModel? = null

    override val viewModelClass: Class<ShowQuestionsViewModel> = ShowQuestionsViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_pre_assessment_test

    override fun initiate() {

        arguments?.apply {
            courseModel = getParcelable(KEY_COURSE_MODEL)!!
            chapterModel = getParcelable(KEY_CHAPTER_MODEL)
            viewModel.testType = getInt(keyTestType, 0)
        }

        viewModel.apply {
            coursesModelLiveData.value = courseModel
            chapterModelLiveData.value = chapterModel
        }
        ProgressLoader(mContext!!, viewModel)
        AlertDialog(mContext!!, viewModel)

        layoutBinding.apply {
            lifecycleOwner = this@PreAssessmentTestFragment
        }
        setHasOptionsMenu(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

}
