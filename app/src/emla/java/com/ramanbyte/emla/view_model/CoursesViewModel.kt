package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.emla.data_layer.repositories.CoursesRepository
import com.ramanbyte.emla.models.*
import com.ramanbyte.utilities.AppLog
import org.kodein.di.generic.instance

class CoursesViewModel(val mContext: Context) : BaseViewModel(mContext = mContext) {

    private val coursesRepository: CoursesRepository by instance()

    var coursesModelLiveData: MutableLiveData<CoursesModel> = MutableLiveData()

    var courseSyllabusModelLiveData = MutableLiveData<CourseSyllabusModel?>(null)

    var showValidationMessage = MutableLiveData<String>(null)

    override var noInternetTryAgain: () -> Unit =  {}


    var layoutToShow: MutableLiveData<View> = MutableLiveData()

    fun onHeaderClick(view: View) {
        when (view.id) {
            R.id.headerCourseInfo -> {
                AppLog.infoLog("Course Info Click")
                layoutToShow.value = view
            }
            R.id.headerCourseSyllabus -> {
                AppLog.infoLog("Course Syllabus Click")
                layoutToShow.value = view
            }
            R.id.headerAssessmentInstruction -> {
                AppLog.infoLog("Assessment Instruction Click")
                layoutToShow.value = view
            }
            R.id.headerWhatIWillLearn -> {
                AppLog.infoLog("What i will Learn Click")
                layoutToShow.value = view
            }
            R.id.headerHowToUse -> {
                AppLog.infoLog("How to use Click")
                layoutToShow.value = view
            }
        }
    }

    fun getCoursesSyllabus() {
        val apiCallFunction: suspend () -> Unit = {
            courseSyllabusModelLiveData.postValue(
                coursesRepository.getCoursesSyllabus(
                    coursesModelLiveData.value?.courseId ?: 0
                )
            )
        }
        invokeApiCall(apiCallFunction = apiCallFunction)
    }
}