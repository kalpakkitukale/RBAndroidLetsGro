package com.ramanbyte.emla.data_layer.room.data_converter

import androidx.room.TypeConverter

class DataConverter {
    @TypeConverter
    fun fromByteArrayToString(byteArray: ByteArray): String {
        return String(byteArray)
    }

    @TypeConverter
    fun fromStringToByteArray(string: String): ByteArray {
        return string.toByteArray()
    }
}