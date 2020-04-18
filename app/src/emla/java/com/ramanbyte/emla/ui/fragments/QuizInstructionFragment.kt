package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.ramanbyte.R
import com.ramanbyte.aws_s3_android.accessor.AppS3Client
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.data_layer.SharedPreferencesDatabase
import com.ramanbyte.databinding.FragmentQuizInstructionBinding
import com.ramanbyte.emla.models.InstructionsModel
import com.ramanbyte.emla.ui.activities.ContainerActivity
import com.ramanbyte.emla.view_model.ShowQuestionsViewModel
import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 14/04/20
 */
class QuizInstructionFragment : BaseFragment<FragmentQuizInstructionBinding, ShowQuestionsViewModel>(isActivityParent = false,useParent = true,isNestedGraph = true) {

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

        setHasOptionsMenu(true)
        setViewModelObservers()
    }

    private fun setViewModelObservers() {
        viewModel.apply {

            coursesModelLiveData.value?.courseImageUrl =
                AppS3Client.createInstance(context!!).getFileAccessUrl(
                    coursesModelLiveData.value?.courseImage ?: KEY_BLANK
                ) ?: ""

            AppLog.infoLog("courseImageUrl ${coursesModelLiveData.value?.courseImageUrl}")

            val width = (activity!!).displayMetrics().widthPixels - (BindingUtils.dimen(R.dimen.dp_5) * 2)

            val layoutParams = layoutBinding.ivQuestion.layoutParams

            layoutParams.height = (width * 0.6).toInt()

            layoutBinding.ivQuestion.layoutParams = layoutParams

            layoutBinding.ivQuestion.layoutParams.apply {
                height = (width * 0.6).toInt()
            }

            //setToolbarTitle(coursesModelLiveData.value?.courseName!!)

            /*
            * Get Server instruction
            * */
            getInstructions()



            /*
            * Show alert when there is no test found
            * */
            isQuizFoundLiveData.observe(this@QuizInstructionFragment, Observer {
                if (it != null){
                    if (it == true){
                        setAlertDialogResourceModelMutableLiveData(
                            BindingUtils.string(R.string.no_test_found_message),
                            BindingUtils.drawable(R.drawable.ic_no_data)!!,
                            true,
                            BindingUtils.string(R.string.strOk), {
                                //backPressed(view!!)
                                view?.findNavController()?.navigate(R.id.action_quizInstructionFragment_to_coursesFragment)
                                //startActivity(ContainerActivity.intent(activity!!))

                                //layoutBinding.root.findNavController()?.navigateUp()
                                //findNavController().navigateUp()
                                isAlertDialogShown.postValue(false)
                            }
                        )
                        isAlertDialogShown.postValue(true)
                        isQuizFoundLiveData.value = false
                    }
                }
            })

            onClickStartQuizLiveData.observe(this@QuizInstructionFragment, Observer {
                if (it != null){
                    if (it == true){

                        /*
                        * set the Quiz start time in Shared Preferences Database
                        * */
                        SharedPreferencesDatabase.setStringPref(SharedPreferencesDatabase.KEY_START_QUIZ_DATE_TIME, DateUtils.getCurrentDateTime(DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS))

                        /*
                        * open or display the All the Best Screen
                        * */
                        findNavController().navigate(R.id.action_allTheBestFragment)
                    }
                }
            })

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                viewModel.apply {

                    setAlertDialogResourceModelMutableLiveData(
                        BindingUtils.string(R.string.leave_test_message),
                        BindingUtils.drawable(R.drawable.ic_submit_confirmation)!!,
                        false,
                        BindingUtils.string(R.string.yes), {
                            isAlertDialogShown.postValue(false)
                            findNavController().navigateUp()
                        },
                        BindingUtils.string(R.string.no), {
                            isAlertDialogShown.postValue(false)
                        }
                    )
                    isAlertDialogShown.postValue(true)
                }
                true

            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }




}
