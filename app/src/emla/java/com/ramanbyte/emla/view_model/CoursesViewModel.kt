package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.pagination.PaginationMessages
import com.ramanbyte.emla.data_layer.network.init.NetworkConnectionInterceptor
import com.ramanbyte.emla.data_layer.repositories.ContentRepository
import com.ramanbyte.emla.data_layer.repositories.CourseRepository
import com.ramanbyte.emla.data_layer.room.entities.MediaInfoEntity
import com.ramanbyte.emla.models.*
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import org.kodein.di.generic.instance

class CoursesViewModel(val mContext: Context) : BaseViewModel(mContext = mContext) {

    private val coursesRepository: CourseRepository by instance()
    private val contentRepository: ContentRepository by instance()

    val selectedCourseModelLiveData = MutableLiveData<CoursesModel?>(null)
    var coursesModelLiveData: MutableLiveData<CoursesModel> = MutableLiveData()
    var courseSyllabusModel = MutableLiveData<CourseSyllabusModel?>(null)
    val takeSummativeTestLiveData = MutableLiveData<Boolean?>(null)
    var isAllCourseSessionCompleted = MutableLiveData<Boolean>(true)
    //var courseResultList = MutableLiveData<ArrayList<CourseResultModel>>(arrayListOf())
    var courseResultList: MutableLiveData<ArrayList<CourseResultModel>> = MutableLiveData()
    var contentMutableList: MutableLiveData<ArrayList<ContentModel>> = MutableLiveData()

    var showValidationMessage = MutableLiveData<String>(null)

    override var noInternetTryAgain: () -> Unit = {
        coursesRepository.apply {
            tryAgain()
            refreshChapter()
        }
        getCourseResult()
    }

    fun coursesPagedList(): LiveData<PagedList<CoursesModel>>? {
        return coursesRepository.coursesPagedList
    }

    fun initPaginationResponseHandler() {
        coursesRepository.initiatePagination()

        coursesRepository.getPaginationResponseHandler().observeForever {
            if (it != null) {
                paginationResponse(
                    it,
                    //PaginationMessages("No Data", "No More data", "No Internet", "Something Wrong")
                    PaginationMessages(
                        BindingUtils.string(R.string.no_courses),
                        BindingUtils.string(R.string.no_more_courses),
                        BindingUtils.string(R.string.no_internet_message),
                        BindingUtils.string(R.string.some_thing_went_wrong)
                    )
                )
                AppLog.infoLog("Pagination :: ${it.msg} :: ${it.status}")
            }
        }
    }


    /**
     * Kunal*/
    var courseSessionModel: MutableLiveData<ChapterModel>? = MutableLiveData()
    var layoutToShow: MutableLiveData<View> = MutableLiveData()


    fun onCourseCardClick(chapterModel: ChapterModel) {
        courseSessionModel?.value = chapterModel
    }

    fun onHeaderClick(view: View) {
        when (view.id) {
            R.id.headerCourseInfo -> {
                AppLog.infoLog("Course Info Click")
                layoutToShow.value = view
            }
            R.id.headerCourseSyllabus -> {
                AppLog.infoLog("Course Syllabus Click")
                layoutToShow.value = view
            }
            R.id.headerAssessmentInstruction -> {
                AppLog.infoLog("Assessment Instruction Click")
                layoutToShow.value = view
            }
            R.id.headerWhatIWillLearn -> {
                AppLog.infoLog("What i will Learn Click")
                layoutToShow.value = view
            }
            R.id.headerHowToUse -> {
                AppLog.infoLog("How to use Click")
                layoutToShow.value = view
            }
        }
    }


    /**
     * API CALLS*/
    fun initChapterList() {

        //COURSE SESSION API CALL
        coursesRepository?.initChapterListPage(coursesModelLiveData?.value?.courseId ?: 0)

        coursesRepository?.getChapterResponseHandler()?.observeForever {
            if (it != null) {
                paginationResponse(
                    it, PaginationMessages(
                        BindingUtils.string(R.string.no_chapter),
                        BindingUtils.string(R.string.no_more_chapter),
                        BindingUtils.string(R.string.no_internet_message),
                        BindingUtils.string(R.string.some_thing_went_wrong)
                    )
                )
            }
        }
    }

    //COURSE SESSION API CALL RESPONSE
    fun getChapterPagedList(): LiveData<PagedList<ChapterModel>>? {
        return coursesRepository.chapterPagedList
    }


    fun getCourseResult() {

        invokeApiCall {
            courseResultList.postValue(
                coursesRepository.getCourseResult(
                    coursesModelLiveData.value?.courseId ?: 0
                )
            )
        }
    }

    /**
     * KUNAL END*/

    /*
    * Go to Course Details or Pre-assessment
    * */
    fun courseClick(coursesModel: CoursesModel) {
        selectedCourseModelLiveData.value = coursesModel
    }

    fun getCoursesSyllabus() {
        val apiCallFunction: suspend () -> Unit = {
            courseSyllabusModel.postValue(
                coursesRepository.getCoursesSyllabus(
                    coursesModelLiveData.value?.courseId ?: 0
                )
            )
        }

        invokeApiCall(apiCallFunction = apiCallFunction)
    }

    fun onChapterDownloadClick(buttonView: View, chapterModel: ChapterModel) {

        if (NetworkConnectionInterceptor(mContext).isInternetAvailable()){
            invokeApiCall {

                chapterModel.downloadVisibility = View.GONE

                val contentList = coursesRepository.getChapterContentList(
                    chapterModel.chapterId ?: 0
                )

                contentMutableList.postValue(
                    contentList?.apply {
                        forEach {
                            it.sectionId = it.chapter_Id
                            it.chapter_Id = chapterModel.chapterId ?: 0
                        }
                    }
                )
            }
        }else{
            setAlertDialogResourceModelMutableLiveData(
                BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet),
                BindingUtils.drawable(R.drawable.ic_no_internet)!!,
                false,
                BindingUtils.string(R.string.tryAgain), {
                    onChapterDownloadClick(buttonView, chapterModel)
                    isAlertDialogShown.postValue(false)
                },
                BindingUtils.string(R.string.strCancel), {
                    isAlertDialogShown.postValue(false)
                }
            )
            isAlertDialogShown.postValue(true)
        }
    }

    fun takeSummativeTest(buttonView: View) {
        /*if (courseSyllabusModel.value?.summativeaAtemptCount!! < 3) {
            if (courseSyllabusModel?.value?.summativeAssessmentStatus.equals(
                    "true",
                    true
                )
            ) {
                showValidationMessage.value = "You have clear the exam"
            } else {
                takeSummativeTestLiveData.value = true
            }
        } else {
            showValidationMessage.value = "You have lost all your Attempts"
        }*/

        if (courseSyllabusModel?.value?.summativeAssessmentStatus.equals(
                "true",
                true
            )
        ) {
            showValidationMessage.value = BindingUtils.string(R.string.clear_exam)
        } else {

            if (courseSyllabusModel.value?.summativeaAtemptCount!! < courseSyllabusModel.value?.totalSummativeCount!!) {
                takeSummativeTestLiveData.value = true
            } else {
                showValidationMessage.value = BindingUtils.string(R.string.lost_attempt_message)
            }

            /*if (contentRepository.userId == 23){
                if (courseSyllabusModel.value?.summativeaAtemptCount!! < 6) {
                    takeSummativeTestLiveData.value = true
                } else {
                    showValidationMessage.value = BindingUtils.string(R.string.lost_attempt_message)
                }
            }else{
                if (courseSyllabusModel.value?.summativeaAtemptCount!! < 3) {
                    takeSummativeTestLiveData.value = true
                } else {
                    showValidationMessage.value = BindingUtils.string(R.string.lost_attempt_message)
                }
            }*/
        }
    }

    fun addMediaInfo(mediaInfoModel: MediaInfoModel) =
        contentRepository.insertMediaInfo(mediaInfoModel)

    fun getMediaInfoByChapterId(chapterId: Int): LiveData<List<MediaInfoEntity>> =
        contentRepository.getMediaInfoByChapterId(chapterId)

    fun getLoggedUserModel(): UserModel{
        return  coursesRepository.getLoggedUserModel()!!
    }
}