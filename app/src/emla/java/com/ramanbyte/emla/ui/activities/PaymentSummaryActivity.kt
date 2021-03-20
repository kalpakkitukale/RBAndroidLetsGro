package com.ramanbyte.emla.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.airpay.airpaysdk_simplifiedotp.AirpayActivity
import com.airpay.airpaysdk_simplifiedotp.Transaction
import com.paytm.pgsdk.PaytmConstants
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import com.payu.india.Payu.Payu
import com.payu.india.Payu.PayuConstants
import com.payu.payuui.Activity.PayUBaseActivity
import com.ramanbyte.AppController
import com.ramanbyte.BuildConfig
import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.data_layer.SharedPreferencesDatabase
import com.ramanbyte.databinding.ActivityPaymentSummaryBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.models.PayuGatewayModel
import com.ramanbyte.emla.models.response.CartResponseModel
import com.ramanbyte.emla.view_model.PaymentSummaryViewModel
import com.ramanbyte.utilities.*
import org.json.JSONObject
import java.util.*

class PaymentSummaryActivity : BaseActivity<ActivityPaymentSummaryBinding, PaymentSummaryViewModel>(
    authModuleDependency
), PaytmPaymentTransactionCallback/*, KodeinAware*/ {

    /* private val _parentKodein by kodein()

     override val kodein: Kodein by retainedKodein {
         extend(_parentKodein, copy = Copy.All)
         bind<Context>(ACTIVITY_CONTEXT) with singleton { this@PaymentSummaryActivity }
     }
     private val factory: BaseViewModelFactory by instance()*/

    private var paymentSummaryBinding: ActivityPaymentSummaryBinding? = null
    private var paymentSummaryViewModel: PaymentSummaryViewModel? = null
    private val AIR_PAY_REQUEST_CODE = 0x10
    private var mContext: Context? = null
    private var paymentType: String = ""
    private var transactionRefId: String = ""
    var transactionStatus: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  initViews()
        customRadioClickListener()
    }

    companion object {
        fun openPaymentActivity(
            context: Context,
            amount: String,
            cartList: ArrayList<CartResponseModel>?
        ): Intent {
            val intent = Intent(context, PaymentSummaryActivity::class.java)
            //Add Required fields in Extras
            intent.putExtra(keyAmount, amount)
            intent.putExtra(keyCartData, cartList!!)
            return intent
        }
    }

    override fun initiate() {
        /*  paymentSummaryBinding =
              DataBindingUtil.setContentView(
                  this@PaymentSummaryActivity,
                  R.layout.activity_payment_summary
              )*/
        initToolBar()
        viewModelOperation()
        Payu.setInstance(this)

        /*   paymentSummaryViewModel = ViewModelProviders.of(
               this@PaymentSummaryActivity, factory
           ).get(PaymentSummaryViewModel::class.java)
   */
        //    ProgressLoader(this@PaymentSummaryActivity, paymentSummaryViewModel!!)

        layoutBinding?.apply {
            paymentSummaryViewModel = this@PaymentSummaryActivity.viewModel
            lifecycleOwner = this@PaymentSummaryActivity
        }

        viewModel?.apply {

            ProgressLoader(this@PaymentSummaryActivity, this)
            AlertDialog(this@PaymentSummaryActivity, this)

            intent?.extras?.apply {
                amountLiveData.value = getString(keyAmount) ?: "0"
                cartListData =
                    getParcelableArrayList<CartResponseModel>(keyCartData) as ArrayList<CartResponseModel>

            }
        }

        viewModel?.apply {
            airPayBundleLiveData.observe(this@PaymentSummaryActivity,
                Observer { airPayBundle ->
                    if (airPayBundle != null) {
                        launchAirPayUI(airPayBundle)
                    }
                })

            payuGatewayModelLiveData.observe(
                this@PaymentSummaryActivity,
                Observer { payuGatewayModel ->

                    if (payuGatewayModel != null) {
                        launchPayUUI(payuGatewayModel)
                    }
                })

            paymentOptionErrorLiveData.observe(this@PaymentSummaryActivity, Observer { isError ->

                if (isError) {
                    layoutBinding?.contentLayout?.snackbar(BindingUtils.string(R.string.select_payment_gateway))
                    paymentOptionErrorLiveData.value = false
                }
            })


            tranStatusLiveData.observe(this@PaymentSummaryActivity, Observer {
                it?.let {
                    transactionStatus = it
                }
            })

            transactionResponseIdLiveData.observe(this@PaymentSummaryActivity, Observer {

                if (it != 0) {
                    AppController.setEnterPageAnimation(this@PaymentSummaryActivity)
                    AppLog.infoLog("transactionResponseIdLiveData ----    $it")
                    if (it > 0 && transactionStatus!!.equals(KEY_SUCCESS)) {
                        // payment success
                        startActivityForResult(
                            PaymentStatusActivity.openPaymentStatusActivity(
                                this@PaymentSummaryActivity,
                                KEY_SUCCESS_TRANSACTION_STATUS,
                                it.toString(),
                                amountLiveData.value.toString(),
                                paymentType, transactionRefId, cartListData
                            ), PAYMENT_SUCCESSFUL_REQUEST_CODE
                        )

                    } else {
                        // fail
                        startActivity(
                            PaymentStatusActivity.openPaymentStatusActivity(
                                this@PaymentSummaryActivity,
                                KEY_FAIL_TRANSACTION_STATUS,
                                "",
                                amountLiveData.value.toString(),
                                paymentType,
                                transactionRefId,
                                cartListData
                            )
                        )
                    }
                    finish()
                }
            })

            /* radioPayuChecked.set(!radioAirpayChecked.get())
             radioAirpayChecked.set(!radioPayuChecked.get())*/
        }
    }

    private fun initToolBar() {
        setSupportActionBar(layoutBinding?.appbarLayout?.toolbar)
        val actionBar = supportActionBar
        actionBar!!.apply {
            setDisplayShowHomeEnabled(true) // show or hide the default home button
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        try {
            layoutBinding!!.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (appbarLayout.toolbar != null) {
                        appbarLayout.toolbar.elevation = 0.0f
                    }
                }
                appbarLayout.tvSubTitle.visibility = View.GONE
                appbarLayout.title = BindingUtils.string(R.string.fees_amount)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
    }
    fun launchAirPayUI(airpayBundle: Bundle) {
        val intent = Intent(this, AirpayActivity::class.java)
        intent.putExtras(airpayBundle)
        startActivityForResult(intent, AIR_PAY_REQUEST_CODE)
    }
    fun launchPayUUI(payuGatewayModel: PayuGatewayModel) {
        val intent = Intent(this, PayUBaseActivity::class.java)
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuGatewayModel.payuConfig)
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, payuGatewayModel.mPaymentParams)
        intent.putExtra(
            PayuConstants.SALT, if (BuildConfig.DEBUG) {
                BindingUtils.string(R.string.payU_salt_debug)
            } else {
                BindingUtils.string(R.string.payU_salt_release)
            }
        )
        intent.putExtra(PayuConstants.PAYU_HASHES, payuGatewayModel.payuHashes)
        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE)
    }
    @SuppressLint("DefaultLocale")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_CANCELED -> {
                // payment cancelled
                viewModel?.insertTransactionRequestModel?.apply {
                    transactionStatus =
                        KEY_CANCEL_TRANSACTION_STATUS
                }

                viewModel?.addTransaction(
                    initiateTransaction = false,
                    showLoader = true,
                    terminateTransaction = true,
                    cartList = viewModel!!.cartListData, isSuccessTransaction = false
                )
            }
            else -> {
                var isPaymentSuccessFul = true
                when (requestCode) {
                    PayuConstants.PAYU_REQUEST_CODE -> {
                        viewModel?.insertTransactionRequestModel?.apply {
                            paymentGateway = keyPaymentGatewayPayUBiz
                            if (data != null) {
                                try {
                                    val payResponse = data.getStringExtra(keyPayuResponseStatus)!!
                                    if (payResponse.isNotEmpty()) {
                                        val payUObject = JSONObject(payResponse)
                                        if (payUObject.has(keyPayuStatus)) {
                                            if (payUObject.getString(keyPayuStatus) == keyPayuSuccess) {
                                                var payuTransactionMode =
                                                    payUObject.getString(keyPayuTransactionMode)
                                                        .toLowerCase()
                                                viewModel?.tranStatusLiveData?.postValue(
                                                    KEY_SUCCESS
                                                )
                                                paymentMethod = when (payuTransactionMode) {
                                                    "nb" -> keyInternetBanking
                                                    "cc" -> keyCreditCard
                                                    "db" -> keyDebitCard
                                                    "cash" -> keyCash
                                                    "emi" -> keyEmi
                                                    else -> ""
                                                }
                                                paymentType = paymentMethod
                                                transactionStatus =
                                                    KEY_SUCCESS
                                                transId = payUObject.getString("txnid") /*.toLong()*/
                                                transactionRefId = payUObject.getString("txnid")
                                            } else {
                                                // payment fail
                                                transactionStatus = KEY_FAIL_TRANSACTION_STATUS
                                                isPaymentSuccessFul = false
                                                viewModel?.tranStatusLiveData?.postValue(KEY_CANCEL_TRANSACTION_STATUS)
                                            }
                                        } else {
                                            // payment fail
                                            transactionStatus = KEY_FAIL_TRANSACTION_STATUS
                                            isPaymentSuccessFul = false
                                            viewModel?.tranStatusLiveData?.postValue(KEY_CANCEL_TRANSACTION_STATUS)
                                        }
                                    } else {
                                        // payment fail
                                        transactionStatus = KEY_FAIL_TRANSACTION_STATUS
                                        isPaymentSuccessFul = false
                                        viewModel?.tranStatusLiveData?.postValue(
                                            KEY_CANCEL_TRANSACTION_STATUS
                                        )
                                    }
                                } catch (e: java.lang.NullPointerException) {
                                    e.printStackTrace()
                                    AppLog.errorLog(e.message, e)
                                    transactionStatus =
                                        KEY_FAIL_TRANSACTION_STATUS
                                    isPaymentSuccessFul = false
                                    viewModel?.tranStatusLiveData?.postValue(
                                        KEY_CANCEL_TRANSACTION_STATUS
                                    )
                                }
                            } else {
                                // payment fail
                                transactionStatus =
                                    KEY_FAIL_TRANSACTION_STATUS
                                isPaymentSuccessFul = false
                            }
                        }
                    }
                    AIR_PAY_REQUEST_CODE -> {
                        viewModel?.insertTransactionRequestModel?.apply {
                            paymentGateway = keyPaymentGatewayAirPay
                            if (data != null) {
                                val transactionList = data.extras!!.getSerializable("DATA") as ArrayList<Transaction>
                                AppLog.infoLog("transactionList --------------     $transactionList")
                                if (transactionList.size > 0) {
                                    transactionList[0].apply {
                                        if (!status.isNullOrEmpty() && status.equals("200", true) && !statusmsg.isNullOrEmpty() && statusmsg.toLowerCase().equals("success")) {
                                            transactionStatus = KEY_SUCCESS_TRANSACTION_STATUS
                                            viewModel?.tranStatusLiveData?.postValue(KEY_SUCCESS)
                                        } else {
                                            transactionStatus = KEY_FAIL_TRANSACTION_STATUS
                                            isPaymentSuccessFul = false
                                            viewModel?.tranStatusLiveData?.postValue(KEY_CANCEL_TRANSACTION_STATUS)
                                        }
                                        flag = status
                                        paymentMethod = if (chmod != null) { // payment method
                                            AppLog.infoLog("CHMOD -> =$chmod")
                                            when (chmod) {
                                                "pg" -> keyCard
                                                "nb" -> keyInternetBanking
                                                "upi" -> keyUpi
                                                else -> KEY_NA
                                            }
                                        } else {
                                            KEY_NA
                                        }
                                        paymentType = paymentMethod

                                        if (transactionid != null && transactionid.isNotBlank()) {
                                            AppLog.infoLog("TXN ID -> =$transactionid")
                                            transId = transactionid/*.toLong()*/
                                            transactionRefId = transactionid
                                        }

                                    }
                                } else {
                                    // payment fail
                                    transactionStatus = KEY_FAIL_TRANSACTION_STATUS
                                    isPaymentSuccessFul = false
                                    viewModel?.tranStatusLiveData?.postValue(
                                        KEY_CANCEL_TRANSACTION_STATUS
                                    )
                                }
                            } else {
                                // payment fail
                                transactionStatus = KEY_FAIL_TRANSACTION_STATUS
                                isPaymentSuccessFul = false
                                viewModel?.tranStatusLiveData?.postValue(KEY_CANCEL_TRANSACTION_STATUS)
                            }
                        }
                    }
                    //here is code for the paytm
                    PAYTM_REQUEST_CODE -> {
                        viewModel?.insertTransactionRequestModel.apply {
                            data?.extras?.let { bundle ->
                                if (bundle.containsKey("response") && bundle.getString("response")?.isNotEmpty() == true) {
                                    val responseJson = JSONObject(bundle.getString("response").toString())
                                    if (responseJson.getString(PaytmConstants.STATUS) == "TXN_SUCCESS") {
                                    isPaymentSuccessFul = true
                                        SharedPreferencesDatabase.setStringPref(SharedPreferencesDatabase.TRANSACTION_ID, responseJson.getString(PaytmConstants.TRANSACTION_ID))
                                        SharedPreferencesDatabase.setStringPref(SharedPreferencesDatabase.TRANSACTION_MODE, responseJson.getString(PaytmConstants.PAYMENT_MODE))
                                        transactionStatus = KEY_SUCCESS_TRANSACTION_STATUS
                                        viewModel?.tranStatusLiveData?.postValue(KEY_SUCCESS)
                                    }
                                    responseJson.getString("ORDERID")?.let { it1 -> viewModel.transactionStatus(it1) }
                                }
                            }
                        }
                    }
                }
                viewModel?.addTransaction(
                    initiateTransaction = false,
                    showLoader = true,
                    terminateTransaction = false,
                    cartList = viewModel!!.cartListData, isSuccessTransaction = isPaymentSuccessFul
                )

            }
        }

        /*
        * This code is temporary until DescribedRadioButton get completed
        * */


    }

    fun customRadioClickListener() {
        viewModel?.apply {
            layoutBinding?.apply {
                radioAirpay.setOnClickListener {
                    /* isAirPayChecked = paymentSummaryBinding!!.radioAirpay.isChecked
                     paymentSummaryBinding!!.radioAirpay.isChecked = !isAirPayChecked
                     paymentSummaryBinding!!.radioPayu.isChecked = isAirPayChecked
                     if(paymentSummaryBinding!!.radioPayu.isChecked){
                         paymentSummaryBinding!!.radioPayu.isChecked = false
                     }*/
                    isAirPayChecked = radioAirpay.isChecked

                    if (!isAirPayChecked) {
                        radioAirpay.toggle()
                        radioPayu.isChecked = isAirPayChecked
                        radioPaytm.isChecked = isAirPayChecked
                        isAirPayChecked = true
                        isPayuChecked = false
                        isPaytmChecked = false
                    }
                }
                radioPayu.setOnClickListener {
                    layoutBinding!!.radioPayu.isChecked = true
                    if (layoutBinding!!.radioAirpay.isChecked) {
                        layoutBinding!!.radioAirpay.isChecked = false
                    }
                    isPayuChecked = layoutBinding!!.radioAirpay.isChecked
                    layoutBinding!!.radioPayu.isChecked = !isPayuChecked
                    layoutBinding!!.radioAirpay.isChecked = isPayuChecked
                    layoutBinding!!.radioPaytm.isChecked = isPayuChecked
                    isPayuChecked = radioPayu.isChecked
                    if (!isPayuChecked) {
                        radioPayu.toggle()
                        radioAirpay.isChecked = isPayuChecked
                        isPayuChecked = true
                        isAirPayChecked = false
                        isPaytmChecked = false
                    }
                }
                radioPaytm.setOnClickListener {
                    layoutBinding?.radioPaytm.isChecked = true

                    if (layoutBinding.radioAirpay.isChecked ||layoutBinding.radioPayu.isChecked){
                        layoutBinding.radioAirpay.isChecked = false
                        layoutBinding.radioPayu.isChecked = false
                    }
                    isPayuChecked = layoutBinding.radioPayu.isChecked
                    isAirPayChecked = layoutBinding.radioAirpay.isChecked
                    isPaytmChecked = layoutBinding.radioPaytm.isChecked
                    if (!isPaytmChecked){
                        isPaytmChecked = true
                        isPayuChecked = false
                        isAirPayChecked = false
                    }
                }
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override val viewModelClass: Class<PaymentSummaryViewModel> =
        PaymentSummaryViewModel::class.java
    override fun layoutId(): Int = R.layout.activity_payment_summary
    //payTm gateway releted operation done here.
    fun viewModelOperation() {
        viewModel.apply {
            transactionTokenLiveData.observe(this@PaymentSummaryActivity, Observer {
                it?.let {
                        val responseBody = it.body
                        val responseResult = it.body.resultInfo
                        val responseStatus = it.body.resultInfo.resultStatus
                        if (responseStatus.equals("f", true)) {
                            AlertUtilities.showInfoAlertDialog(
                                this@PaymentSummaryActivity,
                                responseResult.resultMsg
                            )
                        } else {
                            placePaytmOrder(responseBody.txnToken) //txnToken txn token nhi tya entity madhe
                        }


                }
            })

            tokenStatusLiveData.observe(this@PaymentSummaryActivity, Observer {
                it?.let {
                    if (it.body.resultInfo.resultStatus.equals("TXN_SUCCESS")) {
                        transactionStatus = KEY_SUCCESS
                        SharedPreferencesDatabase.setStringPref(SharedPreferencesDatabase.TRANSACTION_ID, it.body.txnId)
                        SharedPreferencesDatabase.setStringPref(SharedPreferencesDatabase.TRANSACTION_MODE, getTransactionMode(it.body.paymentMode))
                      viewModel?.insertTransactionRequestModel.apply {
                            transactionStatus = KEY_SUCCESS_TRANSACTION_STATUS
                            viewModel.tranStatusLiveData.postValue(KEY_SUCCESS)
                            paymentGateway = keyPaymentGatewayPayTmPay
                            paymentMethod = getTransactionMode(it.body.paymentMode)
                            transId = it.body.txnId
                            transId = it.body.txnId

                          paymentType = getTransactionMode(it.body.paymentMode)
                          transactionRefId = it.body.txnId
                        }

                        viewModel?.addTransaction(
                            initiateTransaction = false,
                            showLoader = true,
                            terminateTransaction = false,
                            cartList = viewModel!!.cartListData, isSuccessTransaction = true
                        )

                    } else if (it.body.resultInfo.resultStatus.equals("TXN_FAILURE")) {
                        transactionStatus = KEY_FAIL_TRANSACTION_STATUS
                        AlertUtilities.showAlertDialog(
                            this@PaymentSummaryActivity,
                            BindingUtils.string(R.string.payment_fail_message),
                            "Set",
                            BindingUtils.string(R.string.strCancel),
                            { dialog, which ->
                                try {
                                    viewModel?.insertTransactionRequestModel.apply {
                                        transactionStatus = KEY_FAIL_TRANSACTION_STATUS
                                        viewModel?.tranStatusLiveData?.postValue(
                                            KEY_SUCCESS
                                        )
                                        viewModel.tranStatusLiveData.postValue(KEY_CANCEL_TRANSACTION_STATUS)
                                        this.paymentMethod = it.body.bankName
                                        paymentGateway = keyPaymentGatewayPayTmPay
                                        this.transId ="0"
                                    }
                                    viewModel?.addTransaction(
                                        initiateTransaction = false,
                                        showLoader = true,
                                        terminateTransaction = false,
                                        cartList = viewModel!!.cartListData,
                                        isSuccessTransaction = false
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    AppLog.errorLog(e.message, e)
                                }
                            },
                            { dialog, which -> })
                    }

                }
            })


        }
    }
    fun placePaytmOrder(tokenNo: String) {
        try {
            val host = if (BuildConfig.FLAVOR.equals("prod", true) || BuildConfig.FLAVOR.equals("uat", true)) {
                "https://securegw.paytm.in/" // live
            } else {
                "https://securegw-stage.paytm.in/" // testing
            }
            val callBackUrl = host + "theia/paytmCallback?ORDER_ID=" + viewModel.payTmOrderId
            AppLog.infoLog("callback URL $callBackUrl")
            val payTmOrder = PaytmOrder(
                viewModel.payTmOrderId,
                BuildConfig.MERCHANT_ID,
                tokenNo,
                viewModel.amountLiveData.value,
                callBackUrl
            )
            val transactionManager = TransactionManager(payTmOrder, this)
            transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage")
            transactionManager.startTransaction(this, PAYTM_REQUEST_CODE)
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message,e)
        }
    }
    var isTransaction: Boolean = false
    override fun onTransactionResponse(bundle: Bundle?) {
        bundle?.let {
            if (it.getString(PaytmConstants.STATUS).equals("TXN_SUCCESS")) {
                isTransaction = true
                SharedPreferencesDatabase.setStringPref(SharedPreferencesDatabase.TRANSACTION_ID, it.getString(PaytmConstants.TRANSACTION_ID).toString())
                SharedPreferencesDatabase.setStringPref(SharedPreferencesDatabase.TRANSACTION_MODE, it.getString(PaytmConstants.PAYMENT_MODE).toString())
                it.getString("ORDERID")?.let { id -> viewModel.transactionStatus(id) }
            }
        }
    }
    override fun clientAuthenticationFailed(p0: String?) {
    }
    override fun someUIErrorOccurred(p0: String?) {
    }
    override fun onTransactionCancel(p0: String?, p1: Bundle?) {
    }
    override fun networkNotAvailable() {
    }
    override fun onErrorProceed(p0: String?) {
    }
    override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {
    }
    override fun onBackPressedCancelTransaction() {
        viewModel?.insertTransactionRequestModel.apply {
            transactionStatus = KEY_CANCEL_TRANSACTION_STATUS
            viewModel.tranStatusLiveData.postValue(KEY_CANCEL_TRANSACTION_STATUS)
            paymentGateway = keyPaymentGatewayPayTmPay
            this.transId ="0"
        }
        viewModel?.addTransaction(
            initiateTransaction = false,
            showLoader = true,
            terminateTransaction = false,
            cartList = viewModel!!.cartListData,
            isSuccessTransaction = false
        )
    }
    // get transaction mode for paytm
    private fun getTransactionMode(mode: String): String {
        return when (mode.toLowerCase()) {
            "pg" -> KEY_CARD
            "nb" -> KEY_INTERNET_BANKING
            "ppc" -> KEY_PREPAID
            "upi" -> KEY_UPI
            "cc" -> KEY_CREDIT_CARD
            "db","dc" -> KEY_DEBIT_CARD
            "cash" -> KEY_CASH
            "emi" -> KEY_EMI
            "ppi" -> KEY_PAYTM_WALLET
            "paytmcc" -> KEY_POSTPAID
            else -> KEY_CARD
        }
    }


}
