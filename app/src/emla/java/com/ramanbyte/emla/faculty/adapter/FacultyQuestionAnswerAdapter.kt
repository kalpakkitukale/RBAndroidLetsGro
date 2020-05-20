package com.ramanbyte.emla.faculty.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardChatMsgReceiveBinding
import com.ramanbyte.databinding.CardChatMsgSendBinding
import com.ramanbyte.emla.models.AskQuestionReplyModel
import com.ramanbyte.utilities.KEY_FACULTY

class FacultyQuestionAnswerAdapter :
    RecyclerView.Adapter<FacultyQuestionAnswerAdapter.FacultyQuestionAnswerViewHolder>() {

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2

    var questionReplyList: ArrayList<AskQuestionReplyModel>? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FacultyQuestionAnswerAdapter.FacultyQuestionAnswerViewHolder {

        var chatViewHolder: FacultyQuestionAnswerViewHolder? = null

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
        return FacultyQuestionAnswerViewHolder(view)*/
    }

    override fun getItemCount(): Int {
        return questionReplyList?.size!!
    }

    override fun onBindViewHolder(
        holder: FacultyQuestionAnswerAdapter.FacultyQuestionAnswerViewHolder,
        position: Int
    ) {

        if (holder is SentMessageHolder) {
            holder.bind(questionReplyList!![position])
        } else if (holder is ReceivedMessageHolder) {
            holder.bind(questionReplyList!![position])
        }

    }

    override fun getItemViewType(position: Int): Int {
        //return super.getItemViewType(position)

        val userType = questionReplyList!![position].userType

        return if (userType == KEY_FACULTY) {
            VIEW_TYPE_MESSAGE_SENT
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }

    }


    open inner class FacultyQuestionAnswerViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var chatHolder: ViewDataBinding? = null
    }


    inner class SentMessageHolder(itemSent: View) : FacultyQuestionAnswerViewHolder(itemSent) {

        //card_chat_message_sent
        var sentBinding: CardChatMsgSendBinding? = null

        init {
            sentBinding = CardChatMsgSendBinding.bind(itemView)
        }

        fun bind(askQuestionReplyModel: AskQuestionReplyModel) {
            sentBinding!!.replyModel = askQuestionReplyModel
        }
    }

    inner class ReceivedMessageHolder(itemReceived: View) :
        FacultyQuestionAnswerViewHolder(itemReceived) {
        //card_chat_message_received
        var receivedBinding: CardChatMsgReceiveBinding? = null

        init {
            receivedBinding = CardChatMsgReceiveBinding.bind(itemView)
        }

        fun bind(askQuestionReplyModel: AskQuestionReplyModel) {
            receivedBinding!!.replyModel = askQuestionReplyModel
        }

    }

}