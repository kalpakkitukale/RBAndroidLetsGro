package com.ramanbyte.emla.view_model

import android.content.Context
import com.ramanbyte.base.BaseViewModel

class FileViewerViewModel() : BaseViewModel(){

    override var noInternetTryAgain: () -> Unit = {}


}