package com.ramanbyte.emla.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CustomTabRecyclerviewLayoutBinding
import com.ramanbyte.emla.models.CoursesModel
import com.ramanbyte.emla.models.CustomTabModel
import com.ramanbyte.emla.view_model.CoursesViewModel

/**
 * @author Akash Inkar <akash.1@ramanbyte.com>
 * @since 25/3/21
 */
class CustomTabLayoutAdapter(
    private val customTabModelList: ArrayList<CustomTabModel>,
    private val obj: Any,
    var viewModel: ViewModel?
) : RecyclerView.Adapter<CustomTabLayoutAdapter.CustomTabLayoutViewHolder>() {
var positions= obj
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomTabLayoutViewHolder {
        val rootView = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_tab_recyclerview_layout, parent, false)
        return CustomTabLayoutViewHolder(CustomTabRecyclerviewLayoutBinding.bind(rootView))
    }

    override fun getItemCount(): Int {
        return customTabModelList.size
    }

    override fun onBindViewHolder(holder: CustomTabLayoutViewHolder, position: Int) {
        val customTabModel = customTabModelList[position]
        holder.bind(customTabModelList[position])
        holder.binding.apply {
          root.setOnClickListener {
                customTabModel.clickListener.invoke(it, obj)
            }
            /*tablayout.setOnClickListener {
                customTabModelList.removeAt(position)
                customTabModel.clickListener.invoke(it, obj,position)
                notifyItemRemoved(position)
            }*/
        }
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

    // remove the list for arrayList and refresh adapter
    fun removeFromList(positionclick: Int) {
        try {
          //  notifyItemRemoved(position!!)
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: ConcurrentModificationException) {
            e.printStackTrace()
        }

    }
}