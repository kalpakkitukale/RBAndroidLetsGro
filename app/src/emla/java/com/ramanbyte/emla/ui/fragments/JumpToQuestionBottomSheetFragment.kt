package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ramanbyte.R
import com.ramanbyte.databinding.JumpToQuestionBinding
import com.ramanbyte.emla.adapters.JumpToQueAdapter
import com.ramanbyte.emla.models.QuestionAndAnswerModel
import com.ramanbyte.emla.view_model.ShowQuestionsViewModel
import com.ramanbyte.utilities.*


class JumpToQuestionBottomSheetFragment() : BottomSheetDialogFragment() {

    var mContext: Context? = null
    var jumpToQuestionBinding: JumpToQuestionBinding? = null
    var jumpToQueAdapter: JumpToQueAdapter? = null
    var questionModelDataList = ArrayList<QuestionAndAnswerModel>()
    private var viewModel: ShowQuestionsViewModel? = null
    var questionId = 0

    companion object {

        fun get(
            questionId: Int
        ): JumpToQuestionBottomSheetFragment =
            JumpToQuestionBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putInt(keyQuestionId, questionId)
                }
            }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        jumpToQuestionBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.jump_to_question,
            container,
            false
        )

        if (arguments != null) {
            questionId = arguments?.getInt(keyQuestionId, 0) ?: 0
        }

        initComponents()

        return jumpToQuestionBinding?.root

    }

    /*get context when view is attached to activity*/
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // prevent dismissal of bottomsheetdialogfragment on touch outside
        /*dialog?.requestWindowFeature(DialogFragment.STYLE_NO_TITLE)
        isCancelable = false*/

        //dialog?.window?.setDimAmount(0.0f)
    }

    override fun onStart() {
        super.onStart()

        val window = dialog!!.window
        val windowParams = window!!.attributes
        windowParams.dimAmount = 0.50f
        windowParams.flags = windowParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        window.attributes = windowParams
    }

    private fun initComponents() {

        requireParentFragment()?.requireParentFragment().apply {
            viewModel = ViewModelProvider(this).get(ShowQuestionsViewModel::class.java)
        }

        //Assigning Loader
        ProgressLoader(mContext!!, viewModel!!)
        AlertDialog(mContext!!, viewModel!!)

        jumpToQuestionBinding?.apply {
            lifecycleOwner = this@JumpToQuestionBottomSheetFragment
            showQuestionsViewModel = this@JumpToQuestionBottomSheetFragment.viewModel

            /*
            * Set the Adapter
            * */
            rvJumpToQue.apply {

                jumpToQueAdapter = JumpToQueAdapter(
                    viewModel?.getAllQuestions() ?: arrayListOf(),
                    viewModel?.getTotalQuestionCount() ?: 0,
                    this@JumpToQuestionBottomSheetFragment.questionId
                )
                layoutManager = GridLayoutManager(mContext, KEY_QUESTION_IN_GRID)
                jumpToQueAdapter!!.showQuestionsViewModel = viewModel
                adapter = jumpToQueAdapter
            }
        }

        setViewModelObservers()

    }

    private fun setViewModelObservers() {

        viewModel?.apply {

            isTestSubmited.observe(this@JumpToQuestionBottomSheetFragment, Observer {
                if (it != null) {
                    if (it == true) {
                        dialog?.cancel()
                        isTestSubmited.value = false
                        isTestSubmitedFormJBS.value = true
                        //submitTest()
                    }
                }
            })


            onClickQueNo.observe(this@JumpToQuestionBottomSheetFragment, Observer {
                if (it != null) {
                    if (it >= KEY_QUESTION_START_POSITION) {
                        dialog?.cancel()
                        onClickQueNo.value = -1
                    }
                }
            })

            onClickCloseLiveData.observe(this@JumpToQuestionBottomSheetFragment, Observer {
                if (it != null) {
                    if (it == true) {
                        dismiss()
                        onClickCloseLiveData.value = false
                    }
                }
            })
        }
    }

}
