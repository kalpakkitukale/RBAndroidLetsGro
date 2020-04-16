package com.ramanbyte.emla.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardContentBinding
import com.ramanbyte.databinding.CardDownloadsBinding
import com.ramanbyte.emla.models.MediaInfoModel
import com.ramanbyte.emla.view_model.MyDownloadsViewModel

class MyDownloadsListAdapter(
    private val downloadsViewModel: MyDownloadsViewModel,
    private val mediaList: List<MediaInfoModel>
) : RecyclerView.Adapter<MyDownloadsListAdapter.DownloadsListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadsListViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        return DownloadsListViewHolder(
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.card_downloads,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    override fun onBindViewHolder(holder: DownloadsListViewHolder, position: Int) {
        holder.bindData(mediaList[position])
    }

    inner class DownloadsListViewHolder(private val cardDownloadsBinding: CardDownloadsBinding) :
        RecyclerView.ViewHolder(cardDownloadsBinding.root) {

        fun bindData(mediaInfoModel: MediaInfoModel) {

            cardDownloadsBinding.apply {

                downloadsViewModel = this@MyDownloadsListAdapter.downloadsViewModel

                this@apply.mediaInfoModel = mediaInfoModel
            }

        }

    }

}