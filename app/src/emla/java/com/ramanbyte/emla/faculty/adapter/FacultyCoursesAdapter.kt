package com.ramanbyte.emla.faculty.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import com.ramanbyte.databinding.CardFacultyCourseBinding
import com.ramanbyte.emla.faculty.models.FacultyCoursesModel
import com.ramanbyte.emla.faculty.view_model.FacultyCoursesViewModel
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.utilities.KEY_BLANK

class FacultyCoursesAdapter(var mContext: Context?) :
    PagedListAdapter<FacultyCoursesModel, FacultyCoursesAdapter.FacultyCoursesViewHolder>(
        DIFF_CALLBACK
    ) {

    var coursesViewModel: FacultyCoursesViewModel? = null
    var context: Context? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FacultyCoursesAdapter.FacultyCoursesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_faculty_course, parent, false)
        return FacultyCoursesViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: FacultyCoursesAdapter.FacultyCoursesViewHolder,
        position: Int
    ) {
        val coursesModel: FacultyCoursesModel = getItem(position)!!
        holder.bind(coursesModel.apply {
            courseImageUrl =
                AppS3Client.createInstance(context!!).getFileAccessUrl(courseImage ?: KEY_BLANK)
        })
    }

    inner class FacultyCoursesViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        var dataBinding: CardFacultyCourseBinding = CardFacultyCourseBinding.bind(itemView)

        fun bind(coursesModel: FacultyCoursesModel) {
            dataBinding.apply {
                facultyCoursesViewModel = coursesViewModel
                this.coursesModel = coursesModel

                /*coursesModel.totalNumberOfQuestionCount.apply {
                    if (this.toString().length == 1) {
                        tvQuestionCount.text = "0${this.toString()}"
                    } else {
                        tvQuestionCount.text = this.toString()
                    }
                }*/


            }
        }

    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FacultyCoursesModel>() {
            override fun areItemsTheSame(
                oldItem: FacultyCoursesModel,
                newItem: FacultyCoursesModel
            ): Boolean {
                return oldItem.courseId == newItem.courseId
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: FacultyCoursesModel,
                newItem: FacultyCoursesModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}