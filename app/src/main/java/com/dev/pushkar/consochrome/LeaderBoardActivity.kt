package com.dev.pushkar.consochrome

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.liangfeizc.RubberIndicator

import org.json.JSONArray

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.HashMap
import java.util.Locale

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout

import com.dev.pushkar.consochrome.Data.appTypeface

class LeaderBoardActivity : Activity() {

    private val icon_all_time = R.drawable.ic_action_refresh_all_time
    private val icon_daily = R.drawable.ic_action_refresh_daily
    private var allTimeButton: Button? = null
    private var dailyButton: Button? = null
    private val refreshButton: ImageView? = null
    private var header: TextView? = null
    private val padding = 20
    private var color_id: Int = 0
    private val JSONList: JSONArray? = null
    private val url = ""
    private val usersList: ArrayList<String>? = null
    private val scores: ArrayList<Int>? = null
    private val key_size: String? = null
    private val keys_user: Array<String>? = null
    private val keys_score: Array<String>? = null
    private val table: TableLayout? = null
    private val mRubberIndicator: RubberIndicator? = null
    private val mDetector: GestureDetectorCompat? = null

    internal var array = arrayOf<String>()
    private var list: ArrayList<HashMap<String, String>>? = null
    private var list1: ArrayList<HashMap<String, String>>? = null

    internal var listView: ListView
    internal var listView1: ListView
    internal var mWaveSwipeRefreshLayout: WaveSwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_board)
        listView = findViewById(R.id.list)
        list = ArrayList()
        listView1 = findViewById(R.id.list1)
        list1 = ArrayList()





        allTimeButton = findViewById(R.id.button1)
        //allTimeButton.setTypeface(appTypeface, Typeface.BOLD);
        //allTimeButton.setVisibility(View.GONE);

        dailyButton = findViewById(R.id.button2)
        //dailyButton.setTypeface(appTypeface, Typeface.BOLD);
        //dailyButton.setVisibility(View.GONE);

        allTimeButton!!.setOnClickListener { view -> allTimeLeaderBoard(view) }

        dailyButton!!.setOnClickListener { view -> dailyLeaderBoard(view) }
        //refreshButton = findViewById(R.id.refresh_list);
        //refreshButton.setVisibility(View.GONE);

        header = findViewById(R.id.leader_head)
        header!!.setTypeface(INSTANCE.getAppTypeface(), Typeface.BOLD)

        val temp = HashMap<String, String>()
        temp["first"] = "Name"
        temp["second"] = "Score"
        temp["rank"] = "Rank"
        list!!.add(temp)
        list1!!.add(temp)
        listView1.visibility = View.GONE


        val adapter = ListViewAdapter(this, list)
        listView.adapter = adapter
        val cal = GregorianCalendar()
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val date = df.format(cal.time)

        val adapter1 = ListViewAdapter(this, list1)
        listView1.adapter = adapter1
        Toast.makeText(this, "Pull to refresh", Toast.LENGTH_SHORT).show()


        val dref = FirebaseDatabase.getInstance().getReference("Highscore/$date").orderByChild("score")

        dref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val temp = HashMap<String, String>()
                val person: Highscore?
                Log.d("xx", "child added")
                person = dataSnapshot.getValue(Highscore::class.java)
                temp["first"] = person!!.uid
                temp["second"] = (person.score * -1).toString()
                temp["rank"] = list!!.size.toString()
                list!!.add(temp)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                //list.remove(dataSnapshot.getValue(String.class));
                //adapter.notifyDataSetChanged();
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}


            override fun onCancelled(databaseError: DatabaseError) {}
        })


        val eref = FirebaseDatabase.getInstance().getReference("Alltime/").orderByChild("score")
        eref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val temp = HashMap<String, String>()
                val person: Highscore?
                Log.d("xx", "child added")
                person = dataSnapshot.getValue(Highscore::class.java)
                temp["first"] = person!!.uid
                temp["second"] = (person.score * -1).toString()
                temp["rank"] = list1!!.size.toString()
                list1!!.add(temp)
                Log.d("alltime", person.uid)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                //list.remove(dataSnapshot.getValue(String.class));
                //adapter.notifyDataSetChanged();
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}


            override fun onCancelled(databaseError: DatabaseError) {

            }
        })



        mWaveSwipeRefreshLayout = findViewById<View>(R.id.main_swipe) as WaveSwipeRefreshLayout
        mWaveSwipeRefreshLayout.setColorSchemeColors(R.color.you, R.color.you)
        mWaveSwipeRefreshLayout.setOnRefreshListener {
            // Do work to refresh the list here.
            //new Task().execute();

            finish()
            startActivity(intent)
        }
        mWaveSwipeRefreshLayout.setWaveRGBColor(0, 0, 1)

        dailyLeaderBoard(dailyButton)

    }


    fun dailyLeaderBoard(view: View) {
        color_id = resources.getColor(R.color.color_daily)
        //	mWaveSwipeRefreshLayout.setWaveColor(color_id);
        //		dailyButton.setEnabled(false);
        //		allTimeButton.setEnabled(true);
        header!!.text = "Daily LeaderBoard"
        header!!.setTextColor(color_id)
        //		refreshButton.setImageResource(icon_daily);
        listView.visibility = View.VISIBLE
        listView1.visibility = View.GONE
    }

    fun allTimeLeaderBoard(view: View) {
        listView1.visibility = View.VISIBLE
        listView.visibility = View.GONE
        color_id = resources.getColor(R.color.color_all_time)
        ///        mWaveSwipeRefreshLayout.setWaveColor(color_id);
        //key_size = KEY_ALL_TIME_SIZE;
        //keys_user = KEY_USERS_ALL_TIME;
        //keys_score = KEY_SCORES_ALL_TIME;
        //	allTimeButton.setEnabled(false);
        //	dailyButton.setEnabled(true);
        header!!.text = "All time LeaderBoard"
        header!!.setTextColor(color_id)
        //	refreshButton.setImageResource(icon_all_time);

    }


    private fun getCell(str: String): TextView {
        val cell = TextView(this@LeaderBoardActivity)
        cell.textSize = 20f
        cell.setTypeface(INSTANCE.getAppTypeface(), Typeface.BOLD)
        cell.setPadding(padding, padding, padding, padding)
        cell.text = str
        cell.setTextColor(color_id)
        return cell
    }


    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

    fun refresh(view: View) {
        mWaveSwipeRefreshLayout.animate()
    }


    internal inner class Stuff(var name: String, var score: String)

    companion object {

        private val TAG_SCORE_ID = "score1"
        private val TAG_SUCCESS = "success"
        private val TAG_USERNAME = "username1"
        private val TAG_USERS = "users"
    }
}