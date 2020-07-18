package com.ramanbyte.emla.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ramanbyte.R
import com.ramanbyte.emla.models.MenuPojo
import com.ramanbyte.utilities.ViewAnimationUtils
import java.util.ArrayList
import java.util.HashMap


/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 18/7/20
 */
class NavigationDrawerExpandableListAdapter (private val context: Context, private val listDataHeader: ArrayList<MenuPojo>,
                                             private val listDataChild: HashMap<MenuPojo, ArrayList<MenuPojo>>
) : BaseExpandableListAdapter() {


    override fun getChild(groupPosition: Int, childPosititon: Int): MenuPojo {
        return this.listDataChild[this.listDataHeader[groupPosition]]!![childPosititon]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int,
                              isLastChild: Boolean, convertView: View?, parent: ViewGroup
    ): View {
        var convertView = convertView

        val childText = getChild(groupPosition, childPosition).menuName

        if (convertView == null) {
            val inflater = this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.nav_menu_child, null)
        }

        val txtListChild = convertView!!.findViewById<TextView>(R.id.lblNavChild)
        txtListChild.text = childText

        /*val imgNavChild = convertView.findViewById<ImageView>(R.id.imgNavChild)
        imgNavChild.setImageResource(getChild(groupPosition, childPosition).menuImage)*/

        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {

        return if (this.listDataChild[this.listDataHeader[groupPosition]] == null)
            0
        else
            this.listDataChild[this.listDataHeader[groupPosition]]!!.size
    }

    override fun getGroup(groupPosition: Int): MenuPojo {
        return this.listDataHeader[groupPosition]
    }

    override fun getGroupCount(): Int {
        return this.listDataHeader.size

    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean,
                              convertView: View?, parent: ViewGroup
    ): View {
        var convertView = convertView
        val headerTitle = getGroup(groupPosition).menuName
        val hasChildren = getGroup(groupPosition).hasChildren
        if (convertView == null) {
            val inflater = this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.nav_menu_header, null)
        }

        val lblListHeader = convertView!!.findViewById<TextView>(R.id.lblNavHeader)
        lblListHeader.text = headerTitle

        val imgNavHeader = convertView.findViewById<ImageView>(R.id.imgNavHeader)
        imgNavHeader.setImageResource(getGroup(groupPosition).menuImage)

        val imgNavArrow = convertView.findViewById<ImageView>(R.id.imgNavArrow)
        if (hasChildren) {
            imgNavArrow.visibility = View.VISIBLE
            if (isExpanded) {
                ViewAnimationUtils.rotateView(imgNavArrow, 0, 400)
            } else {
                ViewAnimationUtils.rotateView(imgNavArrow, 180, 400)
            }
        } else
            imgNavArrow.visibility = View.GONE

        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    fun updateUnreadCount(count: Int, position: Int) {
        var menupojo = listDataHeader.get(position)
        menupojo.unreadNotificationCount = count
        listDataHeader.set(position, menupojo)
        notifyDataSetChanged()

    }
}