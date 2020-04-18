package com.ramanbyte.emla.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import com.ramanbyte.R
import com.ramanbyte.databinding.CustomSpinnerListBinding
import com.ramanbyte.models.SpinnerModel
import com.ramanbyte.utilities.AppLog
import java.util.ArrayList

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 11/3/20
 */
class CustomAutocompleteAdapter(
    private var mContext: Context,
    private var spinnerItemsList: ArrayList<SpinnerModel>
) : BaseAdapter(), Filterable {
    private var suggestions = ArrayList<SpinnerModel>()

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val spinnerItemViewHolder: SpinnerItemViewHolder
        if (view == null) {

            val newView = LayoutInflater.from(mContext).inflate(
                R.layout.custom_spinner_list,
                viewGroup,
                false
            )

            spinnerItemViewHolder = SpinnerItemViewHolder(newView)

            newView.tag = spinnerItemViewHolder

        } else {
            spinnerItemViewHolder = view.tag as SpinnerItemViewHolder
        }

        spinnerItemViewHolder.bind(getItem(position))

        return spinnerItemViewHolder.view
    }

    override fun getItem(position: Int): SpinnerModel {
        return spinnerItemsList[position]
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id.toLong()
    }

    override fun getCount(): Int {
        return spinnerItemsList.size
    }

    override fun getFilter(): Filter {
        return nameFilter
    }

    private var nameFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as SpinnerModel).itemName!!
        }
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            if (constraint != null) {
                suggestions.clear()
                try {
                    for (i in spinnerItemsList.indices) {
                        if (spinnerItemsList[i].itemName!!.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            suggestions.add(spinnerItemsList[i])
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    AppLog.errorLog(e.message, e)
                }finally {
                    filterResults.values = suggestions
                    filterResults.count = suggestions.size
                }
            }
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results != null && results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }

    inner class SpinnerItemViewHolder(val view: View) {

        var spinnerItemBinding: CustomSpinnerListBinding = CustomSpinnerListBinding.bind(view)

        fun bind(spinnerModel: SpinnerModel) {

            spinnerItemBinding.spinnerModel = spinnerModel

        }
    }
    companion object {

        fun get(mContext: Context, spinnerItemsList: ArrayList<SpinnerModel>): CustomAutocompleteAdapter = CustomAutocompleteAdapter(mContext, spinnerItemsList)
    }
}