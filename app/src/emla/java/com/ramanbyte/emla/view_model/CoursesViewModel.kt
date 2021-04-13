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
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.data_layer.pagination.PaginationMessages
import com.ramanbyte.data_layer.pagination.Status
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.CoursesRepository
import com.ramanbyte.emla.data_layer.repositories.RegistrationRepository
import com.ramanbyte.emla.data_layer.repositories.TransactionRepository
import com.ramanbyte.emla.models.CourseSyllabusModel
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.emla.models.request.CartRequestModel
import com.ramanbyte.emla.models.request.CoursesRequest
import com.ramanbyte.emla.models.response.CommonDropdownModel
import com.ramanbyte.emla.ui.activities.CertificateViewerActivity
import com.ramanbyte.utilities.*
import org.kodein.di.generic.instance

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 14-04-2020
 */
class CoursesViewModel(var mContext: Context) : BaseViewModel(mContext = mContext) {
    override var noInternetTryAgain: () -> Unit = {
        coursesRepository.tryAgain()
    }
    private val coursesRepository: CoursesRepository by instance()
    private val registrationRepository: RegistrationRepository by instance()
    private val transactionRepository: TransactionRepository by instance()

    var isFilterApplied = MutableLiveData<Boolean>(null)
    var bottomSheetCloseLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }


    var shareLiveData = MutableLiveData<CoursesModel>().apply {
        value = null
    }
    var cartClickMutableLiveData = MutableLiveData<Int>().apply {
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
    var courseInformationLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    var programsListMutableLiveData = MutableLiveData<List<CommonDropdownModel>>()
    var skillsListMutableLiveData = MutableLiveData<List<CommonDropdownModel>>()
    var patternsListMutableLiveData = MutableLiveData<List<CommonDropdownModel>>()
    var specializationsListMutableLiveData = MutableLiveData<List<CommonDropdownModel>>()
    var programName =
        ObservableField<String>().apply { set(BindingUtils.string(R.string.program)) }
    var patternName = ObservableField<String>().apply { set(BindingUtils.string(R.string.pattern)) }
    var specializationName =
        ObservableField<String>().apply { set(BindingUtils.string(R.string.specialisation)) }
    var skillName =
        ObservableField<String>().apply { set(BindingUtils.string(R.string.skill)) }

    var userData: UserModel? = null
    var searchQuery = MutableLiveData<String>().apply {
        value = KEY_BLANK
    }
    var searchQueryForMyCourse = MutableLiveData<String>().apply {
        value = KEY_BLANK
    }
    var selectedCourseCountLiveData = MutableLiveData<Int>().apply {
        postValue(null)
    }


    init {
        toggleLayoutVisibility(View.GONE, View.GONE, View.GONE, "", View.GONE)
        searchQuery.observeForever {
            coursesRepository.searchCourse(it)
        }
        searchQueryForMyCourse.observeForever {
            transactionRepository.searchCourse(it)
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
            isLoaderShowingLiveData.postValue(false)
        }
    }

    fun myCourseListPagination() {
        try {
            transactionRepository.getPaginationResponseHandler().observeForever {
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
        }catch (e:Exception){
            e.printStackTrace()
            isLoaderShowingLiveData.postValue(false)
            AppLog.infoLog(e.message.toString())
        }


        transactionRepository.myCourseList()
        isLoaderShowingLiveData.postValue(false)

    }

    fun coursesPagedList(): LiveData<PagedList<CoursesModel>>? {
        return coursesRepository.coursesPagedList
    }

    fun myCoursesPagedList(): LiveData<PagedList<CoursesModel>>? {
        return transactionRepository.coursesPagedList
    }

    fun insertCartData(view: View, coursesModel: CoursesModel) {
        CoroutineUtils.main {
            try {
                isLoaderShowingLiveData.postValue(true)
                val response = transactionRepository.insertCart(cartRequestModel = CartRequestModel(), courseId = coursesModel.courseId)
                selectedCourseCountLiveData.postValue(selectedCourseCountLiveData.value?.plus(1))
                isLoaderShowingLiveData.postValue(false)
            } catch (e: ApiException) {
                isLoaderShowingLiveData.postValue(false)
                setAlertDialogResourceModelMutableLiveData(
                    e.message.toString(),
                    BindingUtils.drawable(
                        R.drawable.something_went_wrong
                    )!!,
                    true,
                    BindingUtils.string(R.string.strOk), {
                        isAlertDialogShown.postValue(false)
                    }
                )
                isAlertDialogShown.postValue(true)
            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                isLoaderShowingLiveData.postValue(false)
                setAlertDialogResourceModelMutableLiveData(
                    BindingUtils.string(R.string.no_internet_message),
                    BindingUtils.drawable(R.drawable.ic_no_internet)!!,
                    false,
                    BindingUtils.string(R.string.tryAgain), {
                        isAlertDialogShown.postValue(false)
                        insertCartData(view, coursesModel)
                    },
                    BindingUtils.string(R.string.no), {
                        isAlertDialogShown.postValue(false)
                    }
                )
                isAlertDialogShown.postValue(true)
            }
        }


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

    fun showCourseSyllabus(view: View, coursesModel: CoursesModel) {

        view.findNavController()
            .navigate(
                R.id.courseSyllabusFragment, /*action_coursesFragment_to_courseSyllabusFragment*/
                Bundle().apply {
                    putParcelable(KEY_COURSE_MODEL, coursesModel)
                })
    }

    // on click on the topic list
    fun showChapterList(view: View, coursesModel: CoursesModel) {
        view.findNavController().navigate(
            R.id.chaptersListFragment,
            Bundle().apply { putParcelable(KEY_COURSE_MODEL, coursesModel) })
    }

    // on click on performance check 
    fun checkPerformance(view: View, coursesModel: CoursesModel) {
        view.findNavController().navigate(R.id.courseResultFragment, Bundle().apply {
            putParcelable(KEY_COURSE_MODEL, coursesModel)
        })

    }

    fun shareClick(view: View, coursesModel: CoursesModel) {
        shareLiveData.value = coursesModel
    }

    fun onCloseBottomSheet(view: View) {
        /*tempFilterModel.apply {
            userType = filterRequestModel.userType
            programId = filterRequestModel.programId
            specializationId = filterRequestModel.specializationId
            patternId = filterRequestModel.patternId
            skillId = filterRequestModel.skillId
        }*/
        dismissBottomSheet.postValue(true)
    }

    /**Filter operations*/
    fun onApplyFilterClick(view: View) {
        try {
            val isFilterSelected = hasFilter()

            filterRequestModel = CoursesRequest().apply {
                userId = /*if (isFilterSelected) 0 else */ userData?.userId!!
                userType = if (isFilterSelected) userData?.userType!! else ""
                programId = tempFilterModel.programId
                specializationId = tempFilterModel.specializationId
                patternId = tempFilterModel.patternId
                skillId = tempFilterModel.skillId
            }

            isFilterApplied.postValue(isFilterSelected)
            if (filterRequestModel.programId == 0) programName.set(BindingUtils.string(R.string.program))
            if (filterRequestModel.patternId == 0) patternName.set(BindingUtils.string(R.string.pattern))
            if (filterRequestModel.specializationId == 0) specializationName.set(
                BindingUtils.string(
                    R.string.specialisation
                )
            )
            if (filterRequestModel.skillId == 0) skillName.set(BindingUtils.string(R.string.skill))

            filterCourseList(filterRequestModel)
            dismissBottomSheet.postValue(true)

        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
    }

    private fun hasFilter(): Boolean {
        return (/*tempFilterModel.userType.isNotEmpty() ||*/
                tempFilterModel.programId != 0 ||
                        tempFilterModel.patternId != 0 ||
                        tempFilterModel.specializationId != 0 ||
                        tempFilterModel.skillId != 0)
    }

    fun getFilterState(): Boolean {
        return (/*filterRequestModel.userType.isNotEmpty() ||*/
                filterRequestModel.programId != 0 ||
                        filterRequestModel.patternId != 0 ||
                        filterRequestModel.specializationId != 0 ||
                        filterRequestModel.skillId != 0)
    }

    fun onClearFilterClick(view: View) {
        tempFilterModel.apply {
            /* userId = userData?.userId!!*/
            userType = ""
            programId = 0
            specializationId = 0
            patternId = 0
            skillId = 0
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
            programsListMutableLiveData.postValue(coursesRepository.getAllPrograms())
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

    fun getAllSkills() {
        val apiCallFunction: suspend () -> Unit = {
            skillsListMutableLiveData.postValue(coursesRepository.getAllSkills())
        }
        invokeApiCall(apiCallFunction = apiCallFunction)
    }

// custom tab layout click listener here
    val onCoursewareclickListener: (view: View, obj: Any) -> Unit = { view, obj ->
            obj as CoursesModel
        showCourseSyllabus(view,obj)

        }
    val onTopicclickListener: (view: View, obj: Any) -> Unit = { view, obj->
            obj as CoursesModel
            showChapterList(view,obj)
        }
    val onPerformanceclickListener: (view: View, obj: Any) -> Unit = { view, obj->
            obj as CoursesModel
            checkPerformance(view,obj)
        }
    val onCartclickListener: (view: View, obj: Any) -> Unit = { view, obj ->
        obj as CoursesModel
        cartClickMutableLiveData.postValue(obj.courseId)
        insertCartData(view, obj)

        /*var intent = Intent(mContext,CertificateViewerActivity::class.java)
        intent.putExtra("userId", 9211)
        intent.putExtra("courseId", obj.courseId)
        mContext.startActivity(intent)*/

    }

    // on click of course of information
var courseModelLive = MutableLiveData<CoursesModel>().apply {
        value = CoursesModel()
    }

    val onClickCourseInformationClick:(view: View, obj: Any) -> Unit = { view, obj ->
        obj as CoursesModel
        courseInformationLiveData.postValue(true)
        getCourseSyllbus(obj.courseId)
        courseModelLive.postValue(obj)
    }

  // get transaction count from server
    fun getCartCount(){
        CoroutineUtils.main {
            try {
                selectedCourseCountLiveData.postValue(transactionRepository.getCartCount())
            }catch (e:ApiException){
                e.printStackTrace()
                AppLog.infoLog(e.message.toString())
            } catch (e:NoInternetException){
                e.printStackTrace()
                AppLog.infoLog(e.message.toString())
            } catch (e:NoDataException){
                e.printStackTrace()
                AppLog.infoLog(e.message.toString())
            } catch (e:Exception){
                e.printStackTrace()
                AppLog.infoLog(e.message.toString())
            }
        }
    }
// Course of Infromation Sheet close click listener
    fun onCloseClickBottomSheet(view: View){
        bottomSheetCloseLiveData.postValue(true)
    }

    var courseSyllabusLiveData = MutableLiveData<CourseSyllabusModel>().apply {
        value = CourseSyllabusModel()
    }

    // get transaction count from server
    fun getCourseSyllbus(courseId:Int){
        CoroutineUtils.main {
            try {
                AppLog.infoLog("Pibm -->${courseId} ")
                courseSyllabusLiveData.postValue(coursesRepository.getCoursesSyllabus(courseId))
            }catch (e:ApiException){
                e.printStackTrace()
                AppLog.infoLog(e.message.toString())
            } catch (e:NoInternetException){
                e.printStackTrace()
                AppLog.infoLog(e.message.toString())
            } catch (e:NoDataException){
                e.printStackTrace()
                AppLog.infoLog(e.message.toString())
            } catch (e:Exception){
                e.printStackTrace()
                AppLog.infoLog(e.message.toString())
            }
        }
    }
}