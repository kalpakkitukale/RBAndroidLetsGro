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
import com.ramanbyte.databinding.CardFacultyCourseBinding
import com.ramanbyte.emla.faculty.view_model.FacultyCoursesViewModel
import com.ramanbyte.emla.models.CoursesModel

class FacultyCoursesAdapter(var mContext: Context?) : PagedListAdapter<CoursesModel, FacultyCoursesAdapter.FacultyCoursesViewHolder>(DIFF_CALLBACK) {

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
        val coursesModel: CoursesModel = getItem(position)!!
        holder.bind(coursesModel)
    }

    inner class FacultyCoursesViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        var dataBinding: CardFacultyCourseBinding = CardFacultyCourseBinding.bind(itemView)

        fun bind(coursesModel: CoursesModel) {
            dataBinding.apply {
                facultyCoursesViewModel = coursesViewModel
                this.coursesModel1 = coursesModel
            }
        }

    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CoursesModel>() {
            override fun areItemsTheSame(
                oldItem: CoursesModel,
                newItem: CoursesModel
            ): Boolean {
                return oldItem.courseId == newItem.courseId
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: CoursesModel,
                newItem: CoursesModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}