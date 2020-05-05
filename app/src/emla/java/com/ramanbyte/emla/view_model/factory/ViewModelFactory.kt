package com.ramanbyte.emla.view_model.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ramanbyte.emla.view_model.*
import com.ramanbyte.emla.view_model.ChangePasswordViewModel
import com.ramanbyte.emla.view_model.ContainerViewModel
import com.ramanbyte.emla.view_model.ForgetPasswordViewModel
import com.ramanbyte.emla.view_model.LauncherViewModel
import com.ramanbyte.emla.view_model.LoginViewModel
import com.ramanbyte.emla.view_model.ShowQuestionsViewModel
import com.ramanbyte.emla.view_model.*
import com.ramanbyte.view_model.factory.BaseViewModelFactory

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class ViewModelFactory(private val mContext: Context) : BaseViewModelFactory(mContext = mContext) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return when {
            modelClass.isAssignableFrom(LauncherViewModel::class.java) -> {
                return LauncherViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                return LoginViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(CreateAccountViewModel::class.java) -> {
                return CreateAccountViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(ContainerViewModel::class.java) -> {
                return ContainerViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(LearnerProfileViewModel::class.java) -> {
                return LearnerProfileViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(CoursesViewModel::class.java) -> {
                return CoursesViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(CoursesDetailViewModel::class.java) -> {
                return CoursesDetailViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(ChaptersViewModel::class.java) -> {
                return ChaptersViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(ChaptersSectionViewModel::class.java) -> {
                return ChaptersSectionViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(ContentViewModel::class.java) -> {
                return ContentViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(MyDownloadsViewModel::class.java) -> {
                return MyDownloadsViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(ChangePasswordViewModel::class.java) -> {
                return ChangePasswordViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(ForgetPasswordViewModel::class.java) -> {
                return ForgetPasswordViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(ShowQuestionsViewModel::class.java) -> {
                return ShowQuestionsViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(FileViewerViewModel::class.java) -> {
                return FileViewerViewModel() as T
            }
            modelClass.isAssignableFrom(MediaPlaybackViewModel::class.java) -> {
                return MediaPlaybackViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(CoursesDetailViewModel::class.java) -> {
                return CoursesDetailViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                return SettingViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(MyFavouriteVideoViewModel::class.java) -> {
                return MyFavouriteVideoViewModel(mContext) as T
            }
            else -> super.create(modelClass)
        }

    }
}