package com.ramanbyte.emla.view_model

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
import com.ramanbyte.emla.data_layer.repositories.CoursesRepository
import com.ramanbyte.emla.models.CourseResultModel
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.KEY_COURSE_MODEL
import org.kodein.di.generic.instance

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 14-04-2020
 */
class CoursesViewModel(mContext: Context) : BaseViewModel(mContext = mContext) {
    override var noInternetTryAgain: () -> Unit = {
        coursesRepository.tryAgain()
    }
    private val coursesRepository: CoursesRepository by instance()

    var userData: UserModel? = null
    var searchQuery = MutableLiveData<String>().apply {
        value = KEY_BLANK
    }

    init {
        toggleLayoutVisibility(View.GONE, View.GONE, View.GONE, "", View.GONE)
        searchQuery.observeForever {
            coursesRepository.searchCourse(it)
        }
        userData = coursesRepository.getCurrentUser()

    }

    fun initPaginationResponseHandler() {
        coursesRepository.getPaginationResponseHandler().observeForever {
            if (it != null) {
                paginationResponse(
                    it,
                    //PaginationMessages("No Data", "No More data", "No Internet", "Something Wrong")
                    PaginationMessages(
                        BindingUtils.string(R.string.no_courses),
                        BindingUtils.string(R.string.no_more_courses),
                        BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet),
                        BindingUtils.string(R.string.some_thing_went_wrong)
                    )
                )
                AppLog.infoLog("Pagination :: ${it.msg} :: ${it.status}")
            }
        }
        coursesRepository.initiatePagination()
    }

    fun coursesPagedList(): LiveData<PagedList<CoursesModel>>? {
        return coursesRepository.coursesPagedList
    }

    /*
 * Go to Course Details or Pre-assessment
 * */
    fun courseClick(view: View, coursesModel: CoursesModel) {
        view.findNavController()
            .navigate(R.id.action_coursesFragment_to_courseDetailFragment, Bundle().apply {
                putParcelable(KEY_COURSE_MODEL, coursesModel)
            })
        /*  if (coursesModel.preAssessmentStatus == "true") {
              view.findNavController().navigate(R.id.action_coursesFragment_to_courseDetailFragment)
          } else {
              view.findNavController()
                  .navigate(R.id.action_coursesFragment_to_quizInstructionFragment)
          }*/
    }


}