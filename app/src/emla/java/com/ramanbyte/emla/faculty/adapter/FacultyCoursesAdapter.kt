package com.ramanbyte.emla.faculty.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ramanbyte.R
import com.ramanbyte.databinding.CardFacultyCourseBinding
import com.ramanbyte.emla.faculty.models.FacultyCoursesModel
import com.ramanbyte.emla.faculty.view_model.FacultyCoursesViewModel
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.StaticMethodUtilitiesKtx

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
                StaticMethodUtilitiesKtx.getS3DynamicURL(courseImage ?: KEY_BLANK, context!!)
            /*AppS3Client.createInstance(context!!).getFileAccessUrl(courseImage ?: KEY_BLANK)*/
        })
    }

    inner class FacultyCoursesViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        private var dataBinding: CardFacultyCourseBinding = CardFacultyCourseBinding.bind(itemView)

        fun bind(coursesModel: FacultyCoursesModel) {
            dataBinding.apply {
                facultyCoursesViewModel = coursesViewModel
                this.coursesModel = coursesModel

                ivCoursePic.clipToOutline = true

                Glide.with(context!!)
                    .load(coursesModel.courseImageUrl)
                    .placeholder(R.drawable.ic_course_dummy)
                    .error(R.drawable.ic_course_dummy)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .into(ivCoursePic)

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