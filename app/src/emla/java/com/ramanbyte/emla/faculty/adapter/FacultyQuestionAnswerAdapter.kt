package com.ramanbyte.emla.faculty.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardChatMsgReceiveBinding
import com.ramanbyte.databinding.CardChatMsgSendBinding
import com.ramanbyte.emla.faculty.view_model.FacultyQuestionAnswerViewModel
import com.ramanbyte.emla.models.AskQuestionReplyModel
import com.ramanbyte.utilities.KEY_FACULTY
import kotlin.collections.ArrayList

class FacultyQuestionAnswerAdapter :
    RecyclerView.Adapter<FacultyQuestionAnswerAdapter.FacultyQuestionAnswerViewHolder>() {

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2
    var questionAnswerViewModel: FacultyQuestionAnswerViewModel? = null

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
    }

    override fun getItemCount(): Int {
        return questionReplyList?.size ?:0
    }

    override fun onBindViewHolder(
        holder: FacultyQuestionAnswerAdapter.FacultyQuestionAnswerViewHolder,
        position: Int
    ) {

        if (holder is SentMessageHolder) {
            questionReplyList?.get(position)?.let { holder.bind(it) }
        } else if (holder is ReceivedMessageHolder) {
            questionReplyList?.get(position)?.let { holder.bind(it) }
        }

    }

    override fun getItemViewType(position: Int): Int {
        val userType = questionReplyList?.get(position)?.userType

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
            sentBinding?.apply {
                replyModel = askQuestionReplyModel
                viewModel = questionAnswerViewModel
            }
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
            receivedBinding?.replyModel = askQuestionReplyModel
        }
    }
}