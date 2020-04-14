package com.ramanbyte.emla.view_model.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ramanbyte.emla.view_model.ContainerViewModel
import com.ramanbyte.emla.view_model.LoginViewModel
import com.ramanbyte.view_model.factory.BaseViewModelFactory
/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class ViewModelFactory(private val mContext: Context) : BaseViewModelFactory(mContext = mContext) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                return LoginViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(ContainerViewModel::class.java) -> {
                return ContainerViewModel(mContext) as T
            }
            else -> super.create(modelClass)
        }

    }
}