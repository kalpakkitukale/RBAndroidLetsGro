package com.ramanbyte.emla.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.ramanbyte.R
import com.ramanbyte.databinding.ActivityPaymentStatusBinding
import com.ramanbyte.emla.ui.fragments.CartFragment
import com.ramanbyte.emla.ui.fragments.MyCourseFragment
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_FROM_PAY
import com.ramanbyte.utilities.KEY_SUCCESS_TRANSACTION_STATUS

class PaymentStatusActivity : AppCompatActivity() {

    private var paymentStatusBinding: ActivityPaymentStatusBinding? = null
    var transactionStatus = ""
    var transactionResponseId = ""
    var transactionRefId = ""
    var transactionAmount = ""
    var transactionType = ""
    var paymentMessage = ""
    var paymentStatus = ""
    private var activity_result: Int = 0
    var campusId = ""
    var programId = ""

    companion object {
        /**
         * @author Mansi Manakiki Mody
         * @since 13 Nov 2019
         * called after payment success or fail
         */
        fun openPaymentStatusActivity(
            context: Context,
            transactionStatus: String,
            transactionResponseId: String,
            transactionAmount: String,
            transactionType: String,
            transactionRefId: String
        ): Intent {
            val intent = Intent(context, PaymentStatusActivity::class.java)
            intent.putExtra("transactionStatus", transactionStatus)
            intent.putExtra("transactionResponseId", transactionResponseId)
            intent.putExtra("transactionRefId", transactionRefId)
            intent.putExtra("transactionAmount", transactionAmount)
            intent.putExtra("transactionType", transactionType)
            return intent
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
    }

    private fun initViews() {

        paymentStatusBinding =
            DataBindingUtil.setContentView(
                this@PaymentStatusActivity,
                R.layout.activity_payment_status
            )

        intent?.extras?.apply {
            transactionStatus = getString("transactionStatus", "")
            transactionResponseId = getString("transactionResponseId", "")
            transactionRefId = getString("transactionRefId", "")
            transactionAmount = getString("transactionAmount", "")
            transactionType = getString("transactionType", "")
        }
        initToolBar()
        setPaymentStatusData()

    }

    @SuppressLint("DefaultLocale")
    private fun setPaymentStatusData() {
        paymentStatusBinding?.apply {
            status = if (transactionStatus == KEY_SUCCESS_TRANSACTION_STATUS) {
                BindingUtils.string(R.string.payment_complete)
            } else {
                BindingUtils.string(R.string.payment_fail)
            }
            message = if (transactionStatus == KEY_SUCCESS_TRANSACTION_STATUS) {
                BindingUtils.string(R.string.payment_success_message)
            } else {
                BindingUtils.string(R.string.payment_fail_message)
            }
            if (transactionStatus == KEY_SUCCESS_TRANSACTION_STATUS) {
                imgTransactionStatus.setImageResource(R.drawable.ic_payment_successful)
            } else {
                imgTransactionStatus.setImageResource(R.drawable.ic_payment_failed)
            }
            purchaseRef = transactionRefId
            paymentType = transactionType
            totalAmount = transactionAmount

            paymentActivity = this@PaymentStatusActivity
        }
    }

    private fun initToolBar() {
        setSupportActionBar(paymentStatusBinding?.appbarLayout?.toolbar)
        val actionBar = supportActionBar
        actionBar!!.apply {
            setDisplayShowHomeEnabled(true) // show or hide the default home button
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        try {
            paymentStatusBinding!!.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (appbarLayout.toolbar != null) {
                        appbarLayout.toolbar.elevation = 0.0f
                    }
                }

                appbarLayout.tvSubTitle.visibility = View.GONE
                appbarLayout.title = ""
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(activity_result, intent)
        super.onBackPressed()
    }

    fun onclick(view: View){
       val intent = Intent(this@PaymentStatusActivity, ContainerActivity::class.java)
        intent.putExtra(KEY_FROM_PAY, 1)
        startActivity(intent)
    }
}