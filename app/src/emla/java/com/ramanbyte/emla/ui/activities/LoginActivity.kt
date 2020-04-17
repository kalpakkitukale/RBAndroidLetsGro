package com.ramanbyte.emla.ui.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.ramanbyte.BaseAppController
import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityLoginBinding
import com.ramanbyte.databinding.PledgeDialogBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.view_model.LoginViewModel
import com.ramanbyte.services.MangeUserDevice
import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.StaticMethodUtilitiesKtx.setIMEINumber

/**
 * @AddedBy Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(authModuleDependency) {
    override val viewModelClass: Class<LoginViewModel> = LoginViewModel::class.java

    override fun layoutId(): Int = R.layout.activity_login

    override fun initiate() {
        setListeners()
        makeStatusBarTransparent()
        layoutBinding.apply {
            lifecycleOwner = this@LoginActivity
            loginViewModel = viewModel
            ProgressLoader(this@LoginActivity, viewModel)
            AlertDialog(this@LoginActivity, viewModel)
        }
        setViewModelOps()
    }

    private fun setViewModelOps() {
        viewModel.apply {
            if (userEntity != null)
                if (userEntity?.loggedId != KEY_Y) {
                    showPledgeDialog()
                }
            userModelLiveData.observe(this@LoginActivity, Observer {
                it?.let {
                    if (it.userType.equals(KEY_STUDENT, true)) {

                        if (it.loggedId != KEY_Y)
                            showPledgeDialog()
                        else {
                            callWorkerToMangeUserDevice()
                            startActivity(ContainerActivity.intent(this@LoginActivity))
                            finish()
                            BaseAppController.setEnterPageAnimation(this@LoginActivity)
                        }

                    }
                }

            })

        }
    }


    private fun callWorkerToMangeUserDevice() {
        val manageUserDeviceData = Data.Builder()
            .putInt(KEY_LOGIN_LOGOUT_STATUS, 1)
            .build()
        val simpleRequest = OneTimeWorkRequest.Builder(MangeUserDevice::class.java)
            .setInputData(manageUserDeviceData)
            .build()
        WorkManager.getInstance(this@LoginActivity).enqueue(simpleRequest)
    }

    private fun showPledgeDialog() {
        val dialog = Dialog(this)
        val pledgeBinding: PledgeDialogBinding =
            PledgeDialogBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(pledgeBinding.root)
        dialog.window?.setDimAmount(0.2F)
        pledgeBinding.loginViewModel = viewModel
        dialog.setCanceledOnTouchOutside(false)

        viewModel.apply {
            navigateToNextScreen.observe(this@LoginActivity, Observer {
                if (it != null) {
                    if (it == true) {
                        callWorkerToMangeUserDevice()
                        startActivity(ContainerActivity.intent(this@LoginActivity))
                        dialog.dismiss()
                        navigateToNextScreen.value = null
                        finish()

                    } else {
                        //TODO need message for error
                        layoutBinding.layoutMain.snackbar(BindingUtils.string(R.string.please_agree_the_instruction))
                        navigateToNextScreen.value = null
                    }
                }
            })
        }
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        try {
            when (requestCode) {
                READ_PHONE_STATE_PERMISSION_REQUEST_CODE -> {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //if permission is granted for phone ask for storage permission
                        setIMEINumber(this@LoginActivity)
                        layoutBinding.btnLogin.let { viewModel.doLogin(it) }
                    } else if (!PermissionsManager.shouldShowRequestPermissionRationale(
                            this@LoginActivity,
                            Manifest.permission.READ_PHONE_STATE
                        )
                    ) {
                        //permission denied by user with don ask again
                        //Now further we check if used denied permanently then goto setting page of the application to enable permission manually
                        val appname = "<b>${BindingUtils.string(R.string.app_name)}</b>"
                        val message = BindingUtils.string(R.string.phone_state_permission_desc)
                        val myMessage = String.format(message, appname)
                        requestPermissionAlert(myMessage)
                    }
                }
            }


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
    }

    private fun requestPermissionAlert(message: String) {

        viewModel.apply {
            setAlertDialogResourceModelMutableLiveData(
                message,
                BindingUtils.drawable(R.drawable.ic_phone_settings_),
                false,
                BindingUtils.string(R.string.strOk), {
                    try {
                        isAlertDialogShown.postValue(false)
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        AppLog.errorLog(e.message, e)
                    }
                },
                BindingUtils.string(R.string.strCancel),
                negativeButtonClickFunctionality = {
                    isAlertDialogShown.postValue(false)
                }, alertDrawableResourceSign = BindingUtils.drawable(R.drawable.ic_warning)!!
            )
            isAlertDialogShown.postValue(true)
        }
    }

    companion object {
        fun intent(activity: Activity): Intent {
            return Intent(activity, LoginActivity::class.java)
        }
    }

    private fun setListeners() {
        layoutBinding.apply {
            tvForgetPassword.setOnClickListener {
                startActivity(ForgotPasswordActivity.intent(this@LoginActivity))
            }
        }
    }
}