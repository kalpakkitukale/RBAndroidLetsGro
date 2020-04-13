package com.ramanbyte.placement.data_layer.room.data_converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

class JsonConverter : Serializable {

    @TypeConverter // note this annotation
    fun fromOptionValues(optionValues: LogInResponseModel?): String? {
        if (optionValues == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<LogInResponseModel>() {

        }.getType()
        return gson.toJson(optionValues, type)
    }

    @TypeConverter // note this annotation
    fun toOptionValues(optionValuesString: String?): LogInResponseModel? {
        if (optionValuesString == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<LogInResponseModel>() {

        }.getType()
        return gson.fromJson(optionValuesString, type)
    }




}