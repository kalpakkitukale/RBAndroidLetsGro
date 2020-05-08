package com.ramanbyte.emla.faculty.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardFacultyCourseBinding
import com.ramanbyte.emla.faculty.view_model.FacultyCoursesViewModel
import com.ramanbyte.emla.view_model.CoursesViewModel

class FacultyCoursesAdapter : RecyclerView.Adapter<FacultyCoursesAdapter.FacultyCoursesViewHolder>() {

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

    override fun getItemCount(): Int {
        return  10
    }

    override fun onBindViewHolder(
        holder: FacultyCoursesAdapter.FacultyCoursesViewHolder,
        position: Int
    ) {
        holder.bind()
    }

    inner class FacultyCoursesViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        var dataBinding: CardFacultyCourseBinding = CardFacultyCourseBinding.bind(itemView)

        fun bind() {
            dataBinding.apply {
                facultyCoursesViewModel = coursesViewModel
            }
        }

    }
}