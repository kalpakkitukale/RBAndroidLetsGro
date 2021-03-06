package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.os.Handler
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentAllTheBestBinding
import com.ramanbyte.emla.view_model.ShowQuestionsViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.KEY_QUIZ_TYPE_FORMATIVE
import com.ramanbyte.utilities.ProgressLoader

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 14/04/20
 */
class AllTheBestFragment : BaseFragment<FragmentAllTheBestBinding, ShowQuestionsViewModel>(isActivityParent = false,useParent = true,isNestedGraph = true) {

    private var mContext: Context? = null

    override val viewModelClass: Class<ShowQuestionsViewModel> = ShowQuestionsViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_all_the_best

    override fun initiate() {

        ProgressLoader(mContext!!, viewModel)
        AlertDialog(mContext!!, viewModel)

        layoutBinding.apply {
            lifecycleOwner = this@AllTheBestFragment
            allTheBestViewModel = viewModel
        }

        viewModel.apply {

            if (testType == KEY_QUIZ_TYPE_FORMATIVE)
                getQuestionsByByTopic()
            else
                getQuestionsByCourse()

            questionAndAnswerListLiveData.observe(this@AllTheBestFragment, Observer {
                if (it != null) {
                    Handler().postDelayed({
                        startTest()
                    }, 1000)
                } else {
                    AppLog.infoLog("no_question_available")
                }
            })
        }
    }

    private fun startTest() {
        findNavController().navigate(R.id.action_showQuestionFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

}
