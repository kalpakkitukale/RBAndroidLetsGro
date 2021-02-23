package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCoursesBinding
import com.ramanbyte.emla.adapters.CoursesAdapter
import com.ramanbyte.emla.view_model.CoursesViewModel
import com.ramanbyte.utilities.*

class MyCourseFragment : BaseFragment<FragmentCoursesBinding, CoursesViewModel>() {
    private lateinit var mContext: Context
    private var coursesAdapter: CoursesAdapter? = null

    override val viewModelClass: Class<CoursesViewModel>
        get() = CoursesViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_courses

    override fun initiate() {

        layoutBinding.apply {
            setToolbarTitle(BindingUtils.string(R.string.courses))
            lifecycleOwner = this@MyCourseFragment
            ProgressLoader(mContext, viewModel)
            AlertDialog(mContext, viewModel)
            coursesViewModel = viewModel
            noInternet.viewModel = viewModel
            noData.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel
        }

        setAdapter()
        setViewModelOp()

    }

    private fun setAdapter() {
        layoutBinding.apply {
            rvCoursesFragment.apply {
                coursesAdapter = CoursesAdapter((activity!!).displayMetrics(), 1)
                layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                adapter = coursesAdapter?.apply {
                    this.context = mContext
                    viewModel = this@MyCourseFragment.viewModel
                }
            }
        }
    }

    //share Course Details through a link
    private fun setViewModelOp() {
        viewModel.apply {

            myCourseListPagination()
            myCoursesPagedList()?.observe(this@MyCourseFragment, androidx.lifecycle.Observer {
                it?.let { coursesAdapter?.apply { submitList(it) } }
            })
        }
    }

    var menu: Menu? = null
    private var mSearchView: SearchView? = null
    var searchItem: MenuItem? = null
    var filterItem: MenuItem? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_course_search, menu)

        searchItem = menu.findItem(R.id.action_search_key)
        filterItem = menu.findItem(R.id.action_filter)

        filterItem!!.setVisible(false)


        mSearchView = searchItem?.actionView as SearchView

        val searchEditText = mSearchView!!.findViewById(R.id.search_src_text) as EditText

        searchEditText.setTextColor(BindingUtils.color(R.color.colorIconNavyBlueInLightNGrayInDark))
        searchEditText.hint = BindingUtils.string(R.string.search_by_course_or_keywords)
        searchEditText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.sp_12)
        )

        searchEditText.setTextAppearance(R.style.AppTheme_Font)
        searchEditText.setHintTextColor(BindingUtils.color(R.color.colorTextHint))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //searchEditText.setTextCursorDrawable(BindingUtils.color(R.color.colorTextNavyBlueInLightNWhiteInDark))
        }

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
                    viewModel.searchQueryForMyCourse.postValue("")
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
                viewModel.searchQueryForMyCourse.postValue(query.toString())
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search_key -> {
                mSearchView!!.setQuery(
                    KEY_BLANK, false
                )
                mSearchView!!.clearFocus()
                mSearchView!!.isIconified = true

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


}
