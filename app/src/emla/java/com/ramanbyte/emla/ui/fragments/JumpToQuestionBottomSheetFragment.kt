package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ramanbyte.R
import com.ramanbyte.databinding.JumpToQuestionBinding
import com.ramanbyte.emla.models.QuestionAndAnswerModel
import com.ramanbyte.emla.view_model.ShowQuestionsViewModel
import com.ramanbyte.utilities.KEY_QUESTION_MODEL
import com.ramanbyte.utilities.keyQuestionId


class JumpToQuestionBottomSheetFragment : BottomSheetDialogFragment() {

    var mContext: Context? = null
    var jumpToQuestionBinding: JumpToQuestionBinding? = null
    var viewModel: ShowQuestionsViewModel? = null
    //var jumpToQueAdapter: JumpToQueAdapter? = null
    var questionModelDataList = ArrayList<QuestionAndAnswerModel>()

    var questionId = 0

    companion object {

        fun getInstance(questionList: ArrayList<QuestionAndAnswerModel>): JumpToQuestionBottomSheetFragment {

            val jumpToQuestionBottomSheetFragment = JumpToQuestionBottomSheetFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(KEY_QUESTION_MODEL, questionList)
            jumpToQuestionBottomSheetFragment.arguments = bundle

            return jumpToQuestionBottomSheetFragment
        }

        fun get(questionId: Int): JumpToQuestionBottomSheetFragment =
            JumpToQuestionBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putInt(keyQuestionId, questionId)
                }
            }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.jump_to_question, container, false)
    }

}
