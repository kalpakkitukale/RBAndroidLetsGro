package com.ramanbyte.emla.ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.obsez.android.lib.filechooser.ChooserDialog
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentFacultyRegistrationBinding
import com.ramanbyte.emla.adapters.CustomAutocompleteAdapter
import com.ramanbyte.emla.models.AreaOfExpertiseResponseModel
import com.ramanbyte.emla.models.UserDetailsModel
import com.ramanbyte.emla.view_model.CreateAccountViewModel
import com.ramanbyte.emla.view_model.LoginViewModel
import com.ramanbyte.models.SpinnerModel
import com.ramanbyte.utilities.*
import java.io.File

class FacultyRegistrationFragment :
    BaseFragment<FragmentFacultyRegistrationBinding, CreateAccountViewModel>(false, false) {

    var mContext: Context? = null
    private var docPaths = ArrayList<String>()
    var shareViewModel: LoginViewModel? = null

    override val viewModelClass: Class<CreateAccountViewModel> = CreateAccountViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_faculty_registration

    override fun initiate() {
        layoutBinding.apply {
            lifecycleOwner = this@FacultyRegistrationFragment
            createAccountViewModel = viewModel
            ProgressLoader(mContext!!, viewModel)
            AlertDialog(mContext!!, viewModel)
            setHasOptionsMenu(true)

            activity?.run {
                shareViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
                shareViewModel?.setToolbarTitle(
                    View.VISIBLE,
                    BindingUtils.string(R.string.register_as_faculty)
                )
            }



            viewModel.apply {
                /*
                * call API for the area of expertise
                * */
                setAreaOfExpertise()

                /*
                * After successful registration show the alert.
                * */
                registrationSuccessMutableLiveData.observe(
                    this@FacultyRegistrationFragment,
                    Observer {
                        it?.let {
                            setAlertDialogResourceModelMutableLiveData(
                                BindingUtils.string(R.string.faculty_register_success),
                                BindingUtils.drawable(R.drawable.ic_success)!!,
                                true,
                                BindingUtils.string(R.string.strOk), {
                                    val navOption =
                                        NavOptions.Builder().setPopUpTo(R.id.loginFragment, true)
                                            .build()
                                    layoutBinding.root.findNavController()
                                        .navigate(R.id.loginFragment, null, navOption)
                                    isAlertDialogShown.postValue(false)
                                }
                            )
                            isAlertDialogShown.postValue(true)
                        }
                    })

                onClickUploadResumeLiveData.observe(this@FacultyRegistrationFragment, Observer {
                    if (it != null) {
                        it.let {
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

                onClickRemoveResumeLiveData.observe(this@FacultyRegistrationFragment, Observer {
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
            }
        }
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

    //Select file from fileManager
    private fun openDocumentFile() {
        val chooserDialog =
            ChooserDialog(requireActivity(), R.style.FileChooserStyle)

        chooserDialog.apply {
            withStringResources(
                BindingUtils.string(R.string.please_select_resume),
                BindingUtils.string(R.string.strOk),
                BindingUtils.string(R.string.strCancel)
            )
            withDateFormat("dd MMM yyyy")
            withOnBackPressedListener { dialog -> chooserDialog.goBack() }
            withChosenListener { dir: String, dirFile: File ->
                docPaths = ArrayList()
                docPaths.add(dir)
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
                            selectedFilePath.substring(selectedFilePath.lastIndexOf("."))

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
        chooserDialog.show()
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

    //********************************** Area of expertise ****************************

    var tempAreaOfExpertiseList = ArrayList<AreaOfExpertiseResponseModel>()
    var areaOfExpertiseChipList = ArrayList<AreaOfExpertiseResponseModel>()
    fun setAreaOfExpertise() {
        viewModel.apply {
            layoutBinding.tvAreaOfExpertise.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(searckKey: Editable?) {
                    if (searckKey.toString() != KEY_BLANK) {
                        getAreaOfExpertise(searckKey.toString())
                    } else {
                        getAreaOfExpertise(KEY_BLANK_TEXT)
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(searckKey: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }
            })

            /*
            * get the area Of Expertise List
            * */
            areaOfExpertiseListLiveData.observe(
                this@FacultyRegistrationFragment,
                Observer { areaOfExpertiseList ->
                    if (areaOfExpertiseList != null) {
                        tempAreaOfExpertiseList.clear()
                        tempAreaOfExpertiseList.addAll(areaOfExpertiseList)

                        if (areaOfExpertiseSet.size != 0) {
                            tempAreaOfExpertiseList = tempAreaOfExpertiseList.filter { model ->
                                !areaOfExpertiseSet.contains(model.courseId)
                            }.toCollection(arrayListOf())
                            setAreaOfExpertiseAdapter(tempAreaOfExpertiseList)
                        } else {
                            setAreaOfExpertiseAdapter(tempAreaOfExpertiseList)
                        }
                    }
                })
        }
    }


    private fun setAreaOfExpertiseAdapter(list: List<AreaOfExpertiseResponseModel>) {
        val newList = list.map {
            SpinnerModel().apply {
                val model = it
                this.id = model.courseId
                this.itemName = model.courseName

                this.onNameSelected = {
                    if (viewModel.areaOfExpertiseSet.size < 5) {
                        layoutBinding.cgAreaOfExpertise.addView(getChipItem(model))
                        AppLog.infoLog("SelectedCourseName _ ${model.courseName}")
                        viewModel.areaOfExpertiseSet.add(model.courseId)
                        layoutBinding.apply {
                            tvAreaOfExpertise.dismissDropDown()
                            tvAreaOfExpertise.setText(KEY_BLANK)
                        }
                        tempAreaOfExpertiseList.remove(model)
                        areaOfExpertiseChipList.remove(model)
                        setAreaOfExpertiseAdapter(tempAreaOfExpertiseList.sortedBy { it.courseName })
                    } else {
                        viewModel.apply {
                            setAlertDialogResourceModelMutableLiveData(
                                BindingUtils.string(R.string.select_max_5_courses),
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
        }.toCollection(arrayListOf())
        val adapter = CustomAutocompleteAdapter.get(mContext!!, newList)
        if (newList.size > 0) layoutBinding.tvAreaOfExpertise.showDropDown()
        //layoutBinding.tvAreaOfExpertise.setOnClickListener { layoutBinding.tvAreaOfExpertise.showDropDown() }
        layoutBinding.tvAreaOfExpertise.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    /* Add chips on the button click event*/
    private fun getChipItem(
        model: AreaOfExpertiseResponseModel
    ): Chip {
        val chip = activity?.layoutInflater?.inflate(
            R.layout.custom_chip_layout,
            null, false
        ) as Chip
        return chip.apply {
            text = model.courseName
            id = model.courseId
            setTextColor(ColorStateList.valueOf(BindingUtils.color(R.color.colorIcon)))
            isClickable = true
            isCheckable = false
            isCloseIconVisible = true
            chipBackgroundColor =
                ColorStateList.valueOf(BindingUtils.color(R.color.colorButtonLightBlue))
            setOnCloseIconClickListener {
                layoutBinding.cgAreaOfExpertise.removeView(it)
                tempAreaOfExpertiseList.add(model)
                viewModel.areaOfExpertiseSet.remove(model.courseId)
                setAreaOfExpertiseAdapter(tempAreaOfExpertiseList.sortedBy { it.courseName })
            }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}