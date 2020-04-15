package com.ramanbyte.emla.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardJumpToQuestionBinding
import com.ramanbyte.emla.models.QuestionAndAnswerModel
import com.ramanbyte.emla.view_model.ShowQuestionsViewModel
import com.ramanbyte.utilities.BindingUtils

/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 15/04/20
 */
class JumpToQueAdapter(
    private val questionsList: List<QuestionAndAnswerModel>,
    private val size: Int = 0,
    private val questionId: Int = 0
) : RecyclerView.Adapter<JumpToQueAdapter.MyJumpToQueHolder>() {

    var showQuestionsViewModel: ShowQuestionsViewModel? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyJumpToQueHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_jump_to_question, parent, false)
        return MyJumpToQueHolder(view)
    }

    override fun getItemCount(): Int {
        return size
    }

    override fun onBindViewHolder(holder: MyJumpToQueHolder, position: Int) {
        holder.cardJumpToQuestionBinding.apply {
            jumpToQueViewModel = this@JumpToQueAdapter.showQuestionsViewModel
            this.position = position
            tvQueNo.text = (position + 1).toString()
            /*
            * is question is attempted
            * */
            showQuestionsViewModel?.apply {
                if (isQuestionAttempted(questionsList[position].id) ?: 0 > 0) {
                    tvQueNo.background = BindingUtils.drawable(R.drawable.circle_text_view_teal_bg)
                } else {
                    tvQueNo.background = BindingUtils.drawable(R.drawable.circle_text_view)
                }
            }

        }
    }

    class MyJumpToQueHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cardJumpToQuestionBinding: CardJumpToQuestionBinding =
            CardJumpToQuestionBinding.bind(itemView)

    }
}