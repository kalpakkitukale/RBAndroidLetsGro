package com.ramanbyte.emla.models.request

import androidx.databinding.BaseObservable

/**
 * @author Mansi Manakiki Mody
 * @since 5 Feb 2021
 * @description Model class for course fee for transaction
 */
class CourseFeeRequestModel : BaseObservable() {
    var id: Int = 0
    var userId: Int = 0
    var paymentId: Int = 0
    var courseDetailsId: Int = 0
    var courseFeeStructureId: Int = 0
}