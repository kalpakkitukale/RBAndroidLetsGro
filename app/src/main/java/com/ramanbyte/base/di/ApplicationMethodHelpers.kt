package com.ramanbyte.base.di

import android.content.Context
import android.view.View
import androidx.annotation.Nullable
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import android.widget.Checkable




/**
 * @AddedBy Vinay K <vinay.k@ramanbyte.com>
 * @since 29/02/2020
 */

/*
   *
   Dev - 0005
   Test - 0001
   UAT - 0002
   Prod - 0010
   PatkaiUAT - 0012
   PatkaiProd - 0011
   KohimaProd - 0015
   KohimaUat - 0020
   dH44u - Pune Institute of Business Management
   0YUaA - Patkai Christian College
   9mJjq - Kohima Science College
   * */
/*
* Below Code is Static temporary until Client Objects are fetched dynamically through API!
* */
fun setClientS3Object(mContext: Context, clientCode: String) {

    if (AppS3Client.createInstance(mContext).getDefaultObject().isNotEmpty())
        return

    val clientS3Object = when (clientCode) {

        "0005" -> "development"

        "0001" -> "test"

        "0002" -> "pibm-uat"
        "0010", "dH44u" -> "pibm"

        "0012" -> "patkai-uat"
        "0011", "0YUaA" -> "patkai"

        "0020" -> "kohima-uat"
        "0015", "9mJjq" -> "kohima"

        else -> ""
    }

    if (clientS3Object.isNotEmpty()) {
        AppS3Client.createInstance(mContext).setDefaultObject(clientS3Object)
    } else {
        setClientS3Object(mContext, clientCode)
    }
}
/*
interface RadioCheckable : Checkable {
    fun addOnCheckChangeListener(onCheckedChangeListener: OnCheckedChangeListener)
    fun removeOnCheckChangeListener(onCheckedChangeListener: OnCheckedChangeListener)

    interface OnCheckedChangeListener {
        fun onCheckedChanged(radioGroup: View, isChecked: Boolean)
    }
}*/

/*inline fun<T, reified Replica> T.replicate() : Replica {

    if(this != null){

        val tClazz: Class<out T> = this::class.java
        val rClazz = Replica::class.java

        val tMembers = tClazz.declaredFields.toList()
        val rMembers = rClazz.declaredFields.toList()

        return Replica::class.java.newInstance().also {replica ->
            tMembers.forEach { field ->
                val fClazz = rClazz.getDeclaredField(field.name)

            }
        }
    }

    return Replica::class.java.newInstance()
}*/