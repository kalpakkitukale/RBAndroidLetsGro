package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.data_layer.pagination.PaginationResponseHandler
import com.ramanbyte.emla.data_layer.network.api_layer.SkillsController
import com.ramanbyte.emla.data_layer.pagination.PaginationDataSourceFactory
import com.ramanbyte.emla.data_layer.room.entities.UserEntity
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.SkillsRequestModel
import com.ramanbyte.emla.models.response.SkillsModel
import com.ramanbyte.utilities.replicate
import org.kodein.di.generic.instance

class SkillsRepository(mContext: Context) : BaseRepository(mContext) {

    private val skillsController: SkillsController by instance()

    private var paginationResponseHandlerLiveData: MutableLiveData<PaginationResponseHandler?> =
        MutableLiveData(null)
    private val myPageSize = 100
    private var skillsPagedList: LiveData<PagedList<SkillsModel>>? = null

    fun tryAgain() {
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
        skillsPagedList?.value?.dataSource?.invalidate()
    }

    fun getPaginationResponseHandler(): MutableLiveData<PaginationResponseHandler?> =
        paginationResponseHandlerLiveData

    private val skillsModelObservable =
        ObservableField<SkillsRequestModel>().apply { set(SkillsRequestModel()) }

    fun getSkillPagedList(): LiveData<PagedList<SkillsModel>>? = skillsPagedList

    fun getSkillsList(searchStr: String) {

        val userModel =
            applicationDatabase.getUserDao().getCurrentUser()?.replicate<UserEntity, UserModel>()

        val pagedDataSourceFactory = PaginationDataSourceFactory(
            skillsModelObservable.apply {

                set(SkillsRequestModel().apply {
                    this.userId = 1//userModel?.userId ?: 0
                    this.searchKey = searchStr
                    this.pageSize = myPageSize
                })
            },
            paginationResponseHandlerLiveData
        ) {
            apiRequest {
                skillsController.getSkillsList(it)
            } ?: arrayListOf()
        }

        skillsPagedList = LivePagedListBuilder(
            pagedDataSourceFactory,
            PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(myPageSize).build()
        ).build()

        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

    fun searchSkills(searchString: String) {
        skillsModelObservable.get().apply {
            this?.searchKey = searchString
        }
        skillsPagedList?.value?.dataSource?.invalidate()
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

}