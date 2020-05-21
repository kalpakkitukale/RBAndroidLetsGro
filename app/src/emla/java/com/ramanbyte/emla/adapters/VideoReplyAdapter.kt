package com.ramanbyte.emla.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardChatMsgReceiveBinding
import com.ramanbyte.databinding.CardChatMsgSendBinding
import com.ramanbyte.databinding.CardRecentlyAskQuestionsReplyBinding
import com.ramanbyte.emla.faculty.adapter.FacultyQuestionAnswerAdapter
import com.ramanbyte.emla.models.AskQuestionReplyModel
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_FACULTY

class VideoReplyAdapter(private var qnaArrayList: ArrayList<AskQuestionReplyModel>) :
    RecyclerView.Adapter<VideoReplyAdapter.VideoReplyViewHolder>() {

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoReplyAdapter.VideoReplyViewHolder {

        var chatViewHolder: VideoReplyViewHolder? = null

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            chatViewHolder = SentMessageHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.card_chat_msg_send,
                    parent,
                    false
                )
            )
            return chatViewHolder
        } else {
            chatViewHolder = ReceivedMessageHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.card_chat_msg_receive,
                    parent,
                    false
                )
            )
            return chatViewHolder
        }
        return chatViewHolder

        /*val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_recently_ask_questions_reply, parent, false)
        return VideoReplyViewHolder(view)*/
    }

    override fun getItemCount(): Int {
        return qnaArrayList.size
    }

    override fun onBindViewHolder(holder: VideoReplyAdapter.VideoReplyViewHolder, position: Int) {
        //holder.bind(qnaArrayList[position])
        if (holder is SentMessageHolder) {
            holder.bind(qnaArrayList!![position])
        } else if (holder is ReceivedMessageHolder) {
            holder.bind(qnaArrayList!![position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        //return super.getItemViewType(position)

        val userType = qnaArrayList!![position].userType

        return if (userType == KEY_FACULTY) {
            VIEW_TYPE_MESSAGE_SENT
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }

    }

    open inner class VideoReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var chatHolder: ViewDataBinding? = null

        /*var dataBinding: CardRecentlyAskQuestionsReplyBinding =
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
        }*/

    }

    inner class SentMessageHolder(itemSent: View) : VideoReplyViewHolder(itemSent) {

        //card_chat_message_sent
        var sentBinding: CardChatMsgSendBinding? = null

        init {
            sentBinding = CardChatMsgSendBinding.bind(itemView)
        }

        fun bind(askQuestionReplyModel: AskQuestionReplyModel) {
            sentBinding?.apply {
                replyModel = askQuestionReplyModel

                askQuestionReplyModel.apply {
                    if (createDateTime?.isEmpty()!!){
                        tvDateTime.text = createdDateTime
                    }else{
                        tvDateTime.text = createDateTime
                    }

                    if (userType == KEY_FACULTY)
                        verticalView.setBackgroundColor(BindingUtils.color(R.color.colorTeal))
                    else
                        verticalView.setBackgroundColor(BindingUtils.color(R.color.textColorRegOnWhite))
                }

            }
        }
    }

    inner class ReceivedMessageHolder(itemReceived: View) : VideoReplyViewHolder(itemReceived) {
        //card_chat_message_received
        var receivedBinding: CardChatMsgReceiveBinding? = null

        init {
            receivedBinding = CardChatMsgReceiveBinding.bind(itemView)
        }

        fun bind(askQuestionReplyModel: AskQuestionReplyModel) {
            receivedBinding?.apply {
                replyModel = askQuestionReplyModel

                askQuestionReplyModel.apply {
                    if (createDateTime?.isEmpty()!!){
                        tvDateTime.text = createdDateTime
                    }else{
                        tvDateTime.text = createDateTime
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