package com.ramanbyte.emla.data_layer.repositories

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.data_layer.pagination.PaginationResponseHandler
import com.ramanbyte.emla.data_layer.network.api_layer.CourseQuizController
import com.ramanbyte.emla.data_layer.pagination.PaginationDataSourceFactory
import com.ramanbyte.emla.models.request.CourseQuizRequestModel
import com.ramanbyte.emla.models.response.CourseQuizModel
import org.kodein.di.generic.instance

class CourseQuizRepository(val mContext: Context) : BaseRepository(mContext) {

    private val courseQuizController: CourseQuizController by instance()

    var quizListForCoursePagedList: LiveData<PagedList<CourseQuizModel>>? = null
    lateinit var quizListForCoursePagedDataSourceFactory: PaginationDataSourceFactory<CourseQuizModel, CourseQuizRequestModel>
    private val pageSize = 10
    private val pageListConfig =
        PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(pageSize).build()

    private val courseQuizRequestModelObservable = ObservableField<CourseQuizRequestModel>().apply {
        set(CourseQuizRequestModel())
    }

    private var paginationResponseHandlerLiveData: MutableLiveData<PaginationResponseHandler?> =
        MutableLiveData(null)

    fun initiatePagination(courseID: Int) {

        val userID = applicationDatabase.getUserDao().getCurrentUser()?.userId ?: 0

        courseQuizRequestModelObservable.apply {
            set(CourseQuizRequestModel().apply {
                this.courseId = courseID
                this.userId = userID
            })
        }

        quizListForCoursePagedDataSourceFactory = PaginationDataSourceFactory(
            courseQuizRequestModelObservable,
            paginationResponseHandlerLiveData
        ) { courseQuizModel ->
            apiRequest {
                courseQuizController.getQuizListForCourse(
                    userID,
                    courseID,
                    courseQuizModel.pageNo,
                    pageSize
                )
            }!!
        }

        quizListForCoursePagedList =
            LivePagedListBuilder(
                quizListForCoursePagedDataSourceFactory,
                pageListConfig
            ).build()

        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

    fun getPaginationResponseHandler() = paginationResponseHandlerLiveData

    fun retryForQuizList(courseID: Int) {

        val userID = applicationDatabase.getUserDao().getCurrentUser()?.userId ?: 0

        courseQuizRequestModelObservable.apply {
            set(CourseQuizRequestModel().apply {
                this.courseId = courseID
                this.userId = userID
            })
        }

        quizListForCoursePagedList?.value?.dataSource?.invalidate()
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }
}