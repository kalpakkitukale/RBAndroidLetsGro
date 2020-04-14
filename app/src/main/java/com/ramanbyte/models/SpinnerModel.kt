package com.ramanbyte.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 13-04-2020
 */

@Entity
class SpinnerModel {

    @PrimaryKey(autoGenerate = true)
    var localId = 0

    var id: Int = 0
    var alphaId: String = ""
    var itemName: String? = ""

    var selectedPosition = -1

    @ColumnInfo(defaultValue = "N")
    var selectionStatus: String = "N"

    @Ignore
    var onNameSelected: () -> Unit = {}
}