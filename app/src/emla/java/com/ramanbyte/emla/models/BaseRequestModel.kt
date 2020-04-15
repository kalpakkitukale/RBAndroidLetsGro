package com.ramanbyte.emla.models

import androidx.databinding.BaseObservable

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 13-04-2020
 */
abstract class BaseRequestModel : BaseObservable() {

    var pageNo      : Int = 0
    //var pageno      : Int = 0
    var pageSize    : Int = 10
}