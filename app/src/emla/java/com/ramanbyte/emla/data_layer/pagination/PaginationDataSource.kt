package com.ramanbyte.emla.data_layer.pagination

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.data_layer.pagination.PaginationResponseHandler
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.models.BaseRequestModel
import com.ramanbyte.utilities.AppLog

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 3/3/2020
 */
class PaginationDataSource<T, R : BaseRequestModel>(
    private val apiCallFunction: suspend (requestModel: R) -> List<T>,
    private val requestModel: ObservableField<R>,
    private val paginationResponseHandlerLiveData: MutableLiveData<PaginationResponseHandler?>
) :
    PageKeyedDataSource<Int, T>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, T>
    ) {

        CoroutineUtils.main {
            try {
                val initialList: List<T> = apiCallFunction.invoke(requestModel.get()?.apply {
                    pageNo = 1
                }!!)

                callback.onResult(initialList, null, 2)
                paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADED)
            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_ERROR)
            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_NO_INTERNET)
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_NO_DATA)
            } catch (e: Exception) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_NO_INTERNET)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        CoroutineUtils.main {
            try {
                val currentPage = params.key

                val afterList = apiCallFunction.invoke(requestModel.get()?.apply {
                    pageNo = currentPage
                }!!)

                paginationResponseHandlerLiveData.postValue(
                    if (afterList.isNotEmpty()) {
                        PaginationResponseHandler.PAGE_LOADED
                    } else {
                        PaginationResponseHandler.PAGE_NO_DATA
                    }
                )

                callback.onResult(afterList, currentPage + 1)

            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.PAGE_ERROR)
            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.PAGE_NO_INTERNET)
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.PAGE_NO_DATA)
            } catch (e: Exception) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_NO_INTERNET)
            }
        }

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {

    }
}