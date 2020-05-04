package com.ramanbyte.emla.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.ramanbyte.R
import com.ramanbyte.base.BaseViewModel
import com.ramanbyte.data_layer.CoroutineUtils
import com.ramanbyte.emla.data_layer.network.exception.ApiException
import com.ramanbyte.emla.data_layer.network.exception.NoDataException
import com.ramanbyte.emla.data_layer.network.exception.NoInternetException
import com.ramanbyte.emla.data_layer.repositories.QuestionRepository
import com.ramanbyte.emla.models.AskQuestionModel
import com.ramanbyte.emla.models.FavouriteVideosModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_BLANK
import org.kodein.di.generic.instance

class MyFavouriteVideoViewModel(mContext:Context) : BaseViewModel(mContext) {

    private val questionRepository: QuestionRepository by instance()

    var favouriteVideosListLiveData = MutableLiveData<List<FavouriteVideosModel>>().apply {
        value = arrayListOf()
    }

    var onClickFavouriteVideosLiveData = MutableLiveData<Boolean>().apply {
        value = false
    }

    override var noInternetTryAgain: () -> Unit = {
        getFavouriteVideos()
    }

    fun getFavouriteVideos(){
        CoroutineUtils.main {
            try {
                coroutineToggleLoader(BindingUtils.string(R.string.getting_questions_list))

                val response = questionRepository.getFavouriteVideos(14)
                favouriteVideosListLiveData.postValue(response)

                toggleLayoutVisibility(
                    View.VISIBLE,
                    View.GONE,
                    View.GONE,
                    KEY_BLANK,
                    View.GONE
                )
                coroutineToggleLoader()
            } catch (e: ApiException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.GONE,
                    BindingUtils.string(R.string.some_thing_went_wrong),
                    View.VISIBLE
                )
                coroutineToggleLoader()
            } catch (e: NoInternetException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                toggleLayoutVisibility(
                    View.GONE,
                    View.GONE,
                    View.VISIBLE,
                    BindingUtils.string(R.string.no_internet_message),
                    View.GONE
                )
                coroutineToggleLoader()
            } catch (e: NoDataException) {
                e.printStackTrace()
                AppLog.errorLog(e.message, e)
                toggleLayoutVisibility(
                    View.GONE,
                    View.VISIBLE,
                    View.GONE,
                    BindingUtils.string(R.string.favourite_videos_unavailable),
                    View.GONE
                )
                coroutineToggleLoader()
            }
        }

    }

}