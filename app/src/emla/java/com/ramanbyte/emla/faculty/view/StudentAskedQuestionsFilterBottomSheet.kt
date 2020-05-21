package com.ramanbyte.emla.faculty.view

import android.content.Context
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseBottomSheetFragment
import com.ramanbyte.databinding.StudentAskedQuestionsBottomSheetBinding
import com.ramanbyte.emla.faculty.view_model.StudentAskedQuestionsViewModel
import com.ramanbyte.utilities.*

class StudentAskedQuestionsFilterBottomSheet: BaseBottomSheetFragment<StudentAskedQuestionsBottomSheetBinding,StudentAskedQuestionsViewModel>(isActivityParent = false,
    useParent = true) {

    var mContext: Context? = null

    override val viewModelClass: Class<StudentAskedQuestionsViewModel> = StudentAskedQuestionsViewModel::class.java

    override fun layoutId(): Int = R.layout.student_asked_questions_bottom_sheet

    override fun initiate() {
        layoutBinding.apply {
            lifecycleOwner = this@StudentAskedQuestionsFilterBottomSheet
            studentAskedQuestionsViewModel = viewModel
            ProgressLoader(mContext!!, viewModel)
            AlertDialog(mContext!!, viewModel)

            setDataToFilter()

            viewModel.apply {


                onClickCloseBottomSheetLiveData.observe(this@StudentAskedQuestionsFilterBottomSheet, Observer {
                    if (it != null){
                        if (it == true){
                            dismiss()
                            onClickCloseBottomSheetLiveData.value = false
                        }
                    }
                })

                onClickApplyFilterLiveData.observe(this@StudentAskedQuestionsFilterBottomSheet, Observer {
                    if (it != null){
                        if (it == true){
                            dismiss()
                            onClickApplyFilterLiveData.value = false
                        }
                    }
                })

                onClickClearFilterLiveData.observe(this@StudentAskedQuestionsFilterBottomSheet, Observer {
                    if (it != null){
                        if (it == true){
                            //-------- Ans Type
                            cpUnanswered.isChecked = true
                            ansTypeLiveData.value = KEY_N

                            onClickClearFilterLiveData.value = false
                        }
                    }
                })

                ansTypeLiveData.observe(this@StudentAskedQuestionsFilterBottomSheet,
                    Observer {
                        it?.apply {
                            if (ansTypeLiveData.value != KEY_BLANK) {
                                if (cgAnsType.checkedChipId == -1) {
                                    ansTypeLiveData.value = KEY_N
                                    cpUnanswered.isChecked = true
                                }
                            }
                        }
                    })

            }

        }
    }

    private fun setDataToFilter() {

        viewModel.apply {
            layoutBinding.apply {
                //----------- Evaluation Status filter
                questionsRequestModelLiveData.value = questionsRequestModel.apply {
                    when (isQuestionAnswered) {
                        KEY_Y -> {
                            cpAnswered.isChecked = true
                            ansTypeLiveData.value = KEY_Y
                        }
                        KEY_N -> {
                            cpUnanswered.isChecked = true
                            ansTypeLiveData.value = KEY_N
                        }
                        else -> cgAnsType.clearCheck()
                    }
                }
            }
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}