package com.ramanbyte.emla.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CoursePerchesDetailItemListBinding
import com.ramanbyte.emla.models.response.CartResponseModel
import com.ramanbyte.utilities.KEY_BLANK
import com.ramanbyte.utilities.StaticMethodUtilitiesKtx

/**
 * @author Akash Inkar <akash.1@ramanbyte.com>
 * @since 5/3/21
 */
class PurchaseCourseDetailsAdapter(var dataList: ArrayList<CartResponseModel>,var mContext: Context) :
    RecyclerView.Adapter<PurchaseCourseDetailsAdapter.PerchaesDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerchaesDetailsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PerchaesDetailsViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.course_perches_detail_item_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: PerchaesDetailsViewHolder, position: Int) {
        val dataPojo = dataList[position]
        holder.bind(dataPojo.apply {
            courseImageUrl =
                StaticMethodUtilitiesKtx.getS3DynamicURL(courseImage ?: KEY_BLANK, mContext)
        })

    }


    inner class PerchaesDetailsViewHolder(private var binding: CoursePerchesDetailItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(dataPojo: CartResponseModel) {
            binding.apply {
                this.cardData = dataPojo

            }
        }

    }


}