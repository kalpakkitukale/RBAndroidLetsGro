package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.data_layer.pagination.PaginationResponseHandler
import com.ramanbyte.emla.data_layer.network.api_layer.JobSkillsController
import com.ramanbyte.emla.data_layer.pagination.PaginationDataSourceFactory
import com.ramanbyte.emla.data_layer.room.entities.UserEntity
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.SkillsRequestModel
import com.ramanbyte.emla.models.response.SkillsModel
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.replicate
import org.kodein.di.generic.instance

class JobSkillsRepository(mContext: Context) : BaseRepository(mContext) {

    private val jobSkillsController: JobSkillsController by instance()

    private var paginationResponseHandlerLiveData: MutableLiveData<PaginationResponseHandler?> =
        MutableLiveData(null)
    private val myPageSize = 100
    private var skillsRequestModel = ObservableField<SkillsRequestModel>()
    private var pagedList: LiveData<PagedList<SkillsModel>>? = null

    fun tryAgain() {
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
        pagedList?.value?.dataSource?.invalidate()
    }

    fun getPaginationResponseHandler(): MutableLiveData<PaginationResponseHandler?> =
        paginationResponseHandlerLiveData

    fun getList(): LiveData<PagedList<SkillsModel>>? = pagedList

    fun getSkillsList(searchStr: String = KEY_BLANK) {

        val userModel =
            applicationDatabase.getUserDao().getCurrentUser()?.replicate<UserEntity, UserModel>()

        val pagedDataSourceFactory = PaginationDataSourceFactory(
            skillsRequestModel.apply {

                set(SkillsRequestModel().apply {
                    this.userId = userModel?.userId ?: 0
                    this.searchKey = searchStr
                    this.pageSize = myPageSize
                })
            },
            paginationResponseHandlerLiveData
        ) {
            apiRequest {
                jobSkillsController.getSkillsList(it)
            } ?: arrayListOf()
        }

        pagedList = LivePagedListBuilder(
            pagedDataSourceFactory,
            PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(myPageSize).build()
        ).build()

        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

}