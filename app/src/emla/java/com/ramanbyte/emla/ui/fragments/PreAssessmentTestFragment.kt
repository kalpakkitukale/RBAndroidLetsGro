package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentPreAssessmentTestBinding
import com.ramanbyte.emla.view_model.ShowQuestionsViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.ProgressLoader

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 14/04/20
 */
class PreAssessmentTestFragment :
    BaseFragment<FragmentPreAssessmentTestBinding, ShowQuestionsViewModel>() {

    private var mContext: Context? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pre_assessment_test, container, false)
    }

    override val viewModelClass: Class<ShowQuestionsViewModel> = ShowQuestionsViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_pre_assessment_test

    override fun initiate() {
        ProgressLoader(mContext!!,viewModel)
        AlertDialog(mContext!!,viewModel)

        layoutBinding.apply {
            lifecycleOwner = this@PreAssessmentTestFragment
        }

        val fragment = QuizInstructionFragment()
        val transaction: FragmentTransaction = fragmentManager?.beginTransaction()!!
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()

    }

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

}
