package com.ramanbyte.utilities

import android.content.Context
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ramanbyte.emla.adapters.SpinnerAdapter
import com.ramanbyte.models.SpinnerModel

/**
 * @addedBy Vinay Kumbhar <vinay.pkumbhar@gmail.com>
 * @since 13-04-2020
 */
class MasterSpinnerUtil(
    private val mContext: Context,
    private val lifeCycleOwner: LifecycleOwner
) {

    var spinnerItemListLiveData = MutableLiveData<ArrayList<SpinnerModel>>()

    private var spinner: Spinner? = null
    private var autoCompleteTextView: AutoCompleteTextView? = null
    private var noSelectionItemName: String = SELECT
    private var defaultSelectAction: () -> Unit = {}

    var selectedItemId = 0
    var selectedItemPosition = 0

    private fun getDefaultSpinnerModel() = SpinnerModel().apply {

        itemName = noSelectionItemName
        onNameSelected = defaultSelectAction
    }

    fun setup(
        spinner: Spinner,
        noSelectionItemName: String = SELECT,
        defaultSelectAction: () -> Unit = {},
        addDefaultSelection: Boolean = true
    ) {

        this.spinner = spinner
        this.noSelectionItemName = noSelectionItemName
        this.defaultSelectAction = defaultSelectAction

        setupSpinnerAdapter(spinner, arrayListOf<SpinnerModel>().apply {

            if (addDefaultSelection)
                add(getDefaultSpinnerModel())

        }, 0)

        spinnerItemListLiveData.observe(lifeCycleOwner, Observer { spinnerList ->

            spinnerList?.apply {

                val newList = arrayListOf<SpinnerModel>().apply { addAll(spinnerList) }

                if (addDefaultSelection)
                    newList.add(0, getDefaultSpinnerModel())

                setupSpinnerAdapter(spinner, newList, 0)
            }


        })
    }

    fun setup(
        autoCompleteTextView: AutoCompleteTextView,
        noSelectionItemName: String = SELECT,
        defaultSelectAction: () -> Unit = {},
        addDefaultSelection: Boolean = false
    ) {

        this.autoCompleteTextView = autoCompleteTextView
        this.noSelectionItemName = noSelectionItemName
        this.defaultSelectAction = defaultSelectAction

        setupAutoCompleteAdapter(autoCompleteTextView, arrayListOf())

        spinnerItemListLiveData.observe(lifeCycleOwner, Observer { spinnerList ->

            spinnerList?.apply {

                val newList = arrayListOf<SpinnerModel>().apply { addAll(spinnerList) }

                if (addDefaultSelection)
                    newList.add(0, getDefaultSpinnerModel())

                setupAutoCompleteAdapter(autoCompleteTextView, newList)
            }


        })
    }

    fun reset(addDefaultSelection: Boolean = true) {

        setupSpinnerAdapter(spinner!!, arrayListOf<SpinnerModel>().apply {
            if (addDefaultSelection)
                add(getDefaultSpinnerModel())

        }, 0)

        spinnerItemListLiveData.value?.clear()

    }

    private fun setupSpinnerAdapter(
        spinner: Spinner,
        spinnerList: ArrayList<SpinnerModel>,
        selection: Any,
        isSelectionWithIndex: Boolean = true
    ): SpinnerModel {
        spinner.apply {
            adapter =
                SpinnerAdapter.get(
                    mContext,
                    spinnerList
                )

            val selectIndex = if (isSelectionWithIndex) {

                if (selection.toString().toInt() == -1)
                    return SpinnerModel()

                spinnerList.indexOfFirst { it.id == selection.toString().toInt() }
            } else
                spinnerList.indexOfFirst { it.itemName?.contains(selection.toString()) == true }

            return if (selectIndex != -1 && selectIndex < spinnerList.size) {
                //setSelection(selectIndex)
                return spinnerList[selectIndex]
            } else {
                //  setSelection(0)
                spinnerList[0]
            }
        }
    }

    private fun setupAutoCompleteAdapter(
        autoCompleteTextView: AutoCompleteTextView,
        dropDownList: ArrayList<SpinnerModel>
    ) {

        //autoCompleteTextView.setAdapter(CustomAutocompleteAdapter.get(mContext, dropDownList))

    }

    fun hideSpinnerDropDown(spinner: Spinner) {
        try {
            val method = Spinner::class.java.getDeclaredMethod("onDetachedFromWindow")
            method.isAccessible = true
            method.invoke(spinner)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}





