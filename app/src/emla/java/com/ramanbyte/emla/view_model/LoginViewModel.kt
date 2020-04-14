package com.ramanbyte.emla.view_model

import android.Manifest
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.CompoundButton
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.emla.data_layer.repositories.MasterRepository
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.LoginRequest
import com.ramanbyte.utilities.*
import com.ramanbyte.validation.ObservableValidator
import com.ramanbyte.validation.ValidationFlags
import org.kodein.di.generic.instance
/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class LoginViewModel(var mContext: Context) : BaseViewModel(mContext) {

    override var noInternetTryAgain: () -> Unit = {}

    val userLoginRequestLiveData = MutableLiveData<LoginRequest>(LoginRequest())
    private val masterRepository: MasterRepository by instance()
    var isPledgeConfirm: MutableLiveData<Boolean?> = MutableLiveData(null)
    var navigateToNextScreen: MutableLiveData<Boolean?> = MutableLiveData(null)

    val loginRequestValidation =
        ObservableValidator(userLoginRequestLiveData.value!!, BR::class.java).apply {

            addRule(
                keyEmailId,
                ValidationFlags.FIELD_REQUIRED,
                BindingUtils.string(R.string.enter_emailId)
                /*BindingUtils.string(
                    R.string.dynamic_required,
                    BindingUtils.string(R.string.emailId)
                )*/
            )

            addRule(
                keyEmailId,
                ValidationFlags.FIELD_EMAIL,
                BindingUtils.string(
                    R.string.invalid_email_id
                )
            )

            addRule(
                keyPassword,
                ValidationFlags.FIELD_REQUIRED,
                BindingUtils.string(
                    R.string.dynamic_required,
                    BindingUtils.string(R.string.password)
                )
            )
        }

    val userModelLiveData = MutableLiveData<UserModel?>(null)

    val forgotPasswordClick = MutableLiveData<Boolean?>(null)

    fun doLogin() {

        if (loginRequestValidation.validateAll()) {
            if (PermissionsManager.checkPermission(
                    mContext as Activity,
                    Manifest.permission.READ_PHONE_STATE
                )
            ) {
                val apiCallFunction: suspend () -> Unit = {

                    if (masterRepository.doLogin(userLoginRequestLiveData.value!!)?.userType == KEY_STAFF) {
                        userModelLiveData.postValue(
                            masterRepository.doLogin(
                                userLoginRequestLiveData.value!!
                            )
                        )
                    } else {
                        setAlertDialogResourceModelMutableLiveData(
                            BindingUtils.string(R.string.not_valid_user),
                            BindingUtils.drawable(R.drawable.ic_invalid_user)!!,
                            true,
                            BindingUtils.string(R.string.strOk), {
                                isAlertDialogShown.postValue(false)
                            }
                        )
                        isAlertDialogShown.postValue(true)
                    }
                }

                val noDataFunction = {

                }

                invokeApiCall(apiCallFunction = apiCallFunction)

            } else {
                checkPermission()
            }
        }
    }

    fun forgotPassword(view: View) {
        forgotPasswordClick.value = true
    }

    fun onPledgeConfirmation(button: CompoundButton, check: Boolean) {
        isPledgeConfirm.value = check
    }

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