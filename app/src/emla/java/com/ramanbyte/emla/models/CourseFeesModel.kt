package com.ramanbyte.emla.models


/**
 * @author Akash Inkar <akash.1@ramanbyte.com>
 * @since 4/3/21
 */
class CourseFeesModel {
    var id: Int = 0
    var userId: Int = 0
    var paymentId: Int = 0
    var courseDetailsId: Int = 0
    var courseFeeStructureId: Int = 0

    var courseDetails = CourseDetailsModel()
    var courseFeeStructure = CourseFeeStructureModel()
}