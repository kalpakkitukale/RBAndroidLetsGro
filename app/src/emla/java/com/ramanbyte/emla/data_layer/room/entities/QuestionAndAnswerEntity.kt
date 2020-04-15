package com.ramanbyte.emla.data_layer.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ramanbyte.emla.data_layer.room.data_converter.DataConverter
import com.ramanbyte.utilities.KEY_BLANK

@Entity
class QuestionAndAnswerEntity {

    @PrimaryKey(autoGenerate = true)
    var localId = 0

    var id: Int = 0
    var program_Id: Int = 0
    var department_Id: Int = 0
    var course_Id: Int = 0
    var topic_Id: Int = 0
    var question_Type: Int = 0
    var difficulty_Level: Int = 0
    var publicStatus: String? = KEY_BLANK

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    @TypeConverters(DataConverter::class)
    var question_Title: String = KEY_BLANK

    var marks: Double = 0.0
    var createdDate: String = KEY_BLANK
    var modifiedDate: String = KEY_BLANK
    var app_Status: String = KEY_BLANK
    var del_Status: String = KEY_BLANK
    var ipAddress: String = KEY_BLANK
    var total_Options: Int = 0
    var archive: String = KEY_BLANK
    var quizid: Int = 0
    var passignpercentage: Double = 0.0

}