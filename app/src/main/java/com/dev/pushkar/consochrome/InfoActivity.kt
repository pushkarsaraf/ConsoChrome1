package com.dev.pushkar.consochrome

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout

class InfoActivity : AppCompatActivity() {

    @BindView(R.id.recycler_view)
    var mRecyclerView: RecyclerView? = null
    private var updatesArray: ArrayList<Update>? = null
    private var adapter: PhotoAdapter? = null
    internal var mWaveSwipeRefreshLayout: WaveSwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        ButterKnife.bind(this)
        updatesArray = ArrayList()
        val context = this
        Toast.makeText(this, "Pull to refresh", Toast.LENGTH_SHORT).show()


        val query = FirebaseDatabase.getInstance()
                .getReference("updates").orderByChild("pri")

        mRecyclerView!!.layoutManager = LinearLayoutManager(this@InfoActivity)
        mRecyclerView!!.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.bottom = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
            }
        })


        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // ...
                val temp = dataSnapshot.getValue(Update::class.java)
                updatesArray!!.add(temp)
                adapter = PhotoAdapter(updatesArray, context)
                Log.d("recycler", temp!!.img)

                mRecyclerView!!.adapter = adapter
                adapter!!.notifyDataSetChanged()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // ...
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                // ...
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // ...
            }

        }
        query.addChildEventListener(childEventListener)




        mWaveSwipeRefreshLayout = findViewById<View>(R.id.main_swipe) as WaveSwipeRefreshLayout
        mWaveSwipeRefreshLayout.setColorSchemeColors(R.color.you, R.color.you)
        mWaveSwipeRefreshLayout.setOnRefreshListener {
            // Do work to refresh the list here.
            //new Task().execute();

            finish()
            startActivity(intent)
        }
        mWaveSwipeRefreshLayout.setWaveColor(Color.RED)


    }


}