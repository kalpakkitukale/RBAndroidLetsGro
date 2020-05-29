package com.ramanbyte.emla.faculty.ui.fragments

import android.content.Context
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentFacultyQuestionAnswerBinding
import com.ramanbyte.emla.faculty.adapter.FacultyQuestionAnswerAdapter
import com.ramanbyte.emla.faculty.models.StudentAskedQuestionsModel
import com.ramanbyte.emla.faculty.view_model.FacultyQuestionAnswerViewModel
import com.ramanbyte.emla.models.AskQuestionReplyModel
import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.DateUtils.DATE_TIME_PATTERN
import com.ramanbyte.utilities.DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS
import com.ramanbyte.utilities.DateUtils.getDisplayDateFromDate
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * A simple [Fragment] subclass.
 */
class FacultyQuestionAnswerFragment :
    BaseFragment<FragmentFacultyQuestionAnswerBinding, FacultyQuestionAnswerViewModel>() {

    var mContext: Context? = null
    var questionAnswerAdapter: FacultyQuestionAnswerAdapter? = null
    var questionsModel: StudentAskedQuestionsModel? = null

    override val viewModelClass: Class<FacultyQuestionAnswerViewModel> =
        FacultyQuestionAnswerViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_faculty_question_answer

    override fun initiate() {
        setToolbarTitle(BindingUtils.string(R.string.questions_and_answer))

        ProgressLoader(mContext!!, viewModel)
        AlertDialog(mContext!!, viewModel)

        layoutBinding.apply {
            lifecycleOwner = this@FacultyQuestionAnswerFragment
            facultyQuestionAnswerViewModel = viewModel
            noInternet.viewModel = viewModel
            noData.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel


            viewModel.apply {

                arguments?.apply {
                    questionsModel = getParcelable(KEY_QUESTION_MODEL)!!
                    questionId = questionsModel?.questionId!!
                }

                /*
                * Faculty can be able to reply for the question.
                * */
                /*if (questionsModel?.facultyReply == KEY_Y)
                    etAddReply.visibility = View.VISIBLE
                else
                    etAddReply.visibility = View.GONE*/

                /*
                * call API to get conversation data
                * */
                getConversationData()

                questionsReplyListLiveData.observe(this@FacultyQuestionAnswerFragment, Observer {

                    val askQuestionReplyList = ArrayList<AskQuestionReplyModel>()
                    askQuestionReplyList.add(0, AskQuestionReplyModel().apply {
                        createdDateTime = questionsModel?.questionRaisedDateTime!! /*getDisplayDateFromDate(
                            questionsModel?.questionRaisedDateTime!!,
                            DATE_TIME_PATTERN,
                            DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS
                        )*/
                        answer = questionsModel?.question!!
                        userName = questionsModel?.studentName!!
                        userType = KEY_STUDENT
                    })
                    if (it != null) {
                        askQuestionReplyList.addAll(1, it)
                    }

                    setAdapter(askQuestionReplyList)

                })

                enteredReplyLiveData.observe(this@FacultyQuestionAnswerFragment, Observer {
                    if (it != null) {
                        if (it.isNullOrBlank())
                            visibilityReplyBtnLiveData.value = View.GONE
                        else
                            visibilityReplyBtnLiveData.value = View.VISIBLE
                    }
                })

                onClickAddReplyLiveData.observe(this@FacultyQuestionAnswerFragment, Observer {
                    if (it != null) {
                        if (it == true) {
                            val enteredReply = enteredReplyLiveData.value.toString()
                            if (enteredReply.isNotBlank()) {
                                insertQuestionsReply(enteredReply)
                            } else {
                                AppLog.infoLog("Blank Reply not added.")
                            }
                            onClickAddReplyLiveData.value = false
                        }
                    }
                })
            }


        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setAdapter(questionReplyList: ArrayList<AskQuestionReplyModel>) {
        layoutBinding.apply {
            rvCoursesFragment?.apply {
                questionAnswerAdapter = FacultyQuestionAnswerAdapter()
                questionAnswerAdapter?.apply {
                    layoutManager =
                        LinearLayoutManager(
                            mContext,
                            RecyclerView.VERTICAL,
                            false
                        )
                    //(layoutManager as LinearLayoutManager).stackFromEnd = true
                    this.questionReplyList = questionReplyList
                    (layoutManager as LinearLayoutManager).isSmoothScrollbarEnabled = true
                    (layoutManager as LinearLayoutManager).scrollToPosition(itemCount - 1)
                    adapter = this
                }
                //adapter?.itemCount!!-1
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

}
