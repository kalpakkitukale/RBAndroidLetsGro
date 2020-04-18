package com.ramanbyte.emla.ui.fragments

import android.content.Context
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramanbyte.R
import com.ramanbyte.base.BaseFragment
import com.ramanbyte.databinding.FragmentCoursesBinding
import com.ramanbyte.emla.adapters.CoursesAdapter
import com.ramanbyte.emla.view.RecommendedCourseFilterBottomSheet
import com.ramanbyte.emla.view_model.CoursesViewModel
import com.ramanbyte.utilities.*

/**
 * @author Vinay Kumbhar <vinay.k@ramanbyte.com>
 * @since 14/04/2020
 */
class CoursesFragment : BaseFragment<FragmentCoursesBinding, CoursesViewModel>() {
    private lateinit var mContext: Context
    private var coursesAdapter: CoursesAdapter? = null
    private var courseFilterBottomSheet: RecommendedCourseFilterBottomSheet? = null

    override val viewModelClass: Class<CoursesViewModel>
        get() = CoursesViewModel::class.java

    override fun layoutId(): Int = R.layout.fragment_courses

    override fun initiate() {

        if (!viewModel.isUserActive()) {
            findNavController().navigate(R.id.action_coursesFragment_to_learnerProfileFragment)
            return
        }

        layoutBinding.apply {
            setToolbarTitle(BindingUtils.string(R.string.courses))
            lifecycleOwner = this@CoursesFragment
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
                coursesAdapter = CoursesAdapter((activity!!).displayMetrics())
                layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                adapter = coursesAdapter?.apply {
                    this.context = mContext
                    viewModel = this@CoursesFragment.viewModel
                }
            }
        }
    }

    private fun setViewModelOp() {
        viewModel.apply {

            initPaginationResponseHandler()
            coursesPagedList()?.observe(this@CoursesFragment, androidx.lifecycle.Observer {
                it?.let { coursesAdapter?.apply { submitList(it) } }
            })


            isFilterApplied.observe(this@CoursesFragment, Observer {
                it?.let {
                    setupBadge(it)
                    isFilterApplied.postValue(null)
                }
            })
        }
    }

    var menu: Menu? = null
    private var mSearchView: SearchView? = null
    var searchItem: MenuItem? = null
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_course_search, menu)

        val menuItem = menu.findItem(R.id.action_filter)

        val actionView = menuItem.actionView
        badgeView = actionView.findViewById(R.id.filter_badge) as View
        setupBadge()
        actionView.setOnClickListener {
            onOptionsItemSelected(menuItem)
        }

        searchItem = menu.findItem(R.id.action_search_key)

        mSearchView = searchItem?.actionView as SearchView

        val searchEditText = mSearchView!!.findViewById(R.id.search_src_text) as EditText

        searchEditText.setTextColor(BindingUtils.color(R.color.colorWhite))
        searchEditText.setHintTextColor(BindingUtils.color(R.color.colorTransparent))
        searchEditText.hint = BindingUtils.string(R.string.search_by_course)

        val searchClose = mSearchView?.findViewById(R.id.search_close_btn) as ImageView
        searchClose.apply {
            setImageResource(R.drawable.ic_close_white)
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
        searchIcon.setImageResource(R.drawable.ic_search)



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
            R.id.action_filter -> {
                courseFilterBottomSheet = RecommendedCourseFilterBottomSheet()
                courseFilterBottomSheet?.show(childFragmentManager, "course_filter")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    override fun onPause() {
        AppLog.infoLog("NirajMethod :: onPause")
        super.onPause()
    }

    override fun onResume() {
        AppLog.infoLog("NirajMethod :: onResume")
        super.onResume()
    }

    override fun onStart() {
        AppLog.infoLog("NirajMethod :: onStart")
        super.onStart()
    }
}
