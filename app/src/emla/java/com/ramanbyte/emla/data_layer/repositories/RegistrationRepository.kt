package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.emla.data_layer.network.api_layer.RegistrationController
import com.ramanbyte.emla.models.*
import com.ramanbyte.emla.models.response.CommonDropdownModel
import com.ramanbyte.emla.models.response.MasterDataResponseModel
import com.ramanbyte.utilities.KEY_Y
import org.kodein.di.generic.instance

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 15-04-2020
 */
class RegistrationRepository(val mContext: Context) : BaseRepository(mContext) {

    private val registrationController: RegistrationController by instance()

    fun isUserActive(): Boolean {

        return applicationDatabase?.getUserDao()?.getCurrentUser()?.loggedId == KEY_Y

    }

    suspend fun register(registrationModel: RegistrationModel): String? {
        return apiRequest {
            registrationController.register(registrationModel)
        }
    }

    suspend fun getProfile(): UserDetailsModel? {

        val userReffId = applicationDatabase.getUserDao().getCurrentUser()?.userId ?: 0

        return apiRequest {

            registrationController.getProfile(userReffId)

        }

    }

    suspend fun updateLearnerProfile(registrationModel: UserDetailsModel): Int {
        return apiRequest {
            registrationController.updateLearnerProfile(registrationModel)
        } ?: 0
    }

    suspend fun getCountries(): ArrayList<CountryModel> {

        return apiRequest {
            registrationController.getCountries()
        } ?: arrayListOf()
    }

    suspend fun getStates(countryId: Int = 1): ArrayList<StateModel> {
        return apiRequest {
            registrationController.getStates(countryId)
        } ?: arrayListOf()
    }

    suspend fun getAreaOfExpertise(searchKey: String): ArrayList<AreaOfExpertiseResponseModel> {
        return apiRequest {
            registrationController.getAreaOfExpertise(searchKey)
        } ?: arrayListOf()
    }

    suspend fun getCities(stateId: Int): ArrayList<CityModel> {
        return apiRequest {
            registrationController.getCities(stateId)
        } ?: arrayListOf()
    }

    suspend fun getProgramLevels(): ArrayList<MasterDataResponseModel> {
        return apiRequest {
            registrationController.getProgramLevels()
        } ?: arrayListOf()
    }

    suspend fun getEducationPatterns(): ArrayList<MasterDataResponseModel> {
        return apiRequest {
            registrationController.getEducationPatterns()
        } ?: arrayListOf()
    }

    suspend fun getSpecializations(): ArrayList<MasterDataResponseModel> {
        return apiRequest {
            registrationController.getSpecializations()
        } ?: arrayListOf()
    }

    suspend fun getAllPatterns(): List<CommonDropdownModel>? {
        return apiRequest {
            registrationController.getAllPatterns()
        }
    }

    suspend fun getAllSpecializations(): List<CommonDropdownModel>? {
        return apiRequest {
            registrationController.getAllSpecializations()
        }
    }
}