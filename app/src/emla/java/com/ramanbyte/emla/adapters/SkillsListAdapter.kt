package com.ramanbyte.emla.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.databinding.CardSkillBinding
import com.ramanbyte.emla.models.response.SkillsModel
import com.ramanbyte.emla.view_model.SkillsViewModel

class SkillsListAdapter :
    PagedListAdapter<SkillsModel, SkillsListAdapter.SkillsListViewHolder>(DIFF_CALLBACK) {

    var skillsViewModel: SkillsViewModel? = null
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillsListViewHolder {
        return SkillsListViewHolder(
            CardSkillBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SkillsListViewHolder, position: Int) {
        holder.bindData()
    }

    inner class SkillsListViewHolder(private val cardSkillBinding: CardSkillBinding) :
        RecyclerView.ViewHolder(cardSkillBinding.root), LifecycleOwner {

        private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }

        fun bindData() {

            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)

            cardSkillBinding.apply {
                val skillsModel = getItem(adapterPosition)
                this.skillsModel = skillsModel
                this.jobSkillsViewModel = this@SkillsListAdapter.skillsViewModel
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SkillsModel>() {
            override fun areItemsTheSame(
                oldItem: SkillsModel,
                newItem: SkillsModel
            ): Boolean {
                return oldItem.skillId == newItem.skillId
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: SkillsModel,
                newItem: SkillsModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}