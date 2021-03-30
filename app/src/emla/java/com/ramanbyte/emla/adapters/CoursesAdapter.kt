package com.ramanbyte.emla.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardCourseBinding
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.CustomTabModel
import com.ramanbyte.emla.view_model.CoursesViewModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.StaticMethodUtilitiesKtx.getS3DynamicURL


class CoursesAdapter(private val displayMetrics: DisplayMetrics, var myCourse: Int) :
    PagedListAdapter<CoursesModel, CoursesAdapter.CoursesHolder>(DIFF_CALLBACK) {
    var viewModel: CoursesViewModel? = null
    var context: Context? = null
    var lifecycleOwner: LifecycleOwner? = null
    var adapter: CustomTabLayoutAdapter? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoursesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_course, parent, false)
        return CoursesHolder(view)
    }

    override fun onBindViewHolder(holder: CoursesHolder, position: Int) {
        val coursesModel: CoursesModel = getItem(position)!!
        holder.setData(coursesModel, position)
        holder.bind(coursesModel.apply {

            if ((coursesModel.preAssessmentStatus.equals(
                    "true",
                    true
                )) && (coursesModel.summativeAssessmentStatus.equals("true", true))
            ) {
                holder.cardCourseBinding.ivStatus.visibility = View.VISIBLE
                holder.cardCourseBinding.ivStatus.setImageDrawable(BindingUtils.drawable(R.drawable.ic_tick_circle))
            } else if (coursesModel.preAssessmentStatus.equals(
                    "true",
                    true
                ) && (coursesModel.summativeAssessmentStatus.equals(
                    "false",
                    true
                ) || coursesModel.summativeAssessmentStatus.isNullOrEmpty())
            ) {
                holder.cardCourseBinding.ivStatus.visibility = View.VISIBLE
                holder.cardCourseBinding.ivStatus.setImageDrawable(BindingUtils.drawable(R.drawable.ic_success))
            } else {
                holder.cardCourseBinding.ivStatus.visibility = View.GONE
            }
            courseImageUrl = getS3DynamicURL(courseImage ?: KEY_BLANK, context!!)
//                AppS3Client.createInstance(context!!).getFileAccessUrl(courseImage ?: KEY_BLANK)
            if (myCourse == 1) {
                holder.cardCourseBinding.layoutCart.visibility = View.INVISIBLE
                holder.cardCourseBinding.layoutPerformance.visibility = View.INVISIBLE
            } else {
                // /*|| coursesModel.courseFee==0.0F*/ course are mandatory as discuss with Manish Sir
                if (coursesModel.isInCart || coursesModel.isPurchase) {
                    holder.cardCourseBinding.layoutCart.visibility = View.INVISIBLE
                } else {
                    holder.cardCourseBinding.layoutCart.visibility = View.VISIBLE
                }
            }
            courseImageUrl = getS3DynamicURL(courseImage ?: KEY_BLANK, context!!)
//                AppS3Client.createInstance(context!!).getFileAccessUrl(courseImage ?: KEY_BLANK)
        })
    }

    inner class CoursesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardCourseBinding: CardCourseBinding = CardCourseBinding.bind(itemView)
        init {
            cardCourseBinding.coursesViewModel = viewModel
        }

        fun bind(coursesModel: CoursesModel) {
            cardCourseBinding.apply {
                coursesViewModel = viewModel
                this.coursesModel = coursesModel
                viewPosition = adapterPosition
            }
        }

        // bind data to custom tab layout recyclerview
        fun setData(coursesModel: CoursesModel, position: Int) {
            val customList = getCustomTabModuleList(coursesModel)
            cardCourseBinding.apply {
                rvCustomTabList.layoutManager =
                    LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter = CustomTabLayoutAdapter(customList, coursesModel, position, viewModel)
                rvCustomTabList.adapter = adapter
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
// binding the custom data to the custom recyclerview.
    fun getCustomTabModuleList(coursesModel: CoursesModel): ArrayList<CustomTabModel> {
        val customTabModelList = ArrayList<CustomTabModel>()
        customTabModelList.add(CustomTabModel().apply {
            id = 1
            icon = BindingUtils.drawable(R.drawable.ic_course_content_new)
            title = BindingUtils.string(R.string.courseware)
            clickListener = viewModel?.onCoursewareclickListener!!
        })
        customTabModelList.add(CustomTabModel().apply {
            id = 2
            icon = BindingUtils.drawable(R.drawable.ic_topic_list)
            title = BindingUtils.string(R.string.topic_list)
            clickListener = viewModel?.onPerformanceclickListener!!
        })
        customTabModelList.add(CustomTabModel().apply {
            id = 3
            icon = BindingUtils.drawable(R.drawable.ic_performance)
            title = BindingUtils.string(R.string.performance)
            clickListener = viewModel?.onCoursewareclickListener!!
        })
        if (!coursesModel.isInCart && !coursesModel.isPurchase) {
            customTabModelList.add(CustomTabModel().apply {
                id = 4
                icon = BindingUtils.drawable(R.drawable.ic_cart)
                title = BindingUtils.string(R.string.cart)
                clickListener = viewModel?.onCartclickListener!!
            })
        }
        return customTabModelList
    }
}