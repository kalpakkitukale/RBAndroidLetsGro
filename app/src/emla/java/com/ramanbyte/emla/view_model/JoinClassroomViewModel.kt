package com.ramanbyte.emla.view_model

import android.content.Context
import com.ramanbyte.base.BaseViewModel

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 04/02/2021
 */
class JoinClassroomViewModel(mContext:Context):BaseViewModel(mContext = mContext) {
    override var noInternetTryAgain: () -> Unit ={

    }

}