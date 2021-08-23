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
import com.ramanbyte.databinding.CardSectionBinding
import com.ramanbyte.emla.models.SectionsModel
import com.ramanbyte.emla.view_model.ChaptersSectionViewModel

class ChaptersSectionListAdapter(private val chaptersSectionViewModel: ChaptersSectionViewModel) :
    PagedListAdapter<SectionsModel, ChaptersSectionListAdapter.ChaptersSectionListViewHolder>(
        DIFF_CALLBACK
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChaptersSectionListViewHolder {
        return ChaptersSectionListViewHolder(
            CardSectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChaptersSectionListViewHolder, position: Int) {
        holder.bindData()
    }

    inner class ChaptersSectionListViewHolder(private val cardSectionBinding: CardSectionBinding) :
        RecyclerView.ViewHolder(cardSectionBinding.root), LifecycleOwner {

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

            cardSectionBinding.apply {

                val sectionsModel = getItem(adapterPosition)

                this@ChaptersSectionListAdapter.chaptersSectionViewModel?.getMediaInfoBySectionId(sectionsModel?.id ?: 0)
                    ?.observe(this@ChaptersSectionListViewHolder, Observer {

                        it?.apply {

                            val downloadedList = filter {
                                it.mediaStatus != "-1"
                            }

                            if ((downloadedList.isNotEmpty() && downloadedList.size >= sectionsModel?.contentCount ?: -1) || sectionsModel?.contentCount == 0) {
                                sectionsModel?.downloadVisibility = View.GONE
                            } else {
                                sectionsModel?.downloadVisibility = View.VISIBLE
                            }
                        }
                    })

                this.sectionsModel = sectionsModel

                this.chaptersSectionViewModel =
                    this@ChaptersSectionListAdapter.chaptersSectionViewModel
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SectionsModel>() {
            override fun areItemsTheSame(
                oldItem: SectionsModel,
                newItem: SectionsModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: SectionsModel,
                newItem: SectionsModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}