package com.ramanbyte.services

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.MasterRepository
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.DI_ACTIVITY_CONTEXT
import com.ramanbyte.utilities.KEY_LOGIN_LOGOUT_STATUS
import com.ramanbyte.utilities.StaticMethodUtilitiesKtx.setIMEINumber
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 * This worker is used to post the device info to the device management.
 */
class MangeUserDevice(val mContext: Context, workerParams: WorkerParameters) :
    Worker(mContext, workerParams),
    KodeinAware {
    private val parentKodein by kodein(mContext)

    private val masterRepository: MasterRepository by instance()

    override val kodein by Kodein.lazy(allowSilentOverride = true) {
        extend(
            this@MangeUserDevice.parentKodein,
            true,
            Copy.All
        )
        bind<Context>(DI_ACTIVITY_CONTEXT) with singleton { mContext }
        import(authModuleDependency, true)
    }



    override fun doWork(): Result {
        return try {
            setIMEINumber(mContext)
            val statusType = inputData.getInt(KEY_LOGIN_LOGOUT_STATUS, -1)
            if (statusType == 1) {
                manageUserDevicesForLogIn(statusType)
            } else {
                manageUserDevicesForLogOut(statusType)
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
            Result.failure()
        }
    }

    private fun manageUserDevicesForLogIn(activeStatus: Int) {
        CoroutineUtils.main {
            try {
                masterRepository.insertLogout(activeStatus)
            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
            }
        }
    }

    private fun manageUserDevicesForLogOut(activeStatus: Int) {
        CoroutineUtils.main {
            try {
                masterRepository.updateLogout(activeStatus)
                masterRepository.deleteUser()
            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e);
            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
            }
        }
    }
}