package com.ramanbyte.emla.data_layer.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

@Entity
class UserEntity {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var userId: Int = 0

    var firstName = ""
    var middleName = ""
    var lastName = ""
    var emailId = ""
    var loggedId = "N"
    var manager = false
    var userType = ""
}