package com.ramanbyte.emla.view

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ramanbyte.R
import com.ramanbyte.base.BaseBottomSheetFragment
import com.ramanbyte.databinding.CourseInformationBottomSheetLayoutBinding
import com.ramanbyte.emla.view_model.CoursesViewModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.StaticMethodUtilitiesKtx

/**
 * @author Akash Inkar <akash.1@ramanbyte.com>
 * @since 12/4/21
 */
class CourseInformationBottomSheet(var isActivityParent: Boolean, useParent: Boolean):BaseBottomSheetFragment<CourseInformationBottomSheetLayoutBinding, CoursesViewModel>
    (  isActivityParent = isActivityParent,
    useParent = useParent){

    override val viewModelClass: Class<CoursesViewModel> = CoursesViewModel::class.java

    override fun layoutId(): Int = R.layout.course_information_bottom_sheet_layout

    override fun initiate() {
        layoutBinding?.apply {
            lifecycleOwner = this@CourseInformationBottomSheet
            dataViewModel = viewModel
            fullBottomSheet()
            viewModelOperation()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, 0)
    }

    override fun onStart() {
        super.onStart()
        val window = dialog!!.window
        val windowParams = window!!.attributes
        windowParams.dimAmount = 0.70f
        windowParams.flags = windowParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        window.attributes = windowParams
    }

    private fun fullBottomSheet() {
        layoutBinding.apply {
            layoutMainContainer.viewTreeObserver.addOnGlobalLayoutListener {
                val dialog = dialog as BottomSheetDialog
                getDialog()?.window?.apply {
                    setSoftInputMode(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    setDimAmount(0.5f)
                }
                val bottomSheet =
                    dialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
                val mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
                val height = layoutMainContainer.height
                val displayMetrics = DisplayMetrics()
                requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
                var screenHeight = displayMetrics.heightPixels
                val statusBarHeight =
                    StaticMethodUtilitiesKtx.getStatusBarHeight(requireActivity())
                screenHeight -= statusBarHeight
                mBottomSheetBehavior.peekHeight = height
            }
        }
    }

    fun viewModelOperation(){
        viewModel?.apply {
            bottomSheetCloseLiveData.observe(this@CourseInformationBottomSheet, Observer {
                it?.let {
                    if (it)
                        dismiss()
                    bottomSheetCloseLiveData.postValue(false)
                }
            })

            courseSyllabusLiveData.observe(this@CourseInformationBottomSheet, Observer {
                it?.let {
                        layoutBinding?.dataList = it
                }
            })

            courseModelLive.observe(this@CourseInformationBottomSheet, Observer {
                it?.let {
                    layoutBinding?.courseModel = it
                }
            })
        }
    }
}