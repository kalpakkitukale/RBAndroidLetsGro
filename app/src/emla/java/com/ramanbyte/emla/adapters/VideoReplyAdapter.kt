package com.ramanbyte.emla.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardRecentlyAskQuestionsReplyBinding
import com.ramanbyte.emla.models.AskQuestionReplyModel
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_FACULTY

class VideoReplyAdapter(private var qnaArrayList: ArrayList<AskQuestionReplyModel>) :
    RecyclerView.Adapter<VideoReplyAdapter.VideoReplyViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoReplyAdapter.VideoReplyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_recently_ask_questions_reply, parent, false)
        return VideoReplyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return qnaArrayList.size
    }

    override fun onBindViewHolder(holder: VideoReplyAdapter.VideoReplyViewHolder, position: Int) {
        holder.bind(qnaArrayList[position])
    }

    inner class VideoReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var dataBinding: CardRecentlyAskQuestionsReplyBinding =
            CardRecentlyAskQuestionsReplyBinding.bind(itemView)

        fun bind(replyModel: AskQuestionReplyModel) {
            dataBinding.apply {
                this.replyModel = replyModel

                replyModel.apply {
                    if (createDateTime?.isEmpty()!!){
                        tvDateTime.text = replyModel.createdDateTime
                    }else{
                        tvDateTime.text = replyModel.createDateTime
                    }

                    if (userType == KEY_FACULTY)
                        verticalView.setBackgroundColor(BindingUtils.color(R.color.colorTeal))
                    else
                        verticalView.setBackgroundColor(BindingUtils.color(R.color.textColorRegOnWhite))
                }

            }
        }

    }
}