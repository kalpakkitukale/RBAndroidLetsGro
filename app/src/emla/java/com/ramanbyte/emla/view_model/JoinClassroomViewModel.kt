package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import android.widget.RadioGroup
import androidx.databinding.ObservableField
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.MasterRepository
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.JoinClassroomModel
import com.ramanbyte.utilities.*
import com.ramanbyte.validation.ObservableValidator
import com.ramanbyte.validation.ValidationFlags
import org.kodein.di.generic.instance

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 04/02/2021
 */
class JoinClassroomViewModel(mContext: Context) : BaseViewModel(mContext = mContext) {

    private val masterRepository: MasterRepository by instance()
    var userModelLiveData = MutableLiveData<UserModel>().apply {
        value = null
    }
    var classroom_user_ref_id: Int = 0
    var classroomUserType: String = ""

    var userModel: UserModel? = null

    val viewVisibility = MutableLiveData<Int>().apply {
        value = View.GONE
    }
    var createAccountButtonText = ObservableField<String>().apply { set("Create Account") }

    val exitApplicationLiveData = MutableLiveData<Boolean>().apply {
        value = null
    }

    init {
        viewVisibility.postValue(View.GONE)
    }

    override var noInternetTryAgain: () -> Unit = {

    }

    val joinClassroomLiveData = MutableLiveData(JoinClassroomModel())

    val loginValidator = ObservableValidator(joinClassroomLiveData.value!!, BR::class.java).apply {

        addRule(
            KEY_LOGIN_USERNAME,
            ValidationFlags.FIELD_REQUIRED,
            BindingUtils.string(R.string.username_require)
        )
        addRule(
            KEY_LOGIN_USERNAME,
            ValidationFlags.FIELD_CONTAINS_SPACE,
            BindingUtils.string(R.string.invalid_username)
        )
        addRule(
            KEY_LOGIN_PASSWORD,
            ValidationFlags.FIELD_REQUIRED,
            BindingUtils.string(
                R.string.password_require
            )
        )
    }

    fun isUserLoggedIn() {
        userModel = masterRepository.getCurrentUser()
        if (userModel != null) {
            if (userModel!!.loggedId == KEY_Y) {
                if (userModel?.classroomUserId == 0) {
                    AppLog.infoLog("user is already logged in and may/may not be linked accounts from mobile")
                    if (classroom_user_ref_id != 0) getUserDetails(classroom_user_ref_id, true)
                } else if (classroom_user_ref_id != userModel?.classroomUserId) {
                    AppLog.infoLog("logged user is different")
                    setAlertDialogResourceModelMutableLiveData(
                        BindingUtils.string(R.string.classroom_lets_gro_unmatched),
                        BindingUtils.drawable(R.drawable.ic_something_went_wrong)!!,
                        true,
                        BindingUtils.string(R.string.strOk), {
                            //Exit from application or force login with the associated login.
                            exitApplicationLiveData.postValue(true)
                            isAlertDialogShown.postValue(false)
                        }
                    )
                    isAlertDialogShown.postValue(true)
                } else {
                    userModelLiveData.postValue(userModel)
                    //viewVisibility.postValue(View.VISIBLE)
                    AppLog.infoLog("logged in and linked user")
                }
            } else {
                userModelLiveData.postValue(userModel)
            }
        } else {
            AppLog.infoLog("Get /api/Login/GetUserDetails/{userID} call")
            if (classroom_user_ref_id != 0)
                getUserDetails(classroom_user_ref_id, false)
        }
    }

    private fun getUserDetails(userRefId: Int, isLoggedIn: Boolean) {
        CoroutineUtils.main {
            try {
                loaderMessageLiveData.set(BindingUtils.string(R.string.getting_user_details))
                isLoaderShowingLiveData.postValue(true)
                val response = masterRepository.getUserDetails(userRefId, isLoggedIn)
                if (userModel != null && userModel?.userId != response?.userId) {
                    setAlertDialogResourceModelMutableLiveData(
                        BindingUtils.string(R.string.classroom_lets_gro_unmatched),
                        BindingUtils.drawable(R.drawable.ic_something_went_wrong)!!,
                        true,
                        BindingUtils.string(R.string.strOk), {
                            //Exit from application or force login with the associated login.
                            isAlertDialogShown.postValue(false)
                        }
                    )
                    isAlertDialogShown.postValue(true)
                } else when (response?.userType) {
                    KEY_STUDENT, KEY_FACULTY -> {
                        userModel?.userId?.let {
                            masterRepository.updateCurrentClassroomId(classroom_user_ref_id, it)
                        }
                        userModelLiveData.postValue(response)
                    }
                    else -> {
                        isLoaderShowingLiveData.postValue(false)
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
                isLoaderShowingLiveData.postValue(false)
            } catch (e: ApiException) {
                isLoaderShowingLiveData.postValue(false)
                AppLog.errorLog(e.message, e)
                //view.snackbar(BindingUtils.string(R.string.some_thing_went_wrong))
            } catch (e: NoInternetException) {
                isLoaderShowingLiveData.postValue(false)
                AppLog.errorLog(e.message, e)
                //view.snackbar(BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet))
            } catch (e: NoDataException) {
                isLoaderShowingLiveData.postValue(false)
                AppLog.errorLog(e.message, e)
                AppLog.infoLog("Account is not linked with classroom")
                viewVisibility.postValue(View.VISIBLE)
            }
        }
    }


    fun doLogin(view: View) {
        if (loginValidator.validateAll()) {
            if (classroom_user_ref_id == 0) {
                setAlertDialogResourceModelMutableLiveData(
                    BindingUtils.string(R.string.invalid_classroom_user),
                    BindingUtils.drawable(R.drawable.something_went_wrong)!!,
                    true,
                    BindingUtils.string(R.string.yes), {
                        isAlertDialogShown.postValue(false)
                    }
                )
                isAlertDialogShown.postValue(true)
            } else {
                AppLog.infoLog("proceed to join account with api call")
                CoroutineUtils.main {
                    try {
                        isLoaderShowingLiveData.postValue(true)
                        val response =
                            masterRepository.updateLetsGroUser(joinClassroomLiveData.value?.also {
                                it.userId = classroom_user_ref_id
                                it.userType = classroomUserType
                            }!!)

                        setAlertDialogResourceModelMutableLiveData(
                            BindingUtils.string(R.string.account_linked_successfully),
                            BindingUtils.drawable(R.drawable.ic_all_the_best)!!,
                            true,
                            BindingUtils.string(R.string.yes), {
                                userModelLiveData.postValue(response)
                                isAlertDialogShown.postValue(false)
                            }
                        )
                        isAlertDialogShown.postValue(true)
                        isLoaderShowingLiveData.postValue(false)
                    } catch (e: ApiException) {
                        isLoaderShowingLiveData.postValue(false)
                        AppLog.errorLog(e.message, e)
                        setAlertDialogResourceModelMutableLiveData(
                            e.message ?: BindingUtils.string(R.string.some_thing_went_wrong),
                            BindingUtils.drawable(R.drawable.something_went_wrong),
                            isInfoAlert = false,
                            positiveButtonText = BindingUtils.string(R.string.tryAgain),
                            positiveButtonClickFunctionality = {
                                isAlertDialogShown.postValue(false)
                                doLogin(view)
                            },
                            negativeButtonText = BindingUtils.string(R.string.strCancel),
                            negativeButtonClickFunctionality = {
                                isAlertDialogShown.postValue(false)
                            }
                        )
                        isAlertDialogShown.postValue(true)
                    } catch (e: NoInternetException) {
                        isLoaderShowingLiveData.postValue(false)
                        AppLog.errorLog(e.message, e)
                        setAlertDialogResourceModelMutableLiveData(
                            e.message
                                ?: BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet),
                            BindingUtils.drawable(R.drawable.ic_no_internet),
                            isInfoAlert = false,
                            positiveButtonText = BindingUtils.string(R.string.tryAgain),
                            positiveButtonClickFunctionality = {
                                isAlertDialogShown.postValue(false)
                                doLogin(view)
                            },
                            negativeButtonText = BindingUtils.string(R.string.strCancel),
                            negativeButtonClickFunctionality = {
                                isAlertDialogShown.postValue(false)
                            }
                        )
                        isAlertDialogShown.postValue(true)
                    } catch (e: NoDataException) {
                        isLoaderShowingLiveData.postValue(false)
                        AppLog.errorLog(e.message, e)
                        setAlertDialogResourceModelMutableLiveData(
                            e.message ?: BindingUtils.string(R.string.some_thing_went_wrong),
                            BindingUtils.drawable(R.drawable.something_went_wrong),
                            isInfoAlert = false,
                            positiveButtonText = BindingUtils.string(R.string.tryAgain),
                            positiveButtonClickFunctionality = {
                                isAlertDialogShown.postValue(false)
                                doLogin(view)
                            },
                            negativeButtonText = BindingUtils.string(R.string.strCancel),
                            negativeButtonClickFunctionality = {
                                isAlertDialogShown.postValue(false)
                            }
                        )
                        isAlertDialogShown.postValue(true)
                    }
                }
                //on success show message your classroom account is linked with the Let's gro Successfully
            }
        }
    }

    fun createAccountClick(view: View) {
        if (isCreteAccount) {
            setAlertDialogResourceModelMutableLiveData(
                BindingUtils.string(R.string.create_account_confirmation),
                BindingUtils.drawable(R.drawable.ic_submit_confirmation)!!,
                false,
                BindingUtils.string(R.string.yes), {
                    createAccountApiCall()
                    isAlertDialogShown.postValue(false)
                }, BindingUtils.string(R.string.no),
                {
                    isAlertDialogShown.postValue(false)
                }
            )
            isAlertDialogShown.postValue(true)
        }else{
            exitApplicationLiveData.postValue(true)
        }
    }

    private fun createAccountApiCall() {
        CoroutineUtils.main {
            try {
                isLoaderShowingLiveData.postValue(true)
                val response = masterRepository.createAccount(classroom_user_ref_id)
                setAlertDialogResourceModelMutableLiveData(
                    BindingUtils.string(R.string.account_created_successfully),
                    BindingUtils.drawable(R.drawable.ic_all_the_best)!!,
                    true,
                    BindingUtils.string(R.string.yes), {
                        userModelLiveData.postValue(response)
                        isAlertDialogShown.postValue(false)
                    }
                )
                isAlertDialogShown.postValue(true)

                isLoaderShowingLiveData.postValue(false)
            } catch (e: ApiException) {
                isLoaderShowingLiveData.postValue(false)
                setAlertDialogResourceModelMutableLiveData(
                    e.message ?: BindingUtils.string(R.string.some_thing_went_wrong),
                    BindingUtils.drawable(R.drawable.something_went_wrong),
                    isInfoAlert = false,
                    positiveButtonText = BindingUtils.string(R.string.tryAgain),
                    positiveButtonClickFunctionality = {
                        createAccountApiCall()
                        isAlertDialogShown.postValue(false)
                    },
                    negativeButtonText = BindingUtils.string(R.string.strCancel),
                    negativeButtonClickFunctionality = {
                        isAlertDialogShown.postValue(false)
                    }
                )
                isAlertDialogShown.postValue(true)
                AppLog.errorLog(e.message, e)
            } catch (e: NoInternetException) {
                isLoaderShowingLiveData.postValue(false)
                setAlertDialogResourceModelMutableLiveData(
                    e.message
                        ?: BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet),
                    BindingUtils.drawable(R.drawable.ic_no_internet),
                    isInfoAlert = false,
                    positiveButtonText = BindingUtils.string(R.string.tryAgain),
                    positiveButtonClickFunctionality = {
                        createAccountApiCall()
                        isAlertDialogShown.postValue(false)
                    },
                    negativeButtonText = BindingUtils.string(R.string.strCancel),
                    negativeButtonClickFunctionality = {
                        isAlertDialogShown.postValue(false)
                    }
                )
                isAlertDialogShown.postValue(true)
                AppLog.errorLog(e.message, e)
            } catch (e: NoDataException) {
                isLoaderShowingLiveData.postValue(false)
                setAlertDialogResourceModelMutableLiveData(
                    e.message ?: BindingUtils.string(R.string.some_thing_went_wrong),
                    BindingUtils.drawable(R.drawable.something_went_wrong),
                    isInfoAlert = false,
                    positiveButtonText = BindingUtils.string(R.string.tryAgain),
                    positiveButtonClickFunctionality = {
                        createAccountApiCall()
                        isAlertDialogShown.postValue(false)
                    },
                    negativeButtonText = BindingUtils.string(R.string.strCancel),
                    negativeButtonClickFunctionality = {
                        isAlertDialogShown.postValue(false)
                    }
                )
                isAlertDialogShown.postValue(true)
                AppLog.errorLog(e.message, e)
            }
        }
    }

    var isCreteAccount: Boolean = true
    fun createAccountRadioGroupClick(radioGroup: RadioGroup, id: Int) {
        val radioID = radioGroup.checkedRadioButtonId
        when (radioID) {
            R.id.rbCreateYes -> {
                createAccountButtonText.set("Create Account")
                isCreteAccount = true
            }
            R.id.rbCreateNo -> {
                createAccountButtonText.set("Exit")
                isCreteAccount = false
            }
        }
    }
}