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
import com.ramanbyte.databinding.MyCourseCardviewBinding
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.CustomTabModel
import com.ramanbyte.emla.view_model.MyCourseViewModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.StaticMethodUtilitiesKtx

/**
 * @author Akash Inkar <akash.1@ramanbyte.com>
 * @since 17/4/21
 */


class MyCourseAdapter(private val displayMetrics: DisplayMetrics, var myCourse: Int) :
    PagedListAdapter<CoursesModel, MyCourseAdapter.CoursesHolder>(DIFF_CALLBACK) {
    var viewModel: MyCourseViewModel? = null
    var context: Context? = null
    var lifecycleOwner: LifecycleOwner? = null
    var adapter: CustomTabLayoutAdapter? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoursesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_course_cardview, parent, false)
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
                holder.cardCourseBinding.tvcourseCost.visibility = View.INVISIBLE
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
                holder.cardCourseBinding.tvcourseCost.visibility = View.INVISIBLE
                holder.cardCourseBinding.ivStatus.setImageDrawable(BindingUtils.drawable(R.drawable.ic_success))
            } else {
                holder.cardCourseBinding.ivStatus.visibility = View.GONE
                holder.cardCourseBinding.tvcourseCost.visibility = View.VISIBLE
            }
            courseImageUrl =
                StaticMethodUtilitiesKtx.getS3DynamicURL(courseImage ?: KEY_BLANK, context!!)
//                AppS3Client.createInstance(context!!).getFileAccessUrl(courseImage ?: KEY_BLANK)
            courseImageUrl =
                StaticMethodUtilitiesKtx.getS3DynamicURL(courseImage ?: KEY_BLANK, context!!)
//                AppS3Client.createInstance(context!!).getFileAccessUrl(courseImage ?: KEY_BLANK)
        })
    }

    inner class CoursesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardCourseBinding: MyCourseCardviewBinding = MyCourseCardviewBinding.bind(itemView)

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
            // this is working on only course list
          /*  if (myCourse!=1) {
                viewModel?.cartClickMutableLiveData?.observe(lifecycleOwner!!, Observer {
                    it?.let {
                        if (coursesModel.courseId == it) {
                            coursesModel.isInCart = true
                            adapterRefresh(position)
                            viewModel!!.cartClickMutableLiveData.postValue(0)
                        }
                    }
                })
            }*/
            val customList = getCustomTabModuleList(coursesModel)
            cardCourseBinding.apply {
                rvCustomTabList.layoutManager =
                    LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter = CustomTabLayoutAdapter(customList, coursesModel, viewModel)
                rvCustomTabList.adapter = adapter
            }
        }

    }

    //refresh the adapter
    fun adapterRefresh(position: Int) {
        try {
            notifyItemChanged(position)
            //notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.infoLog(e.message.toString())
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

        /*customTabModelList.add(CustomTabModel().apply {
            id = 5
            icon = BindingUtils.drawable(R.drawable.ic_course_information)
            title = BindingUtils.string(R.string.course_information)
            clickListener = viewModel?.onClickCourseInformationClick!!
        })*/

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
            clickListener = viewModel?.onTopicclickListener!!
        })
        /*if (myCourse != 1) {
            customTabModelList.add(CustomTabModel().apply {
                id = 3
                icon = BindingUtils.drawable(R.drawable.ic_performance)
                title = BindingUtils.string(R.string.performance)
                clickListener = viewModel?.onPerformanceclickListener!!
            })

            if (!coursesModel.isInCart && !coursesModel.isPurchase) {
                customTabModelList.add(CustomTabModel().apply {
                    id = 4
                    icon = BindingUtils.drawable(R.drawable.ic_cart)
                    title = BindingUtils.string(R.string.cart)
                    clickListener = viewModel?.onCartclickListener!!
                })
            }
        }*/

        return customTabModelList
    }


}