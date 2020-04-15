package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.emla.data_layer.network.api_layer.RegistrationController
import com.ramanbyte.emla.models.RegistrationModel
import org.kodein.di.generic.instance

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 15-04-2020
 */
class RegistrationRepository(val mContext: Context) : BaseRepository(mContext) {

    private val registrationController : RegistrationController by instance()

    suspend fun register(registrationModel: RegistrationModel): String? {
        return apiRequest {
        registrationController.register(registrationModel)
    }
    }


}