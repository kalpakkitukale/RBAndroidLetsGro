package com.ramanbyte.utilities

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Base64
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Spinner
import com.ramanbyte.R
import com.ramanbyte.data_layer.SharedPreferencesDatabase
import com.ramanbyte.views.RoundedTextDrawable
import java.util.*


/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 27/02/2020
 */
object StaticMethodUtilitiesKtx {

    val currentMonthAsS3KeyObject =
        DateUtils.getCurrentDateTime(DateUtils.DATE_ONLY_MONTH_YEAR_PATTERN_NO_SPACE)?.capitalize()

    fun hideSpinnerDropDown(spinner: Spinner) {
        try {
            val method = Spinner::class.java.getDeclaredMethod("onDetachedFromWindow")
            method.isAccessible = true
            method.invoke(spinner)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*To hide the keyboard*/
    @SuppressLint("WrongConstant")
    fun hideKeyBoard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService("input_method") as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun getAppVersion(context: Context): String {
        var result = ""
        try {
            result = context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionName
            result = result.replace("[a-zA-Z]|-".toRegex(), "")
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }

        return result
    }

    fun getDeviceManufacturer(): String {
        return Build.MANUFACTURER
    }

    fun getDeviceModelName(): String {
        return Build.MODEL
    }

    fun getDeviceVersion(): String {
        return BindingUtils.string(R.string.android) + " " + Build.VERSION.RELEASE
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    fun setIMEINumber(mContext: Context) {
        var deviceUniqueIdentifier: String? = ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (null == deviceUniqueIdentifier || deviceUniqueIdentifier.isEmpty()) {
                deviceUniqueIdentifier =
                    Settings.Secure.getString(mContext.contentResolver, Settings.Secure.ANDROID_ID)
            }
        } else {
            val telephonyManager =
                mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (telephonyManager != null) {
                @Suppress("DEPRECATION")
                deviceUniqueIdentifier = telephonyManager.deviceId
            }
        }
        deviceUniqueIdentifier?.let {
            SharedPreferencesDatabase.setStringPref(
                SharedPreferencesDatabase.KEY_DEVICE_IMEI,
                it
            )
        }
    }


    fun getBitmapFromBase64String(encodedImage: String?): Bitmap? {
        try {
            if (encodedImage != null) {
                val decodedString =
                    Base64.decode(encodedImage, Base64.DEFAULT)
                return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
        return null
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getDrawableDensity(mContext: Context): Int {
        val densityDpi = mContext.resources.displayMetrics.densityDpi
        return if (densityDpi <= 120) { //            return DisplayMetrics.DENSITY_LOW;
            DENSITY__LOW
        } else if (densityDpi <= 160) { //            return DisplayMetrics.DENSITY_MEDIUM;
            DENSITY__MEDIUM
        } else if (densityDpi <= 240) { //            return DisplayMetrics.DENSITY_HIGH;
            DENSITY__HIGH
        } else if (densityDpi <= 320) { //            return DisplayMetrics.DENSITY_XHIGH;
            DENSITY__XHIGH
        } else if (densityDpi <= 480) { //            return DisplayMetrics.DENSITY_XXHIGH;
            DENSITY__XXHIGH
        } else if (densityDpi <= 640) { //            return DisplayMetrics.DENSITY_XXXHIGH;
            DENSITY__XXXHIGH
        } else { //            return DisplayMetrics.DENSITY_DEFAULT;
            DENSITY__DEFAULT
        }
    }

    fun getRandomColors(): Int {
        //arrayOf<Int>(R.color.middleTurquoise,R.color.colorLogoPurple,R.color.colorOrange,R.color.colorCatalinaBlue)
        val rnd = Random()
        val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        return color
    }

    fun getGoogleApiKey(context: Context): String {

        val app: ApplicationInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        val bundle = app.metaData

        return bundle?.getString("google_api_key", "") ?: ""

    }

    /***Vinay K*/
    fun changeStatusBarColor(window: Window, colorResourceId: Int) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //window.addFlags(WindowManager.LayoutParams.)
            window.statusBarColor = BindingUtils.color(colorResourceId)
            //window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun checkValues(givenValue: String?): String? {
        try {
            return if (givenValue.isNullOrBlank() || givenValue.isNullOrEmpty() || givenValue.equals(
                    "n/a",
                    true
                )
                || givenValue.equals("NA", true)
            ) {
                KEY_NA
            } else {
                givenValue
            }
        } catch (e: java.lang.NullPointerException) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
        return KEY_NA
    }

    fun calculateCustomChipWidth(context: Context, noOfColumn: Int = 3): Int {
        val displayMetrics = context.resources.displayMetrics
        val totalWidth = displayMetrics.widthPixels
//        return (totalWidth / noOfColumn)
        return (totalWidth * 0.25).toInt()
    }

    fun calculateCustomChipHeight(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val totalWidth = displayMetrics.widthPixels
        return (totalWidth * 0.125).toInt()
    }

    fun getStatusBarHeight(mContext: Activity): Int {
        var result = 0
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                val rectangle = Rect()
                val window = mContext!!.window
                window.decorView.getWindowVisibleDisplayFrame(rectangle)
                val statusBarHeight = rectangle.top
                val contentViewTop = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
                result = contentViewTop - statusBarHeight
            } else {
                val resourceId =
                    mContext.resources.getIdentifier(
                        KEY_STATUS_BAR_HEIGHT,
                        KEY_DIMEN,
                        KEY_ANDROID
                    )

                if (resourceId > 0) {
                    result = mContext.resources.getDimensionPixelSize(resourceId)
                }
            }

        } catch (throwable: Throwable) {
            throwable.message

        }
        return result
    }

    /*SET THE DEFAULT IMAGE ICON WITH FIRST LETTER*/
    fun setDefaultImage(name: String, view: View, width: Float, height: Float) {
        try {
            val textDrawable = RoundedTextDrawable.builder()
                .beginConfig()
                .textColor(BindingUtils.color(R.color.colorWhite))
                .width(width.toInt())
                .height(height.toInt())
                .endConfig()
                .round()
                .build(
                    name.toUpperCase(),
                    BindingUtils.color(R.color.colorBlueIconOnLightBgAndDarkBg)
                )
            view?.background = textDrawable
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
    }
}