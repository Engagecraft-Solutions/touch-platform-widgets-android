package com.engagecraft.touchplatform.demo

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.text.SimpleDateFormat
import java.util.*


class LogActivity : AppCompatActivity() {

    companion object {
        private const val LOG_TAG = "EC_TOUCH_DEMO"

        private val logList = mutableListOf<Pair<String, String>>()

        private var instance: LogActivity? = null

        fun log(msg: String) {
            Log.d(LOG_TAG, msg)
            saveLog("$LOG_TAG $msg")
        }

        @Synchronized
        fun saveLog(msg: String) {
            logList.add(
                0,
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).format(Date()) to msg
            )
            instance?.list?.adapter?.notifyItemInserted(0)
        }
    }

    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var list: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        findViewById<SwipeRefreshLayout>(R.id.swipeRefresh).apply {
            refresh = this
            setOnRefreshListener {
                list.adapter?.notifyDataSetChanged()
                refresh.isRefreshing = false
            }
        }
        findViewById<RecyclerView>(R.id.log).apply {
            list = this
            layoutManager = LinearLayoutManager(this@LogActivity)
            adapter = Adapter()
            addItemDecoration(
                DividerItemDecoration(
                    this@LogActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        instance = this
        instance?.list?.adapter?.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        instance = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_log, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_log_clear -> {
                logList.clear()
                list.adapter?.notifyDataSetChanged()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    android.R.layout.simple_list_item_2,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: Adapter.ViewHolder, position: Int) {
            holder.itemView.apply {
                findViewById<TextView>(android.R.id.text1).text = logList[position].first
                findViewById<TextView>(android.R.id.text2).apply {
                    text = logList[position].second
                    setTextIsSelectable(true)
                }
            }
        }

        override fun getItemCount(): Int {
            return logList.size
        }
    }

}