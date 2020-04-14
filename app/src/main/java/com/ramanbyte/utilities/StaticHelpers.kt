package com.ramanbyte.utilities

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import androidx.fragment.app.FragmentActivity
import com.ramanbyte.R
import java.lang.reflect.InvocationTargetException

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 27/02/2020
 */
const val CLIENT_IMAGES_PATH = "http://upload.classroomplus.in/ClientMgmt/clientlogo/"
const val DI_ACTIVITY_CONTEXT = "activity_context"
const val DI_AUTH_MODULE = "auth_module"


// Firebase remote config keys
const val KEY_API_ERROR = "key_api_error"
const val KEY_NO_INTERNET_ERROR = "key_internet_error"
const val KEY_PAGINATION_NO_DATA = "key_pagination_no_data"
const val KEY_SOMETHING_WENT_WRONG_ERROR = "key_something_wend_wrong_error"

const val KEY_NA = "N/A"
const val KEY_BLANK = ""
const val KEY_OK = "Ok"
const val KEY_NAME_PATTERN = "((?=.*[a-z])(?=.*[A-Z]))"
const val KEY_PASSWORD_PATTERN =
    "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,-./:;<=>?@[\\^]_`{|}~]).{6,50})"
const val numRegex: String = ".*[0-9].*"
const val upperRegex: String = ".*[A-Z].*"
const val lowerRegex: String = ".*[a-z].*"
const val specialRegex: String = ".*[!\"#\$%&'()*+,-./:;<=>?@[\\^]_`{|}~].*"
const val KEY_EMAIL = "email"
const val KEY_NEW_PASSWORD = "newPassword"
const val KEY_CONFIRM_NEW_PASSWORD = "confirmPassword"
const val SELECT = "-Select-"

const val KEY_IS_FIXED_ASPECT_RATIO = "isFixedAspectRatio"
const val KEY_IMAGE_PATH = "imagePath"
val KEY_CROP_IMAGE_PATH = "cropImagePath"
val KEY_CROP_IMAGE_SIZE = "cropImageSize"
val KEY_APP_STORAGE_FOLDER = BindingUtils.string(R.string.app_name)

const val KEY_RADIO = 1
const val KEY_CHECKBOX = 2
const val KEY_QUESTION_IN_GRID = 5
const val KEY_QUESTION_START_POSITION = 0
const val KEY_QUE_COUNT = "queCount"
const val KEY_TOTAL_QUE_COUNT = "totalQueCount"
const val keyQuestionId = "questionId"

const val keyEmailId = "emailId"
const val keyPassword = "password"

const val KEY_OTP_ONE = "otp_place_one"
const val KEY_OTP_TWO = "otp_place_two"
const val KEY_OTP_THREE = "otp_place_three"
const val KEY_OTP_FOUR = "otp_place_four"
const val KEY_OTP_FIVE = "otp_place_five"
const val KEY_OTP_SIX = "otp_place_six"


const val KEY_ANSWER = "answer"
const val KEY_QUESTION = "questionId"
const val KEY_OTP = "enteredOTP"
const val KEY_USERNAME = "userName"
const val keyNa = "N/A"

//validation keye
const val keyCollegeCode: String = "collegeCode"
const val KEY_LOGIN_LOGOUT_STATUS = "loginLogOutStatus"

//reset password Key
const val KEY_PASSWORD_OLD = "Password"
const val KEY_RESET_NEW_PASSWORD = "NewPassword"
const val KEY_CONFIRM_RESET_NEW_PASSWORD = "ConfirmPassword"
const val KEY_LOGIN_USERNAME = "username"
const val KEY_LOGIN_PASSWORD = "password"


const val APP_STATUS = "APP"
const val DEL_STATUS = "N"
const val PASSWORD_SECURITY_STATUS = "passwordSecurityStatus"
const val IS_ACTIVE_USER = "Y"
const val PASSWORD_SECURITY_QUESTION_STATUS = "Y"

const val IS_VALID_AND_ACTIVE = 1
const val PASSWORD_NOT_UPDATED = 2
const val SECURITY_QUESTION_NOT_UPDATED = 3
const val USER_DEACTIVATED = 4

const val KEY_STATUS_BAR_HEIGHT = "status_bar_height"
const val KEY_DIMEN = "dimen"
const val KEY_ANDROID = "android"

const val DENSITY__LOW = 1
const val DENSITY__MEDIUM = 2
const val DENSITY__HIGH = 3
const val DENSITY__XHIGH = 4
const val DENSITY__XXHIGH = 5
const val DENSITY__XXXHIGH = 6
const val DENSITY__DEFAULT = 7

//Request Codes
val READ_PHONE_STATE_PERMISSION_REQUEST_CODE = 0x100
const val WRITE_STORAGE_PERMISSION_CODE = 0X102
const val REQUEST_CODE_GALLERY_PIC = 0X504
const val REQUEST_CODE_CAMERA = 0X505
const val CROP_IMAGE = 0x8
val CAMERA_PERMISSION_REQUEST_CODE = 100
val WRITE_STORGAE_PERMISSION_REQUEST_CODE = 200
val CROP_IMAGE_REQUEST_CODE = 107
const val CAMERA = 0
const val GALLERY = 1
const val REMOVE_PHOTO = 2
const val PATH_SEPARATOR = "/"


/**
 * DATA PARSE KEY
 * */

const val KEY_COURSE_MODEL = "CourseModel"
const val KEY_CHAPTER_MODEL = "ChapterModel"
const val KEY_QUESTION_MODEL = "QuestionModel"
const val KEY_OPTIONS_MODEL = "OptionsModel"
const val keyContentModel = "ContentModel"
const val keySectionId = "sectionId"
const val keyId = "id"
const val keyMediaId = "mediaId"
const val keyIsOffLine = "isOffLine"
const val keySectionName = "sectionName"
const val keyCourseId = "courseId"
const val keyCourseName = "courseName"
const val keyChapterName = "chapterName"
const val KEY_VIDEO = "VIDEO"
const val KEY_Y = "Y"
const val KEY_N = "N"
const val KEY_SKIP = "S"
const val KEY_STAFF = "STF"
const val KEY_APP = "APP"
const val keyContentUrl = "contentUrl"
const val KEY_QUIZ_TYPE_ASSESSMENT = 1
const val KEY_QUIZ_TYPE_FORMATIVE = 3
const val KEY_QUIZ_TYPE_SUMMATIVE = 2
const val keyTestSubmittedSuccess = "Created."
const val REQUEST_CODE_APPLICATION_UPDATE = 0X503

const val videoS3UrlTimeOut: Long = 172800000
const val keyTestType = "testType"
const val keyCorrect = "correctQuestion"
const val keyWrong = "inCorrectQuestion"
const val KEY_QUESTION_STATUS = "questionStatus"
const val KEY_QUIZ_RESULT_MODEL = "quizResultModel"
const val KEY_COURSE_IMAGE_URL = "courseImageUrl"
const val KEY_DEFAULT_MEDIA_STATUS = -1


fun Window.changeStatusBarColor(view: View, colorResourceId: Int) {
    //clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        /*      addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
              //window.addFlags(WindowManager.LayoutParams.)
              //statusBarColor = BindingUtils.color(colorResourceId)
              //decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

              var flags = view.systemUiVisibility
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                  flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
              }
              view.systemUiVisibility = flags*/
        statusBarColor = BindingUtils.color(colorResourceId)
    }
}

/*
* ===== Table names begins =====
* */

const val tableStudentDetails = "tbl_studentDetails"
const val tableSessionAttendance = "tbl_sessionAttendance"
const val tableStudentAttendance = "tbl_studentAttendance"
const val tableSessionCp = "tbl_sessionCp"
const val tableStudentCp = "tbl_studentCp"

/*
* ===== Table names ends
* */


inline fun <reified From, reified To> From.replicate(): To? {

    this?.apply {

        val fromClazz = From::class.java
        val toClazz = To::class.java

        val fromMembers = fromClazz.declaredFields.toList()

        return toClazz.newInstance().also { to ->
            fromMembers.forEachIndexed { index, field ->

                try {
                    /*val fromValue = field.call(this@replicate)

                    field.call*/
                    val fieldType = field.type

                    /*if (fieldType.simpleName.contains("bool", true)) {
                       fieldType = Boolean.javaClass
                   }*/

                    if (fieldExists(toClazz, field.name)) {
                        val fromValue =
                            fromClazz.getMethod("get" + field.name.capitalize())
                                .invoke(this@replicate)

                        toClazz.getMethod("set" + field.name.capitalize(), fieldType)
                            .invoke(to, fromValue)
                    }

                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                    AppLog.infoLog("Field :: ${field.name}")
                }
                //toField.set(to, fromValue)
            }
        }
    }

    return To::class.java.newInstance()
}

fun fieldExists(clazz: Class<*>, fieldName: String): Boolean {
    return try {
        clazz.getDeclaredField(fieldName) //will throw exception if field not present.
        true
    } catch (e: NoSuchFieldException) {
        false
    }
}

fun getAppVersion(context: Context): String {
    var result = ""

    try {
        result = context.packageManager
            .getPackageInfo(context.packageName, 0)
            .versionName
        result = result.replace("[a-zA-Z]|-".toRegex(), "")
    } catch (e: PackageManager.NameNotFoundException) {
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

fun String?.checkValues(): String = if (isNullOrEmpty()) "NA" else this!!

fun FragmentActivity.displayMetrics(): DisplayMetrics {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics
}

/*Topic keys*/
const val KEK_NO_COURSE_WISE_TOPIC_LIST = "key_no_course_wise_topic-list"

const val KEY_TOPIC = "topicName"
const val KEY_SPINNER = "sessionId"
const val KEY_TOPIC_CONFIG = "topic config"
const val KEY_COURSE_NAME = "course_name"
const val KEY_COURSE_ID = "course id"

const val keyPresentInitial = "P"
const val keyPresent = "Present"
const val keyAbsentInitial = "A"
const val keyAbsent = "Absent"
const val keyLateInitial = "L"
const val keyLate = "Late"
const val keyNotRated = "NR"
const val keyNoAnswer = "NP"
const val keySingleStar = "MP"
const val keyDoubleStar = "HP"

const val keyCompanyModel = "company_model"
const val keyCompanyId = "company_id"
const val keyOfficeId = "office_id"
const val keyIsEdit = "keyIsEdit"
const val keyContactPersonId = "contactPersonId"

/*
* Google Maps Keys
* */

const val KEY_ADMINISTRATIVE_AREA_LEVEL_1 = "administrative_area_level_1"
const val KEY_ADMINISTRATIVE_AREA_LEVEL_2 = "administrative_area_level_2"
const val KEY_G_COUNTRY = "country"
const val KEY_G_POSTAL_ADDRESS = "postal_code"
const val KEY_G_ROUTE = "route"
const val KEY_G_POLITICAL = "political"
const val KEY_G_STREET_NUMBER = "street_number"
const val KEY_SUB_LOCALITY_LEVEL_2 = "sublocality_level_2"

/*
* Google Map Keys Ends
* */

/*
*Office Validation Keys
* */

const val KEY_OFFICE_TYPE = "office_Type"
const val KEY_OFFICE_TITLE = "office_Title"
const val KEY_ADDRESS_LINE_1 = "address_line1"
const val KEY_ADDRESS_LINE_2 = "address_line2"
const val KEY_COUNTRY = "country"
const val KEY_STATE = "state"
const val KEY_CITY = "city"
const val KEY_PINCODE = "pincode"
const val KEY_LOCATION_URL = "locationUrl"
const val KEY_ADDRESS = "address"

/*
*Office Validation Keys Ends
* */

/*
*Key Contact Validation Keys
* */

const val KEY_CONTACT_NAME = "contactPerson_Name"
const val KEY_CONTACT_GENDER = "gender"
const val KEY_CONTACT_DESIGNATION = "designation"
const val KEY_CONTACT_CONTACT_NO = "contactNo"
const val KEY_CONTACT_EMAIL_ID = "email_Id"
const val KEY_CONTACT_OFFICE_TYPE = "officetitle"

/*
*Key Contact Validation Keys Ends
* */

/*Shital K.*/
/*Add remark validation key*/
const val KEY_TASK_TYPE = "task_Type"
const val KEY_REMARK = "remark"
const val KEY_START_DATE = "startDate"
const val KEY_START_TIME = "startTime"
const val KEY_REMARK_TYPE = "R"
const val KEY_REMARK_TYPE_COMMENT = "C"
const val KEY_GROUP_NAME = "group_name"
const val KEY_CHECK_FLAG = "checkFlag"

const val KEY_COMMENT = "comment"
const val KEY_YEAR = "0"
const val KEY_DESCRIPTION = "description"
