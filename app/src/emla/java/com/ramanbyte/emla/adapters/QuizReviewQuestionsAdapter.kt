package com.ramanbyte.emla.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardQuizReviewBinding
import com.ramanbyte.emla.models.QuizReviewModel
import com.ramanbyte.emla.view_model.ShowQuestionsViewModel

class QuizReviewQuestionsAdapter  :
    PagedListAdapter<QuizReviewModel, QuizReviewQuestionsAdapter.QuizReviewQuestionsHolder>(
        DIFF_CALLBACK
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizReviewQuestionsHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_quiz_review, parent, false)
        return QuizReviewQuestionsHolder(view)
    }

    override fun onBindViewHolder(holder: QuizReviewQuestionsHolder, position: Int) {
        val quizReviewModel: QuizReviewModel = getItem(position)!!

        //holder.onBind(quizReviewModel)
        holder.cardQuizReviewBinding?.apply {
            this.quizReviewModel = quizReviewModel
            this.queNo = (position + 1).toString()


            rvQuizReview?.apply {

                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                val quizReviewOptionsAdapter = QuizReviewOptionsAdapter(quizReviewModel.options)
                adapter = quizReviewOptionsAdapter
            }
        }
    }

    var quizReviewViewModel: ShowQuestionsViewModel? = null

    /* override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
         var view : View ? = null

         view = LayoutInflater.from(parent.context)
             .inflate(R.layout.card_quiz_review, parent, false)
         return QuizReviewQuestionsHolder(view)
     }

     override fun getItemCount(): Int {
         return 5
     }

     override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
         AppLog.infoLog("onBindViewHolder")

         holder.card

     }*/

    inner class QuizReviewQuestionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cardQuizReviewBinding: CardQuizReviewBinding? = null

        init {
            cardQuizReviewBinding = CardQuizReviewBinding.bind(itemView)
        }

        /*fun onBind(quizReviewModel: QuizReviewModel ){
            cardQuizReviewBinding?.llOptions?.apply {

                quizReviewModel.options.map {model->
                    val textView = BulletTextView(itemView.context)
                    textView.setText(model.values)
                    textView.bullt_visible = true
                    textView.radious = 5f
                    addView(textView)
                }
            }
        }*/
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<QuizReviewModel>() {
            override fun areItemsTheSame(
                oldItem: QuizReviewModel,
                newItem: QuizReviewModel
            ): Boolean {
                return oldItem.questionId == newItem.questionId
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: QuizReviewModel,
                newItem: QuizReviewModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}