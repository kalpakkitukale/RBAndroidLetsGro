package com.ramanbyte.emla.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.emla.data_layer.repositories.MasterRepository
import com.ramanbyte.emla.models.UserModel
import org.kodein.di.generic.instance

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 14-04-2020
 */
class LauncherViewModel(var mContext: Context) : BaseViewModel(mContext) {

    private val masterRepository: MasterRepository by instance()

    var userModelLiveData = MutableLiveData<UserModel>().apply {
        value = null
    }

    override var noInternetTryAgain: () -> Unit = {

    }

    init {
        userModelLiveData.postValue(masterRepository.getCurrentUser())
    }
}