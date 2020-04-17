package com.ramanbyte.emla.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentPersonalDetailBinding
import com.ramanbyte.emla.view_model.LearnerProfileViewModel
import com.ramanbyte.models.SpinnerModel
import com.ramanbyte.utilities.*
import kotlinx.android.synthetic.emla.fragment_personal_detail.*


/**
 * A simple [Fragment] subclass.
 */
class PersonalDetailFragment :
    BaseFragment<FragmentPersonalDetailBinding, LearnerProfileViewModel>(
        useParent = true,
        isNestedGraph = true
    ) {

    override val viewModelClass: Class<LearnerProfileViewModel> =
        LearnerProfileViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_personal_detail

    var statesMasterSpinnerUtil: MasterSpinnerUtil? = null
    var citiesMasterSpinnerUtil: MasterSpinnerUtil? = null

    override fun initiate() {

        ProgressLoader(context!!, viewModel)

        layoutBinding.apply {

            lifecycleOwner = this@PersonalDetailFragment

            learnerProfileViewModel = viewModel

        }

        setupSpinners()

        viewModelOps()
    }

    private fun setupSpinners() {

        statesMasterSpinnerUtil = MasterSpinnerUtil(context!!, this@PersonalDetailFragment)

        statesMasterSpinnerUtil?.setup(layoutBinding.spState, defaultSelectAction = {

            StaticMethodUtilitiesKtx.hideSpinnerDropDown(layoutBinding.spState)
            viewModel.registrationModelLiveData.value?.apply {
                state = 0
                layoutBinding.etState.setText(SELECT)
            }
        })

        citiesMasterSpinnerUtil = MasterSpinnerUtil(context!!, this@PersonalDetailFragment).apply {
            setup(layoutBinding.actvCity)
        }
    }

    private fun viewModelOps() {

        viewModel.apply {

            getStates()

            statesListLiveData.observe(this@PersonalDetailFragment, Observer { statesList ->

                statesList?.apply {

                    statesMasterSpinnerUtil?.spinnerItemListLiveData?.value =
                        mapIndexed { index, stateModel ->

                            SpinnerModel().apply {

                                id = stateModel.id
                                itemName = stateModel.state_Name

                                onNameSelected = {

                                    getCities(id)
                                    StaticMethodUtilitiesKtx.hideSpinnerDropDown(spState)
                                    registrationModelLiveData.value?.apply {
                                        state = id
                                        layoutBinding.etState.setText(itemName)
                                    }

                                }
                            }

                        }.toCollection(arrayListOf())

                }

            })

            citiesQueryLiveData.observe(this@PersonalDetailFragment, Observer { query ->

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
                                registrationModelLiveData.value?.apply {
                                    city = id
                                    cityName = itemName ?: ""
                                }
                            }
                        }

                    }.toCollection(arrayListOf())

                    citiesMasterSpinnerUtil?.spinnerItemListLiveData?.value = spinnerList

                    if (spinnerList.size > 0) {

                        if (actvCity.isShown)
                            actvCity.dismissDropDown()

                        actvCity.showDropDown()
                    }
                }

            })
        }

    }
}
