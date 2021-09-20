package com.ramanbyte.emla.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentAddressDetailBinding
import com.ramanbyte.emla.models.UserDetailsModel
import com.ramanbyte.emla.view_model.LearnerProfileViewModel
import com.ramanbyte.models.SpinnerModel
import com.ramanbyte.utilities.*
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.models.sort.SortingTypes
import kotlinx.android.synthetic.emla.fragment_address_detail.*
import java.io.File

/**
 * A simple [Fragment] subclass.
 */
class AddressDetailsFragment :
    BaseFragment<FragmentAddressDetailBinding, LearnerProfileViewModel>(
        useParent = true
    ) {
    override val viewModelClass: Class<LearnerProfileViewModel> =
        LearnerProfileViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_address_detail

    var statesMasterSpinnerUtil: MasterSpinnerUtil? = null
    var citiesMasterSpinnerUtil: MasterSpinnerUtil? = null
    private var docPaths = ArrayList<String>()

    var isSettingCity = false
    override fun initiate() {

        ProgressLoader(context!!, viewModel)

        layoutBinding.apply {

            lifecycleOwner = viewLifecycleOwner

            learnerProfileViewModel = viewModel

        }

        setupSpinners()
        viewModelOps()
    }

    private fun setupSpinners() {

        statesMasterSpinnerUtil = MasterSpinnerUtil(context!!, this@AddressDetailsFragment)

        statesMasterSpinnerUtil?.setup(layoutBinding.spState, defaultSelectAction = {

            StaticMethodUtilitiesKtx.hideSpinnerDropDown(layoutBinding.spState)
            viewModel.registrationModelLiveData?.value?.apply {
                state = 0
                layoutBinding.etState.setText(SELECT)
            }
        }, initialSelection = viewModel?.registrationModelLiveData?.value?.state ?: 0)

        citiesMasterSpinnerUtil = MasterSpinnerUtil(context!!, this@AddressDetailsFragment).apply {
            setup(layoutBinding.actvCity)
        }
    }

    private fun viewModelOps() {

        viewModel.apply {

            isSettingCity = true
            citiesQueryLiveData.value = registrationModelLiveData?.value?.cityName

            statesListLiveData.observe(this@AddressDetailsFragment, Observer { statesList ->

                statesList?.apply {

                    statesMasterSpinnerUtil?.spinnerItemListLiveData?.value =
                        mapIndexed { index, stateModel ->

                            SpinnerModel().apply {

                                id = stateModel.id
                                itemName = stateModel.state_Name

                                onNameSelected = {

                                    getCities(id)
                                    StaticMethodUtilitiesKtx.hideSpinnerDropDown(spState)
                                    registrationModelLiveData?.value?.apply {
                                        state = stateModel.id
                                        layoutBinding.etState.setText(itemName)

                                        city = 0
                                        isSettingCity = true
                                        cityName = ""
                                        layoutBinding.etCity.setText("")
                                    }

                                }
                            }

                        }.toCollection(arrayListOf())

                }
            })

            citiesQueryLiveData.observe(this@AddressDetailsFragment, Observer { query ->

                if (!isSettingCity) {

                    registrationModelLiveData?.value?.cityName = query

                    val queriedList = citiesList.filter {
                        it.city_Name.startsWith(query)
                    }

                    queriedList?.apply {

                        val spinnerList = mapIndexed { index, cityModel ->

                            SpinnerModel().apply {

                                id = cityModel.id
                                itemName = cityModel.city_Name

                                onNameSelected = {
                                    if (actvCity.isShown)
                                        actvCity.dismissDropDown()
                                    registrationModelLiveData?.value?.apply {
                                        city = cityModel.id
                                        //                                    cityName = itemName ?: ""
                                        isSettingCity = true
                                        cityName = itemName ?: ""
                                        layoutBinding.etCity.apply {
                                            setText(itemName)
                                        }
                                        layoutBinding?.tilCity?.apply {
                                            error = ""
                                            isErrorEnabled = false
                                        }
                                    }
                                    isCityAvailable = true
                                }
                            }

                        }.toCollection(arrayListOf())

                        citiesMasterSpinnerUtil?.spinnerItemListLiveData?.value = spinnerList

                        if (spinnerList.size > 0) {

                            if (actvCity.isShown)
                                actvCity.dismissDropDown()

                            actvCity.showDropDown()

                            if (query.isEmpty()) {

                                personalDetailsDataValidator?.updateSpinnerSelection(
                                    -1, keyCity, BindingUtils.string(
                                        R.string.required
                                    )
                                )

                                registrationModelLiveData?.value?.city = -1
                            }

                            layoutBinding?.actvCity.setOnFocusChangeListener { view, isFocussed ->
                                if (isFocussed) {
                                    if (actvCity.isShown)
                                        actvCity.dismissDropDown()

                                    actvCity.showDropDown()
                                }
                            }
                        } else {
                            if (actvCity.isShown)
                                actvCity.dismissDropDown()

                            if (query.isNotEmpty()) {
                                personalDetailsDataValidator?.updateSpinnerSelection(
                                    0, keyCity, BindingUtils.string(
                                        R.string.invalid_city_selected
                                    )
                                )
                                registrationModelLiveData?.value?.city = 0
                            }
                        }
                    }
                } else {
                    isSettingCity = false
                }
            })

            onClickRemoveResumeLiveData.observe(this@AddressDetailsFragment, Observer {
                it?.let {
                    uploadResumeFileName.value = KEY_BLANK
                    uploadResumeFilePath.value = KEY_BLANK
                    btnUploadResume.visibility = View.VISIBLE
                    lblResumeNote.visibility = View.VISIBLE
                    lblResumeFileName.visibility = View.GONE
                    ivRemoveResume.visibility = View.GONE
                    onClickRemoveResumeLiveData.value = null
                }
            })

            onClickUploadResumeLiveData.observe(this@AddressDetailsFragment, Observer {
                if (it != null) {
                    it?.let {
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

            oldResumeFileName.observe(this@AddressDetailsFragment, Observer {
                if (!it.isNullOrEmpty()) {
                    layoutBinding.apply {
                        lblResumeFileName.text = it
                        btnUploadResume.visibility = View.GONE
                        lblResumeNote.visibility = View.GONE
                        lblResumeFileName.visibility = View.VISIBLE
                        ivRemoveResume.visibility = View.VISIBLE
                    }
                }
            })
        }
    }

    private fun openDocumentFile() {
        val pdfs = arrayOf(FileUtils.KEY_PDF_DOCUMENT_EXTENSION)

        FilePickerBuilder.instance
            .setSelectedFiles(docPaths)
            .setActivityTheme(R.style.FilePickerTheme)
            .setActivityTitle(BindingUtils.string(R.string.please_select_resume))
            .addFileSupport(BindingUtils.string(R.string.pdf), pdfs, R.drawable.ic_resume)
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
                var extension: String? = KEY_BLANK
                var thumbnail: Int? = 0
                for (selectedFilePath in docPaths) {
                    val userDetailsModel = UserDetailsModel()
                    userDetailsModel.apply {

                        val filePath = selectedFilePath
                        val fileName = FileUtils.getFileNameFromPath(selectedFilePath)

                        val file = File(filePath)
                        val fileSize = file.length()
                        val fileSizeKB = fileSize / 1024
                        val fileSizeMB = fileSizeKB / 1024

                        AppLog.infoLog("fileName size :: $fileSizeKB     $fileSizeMB      $fileSize      $selectedFilePath    ")

                        extension =
                            selectedFilePath.substring(selectedFilePath.lastIndexOf(".")) ?: ""

                        if (extension.equals(FileUtils.KEY_PDF_DOCUMENT_EXTENSION, true) ||
                            extension.equals(FileUtils.KEY_DOCUMENT_DOC_EXTENSION, true) ||
                            extension.equals(FileUtils.KEY_DOCUMENT_DOCX_EXTENSION, true) ||
                            extension.equals(FileUtils.KEY_TEXT_EXTENSION, true) ||
                            extension.equals(FileUtils.KEY_RICH_TEXT_EXTENSION, true) ||
                            extension.equals(FileUtils.KEY_PRESENTATION_PPT_EXTENSION, true)
                        ) {

                            if (fileSizeMB <= 2) {
                                layoutBinding.apply {
                                    lblResumeFileName.text = fileName
                                    btnUploadResume.visibility = View.GONE
                                    lblResumeNote.visibility = View.GONE
                                    lblResumeFileName.visibility = View.VISIBLE
                                    ivRemoveResume.visibility = View.VISIBLE
                                }
                                AppLog.infoLog("fileNamePath  :: $fileName     $filePath")

                                //thumbnail = setThumbNail(extension ?: "")

                                viewModel.apply {
                                    uploadResumeFileName.value =
                                        StaticMethodUtilitiesKtx.currentMonthAsS3KeyObject + FileUtils.PATH_SEPARATOR + FileUtils.getFileNameFromPath(
                                            selectedFilePath
                                        )
                                    uploadResumeFilePath.value = selectedFilePath
                                }
                            } else {
                                viewModel.apply {
                                    setAlertDialogResourceModelMutableLiveData(
                                        BindingUtils.string(R.string.resume_error),
                                        BindingUtils.drawable(R.drawable.ic_warning)!!,
                                        true,
                                        BindingUtils.string(R.string.strOk), {
                                            isAlertDialogShown.postValue(false)
                                        }
                                    )
                                    isAlertDialogShown.postValue(true)
                                }
                            }
                        } else {
                            viewModel.apply {
                                setAlertDialogResourceModelMutableLiveData(
                                    BindingUtils.string(R.string.file_type_error),
                                    BindingUtils.drawable(R.drawable.ic_warning)!!,
                                    true,
                                    BindingUtils.string(R.string.strOk), {
                                        isAlertDialogShown.postValue(false)
                                    }
                                )
                                isAlertDialogShown.postValue(true)
                            }
                        }

                    }
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
                alertDrawableResource = BindingUtils.drawable(R.drawable.ic_resume),
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
}