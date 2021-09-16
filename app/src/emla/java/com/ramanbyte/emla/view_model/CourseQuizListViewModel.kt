package com.ramanbyte.emla.view_model

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.paging.PagedList
import com.google.android.gms.common.api.ApiException
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.data_layer.pagination.PaginationMessages
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.CourseQuizRepository
import com.ramanbyte.emla.models.ChaptersModel
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.response.CourseQuizModel
import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS
import com.ramanbyte.utilities.DateUtils.TIME_WITH_SECONDS
import org.kodein.di.generic.instance

class CourseQuizListViewModel(mContext: Context) : BaseViewModel(mContext) {
    var courseModel: CoursesModel? = null

    private val courseQuizRepository: CourseQuizRepository by instance()


    override var noInternetTryAgain: () -> Unit = {
        courseQuizRepository.retryForQuizList(courseModel!!.courseId)
    }

    fun getCourseQuizList() {

        courseQuizRepository.initiatePagination(courseModel!!.courseId)

        courseQuizRepository.getPaginationResponseHandler().observeForever {
            if (it != null) {

                paginationResponse(
                    it,
                    PaginationMessages(
                        BindingUtils.string(R.string.no_data),
                        BindingUtils.string(R.string.no_data),
                        BindingUtils.string(R.string.no_internet_message),
                        BindingUtils.string(R.string.some_thing_went_wrong)
                    )
                )
                AppLog.infoLog("Pagination :: ${it.msg} :: ${it.status}")
            }
        }
    }

    fun quizListForCoursePagedList(): LiveData<PagedList<CourseQuizModel>>? {
        return courseQuizRepository.quizListForCoursePagedList
    }

    fun onTakeQuizClick(buttonView: View, modelObj: CourseQuizModel) {
        modelObj.let { model ->
            if (NetworkConnectivity.isConnectedToInternet()) {
                CoroutineUtils.main {
                    try {
                        coroutineToggleLoader("Checking Quiz is active or not")

                        val serverDateTime = courseQuizRepository.getServerDateTime()
                        val isDateInRange = DateUtils.checkDateWithInRange(
                            model.quizStartDate!! + model.quizStartTime,
                            DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS + TIME_WITH_SECONDS,
                            model.quizEndDate!! + model.quizEndTime,
                            DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS + TIME_WITH_SECONDS,
                            serverDateTime,
                            DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS
                        )
                        coroutineToggleLoader()

                        if (isDateInRange) {
                            buttonView.findNavController()
                                .navigate(
                                    R.id.action_courseDetailFragment_to_preAssessmentTestFragment,
                                    Bundle().apply {
                                        putInt(keyTestType, KEY_QUIZ_TYPE_COURSE_QUIZ)
                                        putParcelable(KEY_CHAPTER_MODEL, ChaptersModel())
                                        putParcelable(KEY_COURSE_MODEL, courseModel)
                                        putParcelable(KEY_COURSE_QUIZ_MODEL, model)
                                    })
                        } else {
                            setAlertDialogResourceModelMutableLiveData(
                                BindingUtils.string(R.string.quiz_expired_message),
                                BindingUtils.drawable(R.drawable.ic_no_internet)!!,
                                true,
                                BindingUtils.string(R.string.strOk), {
                                    isAlertDialogShown.postValue(false)
                                },
                                BindingUtils.string(R.string.no), {
                                    isAlertDialogShown.postValue(false)
                                }
                            )
                            isAlertDialogShown.postValue(true)
                        }
                    } catch (e: ApiException) {
                        buttonView.snackbar(BindingUtils.string(R.string.some_thing_went_wrong))
                    } catch (e: NoInternetException) {
                        buttonView.snackbar(BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet))
                    } catch (e: NoDataException) {
                        buttonView.snackbar(BindingUtils.string(R.string.no_content_uploaded))
                    }
                }
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
}