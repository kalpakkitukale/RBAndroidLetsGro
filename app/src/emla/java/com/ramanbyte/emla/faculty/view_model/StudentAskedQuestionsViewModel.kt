package com.ramanbyte.emla.faculty.view_model

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.paging.PagedList
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.pagination.PaginationMessages
import com.ramanbyte.emla.faculty.data_layer.repositories.FacultyQuestionRepository
import com.ramanbyte.emla.faculty.models.StudentAskedQuestionsModel
import com.ramanbyte.emla.faculty.models.request.StudentAskedQuestionsRequestModel
import com.ramanbyte.utilities.*
import org.kodein.di.generic.instance


class StudentAskedQuestionsViewModel(var mContext: Context) : BaseViewModel(mContext = mContext) {

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

    init {
        toggleLayoutVisibility(
            View.GONE,
            View.GONE,
            View.GONE,
            KEY_BLANK
        )
    }

    fun initPaginationResponseHandler() {

        questionRepository.courseId = courseId.value!!

        if (getFilterState()) {
            questionRepository.applyFilter(questionsRequestModelLiveData.value!!)
        } else {
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

    }

    fun coursesPagedList(): LiveData<PagedList<StudentAskedQuestionsModel>>? {
        return questionRepository.questionPagedList
    }

    fun onClickCard(view: View, questionsModel: StudentAskedQuestionsModel) {
        view.findNavController()
            .navigate(
                R.id.action_studentAskedQuestionsFragment_to_facultyQuestionAnswerFragment,
                Bundle().apply {
                    putParcelable(KEY_QUESTION_MODEL, questionsModel)
                })
        //onClickCardLiveData.value = true
    }

    /*
    * On click mobile open mobile dialler
    * */
    fun onClickMobile(view: View, mobile: String) {
        if (!mobile.isBlank() && mobile != KEY_NA_WITHOUT_SPACE) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$mobile")
            mContext.startActivity(intent)
        } else {
            showAlertDialog(BindingUtils.string(R.string.invalid_mobile))
        }
    }

    /*
    * On click email open email application
    * */
    fun onClickEmail(view: View, email: String) {
        if (!email.isBlank() && email != KEY_NA_WITHOUT_SPACE) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("mailto:$email")
            )
            //intent.putExtra(Intent.EXTRA_SUBJECT, "your_subject")
            //intent.putExtra(Intent.EXTRA_TEXT, "your_text")
            mContext.startActivity(intent)
        } else {
            showAlertDialog(BindingUtils.string(R.string.invalid_email))
        }
    }

    //------------- Filter Code --- Start ------------------------

    var questionsRequestModelLiveData =
        MutableLiveData<StudentAskedQuestionsRequestModel>().apply {
            value = StudentAskedQuestionsRequestModel()
        }
    var questionsRequestModel = StudentAskedQuestionsRequestModel()

    var ansTypeLiveData = MutableLiveData<String>().apply {
        value = KEY_ALL
    }

    var sortByDateLiveData = MutableLiveData<String>().apply {
        value = KEY_DESCENDING
    }

    fun onClickAnswerChip(selectedAnsType: String) {
        when (selectedAnsType) {
            KEY_ALL -> {
                ansTypeLiveData.value = KEY_ALL
            }
            KEY_ANSWERED -> {
                ansTypeLiveData.value = KEY_Y
            }
            KEY_UNANSWERED -> {
                ansTypeLiveData.value = KEY_N
            }
            else -> ansTypeLiveData.value = KEY_BLANK
        }
    }

    fun onClickSortByDateChip(selectedSortByDateType: String) {
        when (selectedSortByDateType) {
            KEY_ASCENDING -> {
                sortByDateLiveData.value = KEY_ASCENDING
            }
            KEY_DESCENDING -> {
                sortByDateLiveData.value = KEY_DESCENDING
            }
            else -> sortByDateLiveData.value = KEY_BLANK
        }
    }

    fun onClickCloseBottomSheet(view: View) {
        onClickCloseBottomSheetLiveData.value = true
    }

    fun onClickApplyFilter(view: View) {

        questionsRequestModelLiveData.value = questionsRequestModel.apply {
            isQuestionAnswered = ansTypeLiveData.value!!
            dateWiseSort = sortByDateLiveData.value!!
        }
        questionRepository.applyFilter(questionsRequestModelLiveData.value!!)

        onClickApplyFilterLiveData.value = true
    }

    fun onClickClearFilter(view: View) {
        onClickClearFilterLiveData.value = true
    }

    fun getFilterState(): Boolean {
        return (questionsRequestModelLiveData.value?.isQuestionAnswered != KEY_ALL || questionsRequestModelLiveData.value?.dateWiseSort == KEY_ASCENDING)
    }

    //------------- Filter Code --- End ------------------------

    private fun showAlertDialog(message: String = BindingUtils.string(R.string.no_internet_message)) {

        setAlertDialogResourceModelMutableLiveData(
            message,
            BindingUtils.drawable(R.drawable.ic_warning),
            true,
            positiveButtonText = BindingUtils.string(R.string.strOk),
            positiveButtonClickFunctionality = {
                isAlertDialogShown.value = false
            })

        isAlertDialogShown.value = true
    }

}