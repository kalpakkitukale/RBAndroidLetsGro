package com.ramanbyte.emla.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import com.ramanbyte.R
import com.ramanbyte.databinding.CustomSpinnerListBinding
import com.ramanbyte.models.SpinnerModel

/**
 * @author Shital Kadaganchi <shital.k@ramanbyte.com>
 * @since 31/12/19
 */
class SpinnerAdapter private constructor(
    private val mContext: Context,
    private val spinnerItemsList: ArrayList<SpinnerModel>
) : BaseAdapter(), android.widget.SpinnerAdapter {

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

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return super.getDropDownView(position, convertView, parent)
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

    inner class SpinnerItemViewHolder(val view: View) {

        var spinnerItemBinding: CustomSpinnerListBinding = CustomSpinnerListBinding.bind(view)

        fun bind(spinnerModel: SpinnerModel) {

            spinnerItemBinding.spinnerModel = spinnerModel

        }
    }

    companion object {

        fun get(mContext: Context, spinnerItemsList: ArrayList<SpinnerModel>): SpinnerAdapter =
            SpinnerAdapter(mContext, spinnerItemsList)
    }
}