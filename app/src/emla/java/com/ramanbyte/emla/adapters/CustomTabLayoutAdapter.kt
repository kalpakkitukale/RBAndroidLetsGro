package com.ramanbyte.emla.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CustomTabRecyclerviewLayoutBinding
import com.ramanbyte.emla.models.CustomTabModel
import com.ramanbyte.utilities.AppLog

/**
 * @author Akash Inkar <akash.1@ramanbyte.com>
 * @since 25/3/21
 */
class CustomTabLayoutAdapter(
    private val customTabModelList: List<CustomTabModel>,
    private val obj: Any
) : RecyclerView.Adapter<CustomTabLayoutAdapter.CustomTabLayoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomTabLayoutViewHolder {
        val rootView = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_tab_recyclerview_layout, parent, false)
        return CustomTabLayoutViewHolder(CustomTabRecyclerviewLayoutBinding.bind(rootView))
    }
    override fun getItemCount(): Int {
        return customTabModelList.size
    }
    override fun onBindViewHolder(holder: CustomTabLayoutViewHolder, position: Int) {
        holder.bind(customTabModelList[position])
    }
    inner class CustomTabLayoutViewHolder(var binding: CustomTabRecyclerviewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(customTabModel: CustomTabModel) {
            binding.apply {
                tvTabTitle.text = customTabModel.title
                ivTabIcon.setImageDrawable(customTabModel.icon)
            }
        }
    }
}