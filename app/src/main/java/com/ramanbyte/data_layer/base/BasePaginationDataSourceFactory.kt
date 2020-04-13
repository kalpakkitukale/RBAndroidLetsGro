package com.ramanbyte.data_layer.base

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import java.util.concurrent.Executors

abstract class BasePaginationDataSourceFactory<Key, Value> :
    DataSource.Factory<Key, Value>() {

    val numberOfThreads = 3

    private lateinit var pagedListLiveData: LiveData<PagedList<Value>>

    protected fun setupPagedList(
        pageSize: Int = 10,
        boundaryCallback: PagedList.BoundaryCallback<Value>? = null
    ) {

        val pageListConfig =
            PagedList.Config.Builder().setEnablePlaceholders(false).setInitialLoadSizeHint(pageSize)
                .setPageSize(pageSize).build()

        val executor = Executors.newFixedThreadPool(numberOfThreads)

        val pagedListBuilder = LivePagedListBuilder(this, pageListConfig)
            .setFetchExecutor(executor)

        if (boundaryCallback != null) {
            pagedListBuilder.setBoundaryCallback(boundaryCallback)
        }

        pagedListLiveData = pagedListBuilder.build()
    }

    fun getPagedList(): LiveData<PagedList<Value>>? = pagedListLiveData
}