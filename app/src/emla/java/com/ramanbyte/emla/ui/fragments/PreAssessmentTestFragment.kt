package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentPreAssessmentTestBinding
import com.ramanbyte.emla.models.ChaptersModel
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.response.CourseQuizModel
import com.ramanbyte.emla.view_model.ShowQuestionsViewModel
import com.ramanbyte.utilities.*


/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 14/04/20
 */
class PreAssessmentTestFragment :
    BaseFragment<FragmentPreAssessmentTestBinding, ShowQuestionsViewModel>() {

    private var mContext: Context? = null
    private var courseModel: CoursesModel? = null
    private var chapterModel: ChaptersModel? = null
    private var courseQuizModel: CourseQuizModel? = null
    private lateinit var navController: NavController
    private var _pageTitle = ""

    override val viewModelClass: Class<ShowQuestionsViewModel> = ShowQuestionsViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_pre_assessment_test

    override fun initiate() {


        arguments?.apply {
            courseModel = getParcelable(KEY_COURSE_MODEL)!!
            chapterModel = getParcelable(KEY_CHAPTER_MODEL)
            courseQuizModel = getParcelable(KEY_COURSE_QUIZ_MODEL)
            viewModel.testType = getInt(keyTestType, 0)
            AppLog.infoLog("Test Type ---- ${getInt(keyTestType, 0)}")
        }

        viewModel.apply {
            coursesModelLiveData.value = courseModel
            chapterModelLiveData.value = chapterModel
            courseQuizModelModelLiveData.value = courseQuizModel
            _pageTitle = coursesModelLiveData?.value?.courseName!!
            isBackPressLiveData.observe(this@PreAssessmentTestFragment, Observer {
                if (it != null){
                    if (it == true){
                        findNavController().navigateUp()
                        isBackPressLiveData.value = false
                    }
                }
            })
        }

        ProgressLoader(mContext!!, viewModel)
        AlertDialog(mContext!!, viewModel)

        layoutBinding.apply {
            lifecycleOwner = this@PreAssessmentTestFragment
        }
        setHasOptionsMenu(true)

        /*
        * Code to handle the back press
        * */
        layoutBinding.root.apply {
            isFocusableInTouchMode = true
            requestFocus()
            setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                    AppLog.infoLog("keyCode: $keyCode")
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() === KeyEvent.ACTION_UP) {

                            viewModel.apply {
                                setAlertDialogResourceModelMutableLiveData(
                                    BindingUtils.string(R.string.leave_test_message),
                                    BindingUtils.drawable(R.drawable.ic_submit_confirmation)!!,
                                    false,
                                    BindingUtils.string(R.string.yes), {
                                        isAlertDialogShown.postValue(false)
                                        findNavController().navigateUp()
                                    },
                                    BindingUtils.string(R.string.no), {
                                        isAlertDialogShown.postValue(false)
                                    }
                                )
                                isAlertDialogShown.postValue(true)
                            }

                        return true
                    }
                    return false
                }
            })
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                viewModel.apply {

                    setAlertDialogResourceModelMutableLiveData(
                        BindingUtils.string(R.string.leave_test_message),
                        BindingUtils.drawable(R.drawable.ic_submit_confirmation)!!,
                        false,
                        BindingUtils.string(R.string.yes), {
                            isAlertDialogShown.postValue(false)

                            if (testType == 1){
                                findNavController().navigateUp()
                            }else{
                                if (countDownQuizTimer != null)
                                    countDownQuizTimer!!.cancel()
                                val bundle = Bundle()
                                bundle.putParcelable(
                                    KEY_COURSE_MODEL,
                                    courseModel
                                )
                                val navOption =
                                    NavOptions.Builder().setPopUpTo(R.id.coursesFragment, false)
                                        .build()
                                activity?.let {
                                    Navigation.findNavController(
                                        it,
                                        R.id.containerNavHost
                                    ).navigate(R.id.courseDetailFragment, bundle, navOption)
                                }
                            }
                        },
                        BindingUtils.string(R.string.no), {
                            isAlertDialogShown.postValue(false)
                        }
                    )
                    isAlertDialogShown.postValue(true)
                }
                true

            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        setToolbarTitle(_pageTitle)
    }
}
