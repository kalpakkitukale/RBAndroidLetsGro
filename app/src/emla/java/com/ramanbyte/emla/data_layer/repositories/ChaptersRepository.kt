package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.data_layer.pagination.PaginationResponseHandler
import com.ramanbyte.emla.data_layer.network.api_layer.ChaptersController
import com.ramanbyte.emla.data_layer.pagination.PaginationDataSourceFactory
import com.ramanbyte.emla.data_layer.room.entities.UserEntity
import com.ramanbyte.emla.models.ChaptersModel
import com.ramanbyte.emla.models.ContentModel
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.ChapterRequest
import com.ramanbyte.utilities.replicate
import org.kodein.di.generic.instance
import kotlin.contracts.contract

class ChaptersRepository(mContext: Context) : BaseRepository(mContext) {

    private val chaptersController: ChaptersController by instance()

    private var paginationResponseHandlerLiveData: MutableLiveData<PaginationResponseHandler?> =
        MutableLiveData(null)
    private val pageSize = 10
    private var chapterRequestModel = ObservableField<ChapterRequest>()
    private var pagedList: LiveData<PagedList<ChaptersModel>>? = null

    fun getList(courseId: Int) {

        val userModel =
            applicationDatabase.getUserDao().getCurrentUser()?.replicate<UserEntity, UserModel>()

        val pagedDataSourceFactory = PaginationDataSourceFactory(
            chapterRequestModel.apply {

                set(ChapterRequest().apply {
                    this.userId = userModel?.userId ?: 0
                    this.courseId = courseId

                })
            },
            paginationResponseHandlerLiveData
        ) {
            apiRequest {
                chaptersController.getList(it)
            } ?: arrayListOf()
        }

        pagedList = LivePagedListBuilder(
            pagedDataSourceFactory,
            PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(pageSize).build()
        ).build()

        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

    fun tryAgain() {
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
        pagedList?.value?.dataSource?.invalidate()
    }

    fun getPaginationResponseHandler(): MutableLiveData<PaginationResponseHandler?> =
        paginationResponseHandlerLiveData

    fun getList(): LiveData<PagedList<ChaptersModel>>? = pagedList

    suspend fun getContentList(chapterId: Int): ArrayList<ContentModel> {

        return apiRequest {
            chaptersController.getContentList(chapterId)
        } ?: arrayListOf()
    }
}