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
import com.ramanbyte.databinding.CardStudentAskQuestionBinding
import com.ramanbyte.emla.faculty.models.FacultyCoursesModel
import com.ramanbyte.emla.faculty.view_model.FacultyCoursesViewModel
import com.ramanbyte.emla.faculty.view_model.StudentAskedQuestionsViewModel
import com.ramanbyte.utilities.KEY_BLANK

class StudentAskedQuestionsAdapter()  : PagedListAdapter<FacultyCoursesModel, StudentAskedQuestionsAdapter.StudentAskedQuestionsViewHolder>(
    StudentAskedQuestionsAdapter.DIFF_CALLBACK
)  {

    var studentAskedQuestionsViewModel: StudentAskedQuestionsViewModel? = null
    var context: Context? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StudentAskedQuestionsAdapter.StudentAskedQuestionsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_student_ask_question, parent, false)
        return StudentAskedQuestionsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: StudentAskedQuestionsAdapter.StudentAskedQuestionsViewHolder,
        position: Int
    ) {
        val coursesModel: FacultyCoursesModel = getItem(position)!!
        holder.bind(coursesModel.apply {
            courseImageUrl =
                AppS3Client.createInstance(context!!).getFileAccessUrl(courseImage ?: KEY_BLANK)
        })
    }

    inner class StudentAskedQuestionsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var dataBinding: CardStudentAskQuestionBinding = CardStudentAskQuestionBinding.bind(itemView)

        fun bind(coursesModel: FacultyCoursesModel) {
            dataBinding.apply {
                viewModel = studentAskedQuestionsViewModel
                //this.coursesModel = coursesModel
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