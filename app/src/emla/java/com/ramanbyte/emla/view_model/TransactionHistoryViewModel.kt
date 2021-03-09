package com.ramanbyte.emla.view_model

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.TransactionRepository
import com.ramanbyte.emla.models.CourseDetailsModel
import com.ramanbyte.emla.models.TransactionHistoryModel
import com.ramanbyte.emla.models.response.CartResponseModel
import com.ramanbyte.emla.ui.activities.ContainerActivity
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_BLANK
import org.kodein.di.generic.instance

/**
 * Created by Mansi Manakiki on 11 Feb, 2021
 * Email : mansi.m@ramanbyte.com
 */

class TransactionHistoryViewModel(var mContext: Context) : BaseViewModel(mContext) {

    val transactionRepository: TransactionRepository by instance()

    var transactionHistoryListLiveData = MutableLiveData<List<TransactionHistoryModel>>().apply {
        value = arrayListOf()
    }

    var cartDetailsLiveData = MutableLiveData<ArrayList<CartResponseModel>>().apply {
        postValue(ArrayList<CartResponseModel>())
    }

    override var noInternetTryAgain: () -> Unit = {
        getTransactionsHistory()
    }

    init {
        toggleLayoutVisibility(View.GONE, View.GONE, View.GONE, "", View.GONE)
    }

    fun getTransactionsHistory() {
        CoroutineUtils.main {
            try {
                coroutineToggleLoader(BindingUtils.string(R.string.getting_my_favourite_video_list))

                val response = transactionRepository.getAllTransactionHistory()
                transactionHistoryListLiveData.postValue(response)

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
                AppLog.errorLog(e.message, e)
                toggleLayoutVisibility(
                    View.GONE,
                    View.VISIBLE,
                    View.GONE,
                    BindingUtils.string(R.string.favourite_videos_unavailable),
                    View.GONE
                )
                coroutineToggleLoader()
            }
        }
    }

    // transaction Deatils
    var coureseDetailClicked = MutableLiveData<Boolean>().apply {
        postValue(false)
    }
    var courseDetailsMutableLiveData = MutableLiveData<CourseDetailsModel>().apply {
        postValue(CourseDetailsModel())
    }

    var cardDataList = ArrayList<CartResponseModel>()
    fun perchesDetailsClick(transactiondata: TransactionHistoryModel) {
        coureseDetailClicked.postValue(true)
        cardDataList.clear()
        transactiondata.fees.forEach {
            cardDataList.add(CartResponseModel().apply {
                this.courseName = it.courseDetails.course_Name
                this.courseImage = it.courseDetails.courseIamge
                this.courseDescription = it.courseDetails.description
                this.courseFee = it.courseFeeStructure.courseFee.toString()
                this.totalPaid = transactiondata.amountPaid
            })
        }
        cartDetailsLiveData.postValue(cardDataList)
    }

    // on bottomsheet click listener
    var onClickedBottomSheetLiveData = MutableLiveData<Boolean>().apply {
        value = null
    }

    fun onCloseClick(view: View) {
        onClickedBottomSheetLiveData.postValue(true)
    }

    // on click for the on payment fail sucessful click
    fun onPaymentFailSucessfulClick(view: View) {
        val intent = Intent(mContext, ContainerActivity::class.java)
        intent.putExtra("Payment Status", "My Courese")
        mContext.startActivity(intent)

    }

    // click on the perchaes detail
    var perchaesDetailLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    fun purchaesDetailClick(view: View) {
        perchaesDetailLiveData.postValue(true)

    }


}