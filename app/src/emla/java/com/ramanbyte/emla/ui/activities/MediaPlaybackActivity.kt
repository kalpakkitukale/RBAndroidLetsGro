package com.ramanbyte.emla.ui.activities

import android.net.Uri
import android.view.View
import android.view.WindowManager
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityMediaPlaybackBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.content.ExoMediaDownloadUtil
import com.ramanbyte.emla.view_model.MediaPlaybackViewModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_IS_MEDIA_OFFLINE
import com.ramanbyte.utilities.KEY_MEDIA_ID

class MediaPlaybackActivity : BaseActivity<ActivityMediaPlaybackBinding, MediaPlaybackViewModel>(
    authModuleDependency) {

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
    }

    override fun onStart() {
        super.onStart()

        layoutBinding?.apply {

            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                this@MediaPlaybackActivity,
                DefaultTrackSelector()
            )

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

}
