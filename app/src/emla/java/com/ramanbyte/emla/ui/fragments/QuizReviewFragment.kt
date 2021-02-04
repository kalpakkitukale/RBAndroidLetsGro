package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentQuizReviewBinding
import com.ramanbyte.emla.adapters.QuizReviewQuestionsAdapter
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.QuizResultModel
import com.ramanbyte.emla.view_model.ShowQuestionsViewModel
import com.ramanbyte.utilities.*


class QuizReviewFragment : BaseFragment<FragmentQuizReviewBinding, ShowQuestionsViewModel>() {

    var quizReviewQuestionsAdapter: QuizReviewQuestionsAdapter? = null
    var questionStatus: String? = KEY_BLANK
    var courseImageUrl: String? = KEY_BLANK
    var courseModel: CoursesModel? = null
    var quizResultModel: QuizResultModel? = null
    var mContext: Context? = null

    override val viewModelClass: Class<ShowQuestionsViewModel> = ShowQuestionsViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_quiz_review

    override fun initiate() {

        arguments?.apply {
            questionStatus = getString(KEY_QUESTION_STATUS)!!
            quizResultModel = getParcelable(KEY_QUIZ_RESULT_MODEL)
            courseModel = getParcelable(KEY_COURSE_MODEL)
            courseImageUrl = courseModel?.courseImageUrl
        }

        ProgressLoader(mContext!!, viewModel)
        AlertDialog(mContext!!, viewModel)

        viewModel.apply {

            layoutBinding.apply {
                lifecycleOwner = this@QuizReviewFragment
                quizReviewViewModel = viewModel
                quizResultModel = this@QuizReviewFragment.quizResultModel
                noInternet.viewModel = viewModel
                noData.viewModel = viewModel
                somethingWentWrong.viewModel = viewModel
                courseImageUrl = this@QuizReviewFragment.courseImageUrl

                quizReviewQuestionsAdapter = QuizReviewQuestionsAdapter()
                rvQuizReview.apply {
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    quizReviewQuestionsAdapter?.quizReviewViewModel = viewModel
                    adapter = quizReviewQuestionsAdapter
                }

                // call API
                getQuestionForQuizReview(
                    this@QuizReviewFragment.quizResultModel?.quizid!!,
                    questionStatus!!,
                    this@QuizReviewFragment.quizResultModel?.attemptstatus!!
                )

                if (quizResultModel?.correct == 0){
                    rbCorrect.visibility = View.GONE

                    if (quizResultModel?.incorrect == 0){
                        rbWrong.visibility = View.GONE
                    }else{
                        rbWrong.visibility = View.VISIBLE
                        // wrong
                    }

                }else{
                    rbCorrect.visibility = View.VISIBLE
                    // correct

                    if (quizResultModel?.incorrect == 0){
                        rbWrong.visibility = View.GONE
                    }else{
                        rbWrong.visibility = View.VISIBLE
                    }
                }

                if (questionStatus == keyCorrect) {
                    rbCorrect.isChecked = true
                    rbWrong.isChecked = false
                    tabTypeSelectedLiveData.value = keyCorrect
                } else {
                    rbCorrect.isChecked = false
                    rbWrong.isChecked = true
                    tabTypeSelectedLiveData.value = keyWrong
                }



                questionForQuizReviewPagedList()?.observe(
                    this@QuizReviewFragment,
                    androidx.lifecycle.Observer {
                        quizReviewQuestionsAdapter?.apply { submitList(it) }
                    })

            }

            /*
            * Set Title image
            * */
            //coursesModelLiveData.value?.courseImageUrl = courseImageUrl

            val width = (activity!!).displayMetrics().widthPixels - (BindingUtils.dimen(R.dimen.dp_5) * 2)

            val layoutParams = layoutBinding.ivQuestion.layoutParams

            layoutParams.height = (width * 0.6).toInt()

            layoutBinding.ivQuestion.layoutParams = layoutParams

            layoutBinding.ivQuestion.layoutParams.apply {
                height = (width * 0.6).toInt()
            }


            onClickTabButtonLiveData.observe(this@QuizReviewFragment, Observer {
                if (it != null){
                    when (it) {
                        keyCorrect -> {
                            if (tabTypeSelectedLiveData.value == keyWrong) {
                                //if (it != questionStatus){
                                tabTypeSelectedLiveData.value = keyCorrect
                                quizRepository.retryQuestionForQuizReview(
                                    quizResultModel?.quizid!!,
                                    keyCorrect,
                                    quizResultModel?.attemptstatus!!
                                )
                                //}
                            }
                        }
                        keyWrong -> {
                            if (tabTypeSelectedLiveData.value == keyCorrect) {
                                tabTypeSelectedLiveData.value = keyWrong
                                quizRepository.retryQuestionForQuizReview(
                                    quizResultModel?.quizid!!,
                                    keyWrong,
                                    quizResultModel?.attemptstatus!!
                                )
                            }
                        }
                    }
                }
            })
        }


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
                                false,
                                BindingUtils.string(R.string.yes),
                                {
                                    isAlertDialogShown.postValue(false)

                                    val bundle = Bundle()
                                    bundle.putParcelable(
                                        KEY_COURSE_MODEL,
                                        courseModel
                                    )
                                    val navOption = NavOptions.Builder().setPopUpTo(R.id.coursesFragment, false).build()
                                    activity?.let { Navigation.findNavController(it,R.id.containerNavHost).navigate(R.id.courseDetailFragment, bundle, navOption) }
                                }, BindingUtils.string(R.string.no),
                                {
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


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

}
