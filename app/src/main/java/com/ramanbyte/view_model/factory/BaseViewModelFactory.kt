package com.ramanbyte.view_model.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

open class BaseViewModelFactory(private val mContext: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            else -> throw IllegalArgumentException()
        }
    }
}