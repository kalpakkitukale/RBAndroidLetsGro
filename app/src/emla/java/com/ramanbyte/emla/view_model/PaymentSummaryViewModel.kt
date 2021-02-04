package com.ramanbyte.emla.view_model

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import com.airpay.airpaysdk_simplifiedotp.ResponseMessage
import com.payu.india.Extras.PayUChecksum
import com.payu.india.Model.PaymentParams
import com.payu.india.Model.PostData
import com.payu.india.Payu.PayuConstants
import com.payu.india.Payu.PayuErrors
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.TransactionRepository
import com.ramanbyte.emla.data_layer.repositories.MasterRepository
import com.ramanbyte.emla.models.PayuGatewayModel
import com.ramanbyte.emla.models.request.InsertTransactionRequestModel
import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.StaticMethodUtilitiesKtx.getRandomAlphaNumericString
import org.kodein.di.generic.instance


/**
 * @author Mansi Manakiki Mody
 */
class PaymentSummaryViewModel(mContext: Context) : BaseViewModel(mContext = mContext) {

    val masterRepository: MasterRepository by instance()
    val transactionRepository: TransactionRepository by instance()
    val payuGatewayModel = PayuGatewayModel()
    var payuGatewayModelLiveData: MutableLiveData<PayuGatewayModel?> = MutableLiveData(null)

    val airPayBundle = Bundle()
    var airPayBundleLiveData: MutableLiveData<Bundle?> = MutableLiveData(null)
    internal var resp: ResponseMessage? = null

    /* val radioAirpayChecked = ObservableBoolean(false)
     val radioPayuChecked = ObservableBoolean(false)*/

    var paymentResponse: MutableLiveData<String> = MutableLiveData()

    var examFormId = 0
    var campusId = 0
    var campusNameLiveData = MutableLiveData<String>()
    var programId = 0
    var programNameLiveData = MutableLiveData<String>()
    var admissionYearLiveData = MutableLiveData<String>()
    var amountLiveData = MutableLiveData<String>("0.0")
    var paymentStepIntegration = ""

    var paymentOptionErrorLiveData = MutableLiveData<Boolean>(false)
    var transactionResponseIdLiveData = MutableLiveData<Int>(0)
    var isAirPayChecked = false
    var isPayuChecked = false

    val loggedInUserModel = masterRepository.getCurrentUser()

    var insertTransactionRequestModel = InsertTransactionRequestModel()

    override var noInternetTryAgain: () -> Unit = {
        AppLog.errorLog("No Internet. Try Again")
    }



    @SuppressLint("DefaultLocale")
    fun addTransaction(
        initiateTransaction: Boolean,
        showLoader: Boolean,
        terminateTransaction: Boolean
    ) {
        CoroutineUtils.main {
            try {
                val userName =
                    "${loggedInUserModel?.firstName ?: ""} ${loggedInUserModel?.lastName ?: ""}"
                loaderMessageLiveData.set(BindingUtils.string(R.string.please_wait))
                isLoaderShowingLiveData.postValue(showLoader)
                insertTransactionRequestModel.apply {
                    app_Id =
                        this@PaymentSummaryViewModel.programId // app id as program id. No application ID
                    campusId = this@PaymentSummaryViewModel.campusId
                    programId = this@PaymentSummaryViewModel.programId
                    programName = this@PaymentSummaryViewModel.programNameLiveData.value.toString()
                    amount_Paid = this@PaymentSummaryViewModel.amountLiveData.value.toString()
                    admissionYearId =
                        this@PaymentSummaryViewModel.admissionYearLiveData.value?.toInt() ?: 0
                    if (initiateTransaction) {
                        transactionStatus = KEY_PENDING_TRANSACTION_STATUS
                        paymentGateway = KEY_BLANK
                        createdDate = DateUtils.getCurDate(
                            DATE_TIME_SECONDS_PATTERN
                        )!!
                    }
                    paymentStepIntegration = this@PaymentSummaryViewModel.paymentStepIntegration
                    studentName = userName

                    emailId = loggedInUserModel?.emailId ?: ""
                    paymentDescription = BindingUtils.string(
                        R.string.admission_form_transaction_custom_message,
                        userName.toUpperCase(),
                        this@PaymentSummaryViewModel.amountLiveData.value.toString().toUpperCase()
                    )
                }
                val id = transactionRepository.insertTransaction(
                    insertTransactionRequestModel
                )

                isLoaderShowingLiveData.postValue(false)

                if (!terminateTransaction) {
                    AppLog.infoLog("Response Id ------ $id")
                    if (initiateTransaction) {
                        if (id > 0) {
                            insertTransactionRequestModel.id = id
                            initiatePayment()
                        } else {
                            //payment initiate failed show failed dialog
                            transactionResponseIdLiveData.postValue(-1)
                        }
                    } else {
                        //payment success. Redirect to Success page
                        transactionResponseIdLiveData.postValue(id)
                    }
                }


            } catch (e: ApiException) {
                AppLog.errorLog(e.message.toString())
                e.printStackTrace()
                isLoaderShowingLiveData.postValue(false)

                //Showing Alert dialog for api response
                      setAlertDialogResourceModelMutableLiveData(
                              e.message.toString(),
                              true,
                              BindingUtils.string(R.string.strOk),
                              {
                                  isAlertDialogShown.postValue(false)
                              })

                isAlertDialogShown.postValue(true)
            } catch (e: NoInternetException) {
                AppLog.errorLog(e.message.toString())
                e.printStackTrace()
                isLoaderShowingLiveData.postValue(false)

                //Showing Alert dialog for api response
                   setAlertDialogResourceModelMutableLiveData(
                           e.message.toString(),
                           true,
                           BindingUtils.string(R.string.strOk),
                           {
                               isAlertDialogShown.postValue(false)
                           })
                isAlertDialogShown.postValue(true)
            } catch (e: Exception) {
                AppLog.errorLog(e.message.toString())
                e.printStackTrace()
                isLoaderShowingLiveData.postValue(false)

                   //Showing Alert dialog for api response
                   setAlertDialogResourceModelMutableLiveData(
                           e.message.toString(),
                           true,
                           BindingUtils.string(R.string.strOk),
                           {
                               isAlertDialogShown.postValue(false)
                           })

                   isAlertDialogShown.postValue(true)

                isAlertDialogShown.postValue(true)
            }
        }
    }

    fun onPaymentProceed() {
        try {
            if (!NetworkConnectivity.isConnectedToInternet())
                throw NoInternetException(BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet))

            if (isAirPayChecked || isPayuChecked) {
                addTransaction(
                    initiateTransaction = true,
                    showLoader = false,
                    terminateTransaction = false
                )
            } else {
                paymentOptionErrorLiveData.value = true
            }
        } catch (e: NoInternetException) {
            e.printStackTrace()
            AppLog.errorLog(e.message.toString())
            isLoaderShowingLiveData.postValue(false)
            setAlertDialogResourceModelMutableLiveData(
                    e.message.toString(),
                    true,
                    BindingUtils.string(R.string.strOk), positiveButtonClickFunctionality = {
                        isAlertDialogShown.postValue(false)
                    }
            )
            isAlertDialogShown.postValue(true)
        }
    }

    private fun initiatePayment() {

        when {
            isPayuChecked -> {
                generatePayUParam()
                generateHashFromSDK(
                    /*if (BuildConfig.DEBUG) {*/
                    BindingUtils.string(R.string.payU_salt_debug)
                    /*} else {
                        BindingUtils.string(R.string.payU_salt_release)
                    }*/
                )
            }
            isAirPayChecked -> {
                generateBundleForAirPay()
            }
            else -> paymentOptionErrorLiveData.postValue(true)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun generateBundleForAirPay() {

        val studentName =
            "${loggedInUserModel?.firstName ?: ""} ${loggedInUserModel?.lastName ?: ""}"
        /*if (BuildConfig.DEBUG) {*/
        airPayBundle.putString("USERNAME", BindingUtils.string(R.string.airpay_username_debug))
        airPayBundle.putString("PASSWORD", BindingUtils.string(R.string.airpay_password_debug))
        airPayBundle.putString("SECRET", BindingUtils.string(R.string.airpay_secret_key_debug))
        airPayBundle.putString(
            "MERCHANT_ID",
            BindingUtils.string(R.string.airpay_merchant_key_debug)
        )
        /*} else {
            airPayBundle.putString(
                "USERNAME",
                BindingUtils.string(R.string.airpay_username_release)
            )
            airPayBundle.putString(
                "PASSWORD",
                BindingUtils.string(R.string.airpay_password_release)
            )
            airPayBundle.putString(
                "SECRET",
                BindingUtils.string(R.string.airpay_secret_key_release)
            )
            airPayBundle.putString(
                "MERCHANT_ID",
                BindingUtils.string(R.string.airpay_merchant_key_release)
            )
        }*/

        airPayBundle.putString("PHONE", "1234567890")

        airPayBundle.putString("EMAIL", loggedInUserModel?.emailId)

        airPayBundle.putString("FIRSTNAME", loggedInUserModel?.firstName)
        airPayBundle.putString("LASTNAME", loggedInUserModel?.lastName)
        airPayBundle.putString("ADDRESS", "")
        airPayBundle.putString("CITY", "")
        airPayBundle.putString("STATE", "")
        airPayBundle.putString("COUNTRY", "")
        airPayBundle.putString("PIN_CODE", "")
        airPayBundle.putString("ORDER_ID", getRandomAlphaNumericString(16))
        airPayBundle.putString("AMOUNT", amountLiveData.value ?: "0.0")
        /*airPayBundle.putString("AMOUNT", "2.0")*/
        airPayBundle.putString("CURRENCY", "356")
        airPayBundle.putString("ISOCURRENCY", "INR")
        airPayBundle.putString("CHMOD", "pg_nb_upi")
        /*airPayBundle.putString(
            "CUSTOMVAR",
            StaticHelpers.Constants.KEY_APPLICATION_FORM_TRANSACTION_TYPE + " is paid by ${loggedInUserModel?.firstName} ${loggedInUserModel?.lastName}"
        ) // Custom Message. Only Alphanumeric, sapce and = allowed.*/
        airPayBundle.putString(
            "CUSTOMVAR",
            BindingUtils.string(
                R.string.admission_form_transaction_custom_message,
                studentName.toUpperCase(),
                amountLiveData.value.toString().toUpperCase()
            )
        ) // Custom Message. Only Alphanumeric, space and = allowed.
        airPayBundle.putString("TXNSUBTYPE", "")
        airPayBundle.putString("WALLET", "0")


        // Live Success URL Merchant Id -
        airPayBundle.putString("SUCCESS_URL", "https://pibm.net/AirPayResponse.aspx")
        airPayBundle.putParcelable("RESPONSEMESSAGE", resp as Parcelable?)

        AppLog.infoLog("INPUT PARAMS-->>$airPayBundle")

        airPayBundleLiveData.postValue(airPayBundle)
    }

    private fun generateHashFromSDK(payUSalt: String) {

        payuGatewayModel.apply {

            val checksum = PayUChecksum().also { payuCheckSum ->

                mPaymentParams.apply {

                    payuCheckSum.amount = amount
                    payuCheckSum.key = key
                    payuCheckSum.txnid = txnId
                    payuCheckSum.email = email
                    payuCheckSum.salt = payUSalt
                    payuCheckSum.productinfo = productInfo
                    payuCheckSum.firstname = firstName
                    payuCheckSum.udf1 = udf1
                    payuCheckSum.udf2 = udf2
                    payuCheckSum.udf3 = udf3
                    payuCheckSum.udf4 = udf4
                    payuCheckSum.udf5 = udf5
                }
            }

            // checksum for payemnt related details
            // var1 should be either user credentials or default
            val var1 = if (mPaymentParams.userCredentials == null) {
                PayuConstants.DEFAULT
            } else {
                mPaymentParams.userCredentials
            }
            val key = mPaymentParams.key

            var postData = checksum.hash

            if (postData.code == PayuErrors.NO_ERROR) {
                //payuHashes.paymentHash = postData.result
                payuHashes.paymentHash = postData.result
                AppLog.debugLog("Payment Hash ----- ${postData.result}")
            }

            postData =
                calculateHash(
                    key,
                    PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK,
                    var1,
                    payUSalt
                )

            if (postData.code == PayuErrors.NO_ERROR) // Assign post data first then check for success
            //payuHashes.paymentRelatedDetailsForMobileSdkHash = postData.result
                payuHashes.paymentRelatedDetailsForMobileSdkHash =
                    postData.result
            AppLog.infoLog("PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK ----- ${postData.result}")

            //vas
            postData =
                calculateHash(
                    key,
                    PayuConstants.VAS_FOR_MOBILE_SDK,
                    PayuConstants.DEFAULT,
                    payUSalt
                )
            if (postData.code == PayuErrors.NO_ERROR)
            //payuHashes.vasForMobileSdkHash = postData.result
                payuHashes.vasForMobileSdkHash = postData.result
            AppLog.infoLog("VAS_FOR_MOBILE_SDK ----- ${postData.result}")

            // getIbibocodes
            postData = calculateHash(
                key,
                PayuConstants.GET_MERCHANT_IBIBO_CODES,
                PayuConstants.DEFAULT,
                payUSalt
            )
            if (postData.code == PayuErrors.NO_ERROR)
            //payuHashes.merchantIbiboCodesHash = postData.result
                payuHashes.merchantIbiboCodesHash = postData.result
            AppLog.infoLog("GET_MERCHANT_IBIBO_CODES ----- ${postData.result}")

            if (!var1!!.contentEquals(PayuConstants.DEFAULT)) {
                // get user card
                postData = calculateHash(key, PayuConstants.GET_USER_CARDS, var1, payUSalt)
                if (postData.code == PayuErrors.NO_ERROR) // todo rename storedc ard
                    payuHashes.storedCardsHash = postData.result
                //payuHashes.storedCardsHash = postData.result
                AppLog.infoLog("GET_USER_CARDS ----- ${postData.result}")

                // save user card
                postData = calculateHash(key, PayuConstants.SAVE_USER_CARD, var1, payUSalt)
                if (postData.code == PayuErrors.NO_ERROR)
                    payuHashes.saveCardHash = postData.result
                //payuHashes.saveCardHash = postData.result
                AppLog.infoLog("SAVE_USER_CARD ----- ${postData.result}")

                // delete user card
                postData = calculateHash(key, PayuConstants.DELETE_USER_CARD, var1, payUSalt)
                if (postData.code == PayuErrors.NO_ERROR)
                    payuHashes.deleteCardHash = postData.result
                //payuHashes.deleteCardHash = postData.result
                AppLog.infoLog("DELETE_USER_CARD ----- ${postData.result}")

                // edit user card
                postData = calculateHash(key, PayuConstants.EDIT_USER_CARD, var1, payUSalt)
                if (postData.code == PayuErrors.NO_ERROR)
                    payuHashes.editCardHash = postData.result
                //payuHashes.editCardHash = postData.result
                AppLog.infoLog("EDIT_USER_CARD ----- ${postData.result}")
            }

            if (mPaymentParams.offerKey != null) {
                postData =
                    calculateHash(key, PayuConstants.OFFER_KEY, mPaymentParams.offerKey, payUSalt)
                if (postData.code == PayuErrors.NO_ERROR) {
                    payuHashes.checkOfferStatusHash = postData.result
                    //payuHashes.checkOfferStatusHash = postData.result
                    AppLog.infoLog("OFFER_KEY ----- ${postData.result}")
                }
            }

            if (mPaymentParams.offerKey != null) {
                postData = calculateHash(
                    key,
                    PayuConstants.CHECK_OFFER_STATUS,
                    mPaymentParams.offerKey,
                    payUSalt
                )
                if (postData.code == PayuErrors.NO_ERROR) {
                    payuHashes.checkOfferStatusHash = postData.result
                    //payuHashes.checkOfferStatusHash = postData.result
                    AppLog.infoLog("CHECK_OFFER_STATUS ----- ${postData.result}")
                }
            }
        }

        payuGatewayModelLiveData.postValue(payuGatewayModel)
    }

    private fun calculateHash(
        payUKey: String,
        payUCommand: String,
        payUVar1: String,
        payUSalt: String
    ): PostData {
        val checksum = PayUChecksum().apply {
            key = payUKey
            command = payUCommand
            var1 = payUVar1
            salt = payUSalt
        }
        return checksum.hash
    }

    @SuppressLint("DefaultLocale")
    private fun generatePayUParam() {

        val merchantKey =
            /*if (BuildConfig.DEBUG) {*/
            BindingUtils.string(R.string.payU_merchant_key_debug)
        /*} else {
            BindingUtils.string(R.string.payU_merchant_key_release)
        }*/

        val payUEnvironment =
            /*if (BuildConfig.DEBUG) {*/
            PayuConstants.STAGING_ENV
        /*} else {
            PayuConstants.PRODUCTION_ENV
        }*/

        val loggedInUserModel = masterRepository.getCurrentUser()

        val payUCredentials = "$merchantKey:${loggedInUserModel?.emailId}"
        val mPaymentParams = PaymentParams().apply {
            key = merchantKey
            amount = amountLiveData.value ?: "0.0" // need to set final course fee
            firstName =
                "${loggedInUserModel?.firstName ?: ""} ${loggedInUserModel?.lastName
                    ?: ""}" // student name
            productInfo =
                BindingUtils.string(
                    R.string.admission_form_transaction_custom_message,
                    firstName.toUpperCase(),
                    amountLiveData.value.toString().toUpperCase()
                )
//                StaticHelpers.Constants.KEY_APPLICATION_FORM_TRANSACTION_TYPE + " is paid by $firstName"

            email = loggedInUserModel?.emailId // student email
            //using dummy phone numberr
            phone = "1234567890"
         //   phone = loggedInUserModel?.userMobileNo
            /*  Transaction Id should be kept unique for each transaction. */
            txnId = "" + System.currentTimeMillis()

            /*
            * Surl --> Success url is where the transaction response is posted by PayU on successful transaction
            * Furl --> Failre url is where the transaction response is posted by PayU on failed transaction
            * */
            surl = "https://payuresponse.firebaseapp.com/success"
            furl = "https://payuresponse.firebaseapp.com/failure"
            notifyURL = surl  //for lazy pay

            /*
            * udf1 to udf5 are options params where you can pass additional information related to transaction.
            * If you don't want to use it, then send them as empty string like, udf1=""
            * */
            udf1 = "udf1"
            udf2 = "udf2"
            udf3 = "udf3"
            udf4 = "udf4"
            udf5 = "udf5"

            /*
             * These are used for store card feature. If you are not using it then user_credentials = "default"
             * user_credentials takes of the form like user_credentials = "merchant_key : user_id"
             * here merchant_key = your merchant key,
             * user_id = unique id related to user like, email, phone number, etc.
             * */
            userCredentials = payUCredentials
        }

        payuGatewayModel.apply { payuConfig.environment = payUEnvironment }

//        val payuConfig = PayuConfig().apply { environment = payUEnvironment }

        payuGatewayModel.mPaymentParams = mPaymentParams
    }


}