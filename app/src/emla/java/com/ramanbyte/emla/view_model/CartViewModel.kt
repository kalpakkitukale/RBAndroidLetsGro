package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.network.exception.ResourceNotFound
import com.ramanbyte.emla.data_layer.repositories.CoursesRepository
import com.ramanbyte.emla.data_layer.repositories.TransactionRepository
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.FavouriteVideosModel
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.CartRequestModel
import com.ramanbyte.emla.models.response.CartResponseModel
import com.ramanbyte.utilities.*
import org.kodein.di.generic.instance
import java.util.concurrent.TimeoutException

class CartViewModel(mContext: Context) : BaseViewModel(mContext = mContext) {
    override var noInternetTryAgain: () -> Unit = {
        coursesRepository.tryAgain()
    }
    private val coursesRepository: CoursesRepository by instance()
    val transactionRepository: TransactionRepository by instance()

    var userData: UserModel? = null
    var searchQuery = MutableLiveData<String>().apply {
        value = KEY_BLANK
    }

    var cartListLiveData = MutableLiveData<List<CartResponseModel>>().apply {
        value = arrayListOf()
    }
    var responseList: ArrayList<CartResponseModel>? = null

    init {
        toggleLayoutVisibility(View.GONE, View.GONE, View.GONE, "", View.GONE)
        searchQuery.observeForever {
            coursesRepository.searchCourse(it)
        }
        userData = coursesRepository.getCurrentUser()
    }


    fun getCartList() {
        CoroutineUtils.main {
            try {
                coroutineToggleLoader(BindingUtils.string(R.string.getting_reply_list))
                val response = transactionRepository.getCart()
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

        /* CoroutineUtils.main {
             try {
                 isLoaderShowingLiveData.postValue(true)
                 val requestModel = CartRequestModel().apply {
                 }

                 val response =
                     transactionRepository.getCart()

                 responseList = response
                 cartListLiveData.postValue(response)

                 toggleLayoutVisibility(
                     View.VISIBLE,
                     View.GONE,
                     View.GONE,
                     KEY_BLANK
                 )

                 isLoaderShowingLiveData.postValue(false)
             } catch (e: NoInternetException) {
                 e.printStackTrace()
                 AppLog.errorLog(e.message, e)
                 isLoaderShowingLiveData.postValue(false)
                 toggleLayoutVisibility(
                     View.GONE,
                     View.GONE,
                     View.VISIBLE,
                     e.message ?: BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet)
                 )
             } catch (e: NoDataException) {
                 e.printStackTrace()
                 AppLog.errorLog(e.message, e)
                 isLoaderShowingLiveData.postValue(false)
                 toggleLayoutVisibility(
                     View.GONE,
                     View.VISIBLE,
                     View.GONE,
                     BindingUtils.string(R.string.no_files_found)
                 )
             } catch (e: TimeoutException) {
                 e.printStackTrace()
                 AppLog.errorLog(e.message, e)
                 isLoaderShowingLiveData.postValue(false)
                 toggleLayoutVisibility(
                     View.GONE,
                     View.GONE,
                     View.VISIBLE,
                     e.message ?: BindingUtils.string(R.string.connection_timeout)
                 )
             } catch (e: ResourceNotFound) {
                 e.printStackTrace()
                 AppLog.errorLog(e.message, e)
                 isLoaderShowingLiveData.postValue(false)
                 toggleLayoutVisibility(
                     View.GONE,
                     View.GONE,
                     View.GONE,
                     e.message ?: BindingUtils.string(R.string.resource_not_found),
                     View.VISIBLE
                 )
             } catch (e: ApiException) {
                 e.printStackTrace()
                 AppLog.errorLog(e.message, e)
                 isLoaderShowingLiveData.postValue(false)
                 toggleLayoutVisibility(
                     View.GONE,
                     View.GONE,
                     View.GONE,
                     e.message ?: BindingUtils.string(R.string.some_thing_went_wrong),
                     View.VISIBLE
                 )
             } catch (e: Exception) {
                 e.printStackTrace()
                 AppLog.errorLog(e.message, e)
                 isLoaderShowingLiveData.postValue(false)
                 toggleLayoutVisibility(
                     View.GONE,
                     View.GONE,
                     View.GONE,
                     e.message ?: BindingUtils.string(R.string.some_thing_went_wrong),
                     View.VISIBLE
                 )
             }
         }*/
    }

    fun coursesPagedList(): LiveData<PagedList<CoursesModel>>? {
        return coursesRepository.coursesPagedList
    }


}