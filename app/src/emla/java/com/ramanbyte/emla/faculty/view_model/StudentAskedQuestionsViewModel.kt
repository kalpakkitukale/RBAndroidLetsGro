package com.ramanbyte.emla.faculty.view_model

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.paging.PagedList
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.pagination.PaginationMessages
import com.ramanbyte.emla.faculty.data_layer.repositories.FacultyCoursesRepository
import com.ramanbyte.emla.faculty.models.FacultyCoursesModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.KEY_COURSE_MODEL
import org.kodein.di.generic.instance

class StudentAskedQuestionsViewModel(mContext: Context) : BaseViewModel(mContext = mContext) {

    private val coursesRepository: FacultyCoursesRepository by instance()

    var searchQuery = MutableLiveData<String>().apply {
        value = KEY_BLANK
    }

    var onClickCloseBottomSheetLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    var onClickApplyFilterLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    var onClickClearFilterLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }
    var onClickCardLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

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

    fun onClickCloseBottomSheet(view: View){
        onClickCloseBottomSheetLiveData.value = true
    }
    fun onClickApplyFilter(view: View){
        onClickApplyFilterLiveData.value = true
    }
    fun onClickClearFilter(view: View){
        onClickClearFilterLiveData.value = true
    }

    fun onClickCard(view: View){
        view.findNavController()
            .navigate(
                R.id.action_studentAskedQuestionsFragment_to_facultyQuestionAnswerFragment
                /*Bundle().apply {
                    putParcelable(KEY_COURSE_MODEL, coursesModel)
                }*/)
        //onClickCardLiveData.value = true
    }

}