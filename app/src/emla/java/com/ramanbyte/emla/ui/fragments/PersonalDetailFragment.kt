package com.ramanbyte.emla.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.ramanbyte.BaseAppController
import com.ramanbyte.BuildConfig
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.cropper.ImageCroppingActivity
import com.ramanbyte.databinding.FragmentPersonalDetailBinding
import com.ramanbyte.emla.view_model.LearnerProfileViewModel
import com.ramanbyte.models.SpinnerModel
import com.ramanbyte.utilities.*
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import kotlinx.android.synthetic.emla.fragment_personal_detail.*
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class PersonalDetailFragment :
    BaseFragment<FragmentPersonalDetailBinding, LearnerProfileViewModel>(
        useParent = true
    ) {

    override val viewModelClass: Class<LearnerProfileViewModel> =
        LearnerProfileViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_personal_detail

    var statesMasterSpinnerUtil: MasterSpinnerUtil? = null
    var citiesMasterSpinnerUtil: MasterSpinnerUtil? = null

    var isSettingCity = false

    private var imagePath = ""
    private var fileName = ""

    override fun initiate() {

        ProgressLoader(context!!, viewModel)

        layoutBinding.apply {

            lifecycleOwner = viewLifecycleOwner

            learnerProfileViewModel = viewModel

            ivUserImage.setOnClickListener { openPickerDialog() }
        }

    }
    private fun openPickerDialog() {
        val builder: android.app.AlertDialog.Builder =
            android.app.AlertDialog.Builder(context)
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
                            activity!!,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        openGallery()
                    } else {
                        // ask for permission if not
                        PermissionsManager.checkPermission(
                            activity!!,
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


    private fun openCamera() {
        if (PermissionsManager.checkPermission(
                activity!!,
                Manifest.permission.CAMERA
            )
        ) {
            if (PermissionsManager.checkPermission(
                    activity!!,
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
                        activity!!,
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
                    activity!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    CAMERA_PERMISSION_REQUEST_CODE
                )
            }
        } else {
            PermissionsManager.checkPermission(
                activity!!,
                Manifest.permission.CAMERA,
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }


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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (PermissionsManager.checkPermission(
                            activity!!,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        AppLog.infoLog("permission camera WRITE_EXTERNAL_STORAGE ")
                        openCamera()
                    } else {
                        AppLog.infoLog("permission camera else WRITE_EXTERNAL_STORAGE")
                        PermissionsManager.checkPermission(
                            activity!!,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            CAMERA_PERMISSION_REQUEST_CODE
                        )
                    }

                } else if (!PermissionsManager.shouldShowRequestPermissionRationale(
                        activity!!,
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
                        activity!!,
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

    private fun performCrop() {
        try {
            val intent = Intent(activity!!, ImageCroppingActivity::class.java)
            intent.putExtra(KEY_IMAGE_PATH, imagePath)
            startActivityForResult(
                intent,
                CROP_IMAGE_REQUEST_CODE
            )
            BaseAppController.setEnterPageAnimation(activity!!)
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
    }

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
                            BindingUtils.string(R.string.package_uri), activity?.packageName, null
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
}
