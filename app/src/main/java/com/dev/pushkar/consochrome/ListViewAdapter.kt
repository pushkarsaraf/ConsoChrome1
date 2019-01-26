package com.dev.pushkar.consochrome

/**
 * Created by pushkar on 28/2/18.
 */
import java.util.ArrayList
import java.util.HashMap

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.dev.pushkar.consochrome.R

class ListViewAdapter(internal var activity: Activity, var list: ArrayList<HashMap<String, String>>) : BaseAdapter() {
    private var txtFirst: TextView? = null
    private var txtSecond: TextView? = null
    private var txtRank: TextView? = null

    override fun getCount(): Int {
        // TODO Auto-generated method stub
        return list.size
    }

    override fun getItem(position: Int): Any {
        // TODO Auto-generated method stub
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        // TODO Auto-generated method stub
        return 0
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        // TODO Auto-generated method stub


        val inflater = activity.layoutInflater

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.activity_listview, null)

            txtFirst = convertView!!.findViewById(R.id.name)
            txtSecond = convertView.findViewById(R.id.score)
            txtRank = convertView.findViewById(R.id.rank)

        }

        val map = list[position]
        txtFirst!!.text = map["first"]
        txtSecond!!.text = map["second"]
        txtRank!!.text = map["rank"]

        return convertView
    }

}