package com.ramanbyte.emla.ui.activities

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.snackbar.Snackbar
import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityMediaPlaybackBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.content.ExoMediaDownloadUtil
//import com.ramanbyte.emla.view.OnSwipeTouchListener
import com.ramanbyte.emla.view_model.MediaPlaybackViewModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_IS_MEDIA_OFFLINE
import com.ramanbyte.utilities.KEY_MEDIA_ID
import kotlinx.android.synthetic.emla.activity_media_playback.*
import kotlinx.android.synthetic.emla.exo_comment_layout.*
import kotlinx.android.synthetic.emla.exo_playback_control_view.*
import org.kodein.di.On
import com.ramanbyte.utilities.*

class MediaPlaybackActivity : BaseActivity<ActivityMediaPlaybackBinding, MediaPlaybackViewModel>(
    authModuleDependency
) {

    var layoutInflaterr: LayoutInflater? = null
    var view1: View? = null
    var constraintSet: ConstraintSet? = null

    override val viewModelClass: Class<MediaPlaybackViewModel> = MediaPlaybackViewModel::class.java

    override fun layoutId(): Int = R.layout.activity_media_playback

    var simpleExoPlayer: SimpleExoPlayer? = null
    lateinit var dataSourceFactory: DefaultDataSourceFactory

    var url = ""
    var isOffLine: Boolean = false
    var mediaId: Int = 0


    override fun initiate() {

        intent.extras?.apply {
            mediaId = getInt(KEY_MEDIA_ID) ?: 0
            isOffLine = getBoolean(KEY_IS_MEDIA_OFFLINE) ?: false
        }

        viewModel.getMediaInfo(mediaId)

        url = viewModel.mediaInfoModel?.mediaUrl ?: ""

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        layoutInflaterr = LayoutInflater.from(this)
        view1 = layoutInflaterr?.inflate(R.layout.exo_comment_layout, null, false)
        constraintSet = ConstraintSet()

        exoBtnComment.setOnClickListener(View.OnClickListener {
            exoBtnComment.visibility = View.INVISIBLE
            constraintSet?.clone(mainConstraint)
            mainConstraint?.addView(view1)

            closeComment.setOnClickListener(View.OnClickListener {
                mainConstraint?.removeView(view1)
                constraintSet?.clone(mainConstraint)
                normalConstrains()
                exoBtnComment.visibility = View.VISIBLE
            })

            landscapeConstrains()
        })

        /*doubleTapBackward.setOnTouchListener(object : OnSwipeTouchListener(this@MediaPlaybackActivity){

            override fun onDoubleTap() {
                simpleExoPlayer?.seekTo(simpleExoPlayer!!.currentPosition - 12000)
            }
            override fun onSwipeTop() {
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                var maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                var currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                if(currentVolume < maxVolume){
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume+2,0)
                }
            }

            @RequiresApi(Build.VERSION_CODES.P)
            override fun onSwipeBottom() {
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                var currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                var minVolume = audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC)
                if(currentVolume > minVolume){
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume-2,0)
                }
            }
        })*/

        /*doubleTapForward.setOnTouchListener(object : OnSwipeTouchListener(this@MediaPlaybackActivity){

            override fun onDoubleTap() {
                simpleExoPlayer?.seekTo(simpleExoPlayer!!.currentPosition + 12000)
            }
            override fun onSwipeTop() {
                val curBrightnessValue = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
                val SysBackLightValue = curBrightnessValue + 25
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, SysBackLightValue)
            }
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onSwipeBottom() {
                val curBrightnessValue = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
                val SysBackLightValue = curBrightnessValue - 25
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, SysBackLightValue)
            }
        })*/

        var isLikeClick: Boolean = false
        var isUnlikeClick: Boolean = false
        var addToWishList: Boolean = false

        viewModel.apply {

            /*
            * Click event for the Like
            * */
            exoBtnLike.setOnClickListener(View.OnClickListener {
                if (NetworkConnectivity.isConnectedToInternet()) {
                    if (isUnlikeClick) {
                        AppLog.infoLog("BtnLike :: true -- Y")
                        insertSectionContentLog(it,KEY_LIKE_VIDEO, KEY_Y, KEY_BLANK, mediaId)
                        exoBtnLike.setImageDrawable(BindingUtils.drawable(R.drawable.ic_thumb_up_checked))
                        isLikeClick = true
                        exoBtnDislike.setImageDrawable(BindingUtils.drawable(R.drawable.ic_thumb_down_exo))
                        isUnlikeClick = false
                    } else {
                        if (isLikeClick) {
                            AppLog.infoLog("BtnLike :: false -- ")
                            insertSectionContentLog(it,KEY_LIKE_VIDEO, KEY_BLANK, KEY_BLANK, mediaId)
                            exoBtnLike.setImageDrawable(BindingUtils.drawable(R.drawable.ic_thumb_up))
                            isLikeClick = false
                        } else {
                            AppLog.infoLog("BtnLike :: true -- Y")
                            insertSectionContentLog(it,KEY_LIKE_VIDEO, KEY_Y, KEY_BLANK, mediaId)
                            exoBtnLike.setImageDrawable(BindingUtils.drawable(R.drawable.ic_thumb_up_checked))
                            isLikeClick = true
                        }
                    }
                } else {
                    it.snackbar(BindingUtils.string(R.string.no_internet_message))
                }
            })


            /*
            * Click event for the Unlike
            * */
            exoBtnDislike.setOnClickListener(View.OnClickListener {

                if (NetworkConnectivity.isConnectedToInternet()) {

                    if (isLikeClick) {
                        AppLog.infoLog("BtnUnlike :: true -- N")
                        insertSectionContentLog(it,KEY_LIKE_VIDEO, KEY_N, KEY_BLANK, mediaId)
                        exoBtnDislike.setImageResource(R.drawable.ic_thumb_down_checked)
                        isUnlikeClick = true
                        exoBtnLike.setImageDrawable(BindingUtils.drawable(R.drawable.ic_thumb_up))
                        isLikeClick = false
                    } else {
                        if (isUnlikeClick) {
                            AppLog.infoLog("BtnUnlike :: false -- ")
                            insertSectionContentLog(it,KEY_LIKE_VIDEO, KEY_BLANK, KEY_BLANK, mediaId)
                            exoBtnDislike.setImageDrawable(BindingUtils.drawable(R.drawable.ic_thumb_down_exo))
                            isUnlikeClick = false
                        } else {
                            AppLog.infoLog("BtnUnlike :: true -- N")
                            insertSectionContentLog(it,KEY_LIKE_VIDEO, KEY_N, KEY_BLANK, mediaId)
                            exoBtnDislike.setImageDrawable(BindingUtils.drawable(R.drawable.ic_thumb_down_checked))
                            isUnlikeClick = true
                        }
                    }
                } else {
                    it.snackbar(BindingUtils.string(R.string.no_internet_message))
                }
            })

            /*
            * Click event for the Add to wish list
            * */
            exoBtnWishlist.setOnClickListener(View.OnClickListener {
                if (NetworkConnectivity.isConnectedToInternet()) {
                    if (addToWishList) {
                        AppLog.infoLog("BtnWishlist :: false -- ")
                        insertSectionContentLog(it,KEY_FAVOURITE_VIDEO, KEY_BLANK, KEY_BLANK, mediaId)
                        exoBtnWishlist.setImageDrawable(BindingUtils.drawable(R.drawable.ic_heart))
                        addToWishList = false
                    } else {
                        AppLog.infoLog("BtnWishlist :: true -- Y")
                        insertSectionContentLog(it,KEY_FAVOURITE_VIDEO, KEY_BLANK, KEY_Y, mediaId)
                        exoBtnWishlist.setImageDrawable(BindingUtils.drawable(R.drawable.ic_heart_checked))
                        addToWishList = true
                    }
                } else {
                    it.snackbar(BindingUtils.string(R.string.no_internet_message))
                }
            })

        }
    }

    override fun onStart() {
        super.onStart()

        layoutBinding?.apply {

            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                this@MediaPlaybackActivity,
                DefaultTrackSelector()
            )

            requestedOrientation = (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL)

            playerView.player = simpleExoPlayer

            var mediaSource: ProgressiveMediaSource? = null

            if (isOffLine) {
                /*
                * User is offline
                * */
                dataSourceFactory = DefaultDataSourceFactory(
                    applicationContext,
                    Util.getUserAgent(
                        applicationContext,
                        BindingUtils.string(R.string.app_name)
                    )
                )
                val cacheDataSourceFactory = CacheDataSourceFactory(
                    ExoMediaDownloadUtil.getDownloadCache(applicationContext),
                    dataSourceFactory
                )
                mediaSource =
                    ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                        .createMediaSource(Uri.parse(url))
            } else {
                /*
                * User is online
                * */
                dataSourceFactory = DefaultDataSourceFactory(
                    applicationContext,
                    Util.getUserAgent(
                        applicationContext,
                        BindingUtils.string(R.string.app_name)
                    )
                )
                mediaSource =
                    ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(url))
            }

            simpleExoPlayer?.also { player ->

                viewModel.mediaInfoModel?.apply {
                    player.prepare(
                        mediaSource,
                        seekPosition == 0.toLong(),
                        seekPosition == 0.toLong()
                    )

                    player.seekTo(seekPosition!!)

                    player.playWhenReady = true

                    duration = player.duration

                    AppLog.infoLog("Duration :: ${player.duration}")
                    AppLog.infoLog("Duration :: ${player.contentDuration}")

                }
                /* viewModel.apply {

                     if (seekToTime != 0.toLong()) {

                         seekTo(seekToTime)
                     }
                 }*/
            }

        }
    }

    override fun onStop() {
        super.onStop()

        layoutBinding?.playerView?.player = null
        simpleExoPlayer?.apply {

            viewModel.apply {

                mediaInfoModel?.apply {

                    seekPosition = currentPosition

                    updateMediaInfo(this)
                }
            }

            release()
        }
        simpleExoPlayer = null
    }

    fun landscapeConstrains() {
        constraintSet?.connect(
            R.id.player_view,
            ConstraintSet.END,
            R.id.guidelineHorizontal,
            ConstraintSet.START,
            0
        )
        constraintSet?.connect(
            R.id.player_view,
            ConstraintSet.BOTTOM,
            R.id.mainConstraint,
            ConstraintSet.BOTTOM,
            0
        )
        constraintSet?.connect(
            R.id.commentLayout,
            ConstraintSet.START,
            R.id.guidelineHorizontal,
            ConstraintSet.END,
            0
        )
        constraintSet?.connect(
            R.id.commentLayout,
            ConstraintSet.END,
            R.id.mainConstraint,
            ConstraintSet.END,
            0
        )
        constraintSet?.connect(
            R.id.commentLayout,
            ConstraintSet.TOP,
            R.id.mainConstraint,
            ConstraintSet.TOP,
            0
        )
        constraintSet?.connect(
            R.id.commentLayout,
            ConstraintSet.BOTTOM,
            R.id.mainConstraint,
            ConstraintSet.BOTTOM,
            0
        )
        constraintSet?.applyTo(mainConstraint)
    }

    fun normalConstrains() {
        Log.d("OnMethods", "..normalConstrains")
        constraintSet?.connect(
            R.id.player_view,
            ConstraintSet.START,
            R.id.mainConstraint,
            ConstraintSet.START,
            0
        )
        constraintSet?.connect(
            R.id.player_view,
            ConstraintSet.END,
            R.id.mainConstraint,
            ConstraintSet.END,
            0
        )
        constraintSet?.connect(
            R.id.player_view,
            ConstraintSet.TOP,
            R.id.mainConstraint,
            ConstraintSet.TOP,
            0
        )
        constraintSet?.connect(
            R.id.player_view,
            ConstraintSet.BOTTOM,
            R.id.mainConstraint,
            ConstraintSet.BOTTOM,
            0
        )
        constraintSet?.applyTo(mainConstraint)
    }

}
