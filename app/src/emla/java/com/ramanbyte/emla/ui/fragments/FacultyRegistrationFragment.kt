package com.ramanbyte.emla.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentFacultyRegistrationBinding
import com.ramanbyte.emla.models.UserDetailsModel
import com.ramanbyte.emla.view_model.CreateAccountViewModel
import com.ramanbyte.utilities.*
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.models.sort.SortingTypes
import java.io.File

class FacultyRegistrationFragment :
    BaseFragment<FragmentFacultyRegistrationBinding, CreateAccountViewModel>(false, false) {

    var mContext: Context? = null
    private var docPaths = ArrayList<String>()

    override val viewModelClass: Class<CreateAccountViewModel> = CreateAccountViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_faculty_registration

    override fun initiate() {
        layoutBinding.apply {
            lifecycleOwner = this@FacultyRegistrationFragment
            createAccountViewModel = viewModel
            ProgressLoader(mContext!!, viewModel)
            AlertDialog(mContext!!, viewModel)

            viewModel.apply {

                getAreaOfExpertise()

                onClickUploadResumeLiveData.observe(this@FacultyRegistrationFragment, Observer {
                    if (it != null) {
                        it?.let {
                            AppLog.infoLog("onClickUploadResume :: clicked")
                            if (PermissionsManager.checkPermission(
                                    activity!!,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            ) {
                                openDocumentFile()
                            } else {
                                // ask for permission if not
                                PermissionsManager.checkPermission(
                                    activity!!,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    WRITE_STORAGE_PERMISSION_CODE
                                )
                            }

                            onClickUploadResumeLiveData.value = null
                        }
                    }

                })
            }
        }
    }

    //Select file from fileManager
    private fun openDocumentFile() {
        val zips = arrayOf(FileUtils.KEY_ZIP_EXTENSION, FileUtils.KEY_RAR_EXTENSION)
        val pdfs = arrayOf(FileUtils.KEY_PDF_DOCUMENT_EXTENSION)

        FilePickerBuilder.instance
            .setSelectedFiles(docPaths)
            .setActivityTheme(R.style.FilePickerTheme)
            .setActivityTitle(BindingUtils.string(R.string.please_select_resume))
            //.addFileSupport(BindingUtils.string(R.string.zip), zips)
            .addFileSupport(BindingUtils.string(R.string.pdf), pdfs, R.drawable.ic_pdf)
            .enableDocSupport(true)
            .enableSelectAll(true)
            .setMaxCount(1)
            .sortDocumentsBy(SortingTypes.name)
            .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .pickFile(this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {

            FilePickerConst.REQUEST_CODE_DOC -> if (resultCode == Activity.RESULT_OK && data != null) {
                docPaths = ArrayList()
                docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS))
                for (selectedFilePath in docPaths) {
                    val userDetailsModel = UserDetailsModel()
                    userDetailsModel.apply {

                        filePath = selectedFilePath
                        fileName = FileUtils.getFileNameFromPath(selectedFilePath)

                        var file = File(filePath)
                        val fileSize = file.length()
                        var fileSizeKB =  fileSize / 1024
                        val fileSizeMB = fileSizeKB / 1024

                        AppLog.infoLog("Niru_fileName size :: $fileSizeKB     $fileSizeMB      $fileSize      $selectedFilePath    ")
                        if (fileSizeMB <= 2){
                            layoutBinding.lblResumeNote.text = fileName
                            AppLog.infoLog("Niru_fileName  :: $fileName")
                            extension = selectedFilePath.substring(selectedFilePath.lastIndexOf("."))
                            thumbnail = setThumbNail(extension)
                        }else{
                            layoutBinding.lblResumeNote.text = BindingUtils.string(R.string.resume_note)
                        }


                        if (extension.equals(FileUtils.KEY_PDF_DOCUMENT_EXTENSION, true)) {
                            thumbnail = R.drawable.ic_pdf
                        } else if (extension.equals(
                                FileUtils.KEY_DOCUMENT_DOC_EXTENSION,
                                true
                            ) || extension.equals(FileUtils.KEY_DOCUMENT_DOCX_EXTENSION, true)
                        ) {
                            thumbnail = R.drawable.ic_word
                        } else if (extension.equals(
                                FileUtils.KEY_SPREADSHEET_XLSX_EXTENSION,
                                true
                            ) || extension.equals(
                                FileUtils.KEY_SPREADSHEET_ODS_EXTENSION,
                                true
                            ) || extension.equals(FileUtils.KEY_SPREADSHEET_XLS_EXTENSION, true)
                        ) {
                            thumbnail = R.drawable.ic_excel
                        } else if (extension.equals(
                                FileUtils.KEY_TEXT_EXTENSION,
                                true
                            ) || extension.equals(FileUtils.KEY_RICH_TEXT_EXTENSION, true)
                        ) {
                            thumbnail = R.drawable.ic_text_document
                        } else {
                            thumbnail = R.drawable.icon_file_unknown
                        }

                        layoutBinding.ivThumbnail.setImageResource(thumbnail)
                    }
                }
            }
        }
    }

    fun setThumbNail(extension: String): Int {

        if (extension.equals(FileUtils.KEY_PDF_DOCUMENT_EXTENSION, true)) {
            return R.drawable.ic_pdf
        } else if (extension.equals(
                FileUtils.KEY_DOCUMENT_DOC_EXTENSION,
                true
            ) || extension.equals(FileUtils.KEY_DOCUMENT_DOCX_EXTENSION, true)
        ) {
            return R.drawable.ic_word
        } else if (extension.equals(
                FileUtils.KEY_SPREADSHEET_XLSX_EXTENSION,
                true
            ) || extension.equals(
                FileUtils.KEY_SPREADSHEET_ODS_EXTENSION,
                true
            ) || extension.equals(FileUtils.KEY_SPREADSHEET_XLS_EXTENSION, true)
        ) {
            return R.drawable.ic_excel
        } else if (extension.equals(
                FileUtils.KEY_TEXT_EXTENSION,
                true
            ) || extension.equals(FileUtils.KEY_RICH_TEXT_EXTENSION, true)
        ) {
            return R.drawable.ic_text_document
        } else {
            return R.drawable.icon_file_unknown
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            WRITE_STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (PermissionsManager.checkPermission(
                            activity!!,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        openDocumentFile()
                    } else if (!PermissionsManager.shouldShowRequestPermissionRationale(
                            activity!!,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {

                        requestPermissionAlert(BindingUtils.string(R.string.allow_access_phone_storage))
                    }
                }
            }

        }
    }

    private fun requestPermissionAlert(message: String) {

        viewModel.apply {
            setAlertDialogResourceModelMutableLiveData(
                message,
                alertDrawableResource = BindingUtils.drawable(R.drawable.ic_pdf),
                isInfoAlert = false,
                positiveButtonText = BindingUtils.string(R.string.strOk),
                positiveButtonClickFunctionality = {
                    try {
                        isAlertDialogShown.postValue(false)
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts(
                            BindingUtils.string(R.string.package_uri),
                            activity?.packageName,
                            null
                        )
                        intent.data = uri
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        AppLog.errorLog(e.message, e)
                    }
                },
                negativeButtonText = BindingUtils.string(R.string.strCancel),
                negativeButtonClickFunctionality = {
                    isAlertDialogShown.postValue(false)
                }
            )
            isAlertDialogShown.postValue(true)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}