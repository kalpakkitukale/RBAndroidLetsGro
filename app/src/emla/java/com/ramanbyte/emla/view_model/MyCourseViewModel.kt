package com.ramanbyte.emla.view_model

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.paging.PagedList
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.pagination.PaginationMessages
import com.ramanbyte.emla.data_layer.repositories.TransactionRepository
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_COURSE_MODEL
import org.kodein.di.generic.instance

/**
 * @author Akash Inkar <akash.1@ramanbyte.com>
 * @since 17/4/21
 */
class MyCourseViewModel (var mContext: Context) : BaseViewModel(mContext = mContext) {

    private val transactionRepository: TransactionRepository by instance()
    override var noInternetTryAgain: () -> Unit ={

    }

    fun myCoursesPagedList(): LiveData<PagedList<CoursesModel>>? {
        return transactionRepository.coursesPagedList
    }

    fun myCourseListPagination() {
        try {
            transactionRepository.getPaginationResponseHandler().observeForever {
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
        }catch (e:Exception){
            e.printStackTrace()
            isLoaderShowingLiveData.postValue(false)
            AppLog.infoLog(e.message.toString())
        }
        transactionRepository.myCourseList()
        isLoaderShowingLiveData.postValue(false)
    }


    val onCoursewareclickListener: (view: View, obj: Any) -> Unit = { view, obj ->
        obj as CoursesModel
    showCourseSyllabus(view,obj)

    }
    val onTopicclickListener: (view: View, obj: Any) -> Unit = { view, obj->
        obj as CoursesModel
       showChapterList(view,obj)
    }

    // on click on the topic list
    fun showChapterList(view: View, coursesModel: CoursesModel) {
        view.findNavController().navigate(
            R.id.chaptersListFragment,
            Bundle().apply { putParcelable(KEY_COURSE_MODEL, coursesModel) })
    }

    fun showCourseSyllabus(view: View, coursesModel: CoursesModel) {
        view.findNavController()
            .navigate(
                R.id.courseSyllabusFragment, /*action_coursesFragment_to_courseSyllabusFragment*/
                Bundle().apply {
                    putParcelable(KEY_COURSE_MODEL, coursesModel)
                })
    }
}