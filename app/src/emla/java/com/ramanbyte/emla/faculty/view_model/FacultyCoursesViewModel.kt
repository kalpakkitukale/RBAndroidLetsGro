package com.ramanbyte.emla.faculty.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.emla.faculty.models.FacultyCoursesModel

class FacultyCoursesViewModel (mContext: Context) : BaseViewModel(mContext = mContext) {
    override var noInternetTryAgain: () -> Unit = {

    }

    fun onClickCourse(view: View, coursesModel: FacultyCoursesModel){

    }
}