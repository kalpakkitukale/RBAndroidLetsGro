package com.ramanbyte.emla.view

import android.content.Context
import com.ramanbyte.R
import com.ramanbyte.base.BaseBottomSheetFragment
import com.ramanbyte.databinding.RecommendedCourseFilterBottomsheetBinding
import com.ramanbyte.emla.view_model.CoursesViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.ProgressLoader
import java.lang.Exception

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 16-04-2020
 */
class RecommendedCourseFilterBottomSheet :  BaseBottomSheetFragment<RecommendedCourseFilterBottomsheetBinding, CoursesViewModel>(
    isActivityParent = false,
    useParent = true
){
    lateinit var mContext: Context

    override val viewModelClass: Class<CoursesViewModel> = CoursesViewModel::class.java

    override fun layoutId(): Int = R.layout.recommended_course_filter_bottomsheet

    override fun initiate() {
        try{
            layoutBinding.apply {
                lifecycleOwner = this@RecommendedCourseFilterBottomSheet
                ProgressLoader(context!!, viewModel)
                AlertDialog(context!!, viewModel)

                ivCloseFilter.setOnClickListener {
                    dismiss()
                }

            }
        }catch (e:Exception){
            e.printStackTrace()
            AppLog.errorLog(e.message,e)
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

}