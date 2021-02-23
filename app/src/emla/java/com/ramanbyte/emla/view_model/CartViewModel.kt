package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.CoursesRepository
import com.ramanbyte.emla.data_layer.repositories.TransactionRepository
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.response.CartResponseModel
import com.ramanbyte.emla.ui.activities.PaymentSummaryActivity
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_BLANK
import kotlinx.coroutines.delay
import org.kodein.di.generic.instance

class CartViewModel(var mContext: Context) : BaseViewModel(mContext = mContext) {
    override var noInternetTryAgain: () -> Unit = {
        coursesRepository.tryAgain()
    }
    private val coursesRepository: CoursesRepository by instance()
    val transactionRepository: TransactionRepository by instance()
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
                coroutineToggleLoader(BindingUtils.string(R.string.getting_reply_list))
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

    fun clickOnProceedToPay(view: View) {
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