package com.ramanbyte.emla.faculty.view_model.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ramanbyte.emla.faculty.view_model.FacultyContainerViewModel
import com.ramanbyte.emla.faculty.view_model.FacultyCoursesViewModel
import com.ramanbyte.emla.view_model.LauncherViewModel
import com.ramanbyte.view_model.factory.BaseViewModelFactory

class FacultyViewModelFactory(private val mContext: Context) :
    BaseViewModelFactory(mContext = mContext) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(FacultyContainerViewModel::class.java) -> {
                return FacultyContainerViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(FacultyCoursesViewModel::class.java) -> {
                return FacultyCoursesViewModel(mContext) as T
            }
            else -> super.create(modelClass)
        }
    }
}