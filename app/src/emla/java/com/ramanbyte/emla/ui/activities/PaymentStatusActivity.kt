package com.ramanbyte.emla.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityPaymentStatusBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.models.response.CartResponseModel
import com.ramanbyte.emla.view.CoursePerchesDetailsBottomSheet
import com.ramanbyte.emla.view_model.TransactionHistoryViewModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_SUCCESS_TRANSACTION_STATUS
import com.ramanbyte.utilities.keyCartData
import java.util.*

class PaymentStatusActivity :
    BaseActivity<ActivityPaymentStatusBinding, TransactionHistoryViewModel>(
        authModuleDependency
    ) {

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
    var cartListData = ArrayList<CartResponseModel>()

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
            transactionRefId: String,
            cartList: ArrayList<CartResponseModel>?
        ): Intent {
            val intent = Intent(context, PaymentStatusActivity::class.java)
            intent.putExtra("transactionStatus", transactionStatus)
            intent.putExtra("transactionResponseId", transactionResponseId)
            intent.putExtra("transactionRefId", transactionRefId)
            intent.putExtra("transactionAmount", transactionAmount)
            intent.putExtra("transactionType", transactionType)
            intent.putExtra(keyCartData, cartList!!)
            return intent
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*initViews()*/
    }


    override val viewModelClass: Class<TransactionHistoryViewModel> =
        TransactionHistoryViewModel::class.java

    override fun layoutId(): Int = R.layout.activity_payment_status

    override fun initiate() {
        onClickListener()
        intent?.extras?.apply {
            transactionStatus = getString("transactionStatus", "")
            transactionResponseId = getString("transactionResponseId", "")
            transactionRefId = getString("transactionRefId", "")
            transactionAmount = getString("transactionAmount", "")
            transactionType = getString("transactionType", "")
            cartListData =
                getParcelableArrayList<CartResponseModel>(keyCartData) as ArrayList<CartResponseModel>
            cartListData.forEach { it.totalPaid = transactionAmount }
        }
        initToolBar()
        setPaymentStatusData()
        viewModel.cartDetailsLiveData.postValue(cartListData)
        layoutBinding.trasacationViewModel = viewModel

    }


    /*   private fun initViews() {
           paymentStatusBinding =
               DataBindingUtil.setContentView(
                   this@PaymentStatusActivity,
                   R.layout.activity_payment_status
               )


       }*/

    @SuppressLint("DefaultLocale")
    private fun setPaymentStatusData() {
        layoutBinding?.apply {
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

    var courseDetailsBottomSheet: CoursePerchesDetailsBottomSheet? = null

    // click listener on the details
    fun onClickListener() {
        layoutBinding?.apply {
            lblPurchaseDetail.setOnClickListener {
                courseDetailsBottomSheet = CoursePerchesDetailsBottomSheet(true,true)
                courseDetailsBottomSheet?.show(supportFragmentManager,"Dialog")

            }
        }
    }


}