package com.ramayan.mycom.player.sampleapp

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.os.Bundle
import com.ramayan.mycom.Model.Videos
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.ramayan.mycom.Adaptors.PlayerScreenAdapter
import com.ramayan.mycom.R
import com.ramayan.mycom.Utils.AppConstants
import com.ramayan.mycom.player.sdk.events.*

import kotlinx.android.synthetic.main.new_screen_sample.*

class SampleActivity : AppCompatActivity() {

    private var isPlayerFullscreen = false
    private var listId = "";
    private var videoList = listOf<Videos>()
    private var videoid = "";
    private var position = 0;
    lateinit var tv_title: TextView
    lateinit var tv_description: TextView
    lateinit var recycler_view: RecyclerView
//    lateinit var adView: AdView
    lateinit var seriesScroll: NestedScrollView
    lateinit var linearLayout: LinearLayout




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.right_in_transition, R.anim.left_out_transition)
        if (intent != null) {
            videoList = this.intent.getSerializableExtra(AppConstants.VIDEO_LIST) as List<Videos>
            videoid = this.intent.getStringExtra(AppConstants.VidId)
            listId = this.intent.getStringExtra(AppConstants.ListID)
            position = this.intent.getIntExtra(AppConstants.POSITION,0)
        }

        initializeContentView()
        initializePlayer(mapOf("video" to videoid))
        if (videoList != null) {
            if (videoList.get(position).description != null)
                tv_description.text = videoList.get(position).description
            if (videoList.get(position).title != null)
                tv_title.text = videoList.get(position).title
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    override fun onBackPressed() {
        AppConstants.player_back=true
        when {
            isPlayerFullscreen -> onFullScreenToggleRequested()
            playerWebView.canGoBack() -> playerWebView.goBack()
            else -> finish()
        }
        this.overridePendingTransition(0, R.anim.right_to_left_animation)
    }

    @SuppressLint("WrongConstant")
    private fun initializeContentView() {
        setContentView(R.layout.new_screen_sample)
//        adView = findViewById(R.id.adView)
        recycler_view = findViewById(R.id.recycler_view) as RecyclerView
        tv_title = findViewById(R.id.tv_title) as TextView
        tv_description = findViewById(R.id.tv_description) as TextView
        seriesScroll = findViewById(R.id.seriesScroll) as NestedScrollView
        recycler_view.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recycler_view.adapter = PlayerScreenAdapter(videoList, this) { itemDto: Videos, position: Int ->
            loadPLayerVideo(itemDto)
        }
    }

    private fun initializePlayer(params: Map<String, String>) {

        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            playerWebView.setIsWebContentsDebuggingEnabled(true)
        }
        playerWebView.setEventListener { event ->
            when (event) {
                is FullScreenToggleRequestedEvent -> onFullScreenToggleRequested()
            }
        }
        playerWebView.load(params = params)
    }

    private fun setFullScreenInternal(fullScreen: Boolean) {
        isPlayerFullscreen = fullScreen
        playerWebView.setFullscreenButton(isPlayerFullscreen)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun onFullScreenToggleRequested() {
        setFullScreenInternal(!isPlayerFullscreen)

        if (isPlayerFullscreen) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            playerWebView.layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//            adView.visibility = View.GONE
            val decorView = window.decorView
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            seriesScroll.setVisibility(View.GONE)
        } else {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            seriesScroll.setVisibility(View.VISIBLE)
//            adView.visibility = View.VISIBLE
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            playerWebView.layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (215 * resources.displayMetrics.density).toInt())
        }
    }

    private fun loadBannerAds() {
//        val adRequest = AdRequest.Builder().build()
//        adView.loadAd(adRequest)
    }

    private fun loadPLayerVideo(videos: Videos) {

        tv_description.text = videos.description
        tv_title.text = videos.title
        val params = mutableMapOf("video" to videos.id)
//
//        val playlistId = listId
//        if (playlistId.isNotEmpty()) {
//            params["playlist"] = playlistId
//        }
        playerWebView.load(params)
    }

    override fun onDestroy() {
        //        if (fb_interstitialAd != null) {
        //            fb_interstitialAd.destroy();
        //        }
        playerWebView.stopLoading()
        playerWebView.isEnded
//        if (adView != null) {
//            adView.destroy()
//        }
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        loadBannerAds()
    }
}
