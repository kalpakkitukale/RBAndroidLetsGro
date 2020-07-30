package com.ramanbyte.emla.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.ramanbyte.BaseAppController
import com.ramanbyte.BuildConfig
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.cropper.ImageCroppingActivity
import com.ramanbyte.databinding.FragmentStudentRegistrationBinding
import com.ramanbyte.emla.ui.activities.LoginActivity
import com.ramanbyte.emla.view_model.CreateAccountViewModel
import com.ramanbyte.emla.view_model.LoginViewModel
import com.ramanbyte.utilities.*
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import java.io.File

class StudentRegistrationFragment :
    BaseFragment<FragmentStudentRegistrationBinding, CreateAccountViewModel>(false, false) {

    var mContext: Context? = null
    private var imagePath = ""
    private var fileName = ""
    var shareViewModel: LoginViewModel? = null

    override val viewModelClass: Class<CreateAccountViewModel> = CreateAccountViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_student_registration

    override fun initiate() {
        layoutBinding.apply {
            lifecycleOwner = this@StudentRegistrationFragment
            createAccountViewModel = viewModel
            ProgressLoader(mContext!!, viewModel)
            AlertDialog(mContext!!, viewModel)
            setHasOptionsMenu(true)

            activity?.run {
                shareViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
                shareViewModel?.setToolbarTitle(
                    View.VISIBLE,
                    BindingUtils.string(R.string.register_as_student)
                )
            }

            ivUserImage.setOnClickListener { openPickerDialog() }
        }
        setViewModelOp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                shareViewModel?.setToolbarTitle(View.GONE, KEY_BLANK)
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setViewModelOp() {
        viewModel.apply {
            registrationSuccessMutableLiveData.observe(this@StudentRegistrationFragment, Observer {
                it?.let {
                    setAlertDialogResourceModelMutableLiveData(
                        BindingUtils.string(R.string.successAccountCreation),
                        BindingUtils.drawable(R.drawable.ic_success)!!,
                        true,
                        BindingUtils.string(R.string.strOk), {
                            val navOption =
                                NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
                                layoutBinding.root.findNavController().navigate(R.id.loginFragment, null, navOption)
                            isAlertDialogShown.postValue(false)
                        }
                    )
                    isAlertDialogShown.postValue(true)
                }
            })
        }
    }

    /**Vinay K
     * image picker dialog*/
    private fun openPickerDialog() {
        val builder: android.app.AlertDialog.Builder =
            android.app.AlertDialog.Builder(mContext)
        builder.setTitle(getString(R.string.str_select_from))

        val dialogMenu = if (viewModel.uploadFilePath.value?.isNotEmpty()!!) arrayOf<CharSequence>(
            BindingUtils.string(R.string.strCamera),
            BindingUtils.string(R.string.strGallary),
            BindingUtils.string(R.string.strRemovePhoto)
        ) else
            arrayOf<CharSequence>(
                BindingUtils.string(R.string.strCamera),
                BindingUtils.string(R.string.strGallary)
            )

        builder.setItems(dialogMenu) { _, i ->
            when (i) {
                CAMERA -> {
                    openCamera()
                }
                GALLERY -> {
                    // check permission for read write storage
                    if (PermissionsManager.checkPermission(
                            mContext as Activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        openGallery()
                    } else {
                        // ask for permission if not
                        PermissionsManager.checkPermission(
                            mContext as Activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            WRITE_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                REMOVE_PHOTO -> {
                    viewModel.apply {
                        profileImageUrl.postValue("no_image")
                        uploadFileName.postValue("")
                        uploadFilePath.postValue("")
                    }
                }
            }
        }
        builder.show()
    }

    /**Vinay k
     * open camera**/
    private fun openCamera() {
        if (PermissionsManager.checkPermission(
                mContext as Activity,
                Manifest.permission.CAMERA
            )
        ) {
            if (PermissionsManager.checkPermission(
                    mContext as Activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {

                imagePath = FileUtils.getNewCreatedImageFilePath("")
                fileName =
                    FileUtils.KEY_LOCAL_FILE_INITIALS + FileUtils.KEY_UNIQUE_FILE_NAME_ADDITION_STRING + DateUtils.getCurrentDateTime(
                        DateUtils.DATE_TIME_SECONDS_PATTERN_FOR_FILE
                    ) + FileUtils.KEY_JPG_IMAGE_EXTENSION

                val selectedImageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(
                        mContext as Activity,
                        BuildConfig.APPLICATION_ID,
                        File(imagePath)
                    )
                } else {
                    Uri.fromFile(File(imagePath))
                }

                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri)
                takePictureIntent.flags =
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
            } else {
                PermissionsManager.checkPermission(
                    mContext as Activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            }
        } else {
            PermissionsManager.checkPermission(
                mContext as Activity,
                Manifest.permission.CAMERA,
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    /**Vinay k
     * open Gallery**/
    private fun openGallery() {
        FilePickerBuilder.instance
            .setSelectedFiles(ArrayList())
            .setActivityTheme(R.style.FilePickerTheme)
            .setActivityTitle(BindingUtils.string(R.string.please_select_media))
            .enableVideoPicker(false)
            .enableCameraSupport(false)
            .showGifs(false)
            .setMaxCount(1)
            .showFolderView(true)
            .enableSelectAll(false)
            .enableImagePicker(true)
            .setCameraPlaceholder(R.drawable.ic_camera)
            .withOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            .pickPhoto(this, REQUEST_CODE_GALLERY_PIC)
    }

    /**Vinay k
     * on activityResult here we get the selected and/or cropped image url
     * **/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_GALLERY_PIC -> if (resultCode == Activity.RESULT_OK && data != null) {
                val mediaList = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)
                if (mediaList?.size ?: 0 > 0) {
                    imagePath = mediaList.get(0)
                    performCrop()
                }
            }
            REQUEST_CODE_CAMERA -> {
                if (File(imagePath).length() > 0)
                    performCrop()
            }
            CROP_IMAGE_REQUEST_CODE -> {
                val croppedFilePath = data?.getStringExtra(KEY_CROP_IMAGE_PATH)
                viewModel.apply {
                    profileImageUrl.postValue(data?.getStringExtra(KEY_CROP_IMAGE_PATH))
                    val s3FileKey =
                        "${StaticMethodUtilitiesKtx.currentMonthAsS3KeyObject}/${FileUtils.getFileNameFromPath(
                            croppedFilePath
                        )}"
                    uploadFileName.postValue(s3FileKey)
                    uploadFilePath.postValue(croppedFilePath)
                }
            }
        }
    }

    /**Vinay k
     * permission results for camera and phone storage**/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (PermissionsManager.checkPermission(
                            mContext as Activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        AppLog.infoLog("permission camera WRITE_EXTERNAL_STORAGE ")
                        openCamera()
                    } else {
                        AppLog.infoLog("permission camera else WRITE_EXTERNAL_STORAGE")
                        PermissionsManager.checkPermission(
                            mContext as Activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            CAMERA_PERMISSION_REQUEST_CODE
                        )
                    }

                } else if (!PermissionsManager.shouldShowRequestPermissionRationale(
                        mContext as Activity,
                        Manifest.permission.CAMERA
                    )
                ) {
                    AppLog.infoLog("permission camera else rational permisison")
                    //permission denied by user with don ask again
                    //Now further we check if used denied permanently then goto setting page of the application to enable permission manually
                    val appname = "<b>${BindingUtils.string(R.string.app_name)}</b>"
                    val message = BindingUtils.string(R.string.camera_permission_message)
                    val myMessage = String.format(message, appname)
                    requestPermissionAlert(myMessage)
                }
            }

            WRITE_STORGAE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else if (!PermissionsManager.shouldShowRequestPermissionRationale(
                        mContext as Activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    //permission denied by user with don ask again
                    //Now further we check if used denied permanently then goto setting page of the application to enable permission manually
                    val appname = "<b>${BindingUtils.string(R.string.app_name)}</b>"
                    val message = BindingUtils.string(R.string.camera_permission_message)
                    val myMessage = String.format(message, appname)
                    requestPermissionAlert(myMessage)
                }
            }
        }
    }

    /**Vinay k
     * Performs crop operation from here
     * opens image crop activity**/
    private fun performCrop() {
        try {
            val intent = Intent(mContext, ImageCroppingActivity::class.java)
            intent.putExtra(KEY_IMAGE_PATH, imagePath)
            startActivityForResult(
                intent,
                CROP_IMAGE_REQUEST_CODE
            )
            BaseAppController.setEnterPageAnimation(mContext as Activity)
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
    }

    /**Vinay k
     * alert for permission denied with don't ask again**/
    private fun requestPermissionAlert(message: String) {

        viewModel.apply {
            setAlertDialogResourceModelMutableLiveData(
                message, BindingUtils.drawable(R.drawable.ic_something_went_wrong),
                false, BindingUtils.string(R.string.strOk), {
                    try {
                        isAlertDialogShown.postValue(false)
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts(
                            BindingUtils.string(R.string.package_uri), Activity().packageName, null
                        )
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
                },
                alertDrawableResourceSign = BindingUtils.drawable(R.drawable.ic_warning)!!
            )
            isAlertDialogShown.postValue(true)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}