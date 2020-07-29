package com.ramanbyte.emla.view_model

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ramanbyte.BaseAppController
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.MasterRepository
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.LoginRequest
import com.ramanbyte.emla.models.request.PledgeStatusRequest
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

    val toolbarTitleLiveData = MutableLiveData<String>()
    val userLoginRequestLiveData = MutableLiveData(LoginRequest())
    private val masterRepository: MasterRepository by instance()
    var userEntity = masterRepository.getCurrentUser()
    var isPledgeConfirm: MutableLiveData<Boolean?> = MutableLiveData(null)
    var navigateToNextScreen: MutableLiveData<Boolean?> = MutableLiveData(null)

    var btnGoogleLogin = MutableLiveData<Boolean?>(null)
    var btnFbLogin = MutableLiveData<Boolean?>(null)

    init {
        loaderMessageLiveData.set(BindingUtils.string(R.string.str_loading))
    }

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

    fun doLogin(view: View) {
        /*view.snack(BindingUtils.string(R.string.please_agree_the_instruction),
            Snackbar.LENGTH_LONG,{})*/
        if (loginRequestValidation.validateAll()) {
            if (PermissionsManager.checkPermission(
                    mContext as Activity,
                    Manifest.permission.READ_PHONE_STATE
                )
            ) {
                val apiCallFunction: suspend () -> Unit = {
                    val response = masterRepository.doLogin(userLoginRequestLiveData.value!!)
                    if (response?.userType == KEY_STUDENT) {
                        userModelLiveData.postValue(response)
                    } else if (response?.userType == KEY_FACULTY) {
                        userModelLiveData.postValue(response)
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

    fun onPledgeConfirmation(button: CompoundButton, check: Boolean) {
        isPledgeConfirm.value = check
    }

    fun onPledgeTaken(view: View) {
        if (isPledgeConfirm.value == true) {
            CoroutineUtils.main {
                try {
                    val pledgeStatusRequest = PledgeStatusRequest()
                    pledgeStatusRequest.status = KEY_Y
                    val apiCallFunction: suspend () -> Unit = {
                        masterRepository.updatePledgeStatus(pledgeStatusRequest)
                    }
                    invokeApiCall(apiCallFunction = apiCallFunction)
                    coroutineToggleLoader()
                    navigateToNextScreen.value = true
                } catch (e: ApiException) {
                    e.printStackTrace()
                    AppLog.errorLog(e.message, e)
                    coroutineToggleLoader()
                } catch (e: NoDataException) {
                    e.printStackTrace()
                    AppLog.errorLog(e.message, e)
                    coroutineToggleLoader()
                } catch (e: NoInternetException) {
                    e.printStackTrace()
                    AppLog.errorLog(e.message, e)
                    coroutineToggleLoader()
                }
            }
        } else {
            navigateToNextScreen.value = false
            //show error
        }
    }

    fun createAccount(view: View) {
        view.findNavController().navigate(R.id.action_loginFragment_to_registerAsFragment, null)
    }

    fun forgotPassword(view: View) {
        forgotPasswordClick.value = true
    }

    fun googleLogIn(view: View) {
        btnGoogleLogin.value = true
    }

    fun fbLogIn(view: View) {
        btnFbLogin.value = true
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

    //******************************* Register As ***************************************

    var onClickStudentLiveData: MutableLiveData<Boolean?> = MutableLiveData(null)
    var onClickFacultyLiveData: MutableLiveData<Boolean?> = MutableLiveData(null)
    var isRegisterAsSelected: MutableLiveData<Boolean?> = MutableLiveData(null)
    var selectedAs: MutableLiveData<String?> = MutableLiveData(KEY_BLANK)

    var toolbarVisibilityLiveData: MutableLiveData<Int?> = MutableLiveData(null)
    //var onClickFacultyLiveData: MutableLiveData<Boolean?> = MutableLiveData(null)

    fun onClickStudent(view: View) {
        selectedAs.value = BindingUtils.string(R.string.student)
        isRegisterAsSelected.value = true
        onClickStudentLiveData.value = true
    }

    fun onClickFaculty(view: View) {
        selectedAs.value = BindingUtils.string(R.string.faculty)
        isRegisterAsSelected.value = true
        onClickFacultyLiveData.value = true
    }

    fun onClickContinue(view: View) {
        if (selectedAs.value == BindingUtils.string(R.string.student)) {
            view.findNavController()
                .navigate(R.id.action_registerAsFragment_to_studentRegistationFragment, null)
            /*(mContext as Activity).apply {
                val intent = Intent(this, CreateAccountActivity::class.java)
                startActivity(intent)
                BaseAppController.setEnterPageAnimation(this)
            }*/
        } else {
            view.findNavController()
                .navigate(R.id.action_registerAsFragment_to_facultyRegistationFragment, null)
        }
    }

    fun setToolbarTitle(visibility : Int,title: String){
        toolbarTitleLiveData.postValue(title)
        toolbarVisibilityLiveData.postValue(visibility)
    }


}