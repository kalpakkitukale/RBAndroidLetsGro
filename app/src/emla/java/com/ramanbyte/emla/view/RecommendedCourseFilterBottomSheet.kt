package com.ramanbyte.emla.view

import android.content.Context
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseBottomSheetFragment
import com.ramanbyte.databinding.RecommendedCourseFilterBinding
import com.ramanbyte.emla.view_model.CoursesViewModel
import com.ramanbyte.models.SpinnerModel
import com.ramanbyte.utilities.*

/**
 * @author Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 16-04-2020
 */
class RecommendedCourseFilterBottomSheet :
    BaseBottomSheetFragment<RecommendedCourseFilterBinding, CoursesViewModel>(
        isActivityParent = false,
        useParent = true
    ) {
    lateinit var mContext: Context

    override val viewModelClass: Class<CoursesViewModel> = CoursesViewModel::class.java

    override fun layoutId(): Int = R.layout.recommended_course_filter

    private var programsSpinnerUtil: MasterSpinnerUtil? = null
    private var patternsSpinnerUtil: MasterSpinnerUtil? = null
    private var specializationSpinnerUtil: MasterSpinnerUtil? = null

    override fun initiate() {
        try {
            layoutBinding.apply {

                //                mainContainer.viewTreeObserver.addOnGlobalLayoutListener {
//                    val dialog = dialog as BottomSheetDialog
//                    getDialog()!!.window!!.apply {
//                        setSoftInputMode(WindowManager.LayoutParams.FLAG_FULLSCREEN)
//                        setDimAmount(0.5f)
//                    }
//                    val bottomSheet =
//                        dialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
//                    val mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
//                    val height = mainContainer.height/2
//                    val displayMetrics = DisplayMetrics()
//                    activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
//                    var screenHeight = displayMetrics.heightPixels
//                    val statusBarHeight = StaticMethodUtilitiesKtx.getStatusBarHeight(activity!!)
//                    screenHeight -= statusBarHeight
//                    mBottomSheetBehavior.peekHeight = height
//                }

                lifecycleOwner = this@RecommendedCourseFilterBottomSheet
                courseViewModel = viewModel
                ProgressLoader(mContext, viewModel)
                AlertDialog(mContext, viewModel)
            }
            setSpinners()
            setViewModelOp()
        } catch (e: Exception) {
            e.printStackTrace()
            AppLog.errorLog(e.message, e)
        }
    }

    private fun setSpinners() {

        layoutBinding.apply {

            programsSpinnerUtil =
                MasterSpinnerUtil(context!!, this@RecommendedCourseFilterBottomSheet).apply {

                    setup(spinnerProgram, defaultSelectAction = {
                        viewModel.apply {
                            tempFilterModel.programId = 0
                            programName.set(BindingUtils.string(R.string.program_level))
                        }
                        StaticMethodUtilitiesKtx.hideSpinnerDropDown(layoutBinding.spinnerProgram)

                    })
                }

            patternsSpinnerUtil =
                MasterSpinnerUtil(context!!, this@RecommendedCourseFilterBottomSheet).apply {

                    setup(spinnerPattern, defaultSelectAction = {
                        viewModel.apply {
                            tempFilterModel.patternId = 0
                            patternName.set(BindingUtils.string(R.string.pattern))
                        }
                        StaticMethodUtilitiesKtx.hideSpinnerDropDown(layoutBinding.spinnerPattern)
                    }
                    )
                }

            specializationSpinnerUtil =
                MasterSpinnerUtil(context!!, this@RecommendedCourseFilterBottomSheet).apply {

                    setup(spinnerSpecialisation, defaultSelectAction = {
                        viewModel.apply {
                            tempFilterModel.specializationId = 0
                            specializationName.set(BindingUtils.string(R.string.specialisation))
                        }
                        StaticMethodUtilitiesKtx.hideSpinnerDropDown(layoutBinding.spinnerSpecialisation)
                    })
                }
        }

    }

    private fun setViewModelOp() {
        viewModel.apply {
            if (programsListMutableLiveData.value == null)
                getAllPrograms()
            programsListMutableLiveData.observe(this@RecommendedCourseFilterBottomSheet, Observer {
                it?.let {
                    programsSpinnerUtil?.spinnerItemListLiveData?.value =
                        it.mapIndexed { index, programsModel ->

                            SpinnerModel().apply {

                                id = programsModel.valueField
                                itemName = programsModel.textField

                                onNameSelected = {
                                    tempFilterModel.programId = id
                                    programName.set(programsModel.textField)
                                    StaticMethodUtilitiesKtx.hideSpinnerDropDown(layoutBinding.spinnerProgram)
                                    programsSpinnerUtil?.apply {
                                        selectedItemId = id
                                        selectedItemPosition = index
                                    }
                                }
                            }
                        }.toCollection(arrayListOf())
                }
            })
            if (patternsListMutableLiveData.value == null)
                getAllPatterns()
            patternsListMutableLiveData.observe(this@RecommendedCourseFilterBottomSheet, Observer {
                it?.let {
                    patternsSpinnerUtil?.spinnerItemListLiveData?.value =
                        it.mapIndexed { index, patternModel ->

                            SpinnerModel().apply {

                                id = patternModel.valueField
                                itemName = patternModel.textField

                                onNameSelected = {
                                    tempFilterModel.patternId = id
                                    patternName.set(patternModel.textField)
                                    StaticMethodUtilitiesKtx.hideSpinnerDropDown(layoutBinding.spinnerPattern)
                                    patternsSpinnerUtil?.apply {
                                        selectedItemId = id
                                        selectedItemPosition = index
                                    }

                                }
                            }
                        }.toCollection(arrayListOf())
                }
            })
            if (specializationsListMutableLiveData.value == null)
                getAllSpecializations()
            specializationsListMutableLiveData.observe(this@RecommendedCourseFilterBottomSheet,
                Observer {
                    it?.let {
                        specializationSpinnerUtil?.spinnerItemListLiveData?.value =
                            it.mapIndexed { index, specializationModel ->
                                SpinnerModel().apply {
                                    id = specializationModel.valueField
                                    itemName = specializationModel.textField

                                    onNameSelected = {
                                        tempFilterModel.specializationId = id
                                        specializationName.set(specializationModel.textField)
                                        StaticMethodUtilitiesKtx.hideSpinnerDropDown(layoutBinding.spinnerSpecialisation)
                                        specializationSpinnerUtil?.apply {
                                            selectedItemId = id
                                            selectedItemPosition = index
                                        }

                                    }
                                }
                            }.toCollection(arrayListOf())
                    }
                })

            clearFilter.observe(this@RecommendedCourseFilterBottomSheet, Observer {
                it?.let {
                    if (it) {
                        layoutBinding.apply {
                            programName.set(BindingUtils.string(R.string.program_level))
                            patternName.set(BindingUtils.string(R.string.pattern))
                            specializationName.set(BindingUtils.string(R.string.specialisation))
                        }
                    }
                    clearFilter.postValue(null)
                }
            })

            dismissBottomSheet.observe(this@RecommendedCourseFilterBottomSheet, Observer {
                it?.let {
                    if (it) this@RecommendedCourseFilterBottomSheet.dismiss()
                    dismissBottomSheet.postValue(null)
                }
            })

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

}