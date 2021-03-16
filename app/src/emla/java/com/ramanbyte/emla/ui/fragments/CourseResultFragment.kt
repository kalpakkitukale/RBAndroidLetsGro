package com.ramanbyte.emla.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCourseResultBinding
import com.ramanbyte.emla.adapters.CourseResultAdapter
import com.ramanbyte.emla.models.CourseResultModel
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.view_model.CoursesDetailViewModel
import com.ramanbyte.utilities.KEY_COURSE_MODEL
import com.ramanbyte.utilities.ProgressLoader
import com.ramanbyte.utilities.skipTrailingZeroes

class CourseResultFragment :
    BaseFragment<FragmentCourseResultBinding, CoursesDetailViewModel>(
        isNestedGraph = true,
        hasOptionsMenu = false
    ) {

    private var mContext: Context? = null
    private var maxScoreCourseModel = CourseResultModel()

    override val viewModelClass: Class<CoursesDetailViewModel> = CoursesDetailViewModel::class.java


    override fun layoutId(): Int = R.layout.fragment_course_result
    @SuppressLint("SetTextI18n")
    override fun initiate() {
        ProgressLoader(mContext!!, viewModel!!)

        arguments?.apply {
            viewModel.coursesModelLiveData.postValue(getParcelable<CoursesModel>(KEY_COURSE_MODEL)!!)
        }
        //AlertDialog(mContext!!, viewModel!!)


        /*val width = (activity!!).displayMetrics().widthPixels - (BindingUtils.dimen(R.dimen.dp_5) * 2)
        val layoutParams = layoutBinding.imageView.layoutParams
        layoutParams.height = (width * 0.6).toInt()
        layoutBinding.imageView.layoutParams = layoutParams
        layoutBinding.imageView.layoutParams.apply {
            height = (width * 0.6).toInt()
        }*/

        layoutBinding.apply {
            lifecycleOwner = this@CourseResultFragment
            noInternet.viewModel = viewModel
            noData.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel

            coursesViewModel = viewModel

        }

        viewModel.apply {
            getCourseResult()

            courseResultModelListLiveData.observe(this@CourseResultFragment, Observer {
                if (it != null && it.size > 0) {
                    //finding max obtained value
                    maxScoreCourseModel =
                        it.maxBy { courseResultModel -> courseResultModel.obtainedmarks }!!


                    layoutBinding.tvFinalMarks.text =
                        "${skipTrailingZeroes(maxScoreCourseModel.obtainedmarks)} / ${skipTrailingZeroes(
                            maxScoreCourseModel.totalmarks
                        )}"

                    val courseResultAdapter =
                        CourseResultAdapter(it as ArrayList<CourseResultModel>).apply {
                            courseViewModel = viewModel
                        }
                    layoutBinding.rvResult.adapter = courseResultAdapter
                }
            })
        }
    }

    companion object {
        fun getInstance() = CourseResultFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }
}