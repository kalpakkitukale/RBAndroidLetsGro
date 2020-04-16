package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.data_layer.pagination.PaginationResponseHandler
import com.ramanbyte.emla.data_layer.network.api_layer.SectionsController
import com.ramanbyte.emla.data_layer.pagination.PaginationDataSourceFactory
import com.ramanbyte.emla.data_layer.room.entities.UserEntity
import com.ramanbyte.emla.models.ContentModel
import com.ramanbyte.emla.models.SectionsModel
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.SectionsRequest
import com.ramanbyte.utilities.replicate
import org.kodein.di.generic.instance

class SectionsRepository(mContext: Context) : BaseRepository(mContext) {

    private val sectionsController: SectionsController by instance()

    private var paginationResponseHandlerLiveData: MutableLiveData<PaginationResponseHandler?> =
        MutableLiveData(null)
    private val pageSize = 10
    private var chapterRequestModel = ObservableField<SectionsRequest>()
    private var pagedList: LiveData<PagedList<SectionsModel>>? = null

    fun getList(chapterId: Int) {

        val pagedDataSourceFactory = PaginationDataSourceFactory(
            chapterRequestModel.apply {

                set(SectionsRequest().apply {
                    this.chapterId = chapterId
                })
            },
            paginationResponseHandlerLiveData
        ) {
            apiRequest {
                sectionsController.getList(it)
            } ?: arrayListOf()
        }

        pagedList = LivePagedListBuilder(
            pagedDataSourceFactory,
            PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(pageSize).build()
        ).build()

        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

    fun getPaginationResponseHandler(): MutableLiveData<PaginationResponseHandler?> =
        paginationResponseHandlerLiveData

    fun getList() : LiveData<PagedList<SectionsModel>>? = pagedList

    fun tryAgain() {
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
        pagedList?.value?.dataSource?.invalidate()
    }

    suspend fun getContentList(sectionId: Int): ArrayList<ContentModel> {

        val userModel =
            applicationDatabase.getUserDao().getCurrentUser()?.replicate<UserEntity, UserModel>()

        return apiRequest {
            sectionsController.getContentList(sectionId, userModel?.userId ?: 0)
        } ?: arrayListOf()
    }
}