package com.ramanbyte.emla.view_model

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.paging.PagedList
import com.google.android.gms.common.api.ApiException
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.data_layer.pagination.PaginationMessages
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.ChaptersRepository
import com.ramanbyte.emla.data_layer.repositories.ContentRepository
import com.ramanbyte.emla.models.*
import com.ramanbyte.utilities.*
import org.kodein.di.generic.instance

class ChaptersViewModel(mContext: Context) : BaseViewModel(mContext) {

    private val chaptersRepository: ChaptersRepository by instance()
    private val contentRepository: ContentRepository by instance()

    var courseModel: CoursesModel? = null

    var selectedChaptersModelLiveData: MutableLiveData<ChaptersModel?> = MutableLiveData(null)
    var downloadRequestedChapter: ChaptersModel? = null

    var isAllCourseSessionCompleted = MutableLiveData<Boolean>(true)
    var showValidationMessage = MutableLiveData<String>(null)
    var courseSyllabusModel: CourseSyllabusModel? = null
    var contentMutableList: MutableLiveData<ArrayList<ContentModel>> = MutableLiveData()

    override var noInternetTryAgain: () -> Unit = {
        chaptersRepository.tryAgain()
    }

    fun getList(courseId: Int) = run {

        chaptersRepository.getList(courseId)

        chaptersRepository.getPaginationResponseHandler().observeForever {
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

    fun getList(): LiveData<PagedList<ChaptersModel>>? = chaptersRepository.getList()

    fun onCardClicked(view: View, chaptersModel: ChaptersModel) {
        selectedChaptersModelLiveData.value = chaptersModel
    }

    fun onDownloadClicked(view: View, chaptersModel: ChaptersModel) {

        CoroutineUtils.main {

            try {

                coroutineToggleLoader("${BindingUtils.string(R.string.getting_content_for)} ${chaptersModel.chapterName}")

                val contentList = chaptersRepository.getContentList(chaptersModel?.chapterId ?: 0)
                downloadRequestedChapter = chaptersModel
                contentMutableList.postValue(contentList)

                coroutineToggleLoader()

            } catch (e: ApiException) {
                view.snackbar(BindingUtils.string(R.string.some_thing_went_wrong))
            } catch (e: NoInternetException) {
                view.snackbar(BindingUtils.string(R.string.please_make_sure_you_are_connected_to_internet))
            } catch (e: NoDataException) {
                view.snackbar(BindingUtils.string(R.string.no_content_uploaded))
            }
        }

    }

    fun takeSummativeTest(buttonView: View) {

        if (courseModel?.summativeAssessmentStatus.equals(
                "true",
                true
            )
        ) {
            showValidationMessage.value = BindingUtils.string(R.string.clear_exam)
        } else {

            buttonView.findNavController()
                .navigate(
                    R.id.action_courseDetailFragment_to_preAssessmentTestFragment,
                    Bundle().apply {
                        putInt(keyTestType, KEY_QUIZ_TYPE_SUMMATIVE)
                        putParcelable(KEY_CHAPTER_MODEL, ChaptersModel())
                        putParcelable(KEY_COURSE_MODEL, courseModel)
                    })

            /*if (courseModel?.summativeaAtemptCount!! < courseSyllabusModel?.totalSummativeCount!!) {
                buttonView.findNavController()
                    .navigate(
                        R.id.action_courseDetailFragment_to_preAssessmentTestFragment,
                        Bundle().apply {
                            putInt(keyTestType, KEY_QUIZ_TYPE_SUMMATIVE)
                            putParcelable(KEY_CHAPTER_MODEL, ChaptersModel())
                            putParcelable(KEY_COURSE_MODEL, courseModel)
                        })

            } else {
                showValidationMessage.value = BindingUtils.string(R.string.lost_attempt_message)
            }*/
        }
    }

    fun addMediaInfo(mediaInfoModel: MediaInfoModel): Long =
        contentRepository.insertMediaInfo(mediaInfoModel)

    fun getMediaInfoByChapterId(chapterId: Int): LiveData<List<MediaInfoModel>> =
        contentRepository.getMediaInfoByChapterId(chapterId)

}