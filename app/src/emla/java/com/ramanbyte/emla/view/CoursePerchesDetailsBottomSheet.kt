package com.ramanbyte.emla.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramanbyte.R
import com.ramanbyte.base.BaseBottomSheetFragment
import com.ramanbyte.databinding.CoursePerchaesDetailsBottomSheetBinding
import com.ramanbyte.databinding.CoursePerchesDetailItemListBinding
import com.ramanbyte.emla.adapters.PurchaseCourseDetailsAdapter
import com.ramanbyte.emla.models.response.CartResponseModel
import com.ramanbyte.emla.view_model.TransactionHistoryViewModel

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

        viewModelOperation()
    }

    fun viewModelOperation() {
        viewModel.apply {
            cartDetailsLiveData.observe(this@CoursePerchesDetailsBottomSheet, Observer {
                it?.let {
                    setAdapter(it)
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
}