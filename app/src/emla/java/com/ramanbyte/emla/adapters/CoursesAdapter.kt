package com.ramanbyte.emla.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private val courseImagesArray = BindingUtils.integerArray(R.array.course_images)
    private var coursePosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoursesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_course, parent, false)
        return CoursesHolder(view)
    }

    override fun onBindViewHolder(holder: CoursesHolder, position: Int) {
        val coursesModel: CoursesModel = getItem(position)!!
        //holder.setData(coursesModel)
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
                holder.cardCourseBinding.ivCart.visibility = View.INVISIBLE
                holder.cardCourseBinding.ivPerformance.visibility = View.INVISIBLE
                holder.cardCourseBinding.tvLabeCart.visibility = View.INVISIBLE
                holder.cardCourseBinding.tvLabePerformance.visibility = View.INVISIBLE
            } else {
                // /*|| coursesModel.courseFee==0.0F*/ course are mandatory as discuss with Manish Sir
                if (coursesModel.isInCart  || coursesModel.isPurchase) {
                    holder.cardCourseBinding.ivCart.visibility = View.INVISIBLE
                    holder.cardCourseBinding.tvLabeCart.visibility = View.INVISIBLE
                } else {
                    holder.cardCourseBinding.ivCart.visibility = View.VISIBLE
                    holder.cardCourseBinding.tvLabeCart.visibility = View.VISIBLE
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

           /* val layoutParams = cardCourseBinding.root.layoutParams

            val width = displayMetrics.widthPixels - (BindingUtils.dimen(R.dimen.dp_5) * 2)

            layoutParams.height = (width * 0.6).toInt()

            cardCourseBinding.root.layoutParams = layoutParams

           cardCourseBinding.imageView.layoutParams.apply {

                height = (width * 0.6).toInt()
            }*/
        }

        fun bind(coursesModel: CoursesModel) {

            cardCourseBinding.apply {
                coursesViewModel = viewModel
                this.coursesModel = coursesModel
                viewPosition = adapterPosition
            }
        }
// bind data to custom tab layout recyclerview
       fun setData(coursesModel: CoursesModel) {
            cardCourseBinding.apply {
                if (coursesModel.courseImage == null || coursesModel.courseImage?.isEmpty() == true) {
                    when {
                        adapterPosition < courseImagesArray.size -> {//size 2 adapterPosition=2
                            coursePosition = adapterPosition
                        }
                        coursePosition < courseImagesArray.size - 1 -> {//coursePosition=1<1
                            coursePosition++
                        }
                        else -> {
                            coursePosition = 0
                        }
                    }

                    AppLog.infoLog("CourseListAdapter coursePosition $coursePosition")

                    /* R.array.course_images.resDrawableArray(ivCourseImage.context, coursePosition) {
                         GlideApp.with(ivCourseImage.context).load(it).into(ivCourseImage)
                     }*/
                } else {
                    //loading image
                    /*  val imgUrl = ivCourseImage.context.let {
                          AppS3Client.createInstance(it).getFileAccessUrl(model.imageUrl!!)
                      }*/
                    //   StaticMethodUtilitiesKtx.loadImage(imgUrl!!, ivCourseImage)
                }

                rvCustomTabList.layoutManager =
                    LinearLayoutManager(rvCustomTabList.context, RecyclerView.HORIZONTAL, false)
                rvCustomTabList.adapter = CustomTabLayoutAdapter(getCustomTabModuleList(), coursesModel)

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

    fun getCustomTabModuleList(): ArrayList<CustomTabModel> {
        val customTabModelList = ArrayList<CustomTabModel>()

        customTabModelList.add(CustomTabModel().apply {
            icon = BindingUtils.drawable(R.drawable.ic_course_dummy)
            title = BindingUtils.string(R.string.courseware)
            clickListener = viewModel?.onCoursewareclickListener!!
        })

        customTabModelList.add(CustomTabModel().apply {
            icon = BindingUtils.drawable(R.drawable.ic_all_the_best)
            title = BindingUtils.string(R.string.performance)
            clickListener = viewModel?.onPerformanceclickListener!!
        })



        customTabModelList.add(CustomTabModel().apply {
            icon = BindingUtils.drawable(R.drawable.ic_course_dummy)
            title = BindingUtils.string(R.string.courseware)
            clickListener = viewModel?.onCoursewareclickListener!!
        })

        customTabModelList.add(CustomTabModel().apply {
            icon = BindingUtils.drawable(R.drawable.ic_all_the_best)
            title = BindingUtils.string(R.string.performance)
            clickListener = viewModel?.onPerformanceclickListener!!
        })

        customTabModelList.add(CustomTabModel().apply {
            icon = BindingUtils.drawable(R.drawable.ic_course_dummy)
            title = BindingUtils.string(R.string.courseware)
            clickListener = viewModel?.onCoursewareclickListener!!
        })

        customTabModelList.add(CustomTabModel().apply {
            icon = BindingUtils.drawable(R.drawable.ic_all_the_best)
            title = BindingUtils.string(R.string.performance)
            clickListener = viewModel?.onPerformanceclickListener!!
        })

        customTabModelList.add(CustomTabModel().apply {
            icon = BindingUtils.drawable(R.drawable.ic_course_dummy)
            title = BindingUtils.string(R.string.courseware)
            clickListener = viewModel?.onCoursewareclickListener!!
        })

        customTabModelList.add(CustomTabModel().apply {
            icon = BindingUtils.drawable(R.drawable.ic_all_the_best)
            title = BindingUtils.string(R.string.performance)
            clickListener = viewModel?.onPerformanceclickListener!!
        })

        customTabModelList.add(CustomTabModel().apply {
            icon = BindingUtils.drawable(R.drawable.ic_course_dummy)
            title = BindingUtils.string(R.string.courseware)
            clickListener = viewModel?.onCoursewareclickListener!!
        })
        return customTabModelList
    }
}