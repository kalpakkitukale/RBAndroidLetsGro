package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.emla.models.request.ForgetPasswordModel
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_EMAIL
import com.ramanbyte.validation.ObservableValidator
import com.ramanbyte.validation.ValidationFlags
import androidx.databinding.library.baseAdapters.BR
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.MasterRepository
import com.ramanbyte.utilities.AppLog
import org.kodein.di.generic.instance

class ForgetPasswordViewModel(var mContext: Context) : BaseViewModel(mContext) {
    override var noInternetTryAgain: () -> Unit = {}

    private val masterRepository: MasterRepository by instance()
    val forgotPasswordModelLiveData =
        MutableLiveData<ForgetPasswordModel>(ForgetPasswordModel())
    val isEmailSendSuccessfully = MutableLiveData<Boolean>().apply {
        value = null
    }

    val forgotPasswordValidation =
        ObservableValidator(forgotPasswordModelLiveData.value!!, BR::class.java).apply {

            addRule(
                KEY_EMAIL,
                ValidationFlags.FIELD_REQUIRED,
                BindingUtils.string(
                    R.string.dynamic_required,
                    BindingUtils.string(R.string.emailId)
                )
            )

            addRule(
                KEY_EMAIL,
                ValidationFlags.FIELD_EMAIL,
                BindingUtils.string(
                    R.string.invalid_email_id
                )
            )
        }


    fun forgotPassword(view: View) {

        CoroutineUtils.main {
            try {
                var data = masterRepository.forgetPassword(forgotPasswordModelLiveData.value!!)

                isAlertDialogShown.postValue(true)
                setAlertDialogResourceModelMutableLiveData(data!!,
                    BindingUtils.drawable(R.drawable.ic_all_the_best),
                    true,
                    BindingUtils.string(R.string.strOk),
                    {
                        isEmailSendSuccessfully.value = true
                        isAlertDialogShown.value = false
                    },
                    "",
                    {})

            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)

                setAlertDialogResourceModelMutableLiveData(
                    BindingUtils.string(R.string.some_thing_went_wrong),
                    BindingUtils.drawable(R.drawable.ic_something_went_wrong
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
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
            }
        }
/*
        if (forgotPasswordValidation.validateAll())
            invokeApiCall {
                var data = masterRepository.forgetPassword(forgotPasswordModelLiveData.value!!)

                isAlertDialogShown.postValue(true)
                setAlertDialogResourceModelMutableLiveData(data!!,
                    BindingUtils.drawable(R.drawable.ic_all_the_best),
                    true,
                    BindingUtils.string(R.string.strOk),
                    {
                        isEmailSendSuccessfully.value = true
                        isAlertDialogShown.value = false
                    },
                    "",
                    {})
            }*/
    }

}