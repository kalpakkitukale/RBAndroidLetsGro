package com.ramanbyte.emla.faculty.view

import android.content.Context
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseBottomSheetFragment
import com.ramanbyte.databinding.StudentAskedQuestionsBottomSheetBinding
import com.ramanbyte.emla.faculty.view_model.StudentAskedQuestionsViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.ProgressLoader

class StudentAskedQuestionsBottomSheet: BaseBottomSheetFragment<StudentAskedQuestionsBottomSheetBinding,StudentAskedQuestionsViewModel>(isActivityParent = false,
    useParent = true) {

    var mContext: Context? = null

    override val viewModelClass: Class<StudentAskedQuestionsViewModel> = StudentAskedQuestionsViewModel::class.java

    override fun layoutId(): Int = R.layout.student_asked_questions_bottom_sheet

    override fun initiate() {
        layoutBinding.apply {
            lifecycleOwner = this@StudentAskedQuestionsBottomSheet
            studentAskedQuestionsViewModel = viewModel
            ProgressLoader(mContext!!, viewModel)
            AlertDialog(mContext!!, viewModel)

            viewModel.apply {


                onClickCloseBottomSheetLiveData.observe(this@StudentAskedQuestionsBottomSheet, Observer {
                    if (it != null){
                        if (it == true){
                            AppLog.infoLog("onClickCloseBottomSheetLiveData")
                            dismiss()
                            onClickCloseBottomSheetLiveData.value = false
                        }
                    }
                })

                onClickApplyFilterLiveData.observe(this@StudentAskedQuestionsBottomSheet, Observer {
                    if (it != null){
                        if (it == true){
                            AppLog.infoLog("onClickApplyFilterLiveData")
                            dismiss()
                            onClickApplyFilterLiveData.value = false
                        }
                    }
                })

                onClickClearFilterLiveData.observe(this@StudentAskedQuestionsBottomSheet, Observer {
                    if (it != null){
                        if (it == true){
                            AppLog.infoLog("onClickClearFilterLiveData")
                            onClickClearFilterLiveData.value = false
                        }
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