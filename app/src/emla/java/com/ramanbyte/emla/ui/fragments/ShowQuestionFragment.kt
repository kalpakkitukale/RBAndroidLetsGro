package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentShowQuestionBinding
import com.ramanbyte.emla.adapters.ViewPagerAdapter
import com.ramanbyte.emla.models.QuestionAndAnswerModel
import com.ramanbyte.emla.view_model.ShowQuestionsViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.ProgressLoader

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 14/04/20
 */
class ShowQuestionFragment : BaseFragment<FragmentShowQuestionBinding, ShowQuestionsViewModel>(
    isActivityParent = false,
    useParent = false
) {

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



                questionAndAnswerListLiveData.observe(this@ShowQuestionFragment, Observer {
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


            }
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

                        val facultySubjectPagerFragment =
                            ShowQuestionPagerFragment.newInstance(
                                queCount!!,
                                questionList.size, // totalQueCount
                                questionList[i],
                                it
                            )  // queCount, totalQueCount
                        viewPagerAdapter!!.addFragmentView(facultySubjectPagerFragment, "")
                    }
                })*/

                totalQueCount = questionList.size

                val facultySubjectPagerFragment =
                    ShowQuestionPagerFragment.newInstance(
                        queCount!!,
                        totalQueCount,
                        questionList[i],
                        arrayListOf()
                    )
                viewPagerAdapter!!.addFragmentView(facultySubjectPagerFragment, "")
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
