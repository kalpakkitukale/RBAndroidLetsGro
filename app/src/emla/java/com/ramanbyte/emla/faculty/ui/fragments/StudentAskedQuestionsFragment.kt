package com.ramanbyte.emla.faculty.ui.fragments

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentStudentAskedQuestionsBinding
import com.ramanbyte.emla.faculty.adapter.StudentAskedQuestionsAdapter
import com.ramanbyte.emla.faculty.models.FacultyCoursesModel
import com.ramanbyte.emla.faculty.view.StudentAskedQuestionsFilterBottomSheet
import com.ramanbyte.emla.faculty.view_model.StudentAskedQuestionsViewModel
import com.ramanbyte.utilities.*

/**
 * A simple [Fragment] subclass.
 */
class StudentAskedQuestionsFragment :
    BaseFragment<FragmentStudentAskedQuestionsBinding, StudentAskedQuestionsViewModel>() {

    var mContext: Context? = null
    private var courseModel: FacultyCoursesModel? = null
    private var studentAskedQuestionsAdapter: StudentAskedQuestionsAdapter? = null
    private var studentAskedQuestionsFilterBottomSheet: StudentAskedQuestionsFilterBottomSheet? = null

    override val viewModelClass: Class<StudentAskedQuestionsViewModel> =
        StudentAskedQuestionsViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_student_asked_questions

    override fun initiate() {

        arguments?.apply {
            courseModel = getParcelable(KEY_COURSE_MODEL)!!
            AppLog.infoLog("courseModel ${courseModel?.courseId}")
        }

        setToolbarTitle(courseModel?.courseName!!)

        ProgressLoader(mContext!!, viewModel)
        AlertDialog(mContext!!, viewModel)

        layoutBinding.apply {
            lifecycleOwner = this@StudentAskedQuestionsFragment
            studentAskedQuestionsViewModel = viewModel
            noInternet.viewModel = viewModel
            noData.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel
        }

        setViewModelOp()
        setAdapter()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            R.id.action_search_key -> {
                mSearchView!!.setQuery(
                    KEY_BLANK, false
                )
                mSearchView!!.clearFocus()
                mSearchView!!.isIconified = true
                true
            }
            R.id.action_question_filter -> {
                studentAskedQuestionsFilterBottomSheet = StudentAskedQuestionsFilterBottomSheet()
                studentAskedQuestionsFilterBottomSheet?.show(childFragmentManager, "student_ask_question_filter")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setAdapter() {
        layoutBinding.apply {
            rvAskQuestion.apply {
                studentAskedQuestionsAdapter = StudentAskedQuestionsAdapter()
                layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)

                adapter = studentAskedQuestionsAdapter?.apply {
                    this.context = mContext
                    this.studentAskedQuestionsViewModel = viewModel
                }
            }
        }
    }

    private fun setViewModelOp() {
        viewModel.apply {

            courseId.value = courseModel?.courseId

            /*
               * Check, is filter apply or not
               * if apply then set badge
               * else don't set badge
               * */
            questionsRequestModelLiveData.observe(this@StudentAskedQuestionsFragment,
                Observer {
                    it?.apply {
                        if (isQuestionAnswered == KEY_ALL && dateWiseSort == KEY_DESCENDING) {
                            setupBadge(false)
                        } else {
                            setupBadge(true)
                        }
                    }
                })

            /*
            * API call to get question related courseID
            * */
            initPaginationResponseHandler()
            coursesPagedList()?.observe(this@StudentAskedQuestionsFragment, androidx.lifecycle.Observer {
                it?.let {
                    studentAskedQuestionsAdapter?.apply { submitList(it) }

                    layoutBinding.apply {
                        if (rvAskQuestion.visibility == View.GONE) {
                            rvAskQuestion.removeAllViews()
                            rvAskQuestion.invalidate()
                            rvAskQuestion.postDelayed({
                                rvAskQuestion.visibility = View.VISIBLE
                            }, 400)
                        }
                    }

                }


            })
        }
    }

    var menu: Menu? = null
    private var mSearchView: SearchView? = null
    var searchItem: MenuItem? = null

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_student_asked_questions, menu)

        val menuItem = menu.findItem(R.id.action_question_filter)

        val actionView = menuItem.actionView
        badgeView = actionView.findViewById(R.id.filter_badge) as View

        /*
        * Set the batch if the filter is set.
        * */
        if (viewModel.getFilterState())
            setupBadge(true)
        else
            setupBadge()

        actionView.setOnClickListener {
            onOptionsItemSelected(menuItem)
        }


        searchItem = menu.findItem(R.id.action_search_key)

        mSearchView = searchItem?.actionView as SearchView

        val searchEditText = mSearchView!!.findViewById(R.id.search_src_text) as EditText

        searchEditText.setTextColor(BindingUtils.color(R.color.colorIconNavyBlueInLightNGrayInDark))
        searchEditText.setHintTextColor(BindingUtils.color(R.color.colorTextHint))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //searchEditText.setTextCursorDrawable(BindingUtils.color(R.color.colorTextNavyBlueInLightNWhiteInDark))
        }
        searchEditText.hint = BindingUtils.string(R.string.search_by_student)

        val searchClose = mSearchView?.findViewById(R.id.search_close_btn) as ImageView
        searchClose.apply {
            setImageResource(R.drawable.ic_close_black)
            setOnClickListener {
                if (TextUtils.isEmpty(searchEditText.text.toString())) {
                    if (mSearchView?.isIconified == false) {
                        mSearchView?.isIconified = true
                    }
                } else {
                    searchEditText.setText("")
                    viewModel.searchQuery.postValue("")
                    mSearchView!!.clearFocus()
                }
            }
        }
        val searchImgId = R.id.search_button
        val searchIcon = mSearchView?.findViewById(searchImgId) as ImageView
        searchIcon.setImageResource(R.drawable.ic_baseline_search)



        mSearchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // api call
                viewModel.searchQuery.postValue(query.toString())
                mSearchView!!.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                viewModel.searchQuery.postValue(newText.toString())
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu


    }


    var badgeView: View? = null
    private fun setupBadge(showCount: Boolean? = false) {
        if (badgeView != null) {
            if (showCount == true) {
                if (badgeView?.visibility != View.VISIBLE) {
                    badgeView?.visibility = View.VISIBLE
                }
            } else {
                if (badgeView?.visibility != View.GONE) {
                    badgeView?.visibility = View.GONE
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

}
