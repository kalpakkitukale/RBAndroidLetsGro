package com.ramanbyte.emla.faculty.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.emla.data_layer.repositories.MasterRepository
import com.ramanbyte.emla.models.UserModel
import org.kodein.di.generic.instance

class FacultyContainerViewModel (mContext: Context) : BaseViewModel(mContext = mContext) {
    override var noInternetTryAgain: () -> Unit = {

    }
    val toolbarTitleLiveData = MutableLiveData<String>()

    private val masterRepository: MasterRepository by instance()

    var userModelLiveData = MutableLiveData<UserModel>().apply {
        value = null
    }

    init {
        userModelLiveData.postValue(masterRepository.getCurrentUser())
    }
}