package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.emla.data_layer.network.api_layer.TransactionApiController
import com.ramanbyte.emla.models.request.InsertTransactionRequestModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.KEY_ANDROID
import com.ramanbyte.utilities.KEY_BLANK
import org.kodein.di.generic.instance

class TransactionRepository(val mContext: Context) : BaseRepository(mContext) {

    private val transactionApiController: TransactionApiController by instance()

    suspend fun insertTransaction(insertTransactionRequestModel: InsertTransactionRequestModel): Int {
        val loginResponseModel = applicationDatabase?.getUserDao().getCurrentUser()

        val deviceDetails = ""

        insertTransactionRequestModel.created_By = loginResponseModel!!.userId
        insertTransactionRequestModel.modify_By = loginResponseModel.userId
        insertTransactionRequestModel.added_By = loginResponseModel.userId
        insertTransactionRequestModel.user_Id = loginResponseModel.userId
        insertTransactionRequestModel.registrationId = loginResponseModel.userId

        insertTransactionRequestModel.deviceId = 0
        insertTransactionRequestModel.deviceType = KEY_ANDROID
        insertTransactionRequestModel.clientName = KEY_BLANK

        AppLog.debugLog("insertTransactionRequestModel ---- $insertTransactionRequestModel")

        val insertTransactionString = apiRequest {
            transactionApiController.postTransactionDetails(insertTransactionRequestModel)
        }
        AppLog.debugLog("insertTransactionString ---- $insertTransactionString")
        return insertTransactionString ?: 0
    }
}