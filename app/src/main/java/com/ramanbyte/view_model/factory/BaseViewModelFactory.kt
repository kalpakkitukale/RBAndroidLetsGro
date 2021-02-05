package com.ramanbyte.view_model.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ramanbyte.emla.view_model.PaymentSummaryViewModel

@Suppress("UNCHECKED_CAST")
open class BaseViewModelFactory(private val mContext: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(PaymentSummaryViewModel::class.java) -> PaymentSummaryViewModel(
                mContext
            ) as T
            else -> throw IllegalArgumentException("Please register view model in ViewModelFactory")
        }
    }
}