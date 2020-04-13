package com.ramanbyte.emla.view_model.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ramanbyte.view_model.factory.BaseViewModelFactory

class ViewModelFactory(private val mContext: Context) : BaseViewModelFactory(mContext = mContext) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return when {
//            modelClass.isAssignableFrom(LauncherScreenViewModel::class.java) -> {
//                return LauncherScreenViewModel(mContext) as T
//            }
            else -> super.create(modelClass)
        }

    }
}