package com.ramanbyte.emla.ui.fragments


import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCourseSyllabusBinding
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.view_model.CoursesDetailViewModel
import com.ramanbyte.utilities.*


/**
 * @author Kunal Rathod
 * A simple [Fragment] subclass.
 */
class CourseSyllabusFragment :
    BaseFragment<FragmentCourseSyllabusBinding, CoursesDetailViewModel>(hasOptionsMenu = false) {

    private var mContext: Context? = null

    override val viewModelClass: Class<CoursesDetailViewModel> = CoursesDetailViewModel::class.java
    override fun layoutId(): Int = R.layout.fragment_course_syllabus

    override fun initiate() {

        arguments?.apply {
            if (!this.isEmpty && this != null) {
                viewModel.coursesModelLiveData.value =
                    getParcelable<CoursesModel>(KEY_COURSE_MODEL)!!
            }
        }

        ProgressLoader(mContext!!, viewModel!!)
        //AlertDialog(mContext!!, viewModel!!)

        val width =
            (activity!!).displayMetrics().widthPixels - (BindingUtils.dimen(R.dimen.dp_5) * 2)
        val layoutParams = layoutBinding.imgViewCourseSession.layoutParams
        layoutParams.height = (width * 0.6).toInt()
        layoutBinding.imgViewCourseSession.layoutParams = layoutParams
        layoutBinding.imgViewCourseSession.layoutParams.apply {
            height = (width * 0.6).toInt()
        }

        ViewAnimationUtils.rotateView(layoutBinding.cardCourseLayout.imgViewCourseInfo, 180, 300)


        layoutBinding.apply {
            lifecycleOwner = this@CourseSyllabusFragment
            coursesDetailViewModel = viewModel
            cardCourseLayout.coursesDetailViewModel = viewModel

            if (viewModel.courseSyllabusModelLiveData.value?.credits != 0.0) {
                cardCourseLayout.apply {
                    labelCredits.visibility = View.VISIBLE
                    txtViewCredits.visibility = View.VISIBLE
                }
            }

            rvCourseSyllabus.settings.apply {
                javaScriptEnabled = true
                displayZoomControls = false
            }
            webViewAssessmentInstruction.settings.apply {
                javaScriptEnabled = true
                displayZoomControls = false
            }

            webViewWhatIWillLearn.settings.apply {
                javaScriptEnabled = true
                displayZoomControls = false
            }

            webViewHowToUse.settings.apply {
                javaScriptEnabled = true
                displayZoomControls = false
            }
        }


        viewModel?.apply {


            getCoursesSyllabus()

            layoutToShow.observe(this@CourseSyllabusFragment, Observer {
                if (it != null) {
                    when (it.id) {
                        R.id.headerCourseInfo -> {
                            layoutVisibility(
                                View.VISIBLE,
                                View.GONE,
                                View.GONE,
                                View.GONE,
                                View.GONE
                            )
                        }
                        R.id.headerCourseSyllabus -> {
                            layoutVisibility(
                                View.GONE,
                                View.VISIBLE,
                                View.GONE,
                                View.GONE,
                                View.GONE
                            )
                        }
                        R.id.headerAssessmentInstruction -> {
                            layoutVisibility(
                                View.GONE,
                                View.GONE,
                                View.VISIBLE,
                                View.GONE,
                                View.GONE
                            )
                        }
                        R.id.headerWhatIWillLearn -> {
                            layoutVisibility(
                                View.GONE,
                                View.GONE,
                                View.GONE,
                                View.VISIBLE,
                                View.GONE
                            )
                        }
                        R.id.headerHowToUse -> {
                            layoutVisibility(
                                View.GONE,
                                View.GONE,
                                View.GONE,
                                View.GONE,
                                View.VISIBLE
                            )
                        }
                    }
                }
            })
        }
    }

    private fun layoutVisibility(
        courseInfoVisibility: Int,
        courseSyllabusVisibility: Int,
        assessmentInstructionVisibility: Int,
        whatIWillLearnVisibility: Int,
        howToUseVisibility: Int
    ) {

        layoutBinding.apply {

            if (cardCourseLayout.courseInfoGroup.isVisible) {
                cardCourseLayout.courseInfoGroup.visibility = View.GONE
                cardCourseLayout.apply {
                    labelCredits.visibility = View.GONE
                    txtViewCredits.visibility = View.GONE
                }
                ViewAnimationUtils.rotateView(cardCourseLayout.imgViewCourseInfo, 0, 300)
            } else {
                cardCourseLayout.courseInfoGroup.visibility = courseInfoVisibility
                if (courseInfoVisibility == View.VISIBLE)
                    ViewAnimationUtils.rotateView(cardCourseLayout.imgViewCourseInfo, 180, 300)
                else
                    ViewAnimationUtils.rotateView(cardCourseLayout.imgViewCourseInfo, 0, 300)
            }

            if (rvCourseSyllabus.isVisible) {
                rvCourseSyllabus.visibility = View.GONE
                ViewAnimationUtils.rotateView(imgViewCourseSyllabus, 0, 300)
            } else {
                rvCourseSyllabus.visibility = courseSyllabusVisibility
                if (courseSyllabusVisibility == View.VISIBLE)
                    ViewAnimationUtils.rotateView(imgViewCourseSyllabus, 180, 300)
                else
                    ViewAnimationUtils.rotateView(imgViewCourseSyllabus, 0, 300)
            }

            if (webViewAssessmentInstruction.isVisible) {
                webViewAssessmentInstruction.visibility = View.GONE
                ViewAnimationUtils.rotateView(imgViewAssessmentInstruction, 0, 300)
            } else {
                webViewAssessmentInstruction.visibility = assessmentInstructionVisibility
                if (assessmentInstructionVisibility == View.VISIBLE)
                    ViewAnimationUtils.rotateView(imgViewAssessmentInstruction, 180, 300)
                else
                    ViewAnimationUtils.rotateView(imgViewAssessmentInstruction, 0, 300)
            }

            if (webViewWhatIWillLearn.isVisible) {
                webViewWhatIWillLearn.visibility = View.GONE
                ViewAnimationUtils.rotateView(imgViewWhatIWillLearn, 0, 300)
            } else {
                webViewWhatIWillLearn.visibility = whatIWillLearnVisibility
                if (whatIWillLearnVisibility == View.VISIBLE)
                    ViewAnimationUtils.rotateView(imgViewWhatIWillLearn, 180, 300)
                else
                    ViewAnimationUtils.rotateView(imgViewWhatIWillLearn, 0, 300)
            }

            if (webViewHowToUse.isVisible) {
                webViewHowToUse.visibility = View.GONE //
                ViewAnimationUtils.rotateView(imgViewHowToUse, 0, 300)
            } else {
                webViewHowToUse.visibility = howToUseVisibility
                if (howToUseVisibility == View.VISIBLE)
                    ViewAnimationUtils.rotateView(imgViewHowToUse, 180, 300)
                else
                    ViewAnimationUtils.rotateView(imgViewHowToUse, 0, 300)
            }


            /*if (courseInfoVisibility == View.VISIBLE)
                ViewAnimationUtils.rotateView(cardCourseLayout.imgViewCourseInfo, 180, 300)
            else
                ViewAnimationUtils.rotateView(cardCourseLayout.imgViewCourseInfo, 0, 300)

            if (courseSyllabusVisibility == View.VISIBLE)
                ViewAnimationUtils.rotateView(imgViewCourseSyllabus, 180, 300)
            else
                ViewAnimationUtils.rotateView(imgViewCourseSyllabus, 0, 300)

            if (assessmentInstructionVisibility == View.VISIBLE)
                ViewAnimationUtils.rotateView(imgViewAssessmentInstruction, 180, 300)
            else
                ViewAnimationUtils.rotateView(imgViewAssessmentInstruction, 0, 300)

            if (whatIWillLearnVisibility == View.VISIBLE)
                ViewAnimationUtils.rotateView(imgViewWhatIWillLearn, 180, 300)
            else
                ViewAnimationUtils.rotateView(imgViewWhatIWillLearn, 0, 300)

            if (howToUseVisibility == View.VISIBLE)
                ViewAnimationUtils.rotateView(imgViewHowToUse, 180, 300)
            else
                ViewAnimationUtils.rotateView(imgViewHowToUse, 0, 300)*/

        }

    }


    companion object {
        fun getInstance() = CourseSyllabusFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }


}
