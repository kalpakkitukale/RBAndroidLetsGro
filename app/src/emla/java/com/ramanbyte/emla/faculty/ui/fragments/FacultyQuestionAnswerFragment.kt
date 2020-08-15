package com.ramanbyte.emla.faculty.ui.fragments

import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentFacultyQuestionAnswerBinding
import com.ramanbyte.emla.faculty.adapter.FacultyQuestionAnswerAdapter
import com.ramanbyte.emla.faculty.models.StudentAskedQuestionsModel
import com.ramanbyte.emla.faculty.view.ReplyEditBottomSheet
import com.ramanbyte.emla.faculty.view_model.FacultyQuestionAnswerViewModel
import com.ramanbyte.emla.models.AskQuestionReplyModel
import com.ramanbyte.utilities.*

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 *
 */
class FacultyQuestionAnswerFragment :
    BaseFragment<FragmentFacultyQuestionAnswerBinding, FacultyQuestionAnswerViewModel>() {

    var questionAnswerAdapter: FacultyQuestionAnswerAdapter? = null
    var questionsModel: StudentAskedQuestionsModel? = null

    override val viewModelClass: Class<FacultyQuestionAnswerViewModel> =
        FacultyQuestionAnswerViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_faculty_question_answer

    override fun initiate() {
        setToolbarTitle(BindingUtils.string(R.string.questions_and_answer))

        //activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        //activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        ProgressLoader(requireContext(), viewModel)
        AlertDialog(requireContext(), viewModel)

        layoutBinding.apply {
            lifecycleOwner = this@FacultyQuestionAnswerFragment
            facultyQuestionAnswerViewModel = viewModel
            noInternet.viewModel = viewModel
            noData.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel


            viewModel.apply {

                arguments?.apply {
                    questionsModel = getParcelable(KEY_QUESTION_MODEL)
                    questionId = questionsModel?.questionId ?: 0
                }

                /*
                * Faculty can be able to reply for the question.
                * */
                facultyReplyVisibility.postValue(if (questionsModel?.facultyReply == KEY_Y) View.VISIBLE else View.GONE)


                /*
                * call API to get conversation data
                * */
                getConversationData()

                questionsReplyListLiveData.observe(this@FacultyQuestionAnswerFragment, Observer {

                    val askQuestionReplyList = ArrayList<AskQuestionReplyModel>()
                    askQuestionReplyList.add(0, AskQuestionReplyModel().apply {
                        createdDateTime = questionsModel?.questionRaisedDateTime
                        answer = questionsModel?.question
                        userName = questionsModel?.studentName
                        userType = KEY_STUDENT
                    })
                    if (it != null) {
                        askQuestionReplyList.addAll(1, it)
                    }

                    setAdapter(askQuestionReplyList)

                })

                enteredReplyLiveData.observe(this@FacultyQuestionAnswerFragment, Observer {
                    if (it != null) {
                        visibilityReplyBtnLiveData.postValue(if (it.isNullOrBlank()) View.GONE else View.VISIBLE)
                    }
                })

                onClickAddReplyLiveData.observe(this@FacultyQuestionAnswerFragment, Observer {
                    if (it != null) {
                        if (it == true) {
                            val enteredReply = enteredReplyLiveData.value.toString()
                            if (enteredReply.isNotBlank()) {
                                insertQuestionsReply(enteredReply)
                            }
                            onClickAddReplyLiveData.value = false
                        }
                    }
                })

                onClickMenuLiveData.observe(this@FacultyQuestionAnswerFragment, Observer {
                    it?.let {
                        val replyEditBottomSheet = ReplyEditBottomSheet.get(it)
                        replyEditBottomSheet.show(
                            childFragmentManager,
                            BindingUtils.string(R.string.reply_edit)
                        )
                        onClickMenuLiveData.value = null
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
            rvQnA.apply {
                questionAnswerAdapter = FacultyQuestionAnswerAdapter()
                questionAnswerAdapter?.apply {
                    layoutManager =
                        LinearLayoutManager(
                            requireContext(),
                            RecyclerView.VERTICAL,
                            false
                        )
                    //(layoutManager as LinearLayoutManager).stackFromEnd = true
                    this.questionReplyList = questionReplyList
                    this.questionAnswerViewModel = viewModel
                    (layoutManager as LinearLayoutManager).isSmoothScrollbarEnabled = true
                    (layoutManager as LinearLayoutManager).scrollToPosition(itemCount - 1)
                    adapter = this
                }
                //adapter?.itemCount!!-1
            }
        }
    }

}
