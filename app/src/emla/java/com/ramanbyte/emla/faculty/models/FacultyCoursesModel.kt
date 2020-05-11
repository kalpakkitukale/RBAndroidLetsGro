package com.ramanbyte.emla.faculty.models

import com.google.gson.annotations.SerializedName

class FacultyCoursesModel {
    var courseId = 0
    var courseName: String? = null
    @SerializedName("description")
    var courseDescription: String? = null

    var courseCode: String? = null

    var courseImage: String? = null
    var courseImageUrl: String? = null
}