package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.RegistrationRepository
import com.ramanbyte.emla.models.CityModel
import com.ramanbyte.emla.models.RegistrationModel
import com.ramanbyte.emla.models.StateModel
import com.ramanbyte.emla.models.response.MasterDataResponseModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import org.kodein.di.generic.instance

class LearnerProfileViewModel(mContext: Context) : BaseViewModel(mContext) {

    private val registrationRepository: RegistrationRepository by instance()

    val statesListLiveData = MutableLiveData<ArrayList<StateModel>>()

    var citiesList = arrayListOf<CityModel>()
    val citiesListLiveData = MutableLiveData<ArrayList<CityModel>>()

    val programLevelsListLiveData = MutableLiveData<ArrayList<MasterDataResponseModel>>()
    val educationPatternsListLiveData = MutableLiveData<ArrayList<MasterDataResponseModel>>()
    val specializationsListLiveData = MutableLiveData<ArrayList<MasterDataResponseModel>>()

    val registrationModelLiveData = MutableLiveData<RegistrationModel>(RegistrationModel())

    val citiesQueryLiveData = MutableLiveData<String>()

    fun getProfile() {

        CoroutineUtils.main {

            try {
                coroutineToggleLoader(BindingUtils.string(R.string.getting_patterns))
                registrationModelLiveData.postValue(registrationRepository.getProfile())
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

    fun getStates() {
        CoroutineUtils.main {

            try {

                coroutineToggleLoader(BindingUtils.string(R.string.getting_states))
                statesListLiveData.postValue(registrationRepository.getStates())
                coroutineToggleLoader()

                getProfile()

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

    fun savePersonalDetails(view: View) {
        view.findNavController()
            .navigate(R.id.action_personalDetailFragment_to_educationDetailFragment)
    }

    override var noInternetTryAgain: () -> Unit = {

    }
}