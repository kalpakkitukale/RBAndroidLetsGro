package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentShowQuestionBinding
import com.ramanbyte.emla.view_model.ShowQuestionsViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.ProgressLoader

/**
 * A simple [Fragment] subclass.
 */
class ShowQuestionFragment : BaseFragment<FragmentShowQuestionBinding, ShowQuestionsViewModel>(
    isActivityParent = false,
    useParent = true
) {

    private var mContext: Context? = null

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



            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

}
