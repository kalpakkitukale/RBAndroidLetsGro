package com.ramanbyte.emla.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.databinding.CardCustomCheckboxBinding
import com.ramanbyte.databinding.CardCustomRadioBinding
import com.ramanbyte.emla.data_layer.room.entities.AnswerEntity
import com.ramanbyte.emla.models.OptionsModel
import com.ramanbyte.emla.models.QuestionAndAnswerModel
import com.ramanbyte.emla.view_model.ShowQuestionsViewModel
import com.ramanbyte.utilities.*
import com.ramanbyte.utilities.DateUtils.DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS

class AnswerAdapter(var optionList: ArrayList<OptionsModel>,
                    private val questionAndAnswerModel: QuestionAndAnswerModel?) :  RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var showQuestionsViewModel: ShowQuestionsViewModel? = null
    var clear: Boolean = false
    var mContext: Context? = null
    var radioGroup: RadioGroup? = null
    var lastSelectedPosition = -1
    //var selectedOption : String = KEY_BLANK
    var currentPosition: Int = 0

    init {
        lastSelectedPosition = optionList.indexOfFirst { it.isChecked }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        var view: View? = null

        mContext = parent.context
        radioGroup = RadioGroup(mContext)

        if (viewType == KEY_RADIO) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_custom_radio, parent, false)
            return MyAnswerHolderRadio(view)
        } else {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_custom_checkbox, parent, false)
            return MyAnswerHolderCheckBox(view)
        }
    }

    override fun getItemCount(): Int {
        AppLog.infoLog("optionList_size ${optionList.size}")
        return optionList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (true) {  // if (TextUtils.isEmpty(employees.get(position).getEmail()))
            KEY_RADIO
        } else {
            KEY_CHECKBOX
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (getItemViewType(position) == KEY_RADIO) {
            //(holder as CallViewHolder).setCallDetails(employees.get(position))
            (holder as MyAnswerHolderRadio).cardCustomRadioBinding?.apply {
                this.showQuestionsViewModel = this@AnswerAdapter.showQuestionsViewModel

                AppLog.infoLog("question_idww ${optionList[currentPosition].question_Id}, $currentPosition")

                rbAnswer.isChecked =
                    optionList[position].isChecked && (lastSelectedPosition == position)

                rbAnswer.text = optionList[position].options
                rbAnswer.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            }

        } else {
            //(holder as EmailViewHolder).setEmailDetails(employees.get(position))
            (holder as MyAnswerHolderCheckBox).cardCustomCheckboxBinding?.apply {
                this.showQuestionsViewModel = this@AnswerAdapter.showQuestionsViewModel

                if (clear) {
                    cbAnswer.isChecked = false
                }
            }
        }
    }

    inner class MyAnswerHolderCheckBox(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cardCustomCheckboxBinding: CardCustomCheckboxBinding? = null

        init {
            cardCustomCheckboxBinding = CardCustomCheckboxBinding.bind(itemView)

            cardCustomCheckboxBinding?.apply {
                cbAnswer.setOnClickListener(View.OnClickListener {
                    AppLog.infoLog("cbAnswer $adapterPosition ${cbAnswer.text}")
                })
            }
        }
    }

    inner class MyAnswerHolderRadio(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cardCustomRadioBinding: CardCustomRadioBinding? = null

        init {
            cardCustomRadioBinding = DataBindingUtil.bind(itemView)

            // https://www.zoftino.com/android-recyclerview-radiobutton
            cardCustomRadioBinding?.apply {
                rbAnswer?.setOnClickListener(View.OnClickListener {
                    AppLog.infoLog("rbAnswer $adapterPosition ${rbAnswer.text}")

                    currentPosition = adapterPosition

                    this@AnswerAdapter.showQuestionsViewModel?.apply {
                        optionList[adapterPosition].apply {
                            //selectedOption = rbAnswer.text.toString()

                            this.isChecked = rbAnswer.isChecked

                            //AppLog.infoLog("localDBAns ${rbAnswer.text.toString()} $iscorrect $question_Id")

                            /*val testSubmitModel = TestSubmitModel()
                            testSubmitModel.apply {
                                this.start_Time = DateUtils.getCurDate()!!
                                this.createdDate = DateUtils.getCurDate()!!
                                this.modifiedDate = DateUtils.getCurDate()!!
                            }*/

                            val answerEntity = AnswerEntity()
                            answerEntity.apply {
                                this.quiz_Id = questionAndAnswerModel?.quizid ?: 0
                                this.question_Id = optionList[adapterPosition].question_Id
                                this.options = rbAnswer.text.toString()
                                this.iscorrect = optionList[adapterPosition].iscorrect
                                this.answer = optionList[adapterPosition].id
                                this.createdDate = DateUtils.getCurrentDateTime(DATE_WEB_API_RESPONSE_PATTERN_WITHOUT_MS)
                                this.total_Marks =
                                    if (this.iscorrect == KEY_Y) questionAndAnswerModel?.marks
                                        ?: 0.0 else 0.0
                            }

                            insertOptionLB(answerEntity)


                            /*  if (getQuestionRelatedOptionCountLB(optionList[adapterPosition].question_Id) > 0) {
                                  updateOptionLB(answerEntity)
                              } else {
                              }*/

                        }
                    }

                    lastSelectedPosition = adapterPosition
                    notifyDataSetChanged()
                })
            }

        }
    }

    fun clearAnswer() {

        /* this@AnswerAdapter.showQuestionsViewModel?.apply {
             deleteQuestionRelatedOptionLB(questionAndAnswerModel?.id ?: 0)
         }
         optionList.apply {
             forEach { it.isChecked = false }
         }
         AppLog.infoLog("que_id ${questionAndAnswerModel?.id ?: 0}")
         notifyDataSetChanged()*/
    }

}