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
import com.ramanbyte.emla.data_layer.repositories.ContentRepository
import com.ramanbyte.emla.data_layer.repositories.SectionsRepository
import com.ramanbyte.emla.models.*
import com.ramanbyte.utilities.*
import org.kodein.di.generic.instance

class ChaptersSectionViewModel(mContext: Context) : BaseViewModel(mContext) {

    private val sectionsRepository: SectionsRepository by instance()
    private val contentRepository: ContentRepository by instance()

    override var noInternetTryAgain: () -> Unit = {
        sectionsRepository.tryAgain()
    }

    var coursesModel: CoursesModel? = null
    var chaptersModel: ChaptersModel? = null

    var downloadRequestedSection: SectionsModel? = null
    var contentMutableList: MutableLiveData<ArrayList<ContentModel>> = MutableLiveData()

    fun getList(chapterId: Int) = run {

        sectionsRepository.getList(chapterId)

        sectionsRepository.getPaginationResponseHandler().observeForever {
            if (it != null) {
                paginationResponse(
                    it, PaginationMessages(
                        BindingUtils.string(R.string.no_sections),
                        BindingUtils.string(R.string.no_more_sections),
                        BindingUtils.string(R.string.no_internet_message),
                        BindingUtils.string(R.string.some_thing_went_wrong)
                    )
                )
            }
        }
    }

    fun getList(): LiveData<PagedList<SectionsModel>>? = sectionsRepository.getList()

    fun onCardClicked(view: View, sectionsModel: SectionsModel) {

        view.findNavController().navigate(
            R.id.action_chaptersSectionListFragment_to_contentListFragment,
            Bundle().apply {

                putString(keySectionName, sectionsModel.section_Name)
                putInt(keySectionId, sectionsModel.id)
                putInt(keyCourseId, sectionsModel.course_Id)
                putString(keyCourseName, coursesModel?.courseName)
                putString(keyChapterName, chaptersModel?.chapterName)
                putInt(keyChapterId, chaptersModel?.chapterId ?: 0)
            })
    }

    fun onDownloadClicked(view: View, sectionsModel: SectionsModel) {

        CoroutineUtils.main {

            try {

                coroutineToggleLoader("${BindingUtils.string(R.string.getting_content_for)} ${sectionsModel.section_Name}")

                val contentList = sectionsRepository.getContentList(sectionsModel?.id ?: 0)
                downloadRequestedSection = sectionsModel
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

    fun takeFormativeTest(buttonView: View) {
        buttonView.findNavController()
            .navigate(
                R.id.action_chaptersSectionListFragment_to_quizInstructionFragment,
                Bundle().apply {
                    putInt(keyTestType, KEY_QUIZ_TYPE_FORMATIVE)
                    putParcelable(KEY_CHAPTER_MODEL, chaptersModel)
                    putParcelable(KEY_COURSE_MODEL, coursesModel)
                })
    }

    fun addMediaInfo(mediaInfoModel: MediaInfoModel): Long =
        contentRepository.insertMediaInfo(mediaInfoModel)

    fun getMediaInfoBySectionId(sectionId: Int): LiveData<List<MediaInfoModel>> =
        contentRepository.getMediaInfoBySectionId(sectionId)
}