package com.ramanbyte.emla.view_model

import android.Manifest
import android.app.Activity
import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.init.NetworkConnectionInterceptor
import com.ramanbyte.emla.data_layer.repositories.MasterRepository
import com.ramanbyte.emla.data_layer.room.ApplicationDatabase
import com.ramanbyte.services.MangeUserDevice
import com.ramanbyte.utilities.*
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
                /*clickOnLogoutLiveData.value = true
                masterRepository.deleteUser()*/
                if (NetworkConnectionInterceptor(mContext).isInternetAvailable()) {
                    if (PermissionsManager.checkPermission(
                            mContext as Activity,
                            Manifest.permission.READ_PHONE_STATE
                        )
                    ) {
                        CoroutineUtils.main {
                            /**
                             * @author Niraj Naware
                             * @since 16 March 2020
                             * convert Manage User Device Intent Service to One Time Work Request
                             */
                            val manageUserDeviceData = Data.Builder()
                                .putInt(KEY_LOGIN_LOGOUT_STATUS, 0)
                                .build()

                            val simpleRequest = OneTimeWorkRequest.Builder(MangeUserDevice::class.java)
                                .setInputData(manageUserDeviceData)
                                .build()
                            WorkManager.getInstance(mContext).enqueue(simpleRequest)

                            clickOnLogoutLiveData.value = true
                        }
                    } else {
                        checkPermission()
                    }
                } else {
                    setAlertDialogResourceModelMutableLiveData(
                        BindingUtils.string(R.string.logout_user_alert),
                        BindingUtils.drawable(R.drawable.ic_no_internet)!!,
                        false,
                        BindingUtils.string(R.string.tryAgain), {
                            isAlertDialogShown.postValue(false)
                            clickOnLogout(view)
                        }, BindingUtils.string(R.string.strCancel), {
                            isAlertDialogShown.postValue(false)
                        }
                    )
                    isAlertDialogShown.postValue(true)
                }
            },
            BindingUtils.string(R.string.no), {
                isAlertDialogShown.postValue(false)
            }
        )
        isAlertDialogShown.postValue(true)
    }

    override var noInternetTryAgain: () -> Unit = {}

    private fun checkPermission() {
        if (PermissionsManager.checkPermission(
                mContext as Activity,
                Manifest.permission.READ_PHONE_STATE,
                READ_PHONE_STATE_PERMISSION_REQUEST_CODE
            )
        ) {
            AppLog.infoLog("read phone state not permitted")
        }
    }
}