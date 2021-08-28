package com.ramanbyte.emla.view_model

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.CompoundButton
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.ramanbyte.R
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.MasterRepository
import com.ramanbyte.emla.data_layer.repositories.RegistrationRepository
import com.ramanbyte.emla.models.CityModel
import com.ramanbyte.emla.models.StateModel
import com.ramanbyte.emla.models.UserDetailsModel
import com.ramanbyte.emla.models.request.PledgeStatusRequest
import com.ramanbyte.emla.models.response.MasterDataResponseModel
import com.ramanbyte.utilities.*
import com.ramanbyte.validation.ObservableValidator
import com.ramanbyte.validation.ValidationFlags
import org.kodein.di.generic.instance

class LearnerProfileViewModel(private val mContext: Context) : BaseViewModel(mContext) {

    private val registrationRepository: RegistrationRepository by instance()
    private val masterRepository: MasterRepository by instance()

    val statesListLiveData = MutableLiveData<ArrayList<StateModel>>()

    var citiesList = arrayListOf<CityModel>()
    val citiesListLiveData = MutableLiveData<ArrayList<CityModel>>()

    val programLevelsListLiveData = MutableLiveData<ArrayList<MasterDataResponseModel>>()
    val educationPatternsListLiveData = MutableLiveData<ArrayList<MasterDataResponseModel>>()
    val specializationsListLiveData = MutableLiveData<ArrayList<MasterDataResponseModel>>()

    val registrationModelLiveData: MutableLiveData<UserDetailsModel>? =
        MutableLiveData<UserDetailsModel>(null)!!

    val citiesQueryLiveData = MutableLiveData<String>()

    var personalDetailsDataValidator: ObservableValidator<UserDetailsModel>? = null

    var stateDetailsDataValidator:ObservableValidator<UserDetailsModel>?=null

    var educationDetailsDataValidator: ObservableValidator<UserDetailsModel>? = null

    var goToNextPageLiveData = MutableLiveData<Boolean>(false)

    var goToNexttPageLiveData = MutableLiveData<Boolean>(false)

    var showDatePickerDailogLiveData = MutableLiveData<Boolean>(false)

    var profileImageUrl = MutableLiveData<String>().apply { value = "" }
    var uploadFileName = MutableLiveData<String>().apply { value = "" }
    var uploadFilePath = MutableLiveData<String>().apply { value = "" }
    var profileErrorDrawable =
        MutableLiveData<Drawable>().apply {
            value = BindingUtils.drawable(R.drawable.ic_default_profile)
        }

    val showPledgeDialogLiveData = MutableLiveData<Boolean?>(null)
    var isPledgeConfirm: MutableLiveData<Boolean?> = MutableLiveData(null)
    val navigateToCoursePage = MutableLiveData<Boolean?>(null)

    var isCityAvailable = true

    fun getProfile() {

        CoroutineUtils.main {

            try {
                coroutineToggleLoader(BindingUtils.string(R.string.getting_patterns))
                registrationModelLiveData?.postValue(registrationRepository.getProfile())
                coroutineToggleLoader()
                toggleLayoutVisibility(
                    View.VISIBLE,
                    View.GONE,
                    View.GONE,
                    BindingUtils.string(R.string.error_occured_while_getting_profile),
                    View.GONE
                )
            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                coroutineToggleLoader()
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.GONE,
                    BindingUtils.string(R.string.error_occured_while_getting_profile),
                    View.VISIBLE
                )
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                coroutineToggleLoader()
                toggleLayoutVisibility(
                    View.GONE,
                    View.VISIBLE,
                    View.GONE,
                    BindingUtils.string(R.string.no_profile_data_found),
                    View.GONE
                )
            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                coroutineToggleLoader()
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

    fun setValidation() {

        personalDetailsDataValidator =
            ObservableValidator(registrationModelLiveData?.value!!, BR::class.java).apply {

                addRule(
                    keyFirstName,
                    ValidationFlags.FIELD_REQUIRED,
                    BindingUtils.string(
                        R.string.required
                    )
                )
                addRule(
                    keyLastName,
                    ValidationFlags.FIELD_REQUIRED,
                    BindingUtils.string(
                        R.string.required
                    )
                )

                addRule(
                    keyDateOfBirth,
                    ValidationFlags.FIELD_REQUIRED,
                    BindingUtils.string(
                        R.string.required
                    )
                )

                addRule(
                    keyEmailUsername,
                    ValidationFlags.FIELD_REQUIRED,
                    BindingUtils.string(
                        R.string.required
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
                        R.string.required
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

        stateDetailsDataValidator=
            ObservableValidator(registrationModelLiveData?.value!!, BR::class.java).apply {
                addRule(
                    keyState,
                    ValidationFlags.FIELD_SPINNER_SELECTION,
                    BindingUtils.string(
                        R.string.required
                    ),
                    spinnerSelectPosition = -1
                )
                addRule(
                    keyCity,
                    ValidationFlags.FIELD_SPINNER_SELECTION,
                    BindingUtils.string(
                        R.string.required
                    ),
                    spinnerSelectPosition = -1
                )
            }

        educationDetailsDataValidator =
            ObservableValidator(registrationModelLiveData?.value!!, BR::class.java).apply {

                addRule(
                    keyProgramLevel,
                    ValidationFlags.FIELD_SPINNER_SELECTION,
                    BindingUtils.string(
                        R.string.required
                    ),
                    spinnerSelectPosition = -1
                )

                addRule(
                    keyBatchYear,
                    ValidationFlags.FIELD_REQUIRED,
                    BindingUtils.string(
                        R.string.required
                    )
                )

                addRule(
                    keyPattern,
                    ValidationFlags.FIELD_SPINNER_SELECTION,
                    BindingUtils.string(
                        R.string.required
                    ),
                    spinnerSelectPosition = -1
                )


                addRule(
                    keySpecialization,
                    ValidationFlags.FIELD_SPINNER_SELECTION,
                    BindingUtils.string(
                        R.string.required
                    ),
                    spinnerSelectPosition = -1
                )

                addRule(
                    keyUniversity,
                    ValidationFlags.FIELD_REQUIRED,
                    BindingUtils.string(
                        R.string.required
                    )
                )

                addRule(
                    keyInstitute,
                    ValidationFlags.FIELD_REQUIRED,
                    BindingUtils.string(
                        R.string.required
                    )
                )
            }
    }

    fun getStates() {
        CoroutineUtils.main {

            try {

                coroutineToggleLoader(BindingUtils.string(R.string.getting_states))

                val countryList = registrationRepository.getCountries()

                val countryModelLst =
                    countryList?.firstOrNull { it?.country_name?.toLowerCase()?.contains("india") == true }

                statesListLiveData.postValue(
                    registrationRepository.getStates(
                        countryModelLst?.country_id ?: 0
                    )
                )


                coroutineToggleLoader()

                getProfile()

            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                coroutineToggleLoader()
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.GONE,
                    BindingUtils.string(R.string.error_occured_while_getting_profile),
                    View.VISIBLE
                )
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                coroutineToggleLoader()
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
                coroutineToggleLoader()
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

    fun getCities(stateId: Int) {

        CoroutineUtils.main {

            try {

                coroutineToggleLoader(BindingUtils.string(R.string.getting_cities))

                citiesList = registrationRepository.getCities(stateId)

                coroutineToggleLoader()

            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                coroutineToggleLoader()
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.GONE,
                    BindingUtils.string(R.string.error_occured_while_getting_profile),
                    View.VISIBLE
                )
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                coroutineToggleLoader()
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
                coroutineToggleLoader()
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

    fun getProgramLevels() {
        CoroutineUtils.main {

            try {
                coroutineToggleLoader(BindingUtils.string(R.string.getting_program_level))
                programLevelsListLiveData.postValue(registrationRepository.getProgramLevels())
                coroutineToggleLoader()
                getEducationPatterns()
            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                coroutineToggleLoader()
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.GONE,
                    BindingUtils.string(R.string.error_occured_while_getting_profile),
                    View.VISIBLE
                )
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                coroutineToggleLoader()
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
                coroutineToggleLoader()
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

    fun getEducationPatterns() {
        CoroutineUtils.main {

            try {
                coroutineToggleLoader(BindingUtils.string(R.string.getting_patterns))
                educationPatternsListLiveData.postValue(registrationRepository.getEducationPatterns())
                coroutineToggleLoader()
                getSpecializations()
            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                coroutineToggleLoader()
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.GONE,
                    BindingUtils.string(R.string.error_occured_while_getting_profile),
                    View.VISIBLE
                )
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                coroutineToggleLoader()
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
                coroutineToggleLoader()
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

    fun getSpecializations() {
        CoroutineUtils.main {

            try {

                coroutineToggleLoader(BindingUtils.string(R.string.getting_specializations))
                specializationsListLiveData.postValue(registrationRepository.getSpecializations())
                coroutineToggleLoader()

            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                coroutineToggleLoader()
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.GONE,
                    BindingUtils.string(R.string.error_occured_while_getting_profile),
                    View.VISIBLE
                )
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                coroutineToggleLoader()
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
                coroutineToggleLoader()
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

    fun showDateDialog(view: View) {
        showDatePickerDailogLiveData.value = true
    }

    fun savePersonalDetails(view: View) {
        /*view.findNavController()
            .navigate(R.id.action_personalDetailFragment_to_educationDetailFragment)*/

        if (personalDetailsDataValidator?.validateAll() == true && isCityAvailable)
            goToNextPageLiveData.value = true
    }
    fun saveStateDetails(view: View){
        if (stateDetailsDataValidator?.validateAll() == true && isCityAvailable)
            goToNexttPageLiveData.value = true
    }

    fun saveProfile(view: View) {

        if (educationDetailsDataValidator?.validateAll() == true) {

            if (registrationRepository.isUserActive()) {
                sendSaveProfileRequest(view)
            } else {
                showPledgeDialogLiveData.value = true
            }
        }
    }

    fun onPledgeConfirmation(button: CompoundButton, check: Boolean) {
        isPledgeConfirm.value = check
    }

    fun onPledgeTaken(view: View) {

        if (isPledgeConfirm.value == true) {

            showPledgeDialogLiveData.value = false

            CoroutineUtils.main {

                try {

                    val pledgeStatusRequest = PledgeStatusRequest()
                    pledgeStatusRequest.status = "Y"

                    coroutineToggleLoader(BindingUtils.string(R.string.acknowledge_pledge))

                    val status = masterRepository.updatePledgeStatus(pledgeStatusRequest)

                    coroutineToggleLoader()

                    if (status == 1) {
                        sendSaveProfileRequest(view)
                    } else {
                        throw ApiException(BindingUtils.string(R.string.some_thing_went_wrong))
                    }

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
            //show error
        }
    }

    fun sendSaveProfileRequest(view: View) {

        CoroutineUtils.main {
            try {

                AppLog.infoLog("Final Object::: ${registrationModelLiveData?.value?.firstName}")

                coroutineToggleLoader(BindingUtils.string(R.string.updating_profile))

                val res =
                    registrationRepository.updateLearnerProfile(registrationModelLiveData?.value?.apply {

                        resumeFileName = uploadResumeFileName.value
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
                    }!!)

                if (res != 0) {

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

                    if (uploadResumeFileName.value?.isNotEmpty()!!) {
                        AppS3Client.createInstance(mContext)
                            .upload(
                                uploadResumeFileName.value!!,
                                uploadResumeFilePath.value!!,
                                BindingUtils.string(R.string.uploading_resume),
                                AppS3Client.TAG_UPLOAD,
                                0X101
                            )
                    }

                    setAlertDialogResourceModelMutableLiveData(
                        BindingUtils.string(R.string.profile_updated_successfully),
                        BindingUtils.drawable(R.drawable.ic_success),
                        isInfoAlert = true,
                        positiveButtonText = KEY_OK,
                        positiveButtonClickFunctionality = {

                            if (isPledgeConfirm.value != null) {
                                navigateToCoursePage.postValue(
                                    true
                                )
                            } else {
                                view.findNavController().navigateUp()
                            }

                            isAlertDialogShown.postValue(false)
                        }
                    )

                    isAlertDialogShown.postValue(true)

                    AppLog.infoLog("Profile Udpate ID ::: $res")

                } else {

                    setAlertDialogResourceModelMutableLiveData(
                        BindingUtils.string(R.string.profile_update_failed),
                        BindingUtils.drawable(R.drawable.ic_fail),
                        isInfoAlert = true,
                        positiveButtonText = BindingUtils.string(R.string.tryAgain),
                        positiveButtonClickFunctionality = {

                            sendSaveProfileRequest(view)

                            isAlertDialogShown.postValue(false)
                        },
                        negativeButtonText = BindingUtils.string(R.string.no),
                        negativeButtonClickFunctionality = {
                            isAlertDialogShown.postValue(false)
                        }
                    )

                    isAlertDialogShown.postValue(true)

                }



                coroutineToggleLoader()

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
    }

    override var noInternetTryAgain: () -> Unit = {
        getStates()
    }

    var onClickUploadResumeLiveData = MutableLiveData<Boolean>().apply { value = null }
    var onClickRemoveResumeLiveData = MutableLiveData<Boolean>().apply { value = null }
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
}