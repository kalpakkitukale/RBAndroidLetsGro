package com.ramanbyte.emla.view

import android.app.Activity
import android.content.DialogInterface
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.ramanbyte.BaseAppController
import com.ramanbyte.R
import com.ramanbyte.base.BaseDialogFragment
import com.ramanbyte.databinding.DialogLoginWithClassroomplusBinding
import com.ramanbyte.emla.faculty.ui.activities.FacultyContainerActivity
import com.ramanbyte.emla.ui.activities.ContainerActivity
import com.ramanbyte.emla.view_model.LoginViewModel
import com.ramanbyte.utilities.AlertDialog
import com.ramanbyte.utilities.KEY_FACULTY
import com.ramanbyte.utilities.KEY_STUDENT
import com.ramanbyte.utilities.ProgressLoader

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 12/2/21
 */
class LoginWithClassroomDialog :
    BaseDialogFragment<DialogLoginWithClassroomplusBinding, LoginViewModel>() {

    override val viewModelClass: Class<LoginViewModel>
        get() = LoginViewModel::class.java

    override fun layoutId(): Int = R.layout.dialog_login_with_classroomplus

    override fun initiate() {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.capsule_without_corner)
        layoutBinding.apply {
            lifecycleOwner = this@LoginWithClassroomDialog
            ProgressLoader(requireContext(), viewModel)
            AlertDialog(requireContext(), viewModel)

            loginViewModel = viewModel
            ivClose.setOnClickListener { this@LoginWithClassroomDialog.dismissAllowingStateLoss() }
        }
        setViewModelOps()
    }

    private fun setViewModelOps() {
        viewModel.apply {

            userModelLiveData.observe(this@LoginWithClassroomDialog, Observer {
                it?.let {
                    if (it.userType.equals(KEY_STUDENT, true)) {

                        callWorkerToMangeUserDevice()
                        if (!viewModel.isUserActive()) {
                            findNavController().navigate(R.id.action_loginFragment_to_learnerProfileFragment)
                        } else {
                            startActivity(ContainerActivity.intent(requireActivity()))
                            Activity().finish()
                            BaseAppController.setEnterPageAnimation(requireActivity())
                        }

                    } else if (it.userType.equals(KEY_FACULTY, true)) {
                        callWorkerToMangeUserDevice()
                        startActivity(FacultyContainerActivity.intent(requireActivity()))
                        Activity().finish()
                        BaseAppController.setEnterPageAnimation(requireActivity())
                    }
                    this@LoginWithClassroomDialog.dismissAllowingStateLoss()
                    userModelLiveData.value = null
                }

            })
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        viewModel.apply {
            ThreadUtils.runOnUiThread {
                loginRequestValidation.isDataResetting = true
                userLoginRequestLiveData.value?.apply {
                    this.emailId = ""
                    this.password = ""
                }
            }
            loginRequestValidation.isDataResetting = false
        }
        super.onDismiss(dialog)
    }
}