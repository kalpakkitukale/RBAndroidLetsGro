package com.ramanbyte.emla.view_model

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.R
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.RegistrationRepository
import com.ramanbyte.emla.models.AreaOfExpertiesModel
import com.ramanbyte.emla.models.AreaOfExpertiseResponseModel
import com.ramanbyte.emla.models.RegistrationModel
import com.ramanbyte.emla.models.StateModel
import com.ramanbyte.utilities.*
import com.ramanbyte.validation.ObservableValidator
import com.ramanbyte.validation.ValidationFlags
import org.kodein.di.generic.instance
import java.util.HashSet

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
                        userDetails.userType = KEY_STUDENT
                        userDetails.userImageFilename =
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
                if (response?.isNotEmpty()!!) {
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
        ObservableValidator(registrationMutableLiveData.value!!.userDetails, BR::class.java).apply {

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

            addRule(
                keyPassword,
                ValidationFlags.FIELD_CONTAINS_SPACE,
                BindingUtils.string(R.string.enter_valid_new_password)
            )

            addRule(
                keyConfirmPassword,
                ValidationFlags.FIELD_CONTAINS_SPACE,
                BindingUtils.string(R.string.enter_valid_confirm_new_password)
            )

            addRule(
                keyConfirmPassword,
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
                keyPassword,
                ValidationFlags.FIELD_MAX,
                BindingUtils.string(R.string.youHaveCrossedYourMaximumLimit),
                limit = 15
            )
            addRule(
                keyConfirmPassword,
                ValidationFlags.FIELD_MAX,
                BindingUtils.string(R.string.youHaveCrossedYourMaximumLimit),
                limit = 15
            )
        }


    //********************************* Register as Faculty *********************************

    var onClickUploadResumeLiveData = MutableLiveData<Boolean>().apply { value = null }
    var onClickRemoveResumeLiveData = MutableLiveData<Boolean>().apply { value = null }
    val areaOfExpertiseListLiveData = MutableLiveData<ArrayList<AreaOfExpertiseResponseModel>>()
    var areaOfExpertiseSet = HashSet<Int>()
    var uploadResumeFileName = MutableLiveData<String>().apply { value = KEY_BLANK }
    var uploadResumeFilePath = MutableLiveData<String>().apply { value = KEY_BLANK }

    fun onClickUploadResume(view: View) {
        onClickUploadResumeLiveData.value = true
    }

    fun onClickRemoveResume(view: View) {
        setAlertDialogResourceModelMutableLiveData(
            BindingUtils.string(R.string.remove_resume_alert),
            BindingUtils.drawable(R.drawable.ic_warning)!!,
            false,
            BindingUtils.string(R.string.yes), {
                onClickRemoveResumeLiveData.value = true
                isAlertDialogShown.postValue(false)
            },
            BindingUtils.string(R.string.no), {
                isAlertDialogShown.postValue(false)
            }
        )
        isAlertDialogShown.postValue(true)
    }

    fun getAreaOfExpertise(searchKey: String) {
        CoroutineUtils.main {
            try {
                //coroutineToggleLoader(BindingUtils.string(R.string.getting_area_of_expertise))
                areaOfExpertiseListLiveData.postValue(
                    registrationRepository.getAreaOfExpertise(searchKey)
                )
                //coroutineToggleLoader()
            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                //coroutineToggleLoader()
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.GONE,
                    BindingUtils.string(R.string.error_occured_while_getting_area_of_expertise),
                    View.VISIBLE
                )
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                //coroutineToggleLoader()
                toggleLayoutVisibility(
                    View.GONE,
                    View.VISIBLE,
                    View.GONE,
                    BindingUtils.string(R.string.unable_to_fetch_dependencie),
                    View.GONE
                )
            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                //coroutineToggleLoader()
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.VISIBLE,
                    BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet),
                    View.GONE
                )
            }
        }
    }

    val facultyRegistrationValidator =
        ObservableValidator(registrationMutableLiveData.value!!.userDetails, BR::class.java).apply {

            addRule(
                keyFirstName,
                ValidationFlags.FIELD_REQUIRED,
                BindingUtils.string(
                    R.string.required,
                    BindingUtils.string(R.string.first_name)
                )
            )
            addRule(
                keyLastName,
                ValidationFlags.FIELD_REQUIRED,
                BindingUtils.string(
                    R.string.required,
                    BindingUtils.string(R.string.last_name)
                )
            )
            addRule(
                keyEmailUsername,
                ValidationFlags.FIELD_REQUIRED,
                BindingUtils.string(
                    R.string.required,
                    BindingUtils.string(R.string.emailId)
                )
            )
            addRule(
                keyEmailUsername,
                ValidationFlags.FIELD_EMAIL,
                BindingUtils.string(R.string.enter_emailId)
            )
            addRule(
                keyContactNo,
                ValidationFlags.FIELD_REQUIRED,
                BindingUtils.string(
                    R.string.required,
                    BindingUtils.string(
                        R.string.mobileNumber
                    )
                )
            )
            addRule(
                keyContactNo,
                ValidationFlags.FIELD_MIN,
                BindingUtils.string(
                    R.string.invalid_mobile_no
                ),
                limit = 10
            )
        }

    fun onClickFacultyRegister(view: View) {
        if (facultyRegistrationValidator.validateAll()) {
            if (areaOfExpertiseSet.size != 0) {
                val areaOfExpertiseList = ArrayList<AreaOfExpertiesModel>()
                areaOfExpertiseSet.forEach {
                    areaOfExpertiseList.add(AreaOfExpertiesModel().apply {
                        id = it
                    })
                }

                if (uploadResumeFileName.value?.isNotEmpty()!!) {
                    val apiCallFunction: suspend () -> Unit = {
                        val response = registrationRepository.register(
                            registrationMutableLiveData.value!!.apply {
                                userDetails.apply {
                                    userType = KEY_FACULTY
                                    user_userDelStatus = KEY_Y
                                    user_userStatus = KEY_BLOCK
                                    user_userIsActive = KEY_N
                                    password = KEY_DEFAULT_PASSWORD
                                    resumeFileName = uploadResumeFileName.value
                                }
                                areaofExperties = areaOfExpertiseList
                            }
                        )
                        if (response?.isNotEmpty()!!) {
                            if (uploadResumeFileName.value?.isNotEmpty()!!) {
                                AppS3Client.createInstance(mContext)
                                    .upload(
                                        uploadResumeFileName.value!!,
                                        uploadResumeFilePath.value!!,
                                        BindingUtils.string(R.string.uploading_resume),
                                        AppS3Client.TAG_UPLOAD,
                                        0X100
                                    )
                            }
                        }
                        registrationSuccessMutableLiveData.postValue(response)
                    }
                    invokeApiCall(apiCallFunction = apiCallFunction)

                } else {
                    setAlertDialogResourceModelMutableLiveData(
                        BindingUtils.string(R.string.resume_required),
                        BindingUtils.drawable(R.drawable.ic_warning)!!,
                        true,
                        BindingUtils.string(R.string.strOk), {
                            isAlertDialogShown.postValue(false)
                        }
                    )
                    isAlertDialogShown.postValue(true)
                }
            } else {
                setAlertDialogResourceModelMutableLiveData(
                    BindingUtils.string(R.string.area_of_expertise_required),
                    BindingUtils.drawable(R.drawable.ic_warning)!!,
                    true,
                    BindingUtils.string(R.string.strOk), {
                        isAlertDialogShown.postValue(false)
                    }
                )
                isAlertDialogShown.postValue(true)
            }
        }
    }

}