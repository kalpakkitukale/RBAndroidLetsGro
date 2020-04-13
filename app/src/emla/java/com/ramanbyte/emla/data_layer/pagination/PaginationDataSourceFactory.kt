package com.ramanbyte.emla.data_layer.pagination

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.ramanbyte.data_layer.pagination.PaginationResponseHandler
import com.ramanbyte.emla.models.BaseRequestModel

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 3/3/2020
 */
class PaginationDataSourceFactory<T, R : BaseRequestModel>(
    val requestModel: ObservableField<R>,
    private val paginationResponseHandlerLiveData: MutableLiveData<PaginationResponseHandler?>,
    private val apiCallFunction: suspend (currentPage: R) -> List<T>
) :
    DataSource.Factory<Int, T>() {

    private val paginationDataSourceLiveData: MutableLiveData<PageKeyedDataSource<Int, T>> =
        MutableLiveData()

    private var dataSource: PaginationDataSource<T, R>? = null

    override fun create(): DataSource<Int, T> {

        dataSource =
            PaginationDataSource(apiCallFunction, requestModel, paginationResponseHandlerLiveData)

        paginationDataSourceLiveData.postValue(dataSource)

        return dataSource!!
    }
}