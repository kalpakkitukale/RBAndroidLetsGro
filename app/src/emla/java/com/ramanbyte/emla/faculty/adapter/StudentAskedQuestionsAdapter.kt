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
import com.ramanbyte.emla.faculty.models.StudentAskedQuestionsModel
import com.ramanbyte.emla.faculty.view_model.FacultyCoursesViewModel
import com.ramanbyte.emla.faculty.view_model.StudentAskedQuestionsViewModel
import com.ramanbyte.utilities.DateUtils
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.KEY_N
import com.ramanbyte.utilities.KEY_NA_WITHOUT_SPACE

class StudentAskedQuestionsAdapter() :
    PagedListAdapter<StudentAskedQuestionsModel, StudentAskedQuestionsAdapter.StudentAskedQuestionsViewHolder>(
        StudentAskedQuestionsAdapter.DIFF_CALLBACK
    ) {

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
        val questionsModel: StudentAskedQuestionsModel = getItem(position)!!
        holder.bind(questionsModel.apply {
            /*courseImageUrl =
                AppS3Client.createInstance(context!!).getFileAccessUrl(courseImage ?: KEY_BLANK)*/
        })
    }

    inner class StudentAskedQuestionsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var dataBinding: CardStudentAskQuestionBinding =
            CardStudentAskQuestionBinding.bind(itemView)

        fun bind(questionsModel: StudentAskedQuestionsModel) {
            dataBinding.apply {
                viewModel = studentAskedQuestionsViewModel
                this.studentAskedQuestionsModel = questionsModel

                questionsModel.apply {
                    if (question?.isBlank()!!) {
                        tvQuestion.text = KEY_NA_WITHOUT_SPACE
                    } else {
                        tvQuestion.text = question
                    }

                    if (studentPhoneNo?.isBlank()!!) {
                        tvMobile.text = KEY_NA_WITHOUT_SPACE
                    } else {
                        tvMobile.text = studentPhoneNo
                    }

                    if (isQuestionAnswered == KEY_N) {
                        ivDot.visibility = View.VISIBLE
                    } else {
                        ivDot.visibility = View.GONE
                    }

                    tvDateTime.text = DateUtils.getFreeFormatDateTime(
                        DateUtils.getTimeFormDate(
                            questionRaisedDateTime!!,
                            DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS
                        ),
                        DateUtils.getCalendarByCustomDate(
                            questionRaisedDateTime!!,
                            DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS
                        )!!
                    )

                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StudentAskedQuestionsModel>() {
            override fun areItemsTheSame(
                oldItem: StudentAskedQuestionsModel,
                newItem: StudentAskedQuestionsModel
            ): Boolean {
                return oldItem.courseId == newItem.courseId
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: StudentAskedQuestionsModel,
                newItem: StudentAskedQuestionsModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}