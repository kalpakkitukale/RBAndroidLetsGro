package com.ramanbyte.emla.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.repositories.CoursesRepository
import com.ramanbyte.utilities.KEY_BLANK
import org.kodein.di.generic.instance

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 13-04-2020
 */
class ContainerViewModel(mContext: Context) : BaseViewModel(mContext = mContext) {
    override var noInternetTryAgain = {

    }

    val toolbarTitleLiveData = MutableLiveData<String>()
    private val coursesRepository: CoursesRepository by instance()
    var userFullName = KEY_BLANK

    init {
        CoroutineUtils.main {
            try {
                /**Niraj N
                 * Used to set Name to navigation drawer.*/
                userFullName = "${coursesRepository.getCurrentUser()?.firstName!!} ${coursesRepository.getCurrentUser()?.lastName!!}"
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}