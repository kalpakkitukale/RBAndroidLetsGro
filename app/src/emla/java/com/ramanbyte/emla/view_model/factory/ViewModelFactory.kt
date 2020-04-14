package com.ramanbyte.emla.view_model.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ramanbyte.emla.view_model.ContainerViewModel
import com.ramanbyte.view_model.factory.BaseViewModelFactory

class ViewModelFactory(private val mContext: Context) : BaseViewModelFactory(mContext = mContext) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return when {
            modelClass.isAssignableFrom(ContainerViewModel::class.java) -> {
                return ContainerViewModel(mContext) as T
            }
            else -> super.create(modelClass)
        }

    }
}