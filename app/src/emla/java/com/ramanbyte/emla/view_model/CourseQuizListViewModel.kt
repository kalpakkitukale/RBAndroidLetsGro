package com.ramanbyte.emla.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.pagination.PaginationMessages
import com.ramanbyte.emla.data_layer.repositories.CourseQuizRepository
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.response.CourseQuizModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import org.kodein.di.generic.instance

class CourseQuizListViewModel(mContext: Context) : BaseViewModel(mContext) {
    var courseModel: CoursesModel? = null

    private val courseQuizRepository: CourseQuizRepository by instance()


    override var noInternetTryAgain: () -> Unit = {
        courseQuizRepository.retryForQuizList(courseModel!!.courseId)
    }

    fun getCourseQuizList() {

        courseQuizRepository.initiatePagination(courseModel!!.courseId)

        courseQuizRepository.getPaginationResponseHandler().observeForever {
            if (it != null) {

                paginationResponse(
                    it,
                    PaginationMessages(
                        BindingUtils.string(R.string.no_data),
                        BindingUtils.string(R.string.no_data),
                        BindingUtils.string(R.string.no_internet_message),
                        BindingUtils.string(R.string.some_thing_went_wrong)
                    )
                )
                AppLog.infoLog("Pagination :: ${it.msg} :: ${it.status}")
            }
        }
    }

    fun quizListForCoursePagedList(): LiveData<PagedList<CourseQuizModel>>? {
        return courseQuizRepository.quizListForCoursePagedList
    }
}