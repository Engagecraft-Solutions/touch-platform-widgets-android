package com.engagecraft.touchplatform.demo

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.engagecraft.touchplatform.sdk.Environment
import com.engagecraft.touchplatform.sdk.TouchPlatformSDK

class MainActivity : AppCompatActivity() {

    companion object {
        private const val LOG_TAG = "EC_TOUCH_DEMO"
        const val SETTINGS_REQUEST = 100
    }

    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var list: RecyclerView
    private lateinit var itemDecoration: DividerItemDecoration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PreferenceManager.setDefaultValues(this@MainActivity, R.xml.settings, false)
        findViewById<SwipeRefreshLayout>(R.id.swipeRefresh).apply {
            refresh = this
            setOnRefreshListener {
                setupList()
            }
        }
        findViewById<RecyclerView>(R.id.list).apply {
            list = this
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        itemDecoration = DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL)
        setupList()
        initWidgetsSDK()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                openSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST) {
            when (resultCode) {
                Activity.RESULT_OK -> setupWidgetsSDK { list.adapter?.notifyDataSetChanged() }
                SettingsActivity.RESULT_RELOAD -> setupWidgetsSDK { setupList() }
                SettingsActivity.RESULT_AUTH -> setupWidgetAuthState()
            }
        }
    }

    private fun setupList() {
        list.apply {
            if (useCards()) {
                removeItemDecoration(itemDecoration)
            } else {
                addItemDecoration(itemDecoration)
            }
            adapter = Adapter()
        }
        refresh.isRefreshing = false
    }

    private fun openSettings() {
        startActivityForResult(
            Intent(this, SettingsActivity::class.java),
            SETTINGS_REQUEST
        )
    }

    private fun getSettings() : SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
    }

    private fun getValue(@StringRes id: Int) : String? {
        return getSettings().getString(getString(id), null)
    }
    private fun getBooleanValue(@StringRes id: Int) : Boolean {
        return getSettings().getBoolean(getString(id), false)
    }

    private fun useCards() : Boolean {
        return getBooleanValue(R.string.settings_use_cards)
    }

    private fun setupWidget(parent: ViewGroup) {
        parent.removeAllViews()
        getValue(R.string.settings_widget_id)?.let { widgetId ->
            parent.addView(TouchPlatformSDK.getWidget(
                this@MainActivity,
                widgetId,
                getDeepLink()
            ))
            log("Create widget $widgetId")
        }
    }

    private fun initWidgetsSDK() {
        getValue(R.string.settings_client_id)?.let {
            setupWidgetsSDK()
            if (TextUtils.isEmpty(getValue(R.string.settings_widget_id))) {
                openSettings()
            }
        } ?: run {
            openSettings()
        }
    }
    private fun setupWidgetsSDK(callback: (() -> Unit)? = null) {
        getValue(R.string.settings_client_id)?.let {
            TouchPlatformSDK.init(
                    it,
                    getValue(R.string.settings_language),
                    getBooleanValue(R.string.settings_preview),
                    object : TouchPlatformSDK.Listener {
                        override fun showLogin() {
                            openSettings()
                        }
                    }
            )

            setupWidgetAuthState()

            getValue(R.string.settings_environment)?.let { env ->
                Environment.setEnvironment(env)
            }
            Environment.setDebugMode(getBooleanValue(R.string.settings_debug))

            callback?.invoke()
        }
    }

    private fun setupWidgetAuthState() {
        val userId = getValue(R.string.settings_user_id)
        if (TextUtils.isEmpty(userId))
            TouchPlatformSDK.logout()
        else
            TouchPlatformSDK.login(userId!!)
    }

    private fun getItemWrapper(parent: ViewGroup) : FrameLayout {
        return if (useCards()) {
            LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false) as FrameLayout
        } else {
            FrameLayout(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        }
    }

    private fun log(msg: String) {
        Log.d(LOG_TAG, msg)
    }

    private fun getDeepLink() : String {
        val scheme = if (getBooleanValue(R.string.settings_use_custom_scheme)) BuildConfig.APPLICATION_ID else "https"
        return "$scheme://${getString(R.string.app_link)}/widget"
    }

    abstract inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun onBind(position: Int)
    }

    inner class ItemViewHolder(parent: ViewGroup) : ViewHolder(
        getItemWrapper(parent).apply {
            addView(LayoutInflater.from(parent.context).inflate(R.layout.item_simple, parent, false))
        }
    ) {
        override fun onBind(position: Int) {
            itemView.findViewById<TextView>(R.id.text).text = String.format("Feed item #%s", position + 1)
            itemView.setOnClickListener {
                Toast.makeText(itemView.context, String.format("Clicked on #%s", position + 1), Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class WidgetViewHolder(parent: ViewGroup) : ViewHolder(
            getItemWrapper(parent).apply { setupWidget(this) }
    ) {
        override fun onBind(position: Int) {
            log("Resume/bind widget")
        }
    }

    inner class Adapter : RecyclerView.Adapter<ViewHolder>() {
        private val items = 100
        private val widgetPos = 2

        private val typeItem = 0
        private val typeWidget = 1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return when (viewType) {
                typeWidget -> WidgetViewHolder(parent)
                else -> ItemViewHolder(parent)
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.onBind(position)
        }

        override fun getItemViewType(position: Int): Int {
            return if (position == widgetPos) typeWidget else typeItem
        }

        override fun getItemCount(): Int {
            return items
        }
    }

}