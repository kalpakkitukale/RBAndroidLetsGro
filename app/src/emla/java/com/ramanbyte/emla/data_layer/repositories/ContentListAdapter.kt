package com.ramanbyte.emla.data_layer.repositories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardContentBinding
import com.ramanbyte.emla.models.ContentModel
import com.ramanbyte.emla.view_model.ContentViewModel
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.FileUtils.KEY_PRESENTATION_PPTX_EXTENSION
import com.ramanbyte.utilities.FileUtils.KEY_PRESENTATION_PPT_EXTENSION
import java.util.*

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

            contentModel.apply {

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

                contentModel.apply {
                    if (content_Type == KEY_PRESENTATION_PPTX_EXTENSION || content_Type == KEY_PRESENTATION_PPT_EXTENSION)
                        IvVideo.setImageDrawable(BindingUtils.drawable(R.drawable.ic_ppt_document))
                    else
                        IvVideo.setImageDrawable(BindingUtils.drawable(R.drawable.ic_play))  // .mp4  .mpeg
                }


            }

        }
    }

}