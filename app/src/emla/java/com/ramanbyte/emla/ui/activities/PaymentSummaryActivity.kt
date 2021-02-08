package com.ramanbyte.emla.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.airpay.airpaysdk_simplifiedotp.AirpayActivity
import com.airpay.airpaysdk_simplifiedotp.Transaction
import com.payu.india.Payu.Payu
import com.payu.india.Payu.PayuConstants
import com.payu.payuui.Activity.PayUBaseActivity
import com.ramanbyte.AppController
import com.ramanbyte.BuildConfig
import com.ramanbyte.R
import com.ramanbyte.databinding.ActivityPaymentSummaryBinding
import com.ramanbyte.emla.base.di.ACTIVITY_CONTEXT
import com.ramanbyte.emla.models.PayuGatewayModel
import com.ramanbyte.emla.models.response.CartResponseModel
import com.ramanbyte.emla.view_model.PaymentSummaryViewModel
import com.ramanbyte.utilities.*
import com.ramanbyte.view_model.factory.BaseViewModelFactory
import org.json.JSONObject
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.android.retainedKodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import java.util.*

class PaymentSummaryActivity : AppCompatActivity(), KodeinAware {

    private val _parentKodein by kodein()

    override val kodein: Kodein by retainedKodein {
        extend(_parentKodein, copy = Copy.All)
        bind<Context>(ACTIVITY_CONTEXT) with singleton { this@PaymentSummaryActivity }
    }
    private val factory: BaseViewModelFactory by instance()

    private var paymentSummaryBinding: ActivityPaymentSummaryBinding? = null
    private var paymentSummaryViewModel: PaymentSummaryViewModel? = null
    private val AIR_PAY_REQUEST_CODE = 0x10
    private var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
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

    fun initViews() {
        paymentSummaryBinding =
            DataBindingUtil.setContentView(
                this@PaymentSummaryActivity,
                R.layout.activity_payment_summary
            )
        initToolBar()

        //TODO Must write below code in your activity to set up initial context for PayU
        Payu.setInstance(this)

        paymentSummaryViewModel = ViewModelProviders.of(
            this@PaymentSummaryActivity, factory
        ).get(PaymentSummaryViewModel::class.java)

        ProgressLoader(this@PaymentSummaryActivity, paymentSummaryViewModel!!)

        paymentSummaryBinding?.apply {
            paymentSummaryViewModel = this@PaymentSummaryActivity.paymentSummaryViewModel
            lifecycleOwner = this@PaymentSummaryActivity
        }

        paymentSummaryViewModel?.apply {

            ProgressLoader(this@PaymentSummaryActivity, this)
            AlertDialog(this@PaymentSummaryActivity, this)

            intent?.extras?.apply {
                amountLiveData.value = getString(keyAmount) ?: "0"
                paymentStepIntegration = getString(keyPaymentStepIntegration) ?: ""
                cartListData =
                    getParcelableArrayList<CartResponseModel>(keyCartData) as ArrayList<CartResponseModel>

            }
        }

        paymentSummaryViewModel?.apply {
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
                    paymentSummaryBinding?.contentLayout?.snackbar(BindingUtils.string(R.string.select_payment_gateway))
                    paymentOptionErrorLiveData.value = false
                }
            })

            transactionResponseIdLiveData.observe(this@PaymentSummaryActivity, Observer {

                if (it != 0) {
                    AppController.setEnterPageAnimation(this@PaymentSummaryActivity)
                    if (it > 0) {
                        // payment success
                        startActivityForResult(
                            PaymentStatusActivity.openPaymentStatusActivity(
                                this@PaymentSummaryActivity,
                                KEY_SUCCESS_TRANSACTION_STATUS,
                                it.toString(),
                                paymentStepIntegration,
                                amountLiveData.value.toString()
                                /*campusWiseProgramModel.apply {
                                    paymentAlreadyForProgram = true
                                }*/
                            ), PAYMENT_SUCCESSFUL_REQUEST_CODE
                        )

                    } else {
                        // fail
                        PaymentStatusActivity.openPaymentStatusActivity(
                            this@PaymentSummaryActivity,
                            KEY_FAIL_TRANSACTION_STATUS,
                            "",
                            paymentStepIntegration,
                            amountLiveData.value.toString()/*, campusWiseProgramModel.apply {
                                paymentAlreadyForProgram = false
                            }*/
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
        setSupportActionBar(paymentSummaryBinding?.appbarLayout?.toolbar)
        val actionBar = supportActionBar
        actionBar!!.apply {
            setDisplayShowHomeEnabled(true) // show or hide the default home button
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        try {
            paymentSummaryBinding!!.apply {
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
                paymentSummaryViewModel?.insertTransactionRequestModel?.apply {
                    transactionStatus =
                        KEY_CANCEL_TRANSACTION_STATUS
                }

                paymentSummaryViewModel?.addTransaction(
                    initiateTransaction = false,
                    showLoader = true,
                    terminateTransaction = true
                )
            }
            else -> {
                when (requestCode) {
                    PayuConstants.PAYU_REQUEST_CODE -> {
                        paymentSummaryViewModel?.insertTransactionRequestModel?.apply {
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
                                                paymentMethod = when (payuTransactionMode) {
                                                    "nb" -> keyInternetBanking
                                                    "cc" -> keyCreditCard
                                                    "db" -> keyDebitCard
                                                    "cash" -> keyCash
                                                    "emi" -> keyEmi
                                                    else -> ""
                                                }
                                                transactionStatus =
                                                    KEY_SUCCESS
                                                transId = payUObject.getString("txnid").toInt()
                                            } else {
                                                // payment fail
                                                transactionStatus =
                                                    KEY_FAIL_TRANSACTION_STATUS
                                            }
                                        } else {
                                            // payment fail
                                            transactionStatus =
                                                KEY_FAIL_TRANSACTION_STATUS
                                        }
                                    } else {
                                        // payment fail
                                        transactionStatus = KEY_FAIL_TRANSACTION_STATUS
                                    }


                                } catch (e: java.lang.NullPointerException) {
                                    e.printStackTrace()
                                    AppLog.errorLog(e.message, e)
                                    transactionStatus =
                                        KEY_FAIL_TRANSACTION_STATUS
                                }

                            } else {
                                // payment fail
                                transactionStatus =
                                    KEY_FAIL_TRANSACTION_STATUS
                            }
                        }
                    }
                    AIR_PAY_REQUEST_CODE -> {
                        paymentSummaryViewModel?.insertTransactionRequestModel?.apply {
                            paymentGateway = keyPaymentGatewayAirPay
                            if (data != null) {
                                val transactionList =
                                    data.extras!!.getSerializable("DATA") as ArrayList<Transaction>
                                if (transactionList.size > 0) {
                                    transactionList[0].apply {
                                        if (!status.isNullOrEmpty() && status == "200"
                                            && !statusmsg.isNullOrEmpty() && statusmsg.toLowerCase() == "success"
                                        ) {
                                            flag = status
                                            transactionStatus =
                                                KEY_SUCCESS_TRANSACTION_STATUS
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

                                            if (transactionid != null) {
                                                AppLog.infoLog("TXN ID -> =$transactionid")
                                                transId = transactionid.toInt()
                                            }
                                        } else {
                                            flag = status
                                            transactionStatus =
                                                KEY_FAIL_TRANSACTION_STATUS
                                        }
                                        /*if (status != null) {
                                                AppLog.infoLog("STATUS -> =$status")
                                            }
                                            if (merchantkey != null) {
                                                AppLog.infoLog("MERCHANT KEY -> =$merchantkey")
                                            }
                                            if (merchantposttype != null) {
                                                AppLog.infoLog("MERCHANT POST TYPE =$merchantposttype")
                                            }
                                            if (statusmsg != null) {
                                                AppLog.infoLog("STATUS MSG -> =$statusmsg") //  success or fail
                                            }
                                            if (transactionamt != null) {
                                                AppLog.infoLog("TRANSACTION AMT -> =$transactionamt")
                                            }
                                            if (txN_MODE != null) {
                                                AppLog.infoLog("TXN MODE -> =$txN_MODE")
                                            }
                                            if (merchanttransactionid != null) {
                                                AppLog.infoLog("MERCHANT_TXN_ID -> =$merchanttransactionid") // order id
                                            }
                                            if (securehash != null) {
                                                AppLog.infoLog("SECURE HASH -> =$securehash")
                                            }
                                            if (customvar != null) {
                                                AppLog.infoLog("CUSTOMVAR -> =$customvar")
                                            }
                                            if (transactionstatus != null) {
                                                AppLog.infoLog("TXN STATUS -> =$transactionstatus")
                                            }
                                            if (txN_DATE_TIME != null) {
                                                AppLog.infoLog("TXN_DATETIME -> =$txN_DATE_TIME")
                                            }
                                            if (txN_CURRENCY_CODE != null) {
                                                AppLog.infoLog("TXN_CURRENCY_CODE -> =" + txN_CURRENCY_CODE)
                                            }
                                            if (transactionvariant != null) {
                                                AppLog.infoLog("TRANSACTIONVARIANT -> =" + transactionvariant)
                                            }

                                            if (bankname != null) {
                                                AppLog.infoLog("BANKNAME -> =" + bankname)
                                            }
                                            if (cardissuer != null) {
                                                AppLog.infoLog("CARDISSUER -> =" + cardissuer)
                                            }
                                            if (fullname != null) {
                                                AppLog.infoLog("FULLNAME -> =" + fullname)
                                            }
                                            if (email != null) {
                                                AppLog.infoLog("EMAIL -> =" + email)
                                            }
                                            if (contactno != null) {
                                                AppLog.infoLog("CONTACTNO -> =" + contactno)
                                            }
                                            if (merchanT_NAME != null) {
                                                AppLog.infoLog("MERCHANT_NAME -> =" + merchanT_NAME)
                                            }
                                            if (settlemenT_DATE != null) {
                                                AppLog.infoLog("SETTLEMENT_DATE -> =" + settlemenT_DATE)
                                            }
                                            if (surcharge != null) {
                                                AppLog.infoLog("SURCHARGE -> =" + surcharge)
                                            }
                                            if (billedamount != null) {
                                                AppLog.infoLog("BILLEDAMOUNT -> =" + billedamount)
                                            }
                                            if (isrisk != null) {
                                                AppLog.infoLog("ISRISK -> =" + isrisk)
                                            }*/

                                    }
                                } else {
                                    // payment fail
                                    transactionStatus =
                                        KEY_FAIL_TRANSACTION_STATUS
                                }
                            } else {
                                // payment fail
                                transactionStatus =
                                    KEY_FAIL_TRANSACTION_STATUS
                            }
                        }

                    }
                }
            }
        }


        /*
        * This code is temporary until DescribedRadioButton get completed
        * */


    }

    fun customRadioClickListener() {

        paymentSummaryViewModel?.apply {

            paymentSummaryBinding?.apply {

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
                        isAirPayChecked = true
                        isPayuChecked = false
                    }
                }

                radioPayu.setOnClickListener {
                    paymentSummaryBinding!!.radioPayu.isChecked = true

                    if (paymentSummaryBinding!!.radioAirpay.isChecked) {
                        paymentSummaryBinding!!.radioAirpay.isChecked = false
                    }
                    isPayuChecked = paymentSummaryBinding!!.radioAirpay.isChecked

                    paymentSummaryBinding!!.radioPayu.isChecked = !isPayuChecked
                    paymentSummaryBinding!!.radioAirpay.isChecked = isPayuChecked

                    isPayuChecked = radioPayu.isChecked

                    if (!isPayuChecked) {
                        radioPayu.toggle()
                        radioAirpay.isChecked = isPayuChecked
                        isPayuChecked = true
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
}
