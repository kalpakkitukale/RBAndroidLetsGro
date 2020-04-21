package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import com.ramanbyte.R
import com.ramanbyte.data_layer.SharedPreferencesDatabase.KEY_DEVICE_IMEI
import com.ramanbyte.data_layer.SharedPreferencesDatabase.KEY_DEVICE_INSTANCE_ID
import com.ramanbyte.data_layer.SharedPreferencesDatabase.KEY_IS_DEVICE_TOKEN_SET
import com.ramanbyte.data_layer.SharedPreferencesDatabase.getIntPref
import com.ramanbyte.data_layer.SharedPreferencesDatabase.getStringPref
import com.ramanbyte.data_layer.SharedPreferencesDatabase.setBoolPref
import com.ramanbyte.data_layer.SharedPreferencesDatabase.setIntPref
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.emla.data_layer.network.api_layer.LoginApiController
import com.ramanbyte.emla.data_layer.room.entities.UserEntity
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.*
import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.DateUtils.getCurDate
import com.ramanbyte.utilities.IpUtility.Companion.getIpAddress
import org.kodein.di.generic.instance

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class MasterRepository(val mContext: Context) : BaseRepository(mContext) {

    private val loginApiController: LoginApiController by instance()

    suspend fun doLogin(loginRequest: LoginRequest): UserModel? {

        //UnComment once Api Published
        val userModel = apiRequest {

            loginApiController.doLogin(loginRequest)
        }

        userModel?.apply {
            if (userModel.userType == KEY_STUDENT) {
                applicationDatabase.getUserDao().apply {
                    delete()
                    insert(userModel.replicate<UserModel, UserEntity>()!!)
                }
            }
        }
        return userModel
    }

    suspend fun updatePledgeStatus(pledgeStatusRequest: PledgeStatusRequest): Int {
        val status = apiRequest {
            loginApiController.updatePledgeStatus(pledgeStatusRequest.apply {
                userId = getCurrentUser()?.userId ?: 0
            })
        }
        //If user details is available and pledge is taken then update the pledge field
        if (status == 1) {
            applicationDatabase.getUserDao().updateCurrentUserStatus()
        }
        return status ?: 0
    }

    fun getCurrentUser(): UserModel? {
        return applicationDatabase.getUserDao().getCurrentUser()?.replicate<UserEntity, UserModel>()
    }

    fun deleteUser() {
        applicationDatabase.getUserDao().delete()
    }

    suspend fun forgetPassword(forgetPasswordModel: ForgetPasswordModel): String? {
        return apiRequest {
            loginApiController.forgotPassword(forgetPasswordModel)
        }
    }

    suspend fun changePassword(changePasswordModel: ChangePasswordModel): String? {
        return apiRequest {
            loginApiController.changePassword(changePasswordModel)
        }
    }

    suspend fun insertLogout(activeStatus: Int) {

        val userId = getCurrentUser()?.userId ?: 0
        var fcmToken: String? = KEY_BLANK
        val deviceToken = getStringPref(KEY_DEVICE_INSTANCE_ID)
        if (deviceToken.isNullOrEmpty()) {
            fcmToken = KEY_BLANK
            setBoolPref(KEY_IS_DEVICE_TOKEN_SET, false)
        } else {
            fcmToken = deviceToken
            setBoolPref(KEY_IS_DEVICE_TOKEN_SET, true)
        }

        val deviceId = apiRequest {
            loginApiController.insertLogout(ManageUserDeviceModel().apply {
                this.fcmToken = fcmToken
                deviceName = getDeviceManufacturer() ?: KEY_BLANK
                deviceModel = getDeviceModelName() ?: KEY_BLANK
                deviceOS = getDeviceVersion() ?: KEY_BLANK
                imei = getStringPref(KEY_DEVICE_IMEI)!!
                createdDateTime = getCurDate() ?: KEY_BLANK
                lastUsedDateTime = getCurDate() ?: KEY_BLANK
                modifiedDateTime = getCurDate() ?: KEY_BLANK
                this.userId = getCurrentUser()?.userId ?: 0
                modifiedByUserId = getCurrentUser()?.userId ?: 0
                deviceIP = getIpAddress()
                this.activeStatus = activeStatus
                appVersion = getAppVersion(mContext) ?: KEY_BLANK
                logFrom = BindingUtils.string(R.string.android)
                logStatus = KEY_LOGIN_STATUS_YES
            }, userId)
        }
        /*
        * set device id into shared preference
        * */
        setIntPref(KEY_DEVICE_ID, deviceId!!)
    }

    suspend fun updateLogout(activeStatus: Int) {

        val userId = getCurrentUser()?.userId ?: 0
        var fcmToken: String? = KEY_BLANK
        val deviceToken = getStringPref(KEY_DEVICE_INSTANCE_ID)
        if (deviceToken.isNullOrEmpty()) {
            fcmToken = KEY_BLANK
            setBoolPref(KEY_IS_DEVICE_TOKEN_SET, false)
        } else {
            fcmToken = deviceToken
            setBoolPref(KEY_IS_DEVICE_TOKEN_SET, true)
        }

        val manageUserDeviceModel = ManageUserDeviceModel()

        manageUserDeviceModel.apply {
            this.fcmToken = fcmToken
            deviceName = getDeviceManufacturer() ?: KEY_BLANK
            deviceModel = getDeviceModelName() ?: KEY_BLANK
            deviceOS = getDeviceVersion() ?: KEY_BLANK
            imei = getStringPref(KEY_DEVICE_IMEI)!!
            createdDateTime = getCurDate() ?: KEY_BLANK
            lastUsedDateTime = getCurDate() ?: KEY_BLANK
            modifiedDateTime = getCurDate() ?: KEY_BLANK
            this.userId = getCurrentUser()?.userId ?: 0
            modifiedByUserId = getCurrentUser()?.userId ?: 0
            deviceIP = getIpAddress()
            this.activeStatus = activeStatus
            appVersion = getAppVersion(mContext) ?: KEY_BLANK
            logFrom = BindingUtils.string(R.string.android)
            logStatus = KEY_LOGIN_STATUS_NO
            deviceId = getIntPref(KEY_DEVICE_ID)
        }

        apiRequest {
            loginApiController.updateLogout(manageUserDeviceModel, userId)
        }

    }
}