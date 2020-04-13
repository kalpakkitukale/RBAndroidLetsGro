package com.ramanbyte.utilities

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.view.Window
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

const val KEY_POSITIVE = "Positive"
const val KEY_NEGATIVE = "Negative"
const val KEY_FOLLOW_UP = "Follow up"
const val KEY_NO_RESPONSE = "No response"

const val KEY_OTP_ONE = "otp_place_one"
const val KEY_OTP_TWO = "otp_place_two"
const val KEY_OTP_THREE = "otp_place_three"
const val KEY_OTP_FOUR = "otp_place_four"
const val KEY_OTP_FIVE = "otp_place_five"
const val KEY_OTP_SIX = "otp_place_six"

/*ADD Company START*/

const val keyCompanyName: String = "company_name"
const val keyCompanyType: String = "company_Type"
const val keyCompanyEntityId: String = "company_Entity_Id"
const val keySector_Id: String = "sector_Id"
const val keySectorId: String = "sectorId"
const val keySubSectorId: String = "subSector_Id"
const val keyContactNo: String = "contactNo"
const val keyWebsite: String = "website"
const val keyLinkedInProfile: String = "linkedIn_Profile"
const val KEY_IS_FIXED_ASPECT_RATIO = "isFixedAspectRatio"
const val KEY_IMAGE_PATH = "imagePath"
val KEY_CROP_IMAGE_PATH = "cropImagePath"
val KEY_CROP_IMAGE_SIZE = "cropImageSize"
val KEY_APP_STORAGE_FOLDER = BindingUtils.string(R.string.app_name)

/*ADD Company END*/
/*Create Task START*/
const val keyTaskTitle = "taskTitle"
const val keyTaskStartDateTime = "taskStartDateTime"
const val keyTaskEndDateTime = "taskEndDateTime"
const val keyCityId = "cityId"

const val KEY_SELF = "IND"
const val KEY_INDIVIDUAL = "IND"
const val KEY_GROUP = "GRP"
const val KEY_OUTSIDE_DATABASE = "keyOutsideDatabase"
const val KEY_RANDOM_ALLOCATION = "keyRandomAllocation"
const val KEY_MANUAL_ALLOCATION = "keyManualAllocation"
const val KEY_CREATE_TASK = "keyCreateTask"
const val INDIVIDUAL = "INDIVIDUAL"
const val GROUP = "GROUP"

const val KEY_RANDOM = "RAN"
const val KEY_MANUAL = "MAN"

const val KEY_TASK_NOT_STARTED = "NST"
const val KEY_TASK_IN_PROGRESS = "ING"
const val KEY_TASK_COMPLETED = "CMP"
const val KEY_TASK_EXPIRED = "EXP "

const val KEY_ALLOCATE = "ALLOCATE"
const val KEY_DEALLOCATE = "DEALLOCATE"




/*Create Task END*/

const val KEY_ANSWER = "answer"
const val KEY_QUESTION = "questionId"
const val KEY_OTP = "enteredOTP"
const val KEY_USERNAME = "userName"
const val keySessionId = "keySessionId"
const val keySelectedDate = "keySelectedDate"
const val keySessionStartTime = "sessionStartTime"
const val keySessionEndTime = "sessionEndTime"
const val keyIsSessionGoingOn = "isSessionGoingOn"
const val keyAll = "All" //do not change the value
const val keyAssociated = "Associated" //do not change the value
const val keyNotAssociated = "Not Associated" //do not change the value
const val keyGroupId = "groupId"
const val keyGroupName = "groupName"
const val keyGroupCreatedBy = "groupCreatedBy"
const val keySelectedGroupMembers = "selectedMembers"
const val keyGroupMember="groupMember"

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


//Task Status
const val TASK_COMPLETED = "CMP"
const val TASK_IN_PROGRESS = "INP"
const val TASK_NOT_STARTED = "NST"


// User Types
const val TYPE_ACADEMIC_ADMIN = "ADM"
const val TYPE_MENTOR = "FTL"
const val TYPE_FACULTY = "FTL"
const val TYPE_STUDENT = "STF"
const val TYPE_HOD = ""
const val TYPE_TOP_MANAGMENT = ""
const val TYPE_EXTERNAL_MENTOR = "EXM"
const val TYPE_PARENT = "PRT"
const val TYPE_PLH = "PLH"


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
