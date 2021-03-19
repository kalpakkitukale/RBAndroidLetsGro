package com.ramanbyte.emla.faculty.data_layer.repositories

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.data_layer.pagination.PaginationResponseHandler
import com.ramanbyte.emla.data_layer.pagination.PaginationDataSourceFactory
import com.ramanbyte.emla.data_layer.room.entities.UserEntity
import com.ramanbyte.emla.faculty.data_layer.network.api_layer.FacultyMasterApiController
import com.ramanbyte.emla.faculty.models.FacultyCoursesModel
import com.ramanbyte.emla.faculty.models.request.FacultyCoursesRequestModel
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.utilities.replicate
import org.kodein.di.generic.instance

class FacultyCoursesRepository(mContext: Context) : BaseRepository(mContext) { //FacultyMasterRepository

    private val masterApiController: FacultyMasterApiController by instance()

    fun getCurrentUser(): UserModel? {
        applicationDatabase.getUserDao().apply {
            return getCurrentUser()?.replicate<UserEntity, UserModel>()
        }
    }

    private val pageSize = 10
    private val coursesModelObservable =
        ObservableField<FacultyCoursesRequestModel>().apply { set(FacultyCoursesRequestModel()) }
    lateinit var coursesPagedDataSourceFactory: PaginationDataSourceFactory<FacultyCoursesModel, FacultyCoursesRequestModel>
    private var paginationResponseHandlerLiveData: MutableLiveData<PaginationResponseHandler?> =
        MutableLiveData(null)
    var coursesPagedList: LiveData<PagedList<FacultyCoursesModel>>? = null
    private val pageListConfig =
        PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(pageSize).build()

    fun initiatePagination() {
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
        coursesPagedDataSourceFactory = PaginationDataSourceFactory(
            coursesModelObservable.apply {
                val userModel = getCurrentUser()
                set(FacultyCoursesRequestModel().apply {
                    this.userId = userModel?.userId ?: 0
                })
            },
            paginationResponseHandlerLiveData
        ) {
            apiRequest {
                masterApiController.getCourses(it)
            } ?: arrayListOf()
        }

        coursesPagedList =
            LivePagedListBuilder(coursesPagedDataSourceFactory, pageListConfig).build()

    }

    fun getPaginationResponseHandler() = paginationResponseHandlerLiveData


    /*
    * This function is used for to fetch the fresh order history list on try again button
    * */
    fun tryAgain() {
        coursesPagedList?.value?.dataSource?.invalidate()
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

}