package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.CoursesRepository
import com.ramanbyte.emla.data_layer.repositories.MasterRepository
import com.ramanbyte.emla.data_layer.repositories.TransactionRepository
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.CourseFeeRequestModel
import com.ramanbyte.emla.models.request.InsertTransactionRequestModel
import com.ramanbyte.emla.models.response.CartResponseModel
import com.ramanbyte.emla.ui.activities.PaymentSummaryActivity
import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.DateUtils.DATE_SQLITE_PATTERN
import com.ramanbyte.utilities.DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS
import kotlinx.coroutines.delay
import org.kodein.di.generic.instance

class CartViewModel(var mContext: Context) : BaseViewModel(mContext = mContext) {
    override var noInternetTryAgain: () -> Unit = {
        coursesRepository.tryAgain()
    }
    private val coursesRepository: CoursesRepository by instance()
    val transactionRepository: TransactionRepository by instance()
    val masterRepository: MasterRepository by instance()
    var courseFess = MutableLiveData<Float>().apply {
        value = 0.0f
    }
    var userData: UserModel? = null

    var cartListLiveData = MutableLiveData<List<CartResponseModel>>().apply {
        value = arrayListOf()
    }

    var finalCartList = ArrayList<CartResponseModel>()

    init {
        toggleLayoutVisibility(View.GONE, View.GONE, View.GONE, "", View.GONE)
        userData = coursesRepository.getCurrentUser()
    }

    fun getCartList() {
        CoroutineUtils.main {
            var fee = 0.0f
            try {
                // coroutineToggleLoader(BindingUtils.string(R.string.getting_reply_list))
                isLoaderShowingLiveData.postValue(true)
                loaderMessageLiveData.set(BindingUtils.string(R.string.getting_reply_list))
                val response = transactionRepository.getCart()

                for (i in 0 until response!!.size) {
                    fee = response[i].courseFee!!.toFloat().plus(fee)
                }
                courseFess.postValue(fee)
                cartListLiveData.postValue(response)
                toggleLayoutVisibility(
                    View.VISIBLE,
                    View.GONE,
                    View.GONE,
                    KEY_BLANK,
                    View.GONE
                )
                coroutineToggleLoader()
                isLoaderShowingLiveData.postValue(false)
            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.GONE,
                    BindingUtils.string(R.string.some_thing_went_wrong),
                    View.VISIBLE
                )

                coroutineToggleLoader()
                isLoaderShowingLiveData.postValue(false)
            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.VISIBLE,
                    BindingUtils.string(R.string.no_internet_message),
                    View.GONE
                )
                coroutineToggleLoader()
                isLoaderShowingLiveData.postValue(false)
            } catch (e: NoDataException) {
                e.printStackTrace()
                fee = 0.0f
                courseFess.postValue(fee)
                AppLog.errorLog(e.message, e)
                toggleLayoutVisibility(
                    View.GONE,
                    View.VISIBLE,
                    View.GONE,
                    BindingUtils.string(R.string.empty_cart),
                    View.GONE
                )
                coroutineToggleLoader()
                isLoaderShowingLiveData.postValue(false)
            }
        }
    }

    fun deleteCart(view: View, cartResponseModel: CartResponseModel) {
        CoroutineUtils.main {
            try {
                isLoaderShowingLiveData.postValue(true)
                val response = cartResponseModel.id?.let { transactionRepository.deleteCart(it) }
                isLoaderShowingLiveData.postValue(false)
                getCartList()
            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                setAlertDialogResourceModelMutableLiveData(
                    BindingUtils.string(R.string.some_thing_went_wrong),
                    BindingUtils.drawable(R.drawable.something_went_wrong)!!,
                    true,
                    BindingUtils.string(R.string.strOk), {
                        isAlertDialogShown.postValue(false)
                    }
                )
                isAlertDialogShown.postValue(true)
                isLoaderShowingLiveData.postValue(false)
            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                setAlertDialogResourceModelMutableLiveData(
                    BindingUtils.string(R.string.no_internet_message),
                    BindingUtils.drawable(R.drawable.ic_no_internet)!!,
                    false,
                    BindingUtils.string(R.string.try_again), {
                        isAlertDialogShown.postValue(false)
                        deleteCart(view, cartResponseModel)
                    },
                    negativeButtonClickFunctionality = {
                        isAlertDialogShown.postValue(false)
                    }
                )
                delay(200)
                isAlertDialogShown.postValue(true)
                isLoaderShowingLiveData.postValue(false)
            } catch (e: Exception) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                isLoaderShowingLiveData.postValue(false)
            }
        }

    }

    var isPaid: Boolean? = false
    var demo: Boolean? = false

    // on proceed button click event
    fun clickOnProceedToPay(view: View) {
        isPaid = checkCouresePaidUnpaid()

        // here is temp if else
        if (demo == true){
            if (courseFess.value!! > 0.0f) {
                mContext.startActivity(
                    PaymentSummaryActivity.openPaymentActivity(
                        mContext,
                        courseFess.value!!.toString(),
                        finalCartList
                    )
                )
            }
       }

    }

    var insertTransactionModelLiveData = MutableLiveData<InsertTransactionRequestModel>().apply {
        this.value = InsertTransactionRequestModel()
    }
    val loggedInUserModel = masterRepository.getCurrentUser()
    var courseFeesList: ArrayList<CourseFeeRequestModel>? = ArrayList()
    var unpaidCourse = ArrayList<CartResponseModel>()
    var paidCourse = ArrayList<CartResponseModel>()

    // check the selected course are free or paid
    fun checkCouresePaidUnpaid():Boolean{
        finalCartList?.forEach {
            if (it.courseFee.equals("0", true) || it.courseFee.equals("0.0", true)) {
                unpaidCourse.add(it)
            }else{
                paidCourse.add(it)
            }
        }

// unpaid transaction entry sucessfull going in the local data base
        if (unpaidCourse.size>0){
            insertTransactionModelLiveData.value?.apply {
                transId = DateUtils.getCurrentDateTime(DATE_SQLITE_PATTERN)
                transactionStatus = KEY_SUCCESS_TRANSACTION_STATUS
                transDate = DateUtils.getCurrentDateTime(DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS)
                amountPaid = "0.0"
                paymentDomain = "Online"
                paymentGateway= "Free"
                paymentMethod ="Free"
                paymentDescription ="free"

                courseFeesList = ArrayList()
                for (cart in finalCartList) {
                    val courseFeeRequestModel = CourseFeeRequestModel()
                    courseFeeRequestModel.apply {
                        userId = loggedInUserModel?.userId!!
                        paymentId = 0
                        courseDetailsId = cart.courseDetailsId!!
                        courseFeeStructureId = 61
                        id = 0
                    }
                    courseFeesList!!.add(courseFeeRequestModel)
                }

                fees = courseFeesList!!
            }
            var id: Int? = 0
            CoroutineUtils.main {
                id = transactionRepository.insertTransaction(insertTransactionModelLiveData.value!!, true)
            }



        }
//____________________End____________________________
        return false
    }



}