package com.ramanbyte.utilities

import com.ramanbyte.R
import com.ramanbyte.utilities.AppLog.errorLog
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Minutes
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 26/02/2020
 */
object DateUtils {
    const val TIME_WITH_SECONDS = "HH:mm:ss"
    const val TIME_SERVER_PATTERN = "hh:mma"
    const val TIME_24_HR_PATTERN = "HH:mm"
    const val DATE_DISPLAY_PATTERN = "dd MMM yyyy"
    const val DATE_DISPLAY_PATTERN_SEP = "dd-MMM-yyyy"
    const val DATE_DISPLAY_PATTERN_WITH_WEEKNAME = "dd MMM yyyy '('EEEE')'"
    const val DATE_SQLITE_PATTERN = "yyyy-MM-dd"
    const val DATE_PICKER_PATTERN = "yyyy-M-dd"
    const val DATE_SERVER_PATTERN = "MM-dd-yyyy"
    const val DATE_SERVER_PATTERN_DASH = "dd-MM-yyyy"
    const val DATE_DD_MM_YYYY_PATTERN = "dd-MM-yyyy" //
    const val ANDROID_DATE_FORMAT_UTC_DATE_TIME = "MMM dd, yyyy hh:mm:ss a"
    const val DATE_TIME_PATTERN = "dd MMM, yyyy | hh:mm a"
    const val TIME_DISPLAY_PATTERN = "h:mma"
    const val TIME_DISPLAY_PATTERN_WITH_SPACE = "hh:mm a"
    const val TIME_DISPLAY_PATTERN_12HRS_FORMAT = "hh:mm:aa"
    const val DATE_TIME_SECONDS_PATTERN_FOR_LOCAL_FILE = "ddyyHHssSSS"
    const val DATE_TIME_SECONDS_PATTERN_FOR_FILE = "yyyyMMddHHmmss"
    const val DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS =
        "yyyy-MM-dd'T'HH:mm:ss"//2019-05-16T17:18:56
    const val DATE_MONTH_FEEDBACK_FORMAT = "MMM"
    const val DATE_ONLY_MONTH_YEAR_PATTERN = "MMM yyyy"
    const val DATE_WEB_API_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val DATE_ONLY_MONTH_YEAR_PATTERN_NO_SPACE = "MMMyyyy"

    const val DATE_TIME_SESSION_PATTERN = "$TIME_WITH_SECONDS $DATE_SQLITE_PATTERN"
    const val DATE_TIME_SESSION_STATUS_PATTERN = "MMM dd yyyy $TIME_SERVER_PATTERN"
    const val DATE_TIME_SRVER_PATTERN = "mm/dd/yyyy hh:mm:ss a"
    const val DATE_MONTH_DAY_YEAR_PATTERN = "MMM dd, yyyy"

    private var simpleDateFormat: SimpleDateFormat? = null

    @JvmStatic
    fun getRequestDateFromParams(month: Int, date: Int, year: Int, dateFormat: String): String {

        simpleDateFormat = SimpleDateFormat(dateFormat, Locale.US)

        val calendar = Calendar.getInstance()
        calendar.set(year, month, date)

        return simpleDateFormat!!.format(Date(calendar.timeInMillis))
    }

    /***@param hour will be in 24 hrs format*/
    @JvmStatic
    fun getRequestTimeFromParams(hour: Int, min: Int, timeFormat: String): String? {
        try {
            val sdf = SimpleDateFormat(timeFormat, Locale.US)
            val calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = hour
            calendar[Calendar.MINUTE] = min
            return sdf.format(Date(calendar.timeInMillis))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            errorLog(e.message, e)
        }
        return null
    }

    @JvmStatic
    fun getCalendarByCustomDate(date: String, dateFormat: String): Calendar? {
        val calendar = getCurrentCalender()
        try {
            val sdf = SimpleDateFormat(dateFormat, Locale.US)
            calendar!!.time = sdf.parse(date)
            return calendar
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }

        return calendar
    }

    @JvmStatic
    fun getCurrentDateTime(outputFormat: String): String {
        return try {
            val calendar = getCurrentCalender()
            val simpleDateFormat = SimpleDateFormat(outputFormat, Locale.US)
            simpleDateFormat.format(Date(calendar!!.timeInMillis))
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
            ""
        }
    }

    @JvmStatic
    fun getDateTimeWithCount(outputFormat: String, incrementCount: Int = 0): String {
        return try {
            val calendar = getCurrentCalender()
            calendar?.add(Calendar.DAY_OF_MONTH, incrementCount)
            val simpleDateFormat = SimpleDateFormat(outputFormat, Locale.US)
            simpleDateFormat.format(Date(calendar!!.timeInMillis))

        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
            ""
        }
    }

    @JvmStatic
    fun getCurrentCalender(): Calendar? {
        try {
            val timeZone = TimeZone.getDefault()
            return Calendar.getInstance(timeZone)
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }

        return null
    }

    @JvmStatic
    fun getDisplayDateFromDate(strDate: String, inputFormat: String, outputFormat: String): String {
        try {
            val inputSDF = SimpleDateFormat(inputFormat, Locale.US)
            val outputSDF = SimpleDateFormat(outputFormat, Locale.US)

            val date = inputSDF.parse(strDate)
            return outputSDF.format(date)

        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }

        return strDate
    }

    @JvmStatic
    fun isDateFormatMatch(strDate: String, format: String): Boolean {
        try {
            val inputSDF = SimpleDateFormat(format, Locale.US)

            val date = inputSDF.parse(strDate)
            return true

        } catch (e: Exception) {
            return false
        }
    }

    fun convertInLocalTime(serverDate: String, dateFormat: String): String {
        var strDate = ""
        val sdf = SimpleDateFormat(dateFormat, Locale.US)
        try {
            val utcZone = TimeZone.getTimeZone("UTC")
            sdf.timeZone = utcZone// Set UTC time zone
            val myDate = sdf.parse(serverDate)
            sdf.timeZone = TimeZone.getDefault()// Set device time zone
            strDate = sdf.format(myDate!!)
            return strDate
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return strDate
    }


    fun addMinsInExistingDateTime(
        startDateTime: String?,
        inputFormat: String,
        outputFormat: String,
        minutesToBeAdded: Int
    ): String {
        if (startDateTime != null && !startDateTime.isEmpty()) {
            val calendar = getCurrentCalender()
            try {
                var sdf = SimpleDateFormat(inputFormat, Locale.US)
                calendar!!.time = sdf.parse(startDateTime)!!
                calendar.add(Calendar.MINUTE, minutesToBeAdded)

                sdf = SimpleDateFormat(outputFormat, Locale.US)
                return sdf.format(Date(calendar.timeInMillis))
            } catch (e: Exception) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
            }

        }
        return ""
    }


    fun getUTCDateTime(outPutDateFormat: String): String? {
        var UTCDateTime: String? = ""
        try {
            val df = DateFormat.getDateTimeInstance()
            df.timeZone = TimeZone.getTimeZone("gmt")
            val gmtDateTime = df.format(Date()) // Jul 31, 2019 6:02:05 AM
            AppLog.warningLog("AUTH gmtDateTime ------ $gmtDateTime")
            UTCDateTime = getDisplayDateFromDate(
                gmtDateTime,
                ANDROID_DATE_FORMAT_UTC_DATE_TIME,
                outPutDateFormat
            )
            AppLog.warningLog("AUTH UTC Date Time ------ $UTCDateTime")
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
        return UTCDateTime
    }

    fun getMinutesDifference(
        firstTime: String,
        secondTime: String,
        firstTimeFormat: String,
        secondTimeFormat: String
    ): Int {
        try {
            val previousDateTime =
                DateTime(SimpleDateFormat(firstTimeFormat, Locale.US).parse(firstTime))
            val nextDateTime =
                DateTime(SimpleDateFormat(secondTimeFormat, Locale.US).parse(secondTime))
            return Minutes.minutesBetween(previousDateTime, nextDateTime).minutes
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
        return 0
    }

    fun getDaysDifference(
        firstTime: String,
        secondTime: String,
        firstTimeFormat: String,
        secondTimeFormat: String
    ): Int {
        try {
            val previousDateTime =
                DateTime(SimpleDateFormat(firstTimeFormat, Locale.US).parse(firstTime))
            val nextDateTime =
                DateTime(SimpleDateFormat(secondTimeFormat, Locale.US).parse(secondTime))

            return Days.daysBetween(previousDateTime, nextDateTime).getDays()
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
        return 0
    }

    fun addTimeInCalendar(
        date: String,
        inputTimeFormat: String,
        hour: Int,
        minute: Int,
        outputFormat: String
    ): String {
        val calendar = getCurrentCalender()
        try {
            val inputSDF = SimpleDateFormat(inputTimeFormat, Locale.US)
            val outputSDF = SimpleDateFormat(outputFormat, Locale.US)
            calendar?.time = inputSDF.parse(date)!!
            calendar?.add(Calendar.HOUR, hour)
            calendar?.add(Calendar.MINUTE, minute)

            return outputSDF.format(calendar?.time)
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }

        return ""
    }

    @JvmStatic
    fun getTimeFormDate(date: String, inputFormat: String): Long {
        try {
            val sdf = SimpleDateFormat(inputFormat, Locale.US)
            return sdf.parse(date).time
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }

        return 0
    }

    fun getTimeFormat(hour: Int, min: Int): String? {
        try {
            val sdf = SimpleDateFormat(TIME_DISPLAY_PATTERN, Locale.US)
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, min)

            return sdf.format(Date(calendar.timeInMillis))
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
        return null
    }

    fun getCurDate(): String? {
        try {
            val calendar = getCurrentCalender()
            val simpleDateFormat = SimpleDateFormat(DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS, Locale.US)
            return simpleDateFormat.format(Date(calendar!!.getTimeInMillis()))
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }

        return null
    }

    /**
     * @author Niraj Naware
     * @since 11 June 2020
     * get display date from Calendar
     */
    @JvmStatic
    fun getDisplayDateFromCalender(calendar: Calendar, outputFormat: String): String? {
        try {
            val simpleDateFormat = SimpleDateFormat(outputFormat, Locale.US)
            return simpleDateFormat.format(calendar.timeInMillis)
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
        return null
    }

    /**
     * @author Niraj Naware
     * @since 01 June 2020
     * display date time as in chat
     */
    fun getFreeFormatDateTime(neededTimeMilis: Long, neededTime: Calendar): String? {
        val nowTime = Calendar.getInstance()
        //val neededTime = Calendar.getInstance()
        neededTime.timeInMillis = neededTimeMilis
        return if (neededTime[Calendar.YEAR] == nowTime[Calendar.YEAR]) {
            if (neededTime[Calendar.MONTH] == nowTime[Calendar.MONTH]) {
                if (neededTime[Calendar.DATE] - nowTime[Calendar.DATE] == 1) {
                    //here return like "Tomorrow, 12:00PM"
                    "${BindingUtils.string(R.string.tomorrow)}, " + getTimeFormat(neededTime[Calendar.HOUR],neededTime[Calendar.MINUTE])
                } else if (nowTime[Calendar.DATE] == neededTime[Calendar.DATE]) {
                    //here return like "Today, at 12:00PM"
                    "${BindingUtils.string(R.string.today)}, " + getDisplayDateFromCalender(neededTime,TIME_DISPLAY_PATTERN)
                } else if (nowTime[Calendar.DATE] - neededTime[Calendar.DATE] == 1) {
                    //here return like "Yesterday, 12:00PM"
                    "${BindingUtils.string(R.string.yesterday)}, " + getDisplayDateFromCalender(neededTime,TIME_DISPLAY_PATTERN)
                } else {
                    //here return like "31 May, 12:00PM"
                    getDisplayDateFromCalender(neededTime,"dd MMM, ") + getDisplayDateFromCalender(neededTime,TIME_DISPLAY_PATTERN)
                }
            } else {
                //here return like "31 May, 12:00PM"
                getDisplayDateFromCalender(neededTime,"dd MMM, ") + getDisplayDateFromCalender(neededTime,TIME_DISPLAY_PATTERN)
            }
        } else { // dd MMM, yyyy | hh:mm a
            //here return like "31 May 2010, 12:00PM" - it's a different year we need to show it
            getDisplayDateFromCalender(neededTime,"$DATE_DISPLAY_PATTERN, ") + getDisplayDateFromCalender(neededTime,TIME_DISPLAY_PATTERN)
        }
    }
    @JvmStatic
    fun getCurDate(outputFormat: String): String? {
        try {
            val calendar = getCurrentCalender()
            val simpleDateFormat = SimpleDateFormat(outputFormat, Locale.US)
            return simpleDateFormat.format(Date(calendar!!.timeInMillis))
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }

        return null
    }

    fun getCustomDatePattern(infix: String, pattern: String): String {
        var currentDate = ""
        try {
            val curTimeMillis = System.currentTimeMillis()
            val curDate = Date(curTimeMillis)
            val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
            currentDate = infix + simpleDateFormat.format(curDate)
        } catch (e: ParseException) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
        return currentDate
    }
}