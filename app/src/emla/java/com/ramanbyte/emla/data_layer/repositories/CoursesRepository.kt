package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.data_layer.pagination.PaginationResponseHandler
import com.ramanbyte.emla.data_layer.network.api_layer.CoursesController
import com.ramanbyte.emla.models.CourseSyllabusModel
import org.kodein.di.generic.instance
import com.ramanbyte.emla.data_layer.pagination.PaginationDataSourceFactory
import com.ramanbyte.emla.data_layer.room.ApplicationDatabase
import com.ramanbyte.emla.data_layer.room.entities.UserEntity
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.CoursesRequest
import com.ramanbyte.utilities.replicate
import org.kodein.di.generic.instance

class CoursesRepository(mContext: Context) :BaseRepository(mContext) {

    val coursesController: CoursesController by instance()

    suspend fun getCoursesSyllabus(courseId: Int): CourseSyllabusModel? {
        return apiRequest {
            coursesController.getCoursesSyllabus(courseId, /*userModel?.userId ?:*/ 0)
        }
    }


    val applicationDatabase : ApplicationDatabase by instance()
    val userModel = applicationDatabase.getUserDao().getCurrentUser()?.replicate<UserEntity, UserModel>()

    private val pageSize = 10
    private val coursesModelObservable = ObservableField<CoursesRequest>().apply { set(CoursesRequest()) }
    lateinit var coursesPagedDataSourceFactory: PaginationDataSourceFactory<CoursesModel, CoursesRequest>
    private var paginationResponseHandlerLiveData: MutableLiveData<PaginationResponseHandler?> = MutableLiveData(null)
    var coursesPagedList: LiveData<PagedList<CoursesModel>>? = null
    private val pageListConfig = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(pageSize).build()

    fun initiatePagination() {
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
        coursesPagedDataSourceFactory = PaginationDataSourceFactory(
            coursesModelObservable.apply {
                set(CoursesRequest().apply {
                    this.userId = userModel?.userId ?: 0
                })
            },
            paginationResponseHandlerLiveData
        ) {
            apiRequest {
                coursesController.getCourses(it)
            } ?: arrayListOf()
        }

        coursesPagedList = LivePagedListBuilder(coursesPagedDataSourceFactory, pageListConfig).build()

    }
    fun getPaginationResponseHandler() = paginationResponseHandlerLiveData

    fun searchCourse(searchString: String) {
        coursesModelObservable.get().apply {
            this?.searchKey = searchString
        }
        coursesPagedList?.value?.dataSource?.invalidate()
    }

    /*
  * This function is used for to fetch the fresh order history list on try again button
  * */
    fun tryAgain() {
        coursesPagedList?.value?.dataSource?.invalidate()
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

    fun filterCourseList(coursesRequest: CoursesRequest) {
        coursesModelObservable.get()?.apply {
            this.userId = coursesRequest.userId
        }
        coursesPagedList?.value?.dataSource?.invalidate()
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

}