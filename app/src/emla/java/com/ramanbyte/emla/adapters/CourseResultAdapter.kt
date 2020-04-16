package com.ramanbyte.emla.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardResultDisplayBinding
import com.ramanbyte.emla.models.CourseResultModel
import com.ramanbyte.emla.view_model.CoursesDetailViewModel

class CourseResultAdapter(private val courseResultList: ArrayList<CourseResultModel>) :
    RecyclerView.Adapter<CourseResultAdapter.CourseViewHolder>() {

    var courseViewModel: CoursesDetailViewModel? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_result_display, parent, false)
        return CourseViewHolder(view)
    }

    override fun getItemCount(): Int {
        return courseResultList.size
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        var courseResultModel = courseResultList[position]
        holder.bind(courseResultModel)
    }

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var cardCourseBindable: CardResultDisplayBinding? = null

        init {
            cardCourseBindable = DataBindingUtil.bind(itemView)
        }

        fun bind(courseResultModel: CourseResultModel) {
            cardCourseBindable?.courseResultModel = courseResultModel.apply {
                index = (adapterPosition + 1).toString()
            }
        }
    }
}