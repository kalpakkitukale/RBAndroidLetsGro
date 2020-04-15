package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.navigation.findNavController
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel


class SettingViewModel(var mContext: Context) : BaseViewModel(mContext) {

    fun onClickChangePassword(view: View) {
        view.findNavController()
            .navigate(R.id.action_learnerProfileFragment_to_changePasswordFragment)
    }

    override var noInternetTryAgain: () -> Unit = {}
}