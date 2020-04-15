package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.emla.models.request.ChangePasswordModel
import com.ramanbyte.validation.ObservableValidator
import androidx.databinding.library.baseAdapters.BR
import com.ramanbyte.R
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.MasterRepository
import com.ramanbyte.emla.data_layer.room.ApplicationDatabase
import com.ramanbyte.utilities.*
import com.ramanbyte.validation.ValidationFlags
import org.kodein.di.generic.instance


class ChangePasswordViewModel(var mContext: Context) : BaseViewModel(mContext) {

    var changePasswordModelLiveData = MutableLiveData<ChangePasswordModel>().apply {
        value = ChangePasswordModel()
    }
    private val masterRepository: MasterRepository by instance()
    private val applicationDatabase: ApplicationDatabase by instance()
    var isChangePasswordSuccessfully = MutableLiveData<Boolean>().apply {
        value = null
    }
    var changeValidator =
        ObservableValidator(changePasswordModelLiveData.value!!, BR::class.java).apply {

            addRule(
                KEY_OLD_PASSWORD,
                ValidationFlags.FIELD_REQUIRED,
                BindingUtils.string(R.string.oldPassword)

            )
            addRule(
                keyPassword,
                ValidationFlags.FIELD_REQUIRED,
                BindingUtils.string(R.string.new_password_required)

            )
            addRule(
                KEY_NEW_PASSWORD,
                ValidationFlags.FIELD_REQUIRED,
                BindingUtils.string(R.string.confirm_new_password_required)

            )
            addRule(
                KEY_OLD_PASSWORD,
                ValidationFlags.FIELD_CONTAINS_SPACE,
                BindingUtils.string(R.string.invalid_old_password)
            )

            addRule(
                keyPassword,
                ValidationFlags.FIELD_CONTAINS_SPACE,
                BindingUtils.string(R.string.enter_valid_new_password)
            )

            addRule(
                KEY_NEW_PASSWORD,
                ValidationFlags.FIELD_CONTAINS_SPACE,
                BindingUtils.string(R.string.enter_valid_confirm_new_password)
            )

            addRule(
                keyPassword,
                ValidationFlags.FIELD_NOT_MATCH,
                BindingUtils.string(R.string.same_old_and_new_password),
                KEY_OLD_PASSWORD
            )

            addRule(
                KEY_NEW_PASSWORD,
                ValidationFlags.FIELD_MATCH,
                BindingUtils.string(R.string.not_same_new_and_confirm_password),
                keyPassword
            )
            addRule(
                keyPassword,
                ValidationFlags.FIELD_PASSWORD,
                BindingUtils.string(R.string.not_valid_password_pattern)
            )
            addRule(
                KEY_OLD_PASSWORD,
                ValidationFlags.FIELD_MAX,
                BindingUtils.string(R.string.youHaveCrossedYourMaximumLimit),
                limit = 15
            )
            addRule(
                keyPassword,
                ValidationFlags.FIELD_MAX,
                BindingUtils.string(R.string.youHaveCrossedYourMaximumLimit),
                limit = 15
            )
            addRule(
                KEY_NEW_PASSWORD,
                ValidationFlags.FIELD_MAX,
                BindingUtils.string(R.string.youHaveCrossedYourMaximumLimit),
                limit = 15
            )
        }

    fun changePasswordClick(view: View) {
        CoroutineUtils.main {
            if (changeValidator.validateAll()) {
                try {
                    changePasswordModelLiveData.value?.userId = 2
                    //applicationDatabase?.getUserDao()?.getCurrentUser()?.userId!!
                    val response =
                        masterRepository.changePassword(changePasswordModelLiveData.value!!)

                    isAlertDialogShown.postValue(true)
                    setAlertDialogResourceModelMutableLiveData(response!!,
                        BindingUtils.drawable(R.drawable.ic_all_the_best),
                        true,
                        BindingUtils.string(R.string.strOk),
                        {
                            isChangePasswordSuccessfully.value = true
                            isAlertDialogShown.value = false
                        },
                        "",
                        {})
                } catch (e: ApiException) {
                    e.printStackTrace()
                    AppLog.errorLog(e.message, e)

                    setAlertDialogResourceModelMutableLiveData(
                        e.message.toString(),
                        BindingUtils.drawable(
                            R.drawable.ic_something_went_wrong
                        )!!,
                        true,
                        BindingUtils.string(R.string.strOk), {
                            isAlertDialogShown.postValue(false)
                        }
                    )
                    isAlertDialogShown.postValue(true)
                } catch (e: NoInternetException) {
                    e.printStackTrace()
                    AppLog.errorLog(e.message, e)
                    setAlertDialogResourceModelMutableLiveData(
                        BindingUtils.string(R.string.no_internet_message),
                        BindingUtils.drawable(R.drawable.ic_no_internet)!!,
                        true,
                        BindingUtils.string(R.string.tryAgain), {
                            isAlertDialogShown.postValue(false)
                            changePasswordClick(view)
                        },
                        BindingUtils.string(R.string.no), {
                            isAlertDialogShown.postValue(false)
                        }
                    )
                    isAlertDialogShown.postValue(true)
                } catch (e: NoDataException) {
                    e.printStackTrace()
                    AppLog.errorLog(e.message, e)
                }
            }
        }
    }

    override var noInternetTryAgain: () -> Unit = {}
}