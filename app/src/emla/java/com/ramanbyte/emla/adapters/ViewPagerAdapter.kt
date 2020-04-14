package com.ramanbyte.emla.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.ArrayList


/**
 * Created by Kunal Rathod
 * 7/12/19
 */
class ViewPagerAdapter(fm : FragmentManager, behaviour : Int) : FragmentStatePagerAdapter(fm,behaviour) {


    private val mFragmentTitleList = ArrayList<String>()
    private var fragmentArrayList = ArrayList<Fragment>()

    override fun getItem(position: Int): Fragment {
        return fragmentArrayList[position]
    }

    override fun getCount(): Int {
        return fragmentArrayList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (mFragmentTitleList.size > 0) mFragmentTitleList[position] else ""
    }

    override fun getItemPosition(`object`: Any): Int {
        var returnPosition = POSITION_UNCHANGED

        return returnPosition
    }


    /**
     * add fragment to list
     */
    fun addFragmentView(fragment: Fragment) {
        fragmentArrayList.add(fragment)
    }

    /**
     * add fragment to list and title to list
     */
    fun addFragmentView(fragment: Fragment, title: String) {
        fragmentArrayList.add(fragment)
        mFragmentTitleList.add(title)
    }
}