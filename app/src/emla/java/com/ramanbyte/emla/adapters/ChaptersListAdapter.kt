package com.ramanbyte.emla.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.databinding.CardChapterBinding
import com.ramanbyte.emla.models.ChaptersModel
import com.ramanbyte.emla.view_model.ChaptersViewModel

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
        RecyclerView.ViewHolder(cardChapterBinding.root) {

        fun bindData() {

            cardChapterBinding.apply {

                val chaptersModel = getItem(adapterPosition)

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