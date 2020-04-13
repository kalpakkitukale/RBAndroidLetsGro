package com.ramanbyte.emla.models

import androidx.databinding.BaseObservable

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 27/02/2020
 */
abstract class BaseRequestModel : BaseObservable() {

    var pageNo      : Int = 0
    var pageSize    : Int = 10
}