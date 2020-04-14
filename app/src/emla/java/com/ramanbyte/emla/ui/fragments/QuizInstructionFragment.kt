package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.data_layer.SharedPreferencesDatabase
import com.ramanbyte.databinding.FragmentQuizInstructionBinding
import com.ramanbyte.emla.models.InstructionsModel
import com.ramanbyte.emla.view_model.ShowQuestionsViewModel
import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 14/04/20
 */
class QuizInstructionFragment : BaseFragment<FragmentQuizInstructionBinding, ShowQuestionsViewModel>() {

    private var mContext: Context? = null
    private var instructionsModel = InstructionsModel()

    override val viewModelClass: Class<ShowQuestionsViewModel> = ShowQuestionsViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_quiz_instruction

    override fun initiate() {

        ProgressLoader(mContext!!,viewModel)
        AlertDialog(mContext!!,viewModel)

        layoutBinding.apply {
            lifecycleOwner = this@QuizInstructionFragment
            instructionShowQuestionsViewModel = viewModel
            instructionsModel = this@QuizInstructionFragment.instructionsModel
        }

        setViewModelObservers()
    }

    private fun setViewModelObservers() {
        viewModel.apply {

            coursesModelLiveData.value?.courseImageUrl = AppS3Client.createInstance(context!!).getFileAccessUrl("dev/"+coursesModelLiveData.value?.courseImage?: KEY_BLANK) ?: ""

            AppLog.infoLog("courseImageUrl ${coursesModelLiveData.value?.courseImageUrl}")

            val width = (activity!!).displayMetrics().widthPixels - (BindingUtils.dimen(R.dimen.dp_5) * 2)

            val layoutParams = layoutBinding.ivQuestion.layoutParams

            layoutParams.height = (width * 0.6).toInt()

            layoutBinding.ivQuestion.layoutParams = layoutParams

            layoutBinding.ivQuestion.layoutParams.apply {
                height = (width * 0.6).toInt()
            }

            /*
            * Get Server instruction
            * */
            getInstructions()

//            onClickStartQuizLiveData.observe(this@QuizInstructionFragment, Observer {
//                if (it != null){
//                    if (it == true){
//
//                        /*
//                        * set the Quiz start time in Shared Preferences Database
//                        * */
//                        SharedPreferencesDatabase.setStringPref(SharedPreferencesDatabase.KEY_START_QUIZ_DATE_TIME, DateUtils.getCurrentDateTime(DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS))
//
//                        /*
//                        * open or display the All the Best Screen
//                        * */
//                        val fragment = AllTheBestFragment()
//                        val transaction : FragmentTransaction = fragmentManager!!.beginTransaction()
//                        transaction.replace(R.id.frameLayout, fragment)
//                        transaction.commit()
//                    }
//                }
//            })

        }
    }

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }
}
