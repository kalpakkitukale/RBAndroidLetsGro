package com.ramanbyte.emla.models

import com.ramanbyte.utilities.DateUtils
import com.ramanbyte.utilities.checkValues


/**
 * Created by Kunal Rathod
 * 24/12/19
 */
class CourseSyllabusModel {
    var length: String? = ""
        set(value) {
            field = value.checkValues()
        }
    var efforts: String? = ""
        set(value) {
            field = value.checkValues()
        }
    var level: String? = ""
        set(value) {
            field = value.checkValues()
        }
    var language: String? = ""
        set(value) {
            field = value.checkValues()
        }
    var allocatedFacultys: String? = ""
        set(value) {
            field = value.checkValues()
        }
    var whtILern: String? = ""
        set(value) {
            field = value.checkValues()
        }
    var hwToUse: String? = ""
        set(value) {
            field = value.checkValues()
        }
        get() = "<p>"+field!!+"</p>"
    var chapterHTML: String? = ""
        set(value) {
            field = value.checkValues()
        }
    var instructionAssessment: String? = ""
        set(value) {
            field = value.checkValues()
        }
    var summativeAssessmentStatus : String ?= null
    var specialization : String ?= null
    var durationType : String ?= null
    var summativeaAtemptCount : Int ?= 0
    var totalSummativeCount : Int ?= 0
}