package com.ramanbyte.utilities

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 27/02/2020
 */

object PermissionsManager {


    fun checkPermission(
        thisActivity: Activity,
        permission: String,
        permissionRequestCode: Int
    ): Boolean {

        if (ContextCompat.checkSelfPermission(
                thisActivity,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                thisActivity,
                arrayOf(permission),
                permissionRequestCode
            )

            return false
        } else {
            return true
        }
    }

    fun checkPermission(thisActivity: Activity, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            thisActivity,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * check permission denied by user with dont ask again checked */
    fun shouldShowRequestPermissionRationale(thisActivity: Activity, permission: String): Boolean {
        // return true if user denied permission with do not ask again checked.
        return ActivityCompat.shouldShowRequestPermissionRationale(thisActivity, permission)
    }


/* this for the multiple permission */
    fun checkPermissions(thisActivity: Activity, permissions: Array<String>, permissionRequestCode: Int): Boolean
    {

        val deniedPermissions = arrayListOf<String>()

        permissions.forEach {
            if (ContextCompat.checkSelfPermission(thisActivity, it) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(it)
            }
        }

        return if (deniedPermissions.size > 0) {
//            if (!SharedPreferencesDatabase.getBoolPref(SharedPreferencesDatabase.IS_PERMISSION_DENIED))
//                ActivityCompat.requestPermissions(thisActivity, deniedPermissions.toTypedArray(), permissionRequestCode)

            false
        } else {
            true
        }

        /*  return if (ContextCompat.checkSelfPermission(thisActivity, permission) != PackageManager.PERMISSION_GRANTED) {

              false
          } else {
              true
          }*/
    }
}