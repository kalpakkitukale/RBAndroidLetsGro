package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.emla.data_layer.repositories.MasterRepository
import com.ramanbyte.emla.data_layer.room.ApplicationDatabase
import com.ramanbyte.utilities.BindingUtils
import org.kodein.di.generic.instance


class SettingViewModel(var mContext: Context) : BaseViewModel(mContext) {
    private val masterRepository: MasterRepository by instance()


    var clickOnLogoutLiveData = MutableLiveData<Boolean>().apply {
        value = null
    }

    fun onClickChangePassword(view: View) {
        view.findNavController()
            .navigate(R.id.action_settingFragment_to_changePasswordFragment)
    }

    fun onClickProfile(view: View) {
        view.findNavController()
            .navigate(R.id.action_settingFragment_to_learnerProfileFragment)
    }

    fun clickOnLogout(view: View) {
        setAlertDialogResourceModelMutableLiveData(
            BindingUtils.string(R.string.logout_message),
            null,
            false,
            BindingUtils.string(R.string.yes), {
                isAlertDialogShown.postValue(false)
                clickOnLogoutLiveData.value = true
                masterRepository.deleteUser()
            },
            BindingUtils.string(R.string.no), {
                isAlertDialogShown.postValue(false)
            }
        )
        isAlertDialogShown.postValue(true)
    }

    override var noInternetTryAgain: () -> Unit = {}
}