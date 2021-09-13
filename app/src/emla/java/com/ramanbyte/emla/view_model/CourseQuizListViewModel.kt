package com.ramanbyte.emla.view_model

import android.content.Context
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.emla.models.CoursesModel

class CourseQuizListViewModel(mContext: Context) : BaseViewModel(mContext) {
    var courseModel: CoursesModel? = null


    override var noInternetTryAgain: () -> Unit = {
//        chaptersRepository.tryAgain()
    }
}