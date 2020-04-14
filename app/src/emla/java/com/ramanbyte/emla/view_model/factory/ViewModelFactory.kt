package com.ramanbyte.emla.view_model.factory

import android.content.Context
import androidx.lifecycle.ViewModel
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
            modelClass.isAssignableFrom(ContainerViewModel::class.java) -> {
                return ContainerViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(LearnerProfileViewModel::class.java) -> {
                return LearnerProfileViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(ChaptersViewModel::class.java) -> {
                return ChaptersViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(ChaptersSectionViewModel::class.java) -> {
                return ChaptersSectionViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(ContentViewModel::class.java) -> {
                return ContainerViewModel(mContext) as T
            }
            modelClass.isAssignableFrom(MyDownloadsViewModel::class.java) -> {
                return MyDownloadsViewModel(mContext) as T
            }
            else -> super.create(modelClass)
        }

    }
}