package com.ramanbyte.emla.faculty.view_model

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.paging.PagedList
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.pagination.PaginationMessages
import com.ramanbyte.emla.faculty.data_layer.repositories.FacultyCoursesRepository
import com.ramanbyte.emla.faculty.data_layer.repositories.FacultyQuestionRepository
import com.ramanbyte.emla.faculty.models.FacultyCoursesModel
import com.ramanbyte.emla.faculty.models.StudentAskedQuestionsModel
import com.ramanbyte.emla.faculty.models.request.StudentAskedQuestionsRequestModel
import com.ramanbyte.utilities.*
import org.kodein.di.generic.instance

class StudentAskedQuestionsViewModel(mContext: Context) : BaseViewModel(mContext = mContext) {

    private val questionRepository: FacultyQuestionRepository by instance()

    var searchQuery = MutableLiveData<String>().apply {
        value = KEY_BLANK
    }

    var courseId = MutableLiveData<Int>().apply {
        value = 0
    }

    var onClickCloseBottomSheetLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    var onClickApplyFilterLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    var onClickClearFilterLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }
    var onClickCardLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    override var noInternetTryAgain: () -> Unit = {
        questionRepository.tryAgain()
    }

    fun initPaginationResponseHandler() {
        questionRepository.courseId = courseId.value!!
        questionRepository.getPaginationResponseHandler().observeForever {
            if (it != null) {
                paginationResponse(
                    it,
                    //PaginationMessages("No Data", "No More data", "No Internet", "Something Wrong")
                    PaginationMessages(
                        BindingUtils.string(R.string.faculty_no_questions),
                        BindingUtils.string(R.string.faculty_no_more_questions),
                        BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet),
                        BindingUtils.string(R.string.some_thing_went_wrong)
                    )
                )
                AppLog.infoLog("Pagination :: ${it.msg} :: ${it.status}")
            }
        }

        questionRepository.initiatePagination()
    }

    fun coursesPagedList(): LiveData<PagedList<StudentAskedQuestionsModel>>? {
        return questionRepository.questionPagedList
    }

    fun onClickCard(view: View, questionId:Int){
        view.findNavController()
            .navigate(
                R.id.action_studentAskedQuestionsFragment_to_facultyQuestionAnswerFragment,
                Bundle().apply {
                    putInt(keyQuestionId, questionId)
                })
        //onClickCardLiveData.value = true
    }

    //------------- Filter Code --- Start ------------------------

    var questionsRequestModelLiveData =
        MutableLiveData<StudentAskedQuestionsRequestModel>().apply {
            value = StudentAskedQuestionsRequestModel()
        }
    var questionsRequestModel = StudentAskedQuestionsRequestModel()

    var ansTypeLiveData = MutableLiveData<String>().apply {
        value = KEY_N
    }

    fun  onClickAnswerChip(selectedAnsType : String){
        when (selectedAnsType) {
            KEY_ANSWERED -> {
                ansTypeLiveData.value = KEY_Y
            }
            KEY_UNANSWERED -> {
                ansTypeLiveData.value = KEY_N
            }
            else -> ansTypeLiveData.value = KEY_BLANK
        }
    }

    fun onClickCloseBottomSheet(view: View){
        onClickCloseBottomSheetLiveData.value = true
    }
    fun onClickApplyFilter(view: View){

        questionsRequestModelLiveData.value = questionsRequestModel.apply {
            isQuestionAnswered = ansTypeLiveData.value!!
        }
        questionRepository.applyFilter(questionsRequestModelLiveData.value!!)

        onClickApplyFilterLiveData.value = true
    }
    fun onClickClearFilter(view: View){
        onClickClearFilterLiveData.value = true
    }

    //------------- Filter Code --- End ------------------------

}