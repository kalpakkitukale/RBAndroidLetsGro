package com.ramanbyte.emla.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ramanbyte.AppController
import com.ramanbyte.R
import com.ramanbyte.databinding.ActivityPaymentStatusBinding
import com.ramanbyte.utilities.*
import kotlinx.android.synthetic.emla.activity_payment_status.*

class PaymentStatusActivity : AppCompatActivity() {

    private var paymentStatusBinding: ActivityPaymentStatusBinding? = null

    var transactionStatus = ""
    var transactionResponseId = ""
    var paymentStepIntegration = ""
    var transactionAmount = ""
    var paymentMessage = ""
    var paymentStatus = ""
    private var activity_result: Int = 0
    var campusId = ""
    var programId = ""

    companion object {

        /*fun openPaymentStatusActivity(
            context: Context,
            transactionsResponseModel: TransactionsResponseModel
        ) {
            val intent = Intent(context, PaymentStatusActivity::class.java)
            //Add Required fields in Extras
            intent.putExtra("id", transactionsResponseModel.id)
            intent.putExtra("user_Id", transactionsResponseModel.user_Id)
            context.startActivity(intent)
        }*/

        /**
         * @author Mansi Manakiki Mody
         * @since 13 Nov 2019
         * called after payment success or fail
         */
        fun openPaymentStatusActivity(
            context: Context,
            transactionStatus: String,
            transactionResponseId: String,
            paymentStepIntegration: String,
            transactionAmount: String
        ): Intent {
            val intent = Intent(context, PaymentStatusActivity::class.java)
            intent.putExtra("transactionStatus", transactionStatus)
            intent.putExtra("transactionResponseId", transactionResponseId)
            intent.putExtra("paymentStepIntegration", paymentStepIntegration)
            intent.putExtra("transactionAmount", transactionAmount)
            return intent
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
    }

     fun initViews() {
        paymentStatusBinding =
            DataBindingUtil.setContentView(
                this@PaymentStatusActivity,
                R.layout.activity_payment_status
            )

        intent?.extras?.apply {
            transactionStatus = getString("transactionStatus", "")
            transactionResponseId = getString("transactionResponseId", "")
            paymentStepIntegration = getString("paymentStepIntegration", "")
            transactionAmount = getString("transactionAmount", "")
        }

        initToolBar()
        //setPaymentStatusData()

        /*var id = intent.extras?.getInt("id", 0)!!
        var user_Id = intent.extras?.getInt("user_Id", 0)!!

        AppLog.infoLog("received_data $id $user_Id")*/

    }

    /*@SuppressLint("DefaultLocale")
    private fun setPaymentStatusData() {
        when (transactionStatus.toLowerCase()) {
            KEY_SUCCESS_TRANSACTION_STATUS.toLowerCase() -> {
                paymentStatusBinding!!.ivPaymentStatus.setImageResource(R.drawable.ic_payment_successful)
                paymentStatus = BindingUtils.string(R.string.payment_successful)
                paymentMessage = BindingUtils.string(
                    R.string.payment_successful_message,
                    BindingUtils.string(R.string.rupee_symbol) + " " + this.transactionAmount
                )
            }

            KEY_FAIL_TRANSACTION_STATUS.toLowerCase() -> {
                paymentStatusBinding!!.ivPaymentStatus.setImageResource(R.drawable.ic_payment_failed)
                paymentStatus = BindingUtils.string(R.string.payment_fail)
                paymentMessage = BindingUtils.string(
                    R.string.payment_fail_message,
                    BindingUtils.string(R.string.rupee_symbol) + " " + this.transactionAmount
                )

                btnContinue.text = BindingUtils.string(R.string.try_again_)
            }
        }

        paymentStatusBinding!!.message = paymentMessage
        paymentStatusBinding!!.status = paymentStatus

        btnContinue.setOnClickListener {
            //  finish()
            if (paymentStepIntegration.equals(KEY_BEFORE_EXAM, true)) {

                AppLog.infoLog("Exam Form")
            //    isPaymentDoneSuccessfully = true
                startActivity(
                    ExamDetailsActivity.openExamDetailsActivity(
                        this,
                        campusWiseProgramModel
                    )
                )
                isPaymentDoneSuccessfully = true
                // campusWiseProgramModel.paymentAlreadyForProgram = true
                AppController.setEnterPageAnimation(this)
                finish()

                //  isFlagRefresh = true
            } else {
                *//* var intent = Intent(this, DashBoardActivity::class.java)
                 startActivity(intent)*//*
                isPaymentDoneSuccessfully = true
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

        btnViewInvoice.setOnClickListener {
            if (campusWiseProgramModel != null) {
                startActivity(
                    Intent(
                        this@PaymentStatusActivity,
                        TransactionReceiptActivity::class.java
                    ).apply {
                        putExtra("programId", campusWiseProgramModel.programmId)
                    })
            }
        }
    }*/

    private fun initToolBar() {
        /*setSupportActionBar(paymentStatusBinding?.appbarLa
        yout?.toolbar)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (paymentStatusBinding!!.appbarLayout.toolbar != null) {
                    paymentStatusBinding!!.appbarLayout.toolbar.elevation = 0.0f
                }
            }

            paymentStatusBinding!!.appbarLayout.tvSubTitle.visibility = View.GONE
            paymentStatusBinding!!.appbarLayout.title = ""
            updateActionBarColor(
                BindingUtils.color(R.color.colorYellow),
                BindingUtils.color(R.color.colorYellow)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }*/
    }

    override fun onBackPressed() {

        val intent = Intent()
        setResult(activity_result, intent)
        super.onBackPressed()
        //killVisibleActivity()
    }

}