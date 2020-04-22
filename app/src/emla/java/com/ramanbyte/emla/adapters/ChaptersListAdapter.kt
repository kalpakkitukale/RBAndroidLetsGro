package com.ramanbyte.emla.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.databinding.CardChapterBinding
import com.ramanbyte.emla.models.ChaptersModel
import com.ramanbyte.emla.view_model.ChaptersViewModel
import com.ramanbyte.utilities.AppLog

class ChaptersListAdapter(private val chaptersViewModel: ChaptersViewModel) :
    PagedListAdapter<ChaptersModel, ChaptersListAdapter.ChaptersListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChaptersListViewHolder {
        return ChaptersListViewHolder(
            CardChapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChaptersListViewHolder, position: Int) {
        holder.bindData()
    }

    inner class ChaptersListViewHolder(private val cardChapterBinding: CardChapterBinding) :
        RecyclerView.ViewHolder(cardChapterBinding.root), LifecycleOwner {

        private var lifecycleRegistry: LifecycleRegistry

        init {
            lifecycleRegistry = LifecycleRegistry(this)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }

        fun bindData() {

            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)

            cardChapterBinding.apply {

                val chaptersModel = getItem(adapterPosition)

                ivStatus.visibility =
                    if (chaptersModel?.formativeAssessmentStaus.equals("true", true)) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

                /**
                 * @since 22 Apr 2020
                 * @author Mansi Manakiki Mody
                 * After discussing with Raman Sir, remove chapter lock feature from the eMarket Project.
                 */
                if (adapterPosition > 0) {

                    val isEnabled = true
                        /*!(getItem(adapterPosition - 1)?.formativeAssessmentStaus.isNullOrEmpty()
                                || getItem(adapterPosition - 1)?.formativeAssessmentStaus.equals(
                            "false",
                            true
                        ))*/

                    viewDisable.visibility = if (isEnabled) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }

                    cardLayoutMain.isEnabled = isEnabled
                    IvDownload.isEnabled = isEnabled

                } else {
                    viewDisable.visibility = View.GONE
                }

                this@ChaptersListAdapter.chaptersViewModel.isAllCourseSessionCompleted.value =
                    ((this@ChaptersListAdapter.chaptersViewModel.isAllCourseSessionCompleted.value == true) && (chaptersModel?.formativeAssessmentStaus.equals(
                        "true",
                        true
                    )))

                this@ChaptersListAdapter.chaptersViewModel?.getMediaInfoByChapterId(
                    chaptersModel?.chapterId ?: 0
                )
                    ?.observe(this@ChaptersListViewHolder, Observer { list ->

                        val downloadedList = list.filter {
                            it.mediaStatus != -1
                        }

                        val perSectionTotalCount = chapterModel?.sectionlist?.sumBy {
                            it.contentCount
                        }

                        chaptersModel?.downloadVisibility =
                            if ((list.isNotEmpty() && downloadedList.size >= perSectionTotalCount ?: -1) || perSectionTotalCount == 0) { // chapterModel.totalSectionCount //&& downloadedList.size == chapterModel.totalSectionCount
                                View.GONE
                            } else {
                                View.VISIBLE
                            }
                    })

                this.chapterModel = chaptersModel?.apply {
                    index = (adapterPosition + 1).toString()
                }
                this.chaptersViewModel = this@ChaptersListAdapter.chaptersViewModel
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChaptersModel>() {
            override fun areItemsTheSame(
                oldItem: ChaptersModel,
                newItem: ChaptersModel
            ): Boolean {
                return oldItem.chapterId == newItem.chapterId
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: ChaptersModel,
                newItem: ChaptersModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}