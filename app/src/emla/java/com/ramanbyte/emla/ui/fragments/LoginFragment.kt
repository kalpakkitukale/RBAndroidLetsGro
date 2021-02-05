package com.ramanbyte.emla.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.ramanbyte.BaseAppController
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentLoginBinding
import com.ramanbyte.emla.faculty.ui.activities.FacultyContainerActivity
import com.ramanbyte.emla.ui.activities.ContainerActivity
import com.ramanbyte.emla.ui.activities.ForgotPasswordActivity
import com.ramanbyte.emla.ui.activities.LoginActivity
import com.ramanbyte.emla.view_model.LoginViewModel
import com.ramanbyte.services.MangeUserDevice
import com.ramanbyte.utilities.*
import org.json.JSONObject

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 27/07/2020
 */
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(true, true) {

    private var callbackManager: CallbackManager? = null
    private var accessToken: AccessToken? = null
    var isLoggedIn: Boolean = false
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 101
    var mContext: Context? = null

    override val viewModelClass: Class<LoginViewModel> = LoginViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_login

    override fun initiate() {
        setListeners()
        //makeStatusBarTransparent()

        layoutBinding.apply {
            lifecycleOwner = this@LoginFragment
            loginViewModel = viewModel
            ProgressLoader(mContext!!, viewModel)
            AlertDialog(mContext!!, viewModel)

            viewModel.apply {
                setToolbarTitle(View.GONE, KEY_BLANK)
            }
            setViewModelOps()
            initGoogleSignInClient()
        }
    }

    private fun setViewModelOps() {
        viewModel.apply {

            userModelLiveData.observe(this@LoginFragment, Observer {
                it?.let {
                    if (it.userType.equals(KEY_STUDENT, true)) {

                        callWorkerToMangeUserDevice()
                        if (!viewModel.isUserActive()) {
                            findNavController().navigate(R.id.action_loginFragment_to_learnerProfileFragment)
                        } else {
                            startActivity(ContainerActivity.intent(mContext as Activity))
                            Activity().finish()
                            BaseAppController.setEnterPageAnimation(mContext as Activity)
                        }

                    } else if (it.userType.equals(KEY_FACULTY, true)) {
                        callWorkerToMangeUserDevice()
                        startActivity(FacultyContainerActivity.intent(mContext as Activity))
                        Activity().finish()
                        BaseAppController.setEnterPageAnimation(mContext as Activity)
                    }
                    userModelLiveData.value = null
                }

            })

            btnGoogleLogin.observe(this@LoginFragment, Observer {
                if (it != null) {
                    if (it == true) {
                        signIn()
                        btnGoogleLogin.value = null
                    }
                }
            })

            btnFbLogin.observe(this@LoginFragment, Observer {
                if (it != null) {
                    if (it == true) {
                        initFbLogin()
                        btnFbLogin.value = null
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
        WorkManager.getInstance(mContext!!).enqueue(simpleRequest)
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
                        StaticMethodUtilitiesKtx.setIMEINumber(mContext!!)
                        layoutBinding.btnLogin.let { viewModel.doLogin(it) }
                    } else if (!PermissionsManager.shouldShowRequestPermissionRationale(
                            mContext!! as Activity,
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
                        val uri = Uri.fromParts("package", Activity().packageName, null)
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
                startActivity(ForgotPasswordActivity.intent(mContext as Activity))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager?.onActivityResult(requestCode, resultCode, data)

        if (requestCode === RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun initFbLogin() {

        accessToken = AccessToken.getCurrentAccessToken()
        isLoggedIn = accessToken != null && accessToken!!.isExpired

        if (isLoggedIn != true) {

            callbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance().logInWithReadPermissions(
                mContext as Activity,
                arrayListOf("public_profile", "email")
            )

            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult?> {

                    override fun onSuccess(loginResult: LoginResult?) {
                        Log.d("APPINFO", "LoggedIn success :" + loginResult)

                        callWorkerToMangeUserDevice()
                        startActivity(ContainerActivity.intent(mContext as Activity))
                        Activity().finish()
                        BaseAppController.setEnterPageAnimation(mContext as Activity)

                        val request = GraphRequest.newMeRequest(
                            loginResult!!.accessToken,
                            object : GraphRequest.GraphJSONObjectCallback {
                                override fun onCompleted(
                                    jsonObject: JSONObject?,
                                    response: GraphResponse?
                                ) {

                                    // for email we have to add permissions for email in facebook developer consol

                                    AppLog.infoLog(
                                        "UserInfo"
                                                + loginResult.accessToken
                                                + jsonObject?.getString("id")
                                                + jsonObject?.getString("name")
                                    )
                                }
                            })

                        var parameters = Bundle()
                        parameters.putString("fields", "id,name,email,gender")
                        request.parameters = parameters
                        request.executeAsync()
                    }

                    override fun onCancel() {
                        Log.d("APPINFO", "LoggedIn Cancle :")
                    }

                    override fun onError(exception: FacebookException) {

                        Log.d("APPINFO", "LoggedIn Error :")
                    }
                })
        }
    }

    private fun initGoogleSignInClient() {
        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(mContext as Activity, googleSignInOptions)
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {

        if (completedTask.isSuccessful) {

            callWorkerToMangeUserDevice()
            startActivity(ContainerActivity.intent(mContext as Activity))
            Activity().finish()
            BaseAppController.setEnterPageAnimation(mContext as Activity)

            try {
                val account = completedTask.getResult(ApiException::class.java)
                // Signed in successfully
                AppLog.infoLog(
                    "USER INFO :"
                            + "ID" + account?.id
                            + "First Name" + account?.givenName
                            + "Last Name" + account?.familyName
                            + "Email" + account?.email
                            + "Profile URL" + account?.photoUrl.toString()
                            + "Token ID" + account?.idToken
                )

            } catch (e: ApiException) {
                // Sign in was unsuccessful
                Log.e("failed code=", e.statusCode.toString())
            }

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}