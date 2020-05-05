package com.ramanbyte.emla.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardRecentlyAskQuestionsBinding
import com.ramanbyte.emla.models.AskQuestionModel
import com.ramanbyte.emla.view_model.MediaPlaybackViewModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils

class VideoQuestionReplyAdapter: RecyclerView.Adapter<VideoQuestionReplyAdapter.VideoQuestionReplyViewHolder>() {

    var mediaPlaybackViewModel : MediaPlaybackViewModel? = null
    var askQuestionList :List<AskQuestionModel>? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoQuestionReplyAdapter.VideoQuestionReplyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_recently_ask_questions, parent, false)
        return VideoQuestionReplyViewHolder(view)
    }

    override fun getItemCount(): Int {
       return askQuestionList?.size!!
    }

    override fun onBindViewHolder(
        holder: VideoQuestionReplyAdapter.VideoQuestionReplyViewHolder,
        position: Int
    ) {

        holder.bindData(askQuestionList!![position])
    }

    inner class VideoQuestionReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var dataBinding: CardRecentlyAskQuestionsBinding = CardRecentlyAskQuestionsBinding.bind(itemView)

        fun bindData(askQuestionModel: AskQuestionModel) {
            dataBinding.apply {
                this.askQuestionModel = askQuestionModel
                viewModel = mediaPlaybackViewModel

                AppLog.infoLog("sixeee  ${askQuestionModel.qnaArrayList.size}")

                if (askQuestionModel.qnaArrayList.size >= 1){
                    btnReply.text = BindingUtils.string(R.string.view_reply)
                }else{
                    btnReply.text = BindingUtils.string(R.string.reply)
                }

                rvReply?.apply {
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    val videoReplyAdapter = VideoReplyAdapter(askQuestionModel.qnaArrayList)
                    adapter = videoReplyAdapter
                }
            }

            AppLog.infoLog("bindData")
        }

    }
}