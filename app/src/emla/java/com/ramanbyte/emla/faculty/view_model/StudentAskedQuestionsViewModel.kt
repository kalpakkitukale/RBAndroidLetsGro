package com.ramanbyte.emla.faculty.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.pagination.PaginationMessages
import com.ramanbyte.emla.faculty.data_layer.repositories.FacultyCoursesRepository
import com.ramanbyte.emla.faculty.models.FacultyCoursesModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import org.kodein.di.generic.instance

class StudentAskedQuestionsViewModel(mContext: Context) : BaseViewModel(mContext = mContext) {

    private val coursesRepository: FacultyCoursesRepository by instance()

    override var noInternetTryAgain: () -> Unit = {}

    fun initPaginationResponseHandler() {
        coursesRepository.getPaginationResponseHandler().observeForever {
            if (it != null) {
                paginationResponse(
                    it,
                    //PaginationMessages("No Data", "No More data", "No Internet", "Something Wrong")
                    PaginationMessages(
                        BindingUtils.string(R.string.faculty_no_courses),
                        BindingUtils.string(R.string.faculty_no_more_courses),
                        BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet),
                        BindingUtils.string(R.string.some_thing_went_wrong)
                    )
                )
                AppLog.infoLog("Pagination :: ${it.msg} :: ${it.status}")
            }
        }

        coursesRepository.initiatePagination()
    }

    fun coursesPagedList(): LiveData<PagedList<FacultyCoursesModel>>? {
        return coursesRepository.coursesPagedList
    }

}