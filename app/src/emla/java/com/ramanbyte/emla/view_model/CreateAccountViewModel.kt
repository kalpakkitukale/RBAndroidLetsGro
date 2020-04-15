package com.ramanbyte.emla.view_model

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.R
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.emla.data_layer.repositories.RegistrationRepository
import com.ramanbyte.emla.models.RegistrationModel
import com.ramanbyte.utilities.*
import com.ramanbyte.validation.ObservableValidator
import com.ramanbyte.validation.ValidationFlags
import org.kodein.di.generic.instance

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 15-04-2020
 */
class CreateAccountViewModel(var mContext: Context) : BaseViewModel(mContext = mContext) {
    override var noInternetTryAgain: () -> Unit = {

    }

    val registrationRepository: RegistrationRepository by instance()
    var registrationMutableLiveData = MutableLiveData<RegistrationModel>().apply {
        value = RegistrationModel()
    }
    var profileImageUrl = MutableLiveData<String>().apply { value = "" }
    var profileErrorDrawable =
        MutableLiveData<Drawable>().apply {
            value = BindingUtils.drawable(R.drawable.ic_default_profile)
        }
    var uploadFileName = MutableLiveData<String>().apply { value = "" }
    var uploadFilePath = MutableLiveData<String>().apply { value = "" }
    var registrationSuccessMutableLiveData =
        MutableLiveData<String>().apply { value = null }


    fun onRegisterClick(view: View) {
        if (registrationValidator.validateAll()) {
            val apiCallFunction: suspend () -> Unit = {
                val response = registrationRepository.register(
                    registrationMutableLiveData.value!!.apply {
                        userImageFilename =
                            if (uploadFileName.value?.isNotEmpty()!! && uploadFilePath.value?.isNotEmpty()!!) {
                                /*AppS3Client.createInstance(mContext)
                                    .upload(
                                        uploadFileName.value!!,
                                        uploadFilePath.value!!,
                                        "Uploading profile...",
                                        AppS3Client.TAG_UPLOAD,
                                        0X100
                                    )*/


                                uploadFileName.value
                            } else {
                                ""
                            }
                    }
                )
                if(response?.isNotEmpty()!!){
                    if (uploadFileName.value?.isNotEmpty()!! && uploadFilePath.value?.isNotEmpty()!!) {
                        AppS3Client.createInstance(mContext)
                            .upload(
                                uploadFileName.value!!,
                                uploadFilePath.value!!,
                                "Uploading profile...",
                                AppS3Client.TAG_UPLOAD,
                                0X100
                            )
                    }
                }
                registrationSuccessMutableLiveData.postValue(response)

            }
            invokeApiCall(apiCallFunction = apiCallFunction)
        }
    }

    val registrationValidator =
        ObservableValidator(registrationMutableLiveData.value!!, BR::class.java).apply {

            addRule(
                keyFirstName,
                ValidationFlags.FIELD_REQUIRED,
                BindingUtils.string(
                    R.string.dynamic_required,
                    BindingUtils.string(R.string.first_name)
                )
            )
            addRule(
                keyLastName,
                ValidationFlags.FIELD_REQUIRED,
                BindingUtils.string(
                    R.string.dynamic_required,
                    BindingUtils.string(R.string.last_name)
                )
            )
            addRule(
                keyEmailUsername,
                ValidationFlags.FIELD_REQUIRED,
                BindingUtils.string(
                    R.string.dynamic_required,
                    BindingUtils.string(R.string.emailId)
                )
            )
            addRule(
                keyEmailUsername,
                ValidationFlags.FIELD_EMAIL,
                BindingUtils.string(R.string.enter_emailId)
            )
            addRule(
                keyPassword,
                ValidationFlags.FIELD_REQUIRED,
                BindingUtils.string(
                    R.string.dynamic_required,
                    BindingUtils.string(R.string.password)
                )
            )
            addRule(
                keyConfirmPassword,
                ValidationFlags.FIELD_REQUIRED,
                BindingUtils.string(
                    R.string.dynamic_required,
                    BindingUtils.string(R.string.confirmPassword1)
                )
            )
            addRule(
                keyConfirmPassword,
                ValidationFlags.FIELD_MATCH,
                BindingUtils.string(R.string.not_same_new_and_confirm_password),
                keyPassword
            )
        }

}