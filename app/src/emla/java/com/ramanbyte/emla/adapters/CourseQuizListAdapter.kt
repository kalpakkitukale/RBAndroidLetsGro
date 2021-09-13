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
import com.ramanbyte.databinding.CardCourseQuizListBinding
import com.ramanbyte.emla.models.response.CourseQuizModel
import com.ramanbyte.emla.view_model.CourseQuizListViewModel

class CourseQuizListAdapter :
    PagedListAdapter<CourseQuizModel, CourseQuizListAdapter.CourseQuizListViewHolder>(DIFF_CALLBACK) {

    var courseQuizListViewModel: CourseQuizListViewModel? = null
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseQuizListViewHolder {
        return CourseQuizListViewHolder(
            CardCourseQuizListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CourseQuizListViewHolder, position: Int) {
        holder.bindData()
    }

    inner class CourseQuizListViewHolder(private val cardCourseQuizBinding: CardCourseQuizListBinding) :
        RecyclerView.ViewHolder(cardCourseQuizBinding.root), LifecycleOwner {

        private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }

        fun bindData() {

            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)

            cardCourseQuizBinding.apply {
                val courseQuizModel = getItem(adapterPosition)
                this.courseQuizModel = courseQuizModel
                this.courseQuizViewModel = this@CourseQuizListAdapter.courseQuizListViewModel
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CourseQuizModel>() {
            override fun areItemsTheSame(
                oldItem: CourseQuizModel,
                newItem: CourseQuizModel
            ): Boolean {
                return oldItem.quizId == newItem.quizId
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: CourseQuizModel,
                newItem: CourseQuizModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}