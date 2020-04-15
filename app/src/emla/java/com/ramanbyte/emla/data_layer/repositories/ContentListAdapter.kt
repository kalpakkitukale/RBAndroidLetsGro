package com.ramanbyte.emla.data_layer.repositories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.databinding.CardContentBinding
import com.ramanbyte.emla.models.ContentModel
import com.ramanbyte.emla.view_model.ContentViewModel
import java.util.*
import kotlin.collections.ArrayList

class ContentListAdapter(
    private val viewModel: ContentViewModel,
    private val contentList: ArrayList<ContentModel>
) :
    RecyclerView.Adapter<ContentListAdapter.ContentListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentListViewHolder {
        return ContentListViewHolder(
            CardContentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return contentList.size
    }

    override fun onBindViewHolder(holder: ContentListViewHolder, position: Int) {
        holder.bindData()
    }

    inner class ContentListViewHolder(private val cardContentBinding: CardContentBinding) :
        RecyclerView.ViewHolder(cardContentBinding.root), LifecycleOwner {

        private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

        init {

            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }

        fun bindData() {

            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)

            val contentModel = contentList[adapterPosition]

            contentModel?.apply {

                isDownloaded = viewModel.isMediaDownloaded(this)

                if (!isDownloaded /*&& isDownloadAllowed.toLowerCase(Locale.getDefault()).contains("y")*/)
                    downloadVisibility = View.VISIBLE
                else
                    downloadVisibility = View.GONE
            }

            cardContentBinding.apply {
                lifecycleOwner = this@ContentListViewHolder
                contentViewModel = viewModel
                this@apply.contentModel = contentModel
            }

        }
    }

}