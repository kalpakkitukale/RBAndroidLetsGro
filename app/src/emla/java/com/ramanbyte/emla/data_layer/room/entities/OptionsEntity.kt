package com.ramanbyte.emla.data_layer.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ramanbyte.utilities.KEY_BLANK

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 15/04/20
 */
@Entity
class OptionsEntity {

    @PrimaryKey(autoGenerate = true)
    var localId = 0

    var questionsLocalId: Long = 0

    var id: Int = 0
    var question_Id: Int = 0
    var options: String = KEY_BLANK
    var iscorrect: String = KEY_BLANK
    var createdDate: String = KEY_BLANK
    var modifiedDate: String = KEY_BLANK
    var app_Status: String = KEY_BLANK
    var del_Status: String = KEY_BLANK
    var ipAddress: String = KEY_BLANK

    var answer: Int? = 0

}