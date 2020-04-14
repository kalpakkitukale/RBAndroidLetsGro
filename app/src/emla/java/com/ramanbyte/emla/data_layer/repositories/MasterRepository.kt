package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.emla.data_layer.network.api_layer.eMlaApiController
import com.ramanbyte.emla.data_layer.room.ApplicationDatabase
import com.ramanbyte.emla.data_layer.room.entities.UserEntity
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.LoginRequest
import com.ramanbyte.utilities.KEY_STAFF
import com.ramanbyte.utilities.replicate
import org.kodein.di.generic.instance

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class MasterRepository(val mContext: Context) : BaseRepository(mContext) {
    private val applicationDatabase: ApplicationDatabase by instance()
    private val eMlaApiController : eMlaApiController by instance()

    suspend fun doLogin(loginRequest: LoginRequest): UserModel? {

        //UnComment once Api Published
        val userModel = apiRequest {

            eMlaApiController.doLogin(loginRequest)
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
}