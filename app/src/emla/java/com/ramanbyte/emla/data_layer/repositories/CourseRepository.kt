package com.ramanbyte.emla.data_layer.repositories

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
import com.ramanbyte.emla.models.ContentModel
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.CoursesRequest
import com.ramanbyte.idbi.data_layer.pagination.PaginationDataSourceFactory
import com.ramanbyte.idbi.data_layer.pagination.PaginationResponseHandler
import com.ramanbyte.idbi.data_layer.repositories.base.BaseRepository
import com.ramanbyte.idbi.data_layer.room.entities.UserEntity
import com.ramanbyte.idbi.model.*
import com.ramanbyte.idbi.model.request.ChapterContentRequest
import com.ramanbyte.idbi.model.request.ChapterRequest
import com.ramanbyte.idbi.model.request.CoursesRequest
import com.ramanbyte.idbi.utilities.replicate


/**
 * Created by Kunal Rathod
 * 9/12/19
 */
class CourseRepository(
    private val mContext: Context
) : BaseRepository(mContext) {

    /**
     *Taking currentUser data from local database
     */
    val userModel =
        applicationDatabase.getUserDao().getCurrentUser()?.replicate<UserEntity, UserModel>()

    var coursesPagedList: LiveData<PagedList<CoursesModel>>? = null
    lateinit var coursesPagedDataSourceFactory: PaginationDataSourceFactory<CoursesModel, CoursesRequest>
    private val pageSize = 10
    private val pageListConfig =
        PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(pageSize).build()
    private val pagedListConfig =
        PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(pageSize).build()
    private val coursesModelObservable = ObservableField<CoursesRequest>().apply {
        set(CoursesRequest())
    }
    private var paginationResponseHandlerLiveData: MutableLiveData<PaginationResponseHandler?> =
        MutableLiveData(null)

    /* init {

     }*/

    fun initiatePagination() {

        coursesPagedDataSourceFactory = PaginationDataSourceFactory(
            coursesModelObservable.apply {
                set(CoursesRequest().apply {
                    this.userId = userModel?.userId ?: 0
                })
            },
            paginationResponseHandlerLiveData
        ) {
            apiRequest {
                idbiApiController.getCourses(it)
            } ?: arrayListOf()
        }

        coursesPagedList =
            LivePagedListBuilder(coursesPagedDataSourceFactory, pageListConfig).build()
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

    fun getPaginationResponseHandler() = paginationResponseHandlerLiveData

    /*
    * This function is used for to fetch the fresh order history list on try again button
    * */
    fun tryAgain() {
        coursesPagedList?.value?.dataSource?.invalidate()
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }


    /*Kunal Rathod*/
    //For Chapter Session List
    lateinit var chapterPagedDataSourceFactory: PaginationDataSourceFactory<ChapterModel, ChapterRequest>
    private var chapterPaginationResponseHandlerLiveData: MutableLiveData<PaginationResponseHandler?> =
        MutableLiveData(null)

    private var chapterRequestModel = ObservableField<ChapterRequest>()
    var chapterPagedList: LiveData<PagedList<ChapterModel>>? = null

    fun initChapterListPage(courseId: Int) {
        chapterPagedDataSourceFactory = PaginationDataSourceFactory(
            chapterRequestModel.apply {
                set(ChapterRequest().apply {
                    this.userId = userModel?.userId ?: 0
                    this.courseId = courseId

                })
            },
            chapterPaginationResponseHandlerLiveData
        ) {
            apiRequest {
                idbiApiController.getChapterList(it)
            } ?: arrayListOf()
        }

        chapterPagedList =
            LivePagedListBuilder(chapterPagedDataSourceFactory, pageListConfig).build()

        chapterPaginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

    fun getChapterResponseHandler() = chapterPaginationResponseHandlerLiveData

    /*For refreshing the list on try again button*/
    fun refreshChapter() {
        chapterPagedList?.value?.dataSource?.invalidate()
        chapterPaginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }


    //For Chapter List
    var chapterContentPagedList: LiveData<PagedList<ChapterContentModel>>? = null
    private var chapterContentRequestModel = ObservableField<ChapterContentRequest>()
    lateinit var chapterContentPagedDataSourceFactory: PaginationDataSourceFactory<ChapterContentModel, ChapterContentRequest>
    private var chapterContentPaginationResponseHandlerLiveData: MutableLiveData<PaginationResponseHandler?> =
        MutableLiveData(null)


    fun initChapterContentListPage(chapterId: Int) {
        chapterContentPagedDataSourceFactory = PaginationDataSourceFactory(
            chapterContentRequestModel.apply {
                set(ChapterContentRequest().apply {
                    this.chapterId = chapterId
                })
            },
            chapterContentPaginationResponseHandlerLiveData
        ) {
            apiRequest {
                idbiApiController.getChapterContentList(it)
            } ?: arrayListOf()

        }

        chapterContentPagedList =
            LivePagedListBuilder(chapterContentPagedDataSourceFactory, pageListConfig).build()

        chapterContentPaginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

    fun getChapterContentResponseHandler() = chapterContentPaginationResponseHandlerLiveData


    suspend fun getCoursesSyllabus(courseId: Int): CourseSyllabusModel? {
        return apiRequest {
            idbiApiController.getCoursesSyllabus(courseId,userModel?.userId ?: 0)
        }
    }

    fun refreshContent() {
        chapterContentPagedList?.value?.dataSource?.invalidate()
        chapterContentPaginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

    suspend fun getCourseResult(courseId: Int) : ArrayList<CourseResultModel>? {
        return apiRequest {
            idbiApiController.getCourseResult(courseId,userModel?.userId ?: 0)
        }
    }

    suspend fun getChapterContentList(chapterId: Int) : ArrayList<ContentModel>? {
        return apiRequest {
            idbiApiController.getChapterContentList(chapterId)
        }
    }


    fun getLoggedUserModel() : UserModel?{
      return userModel
    }


}