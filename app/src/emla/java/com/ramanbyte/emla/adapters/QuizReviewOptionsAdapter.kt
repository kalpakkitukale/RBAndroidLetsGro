package com.ramanbyte.emla.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardQuizReviewOptionsBinding
import com.ramanbyte.emla.models.QuizReviewOptionModel
import com.ramanbyte.utilities.AppLog

class QuizReviewOptionsAdapter(var optionsList: ArrayList<QuizReviewOptionModel>) : RecyclerView.Adapter<QuizReviewOptionsAdapter.MyQuizReviewOptionsHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyQuizReviewOptionsHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_quiz_review_options, parent, false)
        return MyQuizReviewOptionsHolder(view)
    }

    override fun getItemCount(): Int {
        return optionsList.size
    }

    override fun onBindViewHolder(holder: MyQuizReviewOptionsHolder, position: Int) {
        val quizReviewOptionModel = optionsList[position]
        holder.setData(quizReviewOptionModel)
        AppLog.infoLog("onBindViewHolder")
    }

    inner class MyQuizReviewOptionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(quizReviewOptionModel: QuizReviewOptionModel) {
            cardQuizReviewOptionsBinding.quizReviewOptionModel = quizReviewOptionModel
            cardQuizReviewOptionsBinding.apply {
                tvOptions.text = quizReviewOptionModel.values
            }
        }

        val cardQuizReviewOptionsBinding= CardQuizReviewOptionsBinding.bind(itemView)

    }
}