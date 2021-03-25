package com.ramanbyte.emla.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardviewAllcoursesListBinding
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.view_model.CoursesViewModel
import com.ramanbyte.utilities.BindingUtils

/**
 * @author Akash Inkar <akash.1@ramanbyte.com>
 * @since 25/3/21
 */
class AllCoursesAdapter ():RecyclerView.Adapter<AllCoursesAdapter.AllCoursesViewHolder>(){

    var courseListViewModel: CoursesViewModel? = null
    var courseList:List<CoursesModel>? = ArrayList()
   // private val courseImagesArray = BindingUtils.integerArray(R.array.course_images)
    private var coursePosition = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllCoursesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_allcourses_list,parent,false)
        return AllCoursesViewHolder(CardviewAllcoursesListBinding.bind(view))
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: AllCoursesViewHolder, position: Int) {

    }

    inner class AllCoursesViewHolder(var binding:CardviewAllcoursesListBinding):RecyclerView.ViewHolder(binding.root){

    }
}