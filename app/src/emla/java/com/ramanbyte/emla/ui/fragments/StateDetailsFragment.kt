package com.ramanbyte.emla.ui.fragments

import androidx.fragment.app.Fragment
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import androidx.lifecycle.Observer
import com.ramanbyte.databinding.FragmentStateDetailBinding
import com.ramanbyte.emla.view_model.LearnerProfileViewModel
import com.ramanbyte.models.SpinnerModel
import com.ramanbyte.utilities.*
import kotlinx.android.synthetic.emla.fragment_state_detail.*

/**
 * A simple [Fragment] subclass.
 */
class StateDetailsFragment :
    BaseFragment<FragmentStateDetailBinding, LearnerProfileViewModel>(
        useParent = true
    ) {
    override val viewModelClass: Class<LearnerProfileViewModel> =
        LearnerProfileViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_state_detail

    var statesMasterSpinnerUtil: MasterSpinnerUtil? = null
    var citiesMasterSpinnerUtil: MasterSpinnerUtil? = null

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

         statesMasterSpinnerUtil = MasterSpinnerUtil(context!!, this@StateDetailsFragment)

         statesMasterSpinnerUtil?.setup(layoutBinding.spState, defaultSelectAction = {

             StaticMethodUtilitiesKtx.hideSpinnerDropDown(layoutBinding.spState)
             viewModel.registrationModelLiveData?.value?.apply {
                 state = 0
                 layoutBinding.etState.setText(SELECT)
             }
         }, initialSelection = viewModel?.registrationModelLiveData?.value?.state ?: 0)

         citiesMasterSpinnerUtil = MasterSpinnerUtil(context!!, this@StateDetailsFragment).apply {
             setup(layoutBinding.actvCity)
         }
     }

     private fun viewModelOps() {

         viewModel.apply {

             isSettingCity = true
             citiesQueryLiveData.value = registrationModelLiveData?.value?.cityName

             statesListLiveData.observe(this@StateDetailsFragment, Observer { statesList ->

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

             citiesQueryLiveData.observe(this@StateDetailsFragment, Observer { query ->

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
                                         R.string.dynamic_required,
                                         BindingUtils.string(
                                             R.string.city
                                         )
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
         }
     }
}