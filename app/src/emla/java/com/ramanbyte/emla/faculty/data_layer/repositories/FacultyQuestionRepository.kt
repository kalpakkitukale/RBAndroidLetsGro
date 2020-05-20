package com.ramanbyte.emla.faculty.data_layer.repositories

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ramanbyte.data_layer.base.BaseRepository
import com.ramanbyte.data_layer.pagination.PaginationResponseHandler
import com.ramanbyte.emla.data_layer.pagination.PaginationDataSourceFactory
import com.ramanbyte.emla.data_layer.room.entities.UserEntity
import com.ramanbyte.emla.faculty.data_layer.network.api_layer.FacultyMasterApiController
import com.ramanbyte.emla.faculty.data_layer.network.api_layer.FacultyQuestionController
import com.ramanbyte.emla.faculty.models.StudentAskedQuestionsModel
import com.ramanbyte.emla.faculty.models.request.StudentAskedQuestionsRequestModel
import com.ramanbyte.emla.models.UserModel
import com.ramanbyte.utilities.replicate
import org.kodein.di.generic.instance

class FacultyQuestionRepository(mContext: Context) : BaseRepository(mContext) {

    private val questionController: FacultyQuestionController by instance()
    var courseId = 0

    fun getCurrentUser(): UserModel? {
        applicationDatabase.getUserDao().apply {
            return getCurrentUser()?.replicate<UserEntity, UserModel>()
        }
    }

    private val pageSize = 10
    private val questionModelObservable =
        ObservableField<StudentAskedQuestionsRequestModel>().apply { set(StudentAskedQuestionsRequestModel()) }
    lateinit var questionPagedDataSourceFactory: PaginationDataSourceFactory<StudentAskedQuestionsModel, StudentAskedQuestionsRequestModel>
    private var paginationResponseHandlerLiveData: MutableLiveData<PaginationResponseHandler?> =
        MutableLiveData(null)
    var questionPagedList: LiveData<PagedList<StudentAskedQuestionsModel>>? = null
    private val pageListConfig =
        PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(pageSize).build()

    fun initiatePagination() {
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
        questionPagedDataSourceFactory = PaginationDataSourceFactory(
            questionModelObservable.apply {
                val userModel = getCurrentUser()
                set(StudentAskedQuestionsRequestModel().apply {
                    this.userId = userModel?.userId ?: 0
                    this.courseId = this@FacultyQuestionRepository.courseId
                })
            },
            paginationResponseHandlerLiveData
        ) {
            apiRequest {
                questionController.getQuestionListByCourse(it)
            } ?: arrayListOf()
        }

        questionPagedList =
            LivePagedListBuilder(questionPagedDataSourceFactory, pageListConfig).build()

    }

    fun getPaginationResponseHandler() = paginationResponseHandlerLiveData

    /*Filter*/
    fun applyFilter(questionsRequestModel: StudentAskedQuestionsRequestModel) {

        val userDetails = getCurrentUser()
        questionModelObservable.set(questionsRequestModel.apply {
            userId = userDetails?.userId!!
            courseId = this@FacultyQuestionRepository.courseId
        })

        questionPagedList?.value?.dataSource?.invalidate()
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

    /*
    * This function is used for to fetch the fresh order history list on try again button
    * */
    fun tryAgain() {
        questionPagedList?.value?.dataSource?.invalidate()
        paginationResponseHandlerLiveData.postValue(PaginationResponseHandler.INIT_LOADING)
    }

}