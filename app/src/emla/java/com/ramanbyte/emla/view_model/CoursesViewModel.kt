package com.ramanbyte.emla.view_model

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.paging.PagedList
import com.ramanbyte.R
import com.ramanbyte.aws_s3_android.utilities.S3Constant.Companion.mContext
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.pagination.PaginationMessages
import com.ramanbyte.emla.data_layer.repositories.CoursesRepository
import com.ramanbyte.emla.data_layer.repositories.RegistrationRepository
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.CoursesRequest
import com.ramanbyte.emla.models.response.CommonDropdownModel
import com.ramanbyte.utilities.*
import org.kodein.di.generic.instance

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 14-04-2020
 */
class CoursesViewModel(mContext: Context) : BaseViewModel(mContext = mContext) {
    override var noInternetTryAgain: () -> Unit = {
        coursesRepository.tryAgain()
    }
    private val coursesRepository: CoursesRepository by instance()
    private val registrationRepository: RegistrationRepository by instance()

    var isFilterApplied = MutableLiveData<Boolean>(null)

    var shareLiveData =  MutableLiveData<String>().apply {
        value = null
    }

    var tempFilterModel = CoursesRequest()
    var filterRequestModel = CoursesRequest()
    val dismissBottomSheet = MutableLiveData<Boolean>().apply {
        value = null
    }
    val clearFilter = MutableLiveData<Boolean>().apply {
        value = null
    }

    var programsListMutableLiveData = MutableLiveData<List<CommonDropdownModel>>()
    var patternsListMutableLiveData = MutableLiveData<List<CommonDropdownModel>>()
    var specializationsListMutableLiveData = MutableLiveData<List<CommonDropdownModel>>()
    var programName =
        ObservableField<String>().apply { set(BindingUtils.string(R.string.program_level)) }
    var patternName = ObservableField<String>().apply { set(BindingUtils.string(R.string.pattern)) }
    var specializationName =
        ObservableField<String>().apply { set(BindingUtils.string(R.string.specialisation)) }

    var userData: UserModel? = null
    var searchQuery = MutableLiveData<String>().apply {
        value = KEY_BLANK
    }

    init {
        toggleLayoutVisibility(View.GONE, View.GONE, View.GONE, "", View.GONE)
        searchQuery.observeForever {
            coursesRepository.searchCourse(it)
        }
        userData = coursesRepository.getCurrentUser()

    }

    fun initPaginationResponseHandler() {
        if (getFilterState()) {
            isFilterApplied.postValue(true)
            //filterCourseList(filterRequestModel)
        } else {
            coursesRepository.getPaginationResponseHandler().observeForever {
                if (it != null) {
                    paginationResponse(
                        it,
                        //PaginationMessages("No Data", "No More data", "No Internet", "Something Wrong")
                        PaginationMessages(
                            BindingUtils.string(R.string.no_courses),
                            BindingUtils.string(R.string.no_more_courses),
                            BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet),
                            BindingUtils.string(R.string.some_thing_went_wrong)
                        )
                    )
                    AppLog.infoLog("Pagination :: ${it.msg} :: ${it.status}")
                }
            }

            coursesRepository.initiatePagination()
        }
    }

    fun coursesPagedList(): LiveData<PagedList<CoursesModel>>? {
        return coursesRepository.coursesPagedList
    }

    /*
 * Go to Course Details or Pre-assessment
 * */

    fun courseClick(view: View, coursesModel: CoursesModel) {
        coursesModel.let { model ->
            if (NetworkConnectivity.isConnectedToInternet()) {
                /**
                 * @author Mansi Manakiki Mody
                 * @since 22 April 2020
                 * After discussing with Raman Sir No need of Pre Assessment in eMarket. User will direct redirect to course detail page
                 */
                /*if (model.preAssessmentStatus.equals("true", true)) {*/
                view.findNavController()
                    .navigate(
                        R.id.action_coursesFragment_to_courseDetailFragment,
                        Bundle().apply {
                            putParcelable(KEY_COURSE_MODEL, coursesModel)
                        })

                /*} else {
                    val bundle = Bundle()
                    bundle.putParcelable(KEY_COURSE_MODEL, model)
                    bundle.putInt(keyTestType, KEY_QUIZ_TYPE_ASSESSMENT)
                    view.findNavController()
                        .navigate(R.id.action_coursesFragment_to_preAssessmentTestFragment, bundle)
                }*/
            } else {
                setAlertDialogResourceModelMutableLiveData(
                    BindingUtils.string(R.string.no_internet_message),
                    BindingUtils.drawable(R.drawable.ic_no_internet)!!,
                    true,
                    BindingUtils.string(R.string.yes), {
                        isAlertDialogShown.postValue(false)
                    },
                    BindingUtils.string(R.string.no), {
                        isAlertDialogShown.postValue(false)
                    }
                )
                isAlertDialogShown.postValue(true)
            }
        }
    }
    fun shareClick(view: View,courseName:String){


        shareLiveData.value=courseName

    }

    fun onCloseBottomSheet(view: View) {
        dismissBottomSheet.postValue(true)
    }

    /**Filter operations*/
    fun onApplyFilterClick(view: View) {
        try {
            val isFilterSelected = hasFilter()

            filterRequestModel = CoursesRequest().apply {
                userId = /*if (isFilterSelected) 0 else */ userData?.userId!!
                userType = userData?.userType!!
                programId = tempFilterModel.programId
                specializationId = tempFilterModel.specializationId
                patternId = tempFilterModel.patternId
            }

            isFilterApplied.postValue(isFilterSelected)
            filterCourseList(filterRequestModel)
            dismissBottomSheet.postValue(true)

        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
    }

    private fun hasFilter(): Boolean {
        return (tempFilterModel.userType.isNotEmpty() ||
                tempFilterModel.programId != 0 ||
                tempFilterModel.patternId != 0 ||
                tempFilterModel.specializationId != 0)
    }

    private fun getFilterState(): Boolean {
        return (filterRequestModel.userType.isNotEmpty() ||
                filterRequestModel.programId != 0 ||
                filterRequestModel.patternId != 0 ||
                filterRequestModel.specializationId != 0)
    }

    fun onClearFilterClick(view: View) {
        tempFilterModel.apply {
            /* userId = userData?.userId!!*/
            userType = ""
            programId = 0
            specializationId = 0
            patternId = 0
        }
        clearFilter.postValue(true)
    }

    fun filterCourseList(filterRequestModel: CoursesRequest) {
        val apiCallFunction: suspend () -> Unit = {
            coursesRepository.filterCourseList(filterRequestModel)
        }
        invokeApiCall(apiCallFunction = apiCallFunction)
    }


    fun getAllPrograms() {
        val apiCallFunction: suspend () -> Unit = {
            programsListMutableLiveData.postValue(registrationRepository.getAllPrograms())
        }
        invokeApiCall(apiCallFunction = apiCallFunction)
    }

    fun getAllPatterns() {
        val apiCallFunction: suspend () -> Unit = {
            patternsListMutableLiveData.postValue(registrationRepository.getAllPatterns())
        }
        invokeApiCall(apiCallFunction = apiCallFunction)
    }

    fun getAllSpecializations() {
        val apiCallFunction: suspend () -> Unit = {
            specializationsListMutableLiveData.postValue(registrationRepository.getAllSpecializations())
        }
        invokeApiCall(apiCallFunction = apiCallFunction)
    }

    fun isUserActive(): Boolean = registrationRepository.isUserActive()

}