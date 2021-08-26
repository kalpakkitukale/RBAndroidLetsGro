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
import com.ramanbyte.databinding.CardJobListBinding
import com.ramanbyte.emla.models.response.JobModel
import com.ramanbyte.emla.view_model.JobsViewModel
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.StaticMethodUtilitiesKtx

class JobListAdapter :
    PagedListAdapter<JobModel, JobListAdapter.JobsListViewHolder>(DIFF_CALLBACK) {

    var jobsViewModel: JobsViewModel? = null
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobsListViewHolder {
        return JobsListViewHolder(
            CardJobListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: JobsListViewHolder, position: Int) {
        val jobModel: JobModel = getItem(position)!!
        holder.bindData(jobModel.apply {
            companyImageURL =
                StaticMethodUtilitiesKtx.getS3DynamicURL(companyLogo ?: KEY_BLANK, context!!)
        })
    }

    inner class JobsListViewHolder(private val cardJobListBinding: CardJobListBinding) :
        RecyclerView.ViewHolder(cardJobListBinding.root), LifecycleOwner {

        private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }

        fun bindData(jobModel: JobModel) {

            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)

            cardJobListBinding.apply {
                val model = getItem(adapterPosition)
                this.jobModel = model
                this.jobsViewModel = this@JobListAdapter.jobsViewModel
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<JobModel>() {
            override fun areItemsTheSame(
                oldItem: JobModel,
                newItem: JobModel
            ): Boolean {
                return oldItem.jobId == newItem.jobId
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: JobModel,
                newItem: JobModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}