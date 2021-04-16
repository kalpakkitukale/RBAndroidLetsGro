package com.ramanbyte.emla.view

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ramanbyte.R
import com.ramanbyte.base.BaseBottomSheetFragment
import com.ramanbyte.databinding.CoursePerchaesDetailsBottomSheetBinding
import com.ramanbyte.emla.adapters.PurchaseCourseDetailsAdapter
import com.ramanbyte.emla.models.response.CartResponseModel
import com.ramanbyte.emla.view_model.TransactionHistoryViewModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.StaticMethodUtilitiesKtx
import kotlin.Exception

/**
 * @author Akash Inkar <akash.1@ramanbyte.com>
 * @since 5/3/21
 */
class CoursePerchesDetailsBottomSheet(var isActivityParent: Boolean, useParent: Boolean) :
    BaseBottomSheetFragment<CoursePerchaesDetailsBottomSheetBinding, TransactionHistoryViewModel>(
        isActivityParent = isActivityParent,
        useParent = useParent
    ) {
    lateinit var mContext: Context
    var cartResponseModeldataList: ArrayList<CartResponseModel>? = null
    override val viewModelClass: Class<TransactionHistoryViewModel> =
        TransactionHistoryViewModel::class.java

    override fun layoutId(): Int = R.layout.course_perchaes_details_bottom_sheet
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, 0)
    }

    override fun initiate() {
        layoutBinding?.apply {
            lifecycleOwner = this@CoursePerchesDetailsBottomSheet
            viewModels = viewModel
        }
        fullBottomSheet()

        viewModelOperation()
    }

    fun viewModelOperation() {
        viewModel.apply {
            cartDetailsLiveData.observe(this@CoursePerchesDetailsBottomSheet, Observer {
                it?.let {
                    try {
                        cartResponseModeldataList = it.distinctBy { it.courseName
                        } as ArrayList<CartResponseModel>
                        cartResponseModeldataList?.let {
                            setAdapter(it)
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                        AppLog.infoLog(e.message.toString())
                    }
                    layoutBinding?.tvTotalAmount.setText(it[0].totalPaid)
                }
            })

            onClickedBottomSheetLiveData.observe(this@CoursePerchesDetailsBottomSheet, Observer {
                it?.let {
                    if (it) {
                        dismiss()
                        onClickedBottomSheetLiveData.value = null

                    }
                }
            })
        }
    };

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    fun setAdapter(dataList: ArrayList<CartResponseModel>) {
        val layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            val adapter = PurchaseCourseDetailsAdapter(dataList, mContext)
        layoutBinding.apply {
            rvPerchaesDetails.layoutManager = layoutManager
            rvPerchaesDetails.adapter = adapter
        }
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
            mainContainer.viewTreeObserver.addOnGlobalLayoutListener {
                val dialog = dialog as BottomSheetDialog
                getDialog()?.window?.apply {
                    setSoftInputMode(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    setDimAmount(0.5f)
                }
                val bottomSheet =
                    dialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
                val mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
                val height = mainContainer.height
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
}