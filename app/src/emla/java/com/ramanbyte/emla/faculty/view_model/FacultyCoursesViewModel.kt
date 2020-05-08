package com.ramanbyte.emla.faculty.view_model

import android.view.View
import androidx.lifecycle.ViewModel
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.emla.faculty.models.FacultyCoursesModel

class FacultyCoursesViewModel : BaseViewModel() {
    override var noInternetTryAgain: () -> Unit = {

    }

    fun onClickCourse(view: View, coursesModel: FacultyCoursesModel){

    }
}