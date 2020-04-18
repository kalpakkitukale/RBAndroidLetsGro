package com.ramanbyte.emla.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentEducationDetailBinding
import com.ramanbyte.emla.view_model.LearnerProfileViewModel
import com.ramanbyte.models.SpinnerModel
import com.ramanbyte.utilities.MasterSpinnerUtil
import com.ramanbyte.utilities.ProgressLoader
import com.ramanbyte.utilities.SELECT
import com.ramanbyte.utilities.StaticMethodUtilitiesKtx
import kotlinx.android.synthetic.emla.fragment_education_detail.*


/**
 * A simple [Fragment] subclass.
 */
class EducationDetailFragment :
    BaseFragment<FragmentEducationDetailBinding, LearnerProfileViewModel>(
        useParent = true
    ) {

    override val viewModelClass: Class<LearnerProfileViewModel> =
        LearnerProfileViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_education_detail

    private var programLevelSpinnerUtil: MasterSpinnerUtil? = null
    private var patternsSpinnerUtil: MasterSpinnerUtil? = null
    private var specializationSpinnerUtil: MasterSpinnerUtil? = null

    override fun initiate() {

        ProgressLoader(context!!, viewModel)

        layoutBinding.apply {

            lifecycleOwner = viewLifecycleOwner

            learnerProfileViewModel = viewModel
        }

        setUpSpinner()
        viewModelOps()
    }

    private fun setUpSpinner() {

        layoutBinding.apply {

            programLevelSpinnerUtil =
                MasterSpinnerUtil(context!!, this@EducationDetailFragment).apply {

                    setup(spinnerProgram, defaultSelectAction = {
                        StaticMethodUtilitiesKtx.hideSpinnerDropDown(spinnerProgram)

                        viewModel.registrationModelLiveData?.value?.apply {
                            programLevelId = 0
                            //programLevel = SELECT
                            layoutBinding.etProgramLevel.setText(SELECT)
                        }
                    })

                }

            patternsSpinnerUtil = MasterSpinnerUtil(context!!, this@EducationDetailFragment).apply {

                setup(spinnerPattern, defaultSelectAction = {
                    StaticMethodUtilitiesKtx.hideSpinnerDropDown(spinnerPattern)

                    viewModel.registrationModelLiveData?.value?.apply {
                        patternId = 0
                        layoutBinding.etPattern.setText(SELECT)
                    }
                })

            }

            specializationSpinnerUtil =
                MasterSpinnerUtil(context!!, this@EducationDetailFragment).apply {

                    setup(spinnerSpecialisation, defaultSelectAction = {
                        StaticMethodUtilitiesKtx.hideSpinnerDropDown(spinnerSpecialisation)

                        viewModel.registrationModelLiveData?.value?.apply {
                            specializationId = 0
                            layoutBinding.etSpecialization.setText(SELECT)
                        }
                    })

                }
        }
    }

    private fun viewModelOps() {

        viewModel.apply {

            getProgramLevels()

            programLevelsListLiveData.observe(
                this@EducationDetailFragment,
                Observer { programLevelList ->

                    programLevelList?.apply {

                        programLevelSpinnerUtil?.spinnerItemListLiveData?.value =
                            mapIndexed { index, masterDataResponseModel ->
                                SpinnerModel().apply {

                                    id = masterDataResponseModel.id
                                    itemName = masterDataResponseModel.itemName

                                    onNameSelected = {

                                        StaticMethodUtilitiesKtx.hideSpinnerDropDown(layoutBinding.spinnerProgram)

                                        registrationModelLiveData?.value?.apply {
                                            programLevelId = masterDataResponseModel.id
                                            //programLevel = itemName ?: ""
                                            layoutBinding.etProgramLevel.setText(itemName ?: "")
                                        }

                                    }
                                }
                            }.toCollection(arrayListOf())

                    }

                })

            educationPatternsListLiveData.observe(
                this@EducationDetailFragment,
                Observer { patternsList ->

                    patternsList?.apply {

                        patternsSpinnerUtil?.spinnerItemListLiveData?.value =
                            mapIndexed { index, masterDataResponseModel ->
                                SpinnerModel().apply {

                                    id = masterDataResponseModel.id
                                    itemName = masterDataResponseModel.itemName

                                    onNameSelected = {

                                        StaticMethodUtilitiesKtx.hideSpinnerDropDown(layoutBinding.spinnerPattern)

                                        registrationModelLiveData?.value?.apply {
                                            patternId = masterDataResponseModel.id
//                                            pattern = itemName ?: ""
                                            layoutBinding.etPattern.setText(itemName ?: "")
                                        }
                                    }
                                }
                            }.toCollection(arrayListOf())

                    }

                })

            specializationsListLiveData.observe(
                this@EducationDetailFragment,
                Observer { specializationList ->

                    specializationList?.apply {

                        specializationSpinnerUtil?.spinnerItemListLiveData?.value =
                            mapIndexed { index, masterDataResponseModel ->
                                SpinnerModel().apply {

                                    id = masterDataResponseModel.id
                                    itemName = masterDataResponseModel.itemName

                                    onNameSelected = {

                                        StaticMethodUtilitiesKtx.hideSpinnerDropDown(
                                            layoutBinding.spinnerSpecialisation
                                        )

                                        registrationModelLiveData?.value?.apply {
                                            specializationId = masterDataResponseModel.id
//                                            specialization = itemName ?: ""
                                            layoutBinding.etSpecialization.setText(itemName ?: "")
                                        }
                                    }
                                }
                            }.toCollection(arrayListOf())

                    }

                })
        }

    }


}
