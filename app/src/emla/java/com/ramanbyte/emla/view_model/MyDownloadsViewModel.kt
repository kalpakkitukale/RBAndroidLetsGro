package com.ramanbyte.emla.view_model

import android.content.Context
import com.ramanbyte.base.BaseViewModel

class MyDownloadsViewModel(mContext: Context) : BaseViewModel(mContext) {

    override var noInternetTryAgain: () -> Unit = {

    }

}