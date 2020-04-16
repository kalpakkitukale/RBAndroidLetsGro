package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.CardShowQuestionsBinding
import com.ramanbyte.emla.adapters.AnswerAdapter
import com.ramanbyte.emla.data_layer.room.entities.AnswerEntity
import com.ramanbyte.emla.models.OptionsModel
import com.ramanbyte.emla.models.QuestionAndAnswerModel
import com.ramanbyte.emla.view_model.ShowQuestionsViewModel
import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 14/04/20
 */
class ShowQuestionPagerFragment : BaseFragment<CardShowQuestionsBinding, ShowQuestionsViewModel>(isActivityParent = false,
    useParent = true){

    var mContext: Context? = null
    var answerAdapter: AnswerAdapter? = null
    var queCount: Int? = null
    var totalQueCount: Int? = null
    var questionAndAnswerModelData = QuestionAndAnswerModel()
    var optionsModelDataList = ArrayList<OptionsModel>()
    var jumpToQuestionBottomSheetFragment: JumpToQuestionBottomSheetFragment? = null

    override val viewModelClass: Class<ShowQuestionsViewModel> = ShowQuestionsViewModel::class.java

    override fun layoutId(): Int = R.layout.card_show_questions

    override fun initiate() {
        setData()
        AppLog.infoLog("Initiate::Page::$queCount")
        layoutBinding.apply {
            lifecycleOwner = this@ShowQuestionPagerFragment
            cardShowQuestionsViewModel = viewModel
            questionAndAnswerModel = questionAndAnswerModelData
        }
        setRecyclerAdapter()
        setViewModelObservers()
    }

    companion object {
        fun newInstance(
            queCount: Int,
            totalQueCount: Int,
            questionAndAnswerModel: QuestionAndAnswerModel,
            optionsModel: ArrayList<OptionsModel>
        ): ShowQuestionPagerFragment {  // queCount: Int, totalQueCount: Int
            val showQuestionPagerFragment = ShowQuestionPagerFragment()
            val bundle = Bundle()
            bundle.putInt(KEY_QUE_COUNT, queCount)
            bundle.putInt(KEY_TOTAL_QUE_COUNT, totalQueCount)
            bundle.putParcelable(KEY_QUESTION_MODEL, questionAndAnswerModel)
            bundle.putParcelableArrayList(KEY_OPTIONS_MODEL, optionsModel)
            showQuestionPagerFragment.arguments = bundle

            return showQuestionPagerFragment
        }
    }



    private fun setRecyclerAdapter() {
        layoutBinding.apply {


            wvQuestion.settings?.apply {

                builtInZoomControls = false
                loadWithOverviewMode = true
                //useWideViewPort = true
                javaScriptEnabled = true
                //domStorageEnabled = true
//                layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
            }

            wvQuestion.apply {

                isNestedScrollingEnabled = false

//                setInitialScale(1)
            }

            wvQuestion.webChromeClient = WebChromeClient()

            rvAnswer?.apply {
                //isNestedScrollingEnabled = true

                val optionsList = viewModel.getOptions(
                    this@ShowQuestionPagerFragment.questionAndAnswerModelData?.id ?: 0
                )

                answerAdapter = AnswerAdapter(
                    optionsList ?: arrayListOf(),
                    questionAndAnswerModel
                )
                layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                answerAdapter?.showQuestionsViewModel = viewModel
                adapter = answerAdapter

                //--------------------------  Clear the selected options ----------------------------
                //answerAdapter.lastSelectedPosition = optionList?.indexOfFirst { it.isChecked }

                questionAndAnswerModelData?.clearOptions = {
                    viewModel.apply {
                        deleteQuestionRelatedOptionLB(questionAndAnswerModel?.id ?: 0)

                        insertOptionLB(AnswerEntity().apply {
                            this.quiz_Id = questionAndAnswerModel?.quizid ?: 0
                            this.question_Id = questionAndAnswerModel?.id ?: 0
                            this.options = KEY_BLANK
                            this.iscorrect = KEY_SKIP
                            this.answer = 0
                            this.createdDate = DateUtils.getCurrentDateTime(
                                DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS
                            )
                        })

                        answerAdapter?.apply {

                            optionList.apply {
                                forEach { it.isChecked = false }
                            }

                            AppLog.infoLog("que_id ${questionAndAnswerModel?.id ?: 0}")
                            notifyDataSetChanged()
                        }
                    }
                }

            }
        }
    }

    private fun setData() {
        layoutBinding.apply {
            if (arguments != null) {
                queCount = arguments?.getInt(KEY_QUE_COUNT)
                totalQueCount = arguments?.getInt(KEY_TOTAL_QUE_COUNT)
                questionAndAnswerModelData = arguments?.getParcelable(KEY_QUESTION_MODEL)!!
                optionsModelDataList = arguments?.getParcelableArrayList(KEY_OPTIONS_MODEL)!!

                /*
                *  Set current question and total question count
                * */

                var queCountString: String = KEY_BLANK
                if (queCount.toString().length == 1)
                    queCountString = "0" + queCount.toString()
                else
                    queCountString = queCount.toString()

                val spannableStringQuestionNo =
                    SpannableString(queCountString + " of " + totalQueCount.toString())
                val foregroundSpan = ForegroundColorSpan(BindingUtils.color(R.color.colorTeal))
                val foregroundSpanTotalQuestion =
                    ForegroundColorSpan(BindingUtils.color(R.color.colorLightGray))
                val backgroundSpan = BackgroundColorSpan(Color.YELLOW)
                spannableStringQuestionNo.setSpan(
                    foregroundSpan,
                    0,
                    2,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableStringQuestionNo.setSpan(
                    foregroundSpanTotalQuestion,
                    2,
                    spannableStringQuestionNo.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableStringQuestionNo.setSpan(RelativeSizeSpan(2f), 0, 2, 0)
                //spannableString.setSpan(backgroundSpan, 3, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                tvJumpToQuestion.text = spannableStringQuestionNo


                /*
                *  Set the marks
                * */
                val marksStr = questionAndAnswerModelData.marks.toString()
                val spannableStringQuestionMarks =
                    SpannableString(
                        "$marksStr ${BindingUtils.string(R.string.marks)}"
                    )
                val foregroundSpanQuestionMarks =
                    ForegroundColorSpan(BindingUtils.color(R.color.colorTeal))
                if (marksStr.length == 3) {
                    spannableStringQuestionMarks.setSpan(
                        foregroundSpanQuestionMarks,
                        0,
                        3,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    spannableStringQuestionMarks.setSpan(
                        foregroundSpanQuestionMarks,
                        0,
                        4,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                tvQuestionMarks.text = spannableStringQuestionMarks
            }
        }
    }

    private fun setViewModelObservers() {
        viewModel.apply {

            onClickClear.observe(this@ShowQuestionPagerFragment, Observer {
                if (it != null) {
                    if (it == true) {
                        answerAdapter?.clearAnswer()
                        onClickClear.value = false
                    }
                }
            })

            isJumpToQuestionBS.observe(this@ShowQuestionPagerFragment, Observer {
                if (it != null) {
                    if (it == true) {
                        // open bottom sheet
                        if (jumpToQuestionBottomSheetFragment == null)
                            jumpToQuestionBottomSheetFragment =
                                JumpToQuestionBottomSheetFragment.get(questionAndAnswerModelData.id)

                        jumpToQuestionBottomSheetFragment?.show(
                            childFragmentManager,
                            getString(R.string.jump_to_question)
                        )
                        isJumpToQuestionBS.value = null
                    }
                    isJumpToQuestionBS.value = null
                }
            })
        }
    }


    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
        AppLog.infoLog("OnDetach::Page::$queCount")
    }

}
