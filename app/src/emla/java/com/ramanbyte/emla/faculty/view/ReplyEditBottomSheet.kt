package com.ramanbyte.emla.faculty.view

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseBottomSheetFragment
import com.ramanbyte.databinding.ReplyEditBottomSheetBinding
import com.ramanbyte.emla.faculty.view_model.FacultyQuestionAnswerViewModel
import com.ramanbyte.emla.models.AskQuestionReplyModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.KEY_REPLY_MODEL



class ReplyEditBottomSheet : BaseBottomSheetFragment<ReplyEditBottomSheetBinding,FacultyQuestionAnswerViewModel>(false,true) {

    var replyModel: AskQuestionReplyModel? = null

    override val viewModelClass: Class<FacultyQuestionAnswerViewModel> = FacultyQuestionAnswerViewModel::class.java

    override fun layoutId(): Int = R.layout.reply_edit_bottom_sheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, 0)
    }

    companion object {
        fun get(
            replyModel: AskQuestionReplyModel
        ): ReplyEditBottomSheet =
            ReplyEditBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_REPLY_MODEL, replyModel)
                }
            }
    }

    override fun initiate() {
        layoutBinding.apply {
            lifecycleOwner = this@ReplyEditBottomSheet
            facultyQuestionAnswerViewModel = viewModel

            if (arguments != null) {
                replyModel = arguments?.getParcelable(KEY_REPLY_MODEL)
                reply = replyModel?.answer
            }

            viewModel.apply {
                onClickCloseBottomSheetLiveData.observe(this@ReplyEditBottomSheet, Observer {
                    if (it != null){
                        if (it == true){
                            dismiss()
                            onClickCloseBottomSheetLiveData.value = false
                        }
                    }
                })
            }

        }
    }


}