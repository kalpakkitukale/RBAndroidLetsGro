package com.ramanbyte.emla.ui.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.ramanbyte.R
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.DialogQuizReviewBinding
import com.ramanbyte.databinding.FragmentShowQuestionBinding
import com.ramanbyte.emla.adapters.ViewPagerAdapter
import com.ramanbyte.emla.models.QuestionAndAnswerModel
import com.ramanbyte.emla.models.QuizResultModel
import com.ramanbyte.emla.ui.activities.ContainerActivity
import com.ramanbyte.emla.view_model.ShowQuestionsViewModel
import com.ramanbyte.utilities.*

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 14/04/20
 */
class ShowQuestionFragment : BaseFragment<FragmentShowQuestionBinding, ShowQuestionsViewModel>(isActivityParent = false,useParent = true,isNestedGraph = true) {

    var viewPagerAdapter: ViewPagerAdapter? = null
    private var mContext: Context? = null
    var queCount: Int? = 0
    var totalQueCount: Int = 0

    override val viewModelClass: Class<ShowQuestionsViewModel> = ShowQuestionsViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_show_question

    override fun initiate() {

        //Assigning Loader
        ProgressLoader(mContext!!, viewModel)
        AlertDialog(mContext!!, viewModel)

        layoutBinding.apply {
            lifecycleOwner = this@ShowQuestionFragment

            showQuestionsViewModel = viewModel
            noInternet.viewModel = viewModel
            noData.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel

            /*val value = arguments!!.getString(KEY_QUESTION_IMAGE)
            AppLog.infoLog("value $value")*/

            setViewModelObservers()
        }

    }



    private fun setViewModelObservers() {
        layoutBinding.apply {
            viewModel.apply {

                coursesModelLiveData.value?.courseImageUrl =
                    AppS3Client.createInstance(context!!).getFileAccessUrl(
                        "dev/" + coursesModelLiveData.value?.courseImage ?: KEY_BLANK
                    )
                        ?: ""

                val width =
                    (activity!!).displayMetrics().widthPixels - (BindingUtils.dimen(R.dimen.dp_5) * 2)

                val layoutParams = layoutBinding.imgQue.layoutParams

                layoutParams.height = (width * 0.6).toInt()

                layoutBinding.imgQue.layoutParams = layoutParams

                layoutBinding.imgQue.layoutParams.apply {
                    height = (width * 0.6).toInt()
                }

                setToolbarTitle(coursesModelLiveData.value?.courseName!!)

                questionAndAnswerListLiveData.observe(this@ShowQuestionFragment, Observer {
                    AppLog.infoLog("questionAndAnswerModelLiveDataNir ${it.size}")
                    setPagerList(it)
                })

                questionAndAnswerModelLiveData.observe(this@ShowQuestionFragment, Observer {
                    if (it != null) {
                        AppLog.infoLog("questionAndAnswerModelLiveData")
                    }
                })

                onClickPreviousLiveData.observe(this@ShowQuestionFragment, Observer {
                    if (it != null) {
                        if (it == true) {
                            vpShowQuestions.apply {
                                currentItem -= 1

                                /*if (currentItem != totalQueCount - 1)
                                    btnNext.text = BindingUtils.string(R.string.next)*/
                            }
                        }
                    }
                })

                onClickNextLiveData.observe(this@ShowQuestionFragment, Observer {
                    if (it != null) {
                        if (it == true) {

                            if (btnNext.text.toString() == BindingUtils.string(R.string.submit_test)) {
                                setAlertDialogResourceModelMutableLiveData(
                                    BindingUtils.string(R.string.submit_test_message),
                                    BindingUtils.drawable(R.drawable.ic_submit_confirmation)!!,
                                    false,
                                    BindingUtils.string(R.string.yes), {
                                        isAlertDialogShown.postValue(false)
                                        //isTestSubmited.value = true
                                        submitTest()
                                    },
                                    BindingUtils.string(R.string.no), {
                                        isAlertDialogShown.postValue(false)
                                    }
                                )
                                isAlertDialogShown.postValue(true)
                            }

                            vpShowQuestions.apply {
                                currentItem += 1

                                /*if (currentItem == totalQueCount - 1)
                                btnNext.text = BindingUtils.string(R.string.submit_test)*/
                            }
                        }
                    }
                })

                /*
                * jump to particular question
                * */
                onClickQueNo.observe(this@ShowQuestionFragment, Observer {
                    if (it != null) {
                        if (it >= KEY_QUESTION_START_POSITION) {

                            vpShowQuestions.apply {
                                currentItem = it
                            }
                        }
                    }
                })

                isQuizSubmited.observe(this@ShowQuestionFragment, Observer {
                    if (it != null) {

                        setAlertDialogResourceModelMutableLiveData(
                            BindingUtils.string(R.string.test_completed),
                            BindingUtils.drawable(R.drawable.ic_e_learning),
                            true,
                            BindingUtils.string(R.string.strOk), {

                                if (testType == KEY_QUIZ_TYPE_ASSESSMENT) {

                                        //niraj
                                        /*startActivity(CourseDetailActivity.intent(this).apply {
                                            putExtra(KEY_COURSE_MODEL, coursesModelLiveData.value)
                                        })
                                        finish()*/

                                        val bundle = Bundle()
                                        bundle.putParcelable(
                                            KEY_COURSE_MODEL,
                                            coursesModelLiveData.value
                                        )
                                        view?.findNavController()
                                            ?.navigate(R.id.courseDetailFragment, bundle)

                                        isAlertDialogShown.postValue(false)

                                } else {
                                    isAlertDialogShown.postValue(false)

                                    loaderMessageLiveData.set(BindingUtils.string(R.string.loader_result_message))
                                    isLoaderShowingLiveData.postValue(true)

                                    val handler = Handler()
                                    handler.postDelayed(Runnable {
                                        isLoaderShowingLiveData.postValue(false)

                                        if (it.ispass == KEY_Y) {
                                            if (testType == KEY_QUIZ_TYPE_FORMATIVE) {
                                                showCustomDialogQuizReview(it)
                                            } else {
                                                quizResultAlertDialog(
                                                    it.passMessage!!,
                                                    R.drawable.ic_pass
                                                )
                                            }
                                        } else {
                                            if (testType == KEY_QUIZ_TYPE_FORMATIVE) {
                                                showCustomDialogQuizReview(it)
                                            } else {
                                                quizResultAlertDialog(
                                                    it.failMessage!!,
                                                    R.drawable.ic_fail
                                                )
                                            }
                                        }

                                    }, 5000)
                                }
                            }
                        )
                        isAlertDialogShown.postValue(true)
                    }
                })


            }
        }
    }

    private fun showCustomDialogQuizReview(quizResultModel: QuizResultModel) {
        val dialog = Dialog(mContext!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        //dialog .setContentView(R.layout.dialog_start_visit_layout)
        var dialogQuizReviewBinding: DialogQuizReviewBinding? = null

        dialogQuizReviewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_quiz_review,
            null,
            false
        )

        dialogQuizReviewBinding.apply {
            showQuestionsViewModel = viewModel

            tvScore.append(
                " ${quizResultModel.passPercentage.toString()}% \n\n ${BindingUtils.string(
                    R.string.your_score
                )} ${quizResultModel.obtainedPercentage.toString()}%"
            )
            tvCorrectQuestion.text = quizResultModel.correct.toString()
            tvIncorrectQuestion.text = quizResultModel.incorrect.toString()

            if (quizResultModel.ispass == KEY_Y) {
                tvTitle.text = quizResultModel.passMessage
            } else {
                tvTitle.text = quizResultModel.failMessage
            }

            viewModel.apply {
                onClickDialogCorrectQuestionLiveData.observe(this@ShowQuestionFragment, Observer {
                    if (it != null) {
                        if (it.isNotEmpty()) {
                            if (quizResultModel.correct != 0) {

                                val bundle = Bundle()
                                bundle.putString(KEY_QUESTION_STATUS, it)
                                bundle.putParcelable(KEY_QUIZ_RESULT_MODEL, quizResultModel)
                                bundle.putString(
                                    KEY_COURSE_IMAGE_URL,
                                    coursesModelLiveData.value?.courseImageUrl
                                )
                                view?.findNavController()
                                    ?.navigate(R.id.quizReviewFragment, bundle)

                                // niraj
                                /*startActivity(QuizReviewActivity.intent(activity!!).apply {
                                    putExtra(KEY_QUESTION_STATUS, it)
                                    putExtra(KEY_QUIZ_RESULT_MODEL, quizResultModel)
                                putExtra(KEY_COURSE_IMAGE_URL, coursesModelLiveData.value?.courseImageUrl)
                            })
                            activity!!.finish()
                                dialog.dismiss()
                                onClickDialogCorrectQuestionLiveData.value = KEY_BLANK*/
                            }

                        }
                    }
                })

                onClickDialogIncorrectQuestionLiveData.observe(this@ShowQuestionFragment, Observer {
                    if (it != null) {
                        if (it.isNotEmpty()) {
                            if (quizResultModel.incorrect != 0) {

                                val bundle = Bundle()
                                bundle.putString(KEY_QUESTION_STATUS, it)
                                bundle.putParcelable(KEY_QUIZ_RESULT_MODEL, quizResultModel)
                                bundle.putString(
                                    KEY_COURSE_IMAGE_URL,
                                    coursesModelLiveData.value?.courseImageUrl
                                )
                                view?.findNavController()
                                    ?.navigate(R.id.quizReviewFragment, bundle)

                                //niraj
                                /*startActivity(QuizReviewActivity.intent(activity!!).apply {
                                    putExtra(KEY_QUESTION_STATUS, it)
                                    putExtra(KEY_QUIZ_RESULT_MODEL, quizResultModel)
                                    putExtra(KEY_COURSE_IMAGE_URL, coursesModelLiveData.value?.courseImageUrl)
                                })
                                activity!!.finish()
                                dialog.dismiss()
                                onClickDialogIncorrectQuestionLiveData.value = KEY_BLANK*/
                            }
                        }
                    }
                })

                onClickDialogOkLiveData.observe(this@ShowQuestionFragment, Observer {
                    if (it != null) {
                        if (it.isNotEmpty()) {

                            if (quizResultModel?.correct == 0 && quizResultModel?.incorrect == 0) {
                                /*
                                * not attempted any of question
                                * */
                                setAlertDialogResourceModelMutableLiveData(
                                    BindingUtils.string(R.string.not_attempted_any_question),
                                    BindingUtils.drawable(R.drawable.ic_submit_confirmation)!!,
                                    true,
                                    BindingUtils.string(R.string.strOk), {
                                        isAlertDialogShown.postValue(false)
                                        startActivity(ContainerActivity.intent(activity!!).apply {
                                            flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        })
                                    }
                                )
                                isAlertDialogShown.postValue(true)
                            } else {

                                val bundle = Bundle()
                                if (quizResultModel.correct == 0){
                                    bundle.putString(KEY_QUESTION_STATUS, keyWrong)
                                }else{
                                    bundle.putString(KEY_QUESTION_STATUS, keyCorrect)
                                }
                                bundle.putParcelable(KEY_QUIZ_RESULT_MODEL, quizResultModel)
                                bundle.putString(
                                    KEY_COURSE_IMAGE_URL,
                                    coursesModelLiveData.value?.courseImageUrl
                                )
                                view?.findNavController()
                                    ?.navigate(R.id.quizReviewFragment, bundle)

                                /*
                                * attempted question
                                * */  // niraj
                                /*startActivity(QuizReviewActivity.intent(activity!!).apply {
                                    if (quizResultModel.correct == 0){
                                        putExtra(KEY_QUESTION_STATUS, keyWrong)
                                    }else{
                                        putExtra(KEY_QUESTION_STATUS, keyCorrect)
                                    }
                                    putExtra(KEY_QUIZ_RESULT_MODEL, quizResultModel)
                                    putExtra(KEY_COURSE_IMAGE_URL, coursesModelLiveData.value?.courseImageUrl)
                                })
                                activity!!.finish()*/
                            }

                            dialog.dismiss()
                            onClickDialogOkLiveData.value = KEY_BLANK
                        }
                    }
                })
            }
        }
        dialog.setContentView(dialogQuizReviewBinding.root);
        dialog.show()
    }

    private fun quizResultAlertDialog(resultText: String, drawable: Int) {
        viewModel.apply {
            setAlertDialogResourceModelMutableLiveData(
                resultText,
                BindingUtils.drawable(drawable)!!,
                true,
                BindingUtils.string(R.string.strOk), {
                    activity?.apply {
                        /*startActivity(
                            CourseDetailActivity.intent(this).apply {
                                putExtra(KEY_COURSE_MODEL, coursesModelLiveData.value)
                                putExtra(KEY_CHAPTER_MODEL, chapterModelLiveData.value)
                            }
                        )
                        activity!!.finish()*/

                        startActivity(ContainerActivity.intent(activity!!).apply {
                            flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        isAlertDialogShown.postValue(false)
                    }
                }
            )
            isAlertDialogShown.postValue(true)
        }
    }

    private fun setPagerList(questionList: ArrayList<QuestionAndAnswerModel>) {

        viewPagerAdapter = ViewPagerAdapter(
            childFragmentManager,
            FragmentStatePagerAdapter.POSITION_UNCHANGED
        )

        for (i in 0 until questionList.size) {

            queCount = i + 1

            // get question realted options
            viewModel.apply {

                //getOptions(questionList[i].id)

                /*optionsListLiveData.observe(this@ShowQuestionFragment, Observer {
                    if (it != null) {
                        //setPagerList(it)
                        //AppLog.infoLog("optionsListLiveData ${it[0].options}")
                        //optionsList = it

                        val questionPagerFragment =
                            ShowQuestionPagerFragment.newInstance(
                                queCount!!,
                                questionList.size, // totalQueCount
                                questionList[i],
                                it
                            )  // queCount, totalQueCount
                        viewPagerAdapter!!.addFragmentView(questionPagerFragment, "")
                    }
                })*/

                totalQueCount = questionList.size

                val questionPagerFragment = ShowQuestionPagerFragment(viewModel,queCount!!, totalQueCount, questionList[i], arrayListOf())
                viewPagerAdapter!!.addFragmentView(questionPagerFragment, "")
            }
        }

        layoutBinding.apply {
            vpShowQuestions.apply {
                adapter = viewPagerAdapter
                addOnPageChangeListener(onPageChangeListener)
                currentItem = 0
            }
        }

    }

    private val onPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            AppLog.infoLog("nnnnnnnn $position")
            layoutBinding.btnPrevious.apply {
                if (position == 0) visibility = View.GONE else visibility = View.VISIBLE
            }
        }

        override fun onPageSelected(position: Int) {
            try {

                AppLog.infoLog("onPageSelected $position")

                layoutBinding.btnNext.apply {
                    if (position == totalQueCount - 1) {
                        text = BindingUtils.string(R.string.submit_test)
                    } else
                        text = BindingUtils.string(R.string.next)

                    /*optionsList[position].apply {
                        AppLog.infoLog("localDBAns ${selectedOption} $iscorrect $question_Id")
                        //updateOption(rbAnswer.text.toString(), iscorrect, question_Id )
                    }*/

                }

                /*setOnViewPagerPageChange(viewPagerAdapter!!.getItem(position) as ShowQuestionPagerFragment)

                if (getOnViewPagerPageChange() != null) {
                    val subjectModel = listSubjects[position]
                    getOnViewPagerPageChange()!!.onPagerPageChange(position, mContext, subjectModel.subjectId,
                        subjectModel.subjectName, subjectModel.batchId, subjectModel.semesterId,
                        subjectModel.subjectCode, subjectModel.subjectType, subjectModel.subjectTypeBgColor)
                }*/
                AppLog.infoLog("")
            } catch (e: NullPointerException) {
                e.printStackTrace()
                AppLog.errorLog(e.message!!)
                //setLayoutVisibility(View.GONE, View.GONE, View.VISIBLE, BindingUtils.string(R.string.server_error_msg))

            } catch (e: ArrayIndexOutOfBoundsException) {
                e.printStackTrace()
                AppLog.errorLog(e.message!!)
                //setLayoutVisibility(View.GONE, View.GONE, View.VISIBLE, BindingUtils.string(R.string.server_error_msg))

            } catch (e: Exception) {
                e.printStackTrace()
                AppLog.errorLog(e.message!!)
                //setLayoutVisibility(View.GONE, View.GONE, View.VISIBLE, BindingUtils.string(R.string.server_error_msg))
            }
        }

        override fun onPageScrollStateChanged(state: Int) {

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}
