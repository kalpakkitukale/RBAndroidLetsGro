package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.emla.data_layer.network.api_layer.LoginApiController
import com.ramanbyte.emla.data_layer.room.ApplicationDatabase
import com.ramanbyte.emla.data_layer.room.entities.UserEntity
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.ChangePasswordModel
import com.ramanbyte.emla.models.request.ForgetPasswordModel
import com.ramanbyte.emla.models.request.LoginRequest
import com.ramanbyte.emla.models.request.PledgeStatusRequest
import com.ramanbyte.utilities.KEY_STAFF
import com.ramanbyte.utilities.replicate
import org.kodein.di.generic.instance

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class MasterRepository(val mContext: Context) : BaseRepository(mContext) {
    private val applicationDatabase: ApplicationDatabase by instance()
    private val loginApiController : LoginApiController by instance()

    suspend fun doLogin(loginRequest: LoginRequest): UserModel? {

        //UnComment once Api Published
        val userModel = apiRequest {

            loginApiController.doLogin(loginRequest)
        }

        userModel?.apply {
            if (userModel.userType == KEY_STAFF) {
                applicationDatabase.getUserDao().apply {
                    delete()
                    insert(userModel.replicate<UserModel, UserEntity>()!!)
                }
            }
        }
        return userModel
    }

    suspend fun updatePledgeStatus(pledgeStatusRequest: PledgeStatusRequest) {
        val status = apiRequest {
            loginApiController.updatePledgeStatus(pledgeStatusRequest.apply {
                userId = getCurrentUser()?.userId ?: 0
            })
        }
        //If user details is available and pledge is taken then update the pledge field
        if (status == 1) {
            applicationDatabase.getUserDao().updateCurrentUserStatus()
        }
    }

    fun getCurrentUser(): UserModel? {
        return applicationDatabase.getUserDao().getCurrentUser()?.replicate<UserEntity, UserModel>()
    }


    suspend fun forgetPassword(forgetPasswordModel: ForgetPasswordModel): String? {
        return apiRequest {
            eMlaApiController.forgotPassword(forgetPasswordModel)
        }
    }

    suspend fun changePassword(changePasswordModel: ChangePasswordModel): String? {
        return apiRequest {
            eMlaApiController.changePassword(changePasswordModel)
        }
    }
}