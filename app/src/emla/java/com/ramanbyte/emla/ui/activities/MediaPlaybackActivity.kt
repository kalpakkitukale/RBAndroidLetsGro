package com.ramanbyte.emla.ui.activities

import android.content.Context
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityMediaPlaybackBinding
import com.ramanbyte.databinding.ExoCommentLayoutBinding
import com.ramanbyte.emla.adapters.VideoQuestionReplyAdapter
import com.ramanbyte.emla.adapters.VideoReplyAdapter
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.content.ExoMediaDownloadUtil
import com.ramanbyte.emla.models.AskQuestionModel
import com.ramanbyte.emla.models.AskQuestionReplyModel
import com.ramanbyte.emla.view.OnSwipeTouchListener
import com.ramanbyte.emla.view_model.MediaPlaybackViewModel
import com.ramanbyte.utilities.AppLog
import com.ramanbyte.utilities.BindingUtils
import com.ramanbyte.utilities.KEY_IS_MEDIA_OFFLINE
import com.ramanbyte.utilities.KEY_MEDIA_ID
import kotlinx.android.synthetic.emla.activity_media_playback.*
import kotlinx.android.synthetic.emla.exo_playback_control_view.*
import com.ramanbyte.utilities.*

class MediaPlaybackActivity : BaseActivity<ActivityMediaPlaybackBinding, MediaPlaybackViewModel>(
    authModuleDependency,
    isLandscape = true
) {

    var layoutInflaterr: LayoutInflater? = null
    var view1: View? = null
    var constraintSet: ConstraintSet? = null

    var isLikeClick: Boolean = false
    var isUnlikeClick: Boolean = false
    var addToWishList: Boolean = false

    var videoQuestionReplyAdapter: VideoQuestionReplyAdapter? = null
    var videoReplyAdapter: VideoReplyAdapter? = null

    var exoCommentLayoutBinding: ExoCommentLayoutBinding? = null

    override val viewModelClass: Class<MediaPlaybackViewModel> = MediaPlaybackViewModel::class.java

    override fun layoutId(): Int = R.layout.activity_media_playback

    var simpleExoPlayer: SimpleExoPlayer? = null
    lateinit var dataSourceFactory: DefaultDataSourceFactory

    var url = ""
    var isOffLine: Boolean = false
    var mediaId: Int = 0


    override fun initiate() {

        //window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        ProgressLoader(this, viewModel)
        AlertDialog(this, viewModel)

        intent.extras?.apply {
            mediaId = getInt(KEY_MEDIA_ID) ?: 0
            isOffLine = getBoolean(KEY_IS_MEDIA_OFFLINE) ?: false
        }

        viewModel.getMediaInfo(mediaId)

        url = viewModel.mediaInfoModel?.mediaUrl ?: ""

        /*
        * Set selection for like unlike and add to wish list
        * */
        viewModel.mediaInfoModel?.apply {
            when (likeVideo) {
                KEY_Y -> {
                    exoBtnLike.setImageDrawable(BindingUtils.drawable(R.drawable.ic_thumb_up_checked))
                    isLikeClick = true
                }
                KEY_N -> {
                    exoBtnDislike.setImageDrawable(BindingUtils.drawable(R.drawable.ic_thumb_down_checked))
                    isUnlikeClick = true
                }
                else -> {
                    exoBtnLike.setImageDrawable(BindingUtils.drawable(R.drawable.ic_thumb_up))
                    isLikeClick = false
                    exoBtnDislike.setImageDrawable(BindingUtils.drawable(R.drawable.ic_thumb_down_exo))
                    isUnlikeClick = false
                }
            }

            addToWishList = if (favouriteVideo == KEY_Y) {
                // lblWishList.setCompoundDrawables(BindingUtils.drawable(R.drawable.ic_heart_checked),null,null,null)
                exoBtnWishlist.setImageDrawable(BindingUtils.drawable(R.drawable.ic_heart_checked))
                true
            } else {
                exoBtnWishlist.setImageDrawable(BindingUtils.drawable(R.drawable.ic_heart))
                false
            }
        }

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

        //val dataBinding: ExoCommentLayoutBinding = DataBindingUtil.setContentView(this, R.layout.exo_comment_layout)
        exoCommentLayoutBinding = ExoCommentLayoutBinding.bind(view1!!)

        exoCommentLayoutBinding?.apply {
            lifecycleOwner = this@MediaPlaybackActivity
            mediaPlaybackViewModel = viewModel
            noData.viewModel = viewModel
            noInternet.viewModel = viewModel
            somethingWentWrong.viewModel = viewModel
            replyLayout.apply {
                mediaPlaybackViewModel = viewModel
                noData.viewModel = viewModel
                noInternet.viewModel = viewModel
                somethingWentWrong.viewModel = viewModel
            }
        }

        constraintSet = ConstraintSet()

        userCommentLayout.setOnClickListener(View.OnClickListener {
            exoBtnComment.visibility = View.GONE
            lblComment.visibility = View.GONE
            constraintSet?.clone(mainConstraint)
            mainConstraint?.addView(view1)

            landscapeConstrains()

            viewModel.apply {

                /*
                * Call the API to get the question list from server.
                * */
                getQuestionAndAnswer()

                /*
                * Set the Question List Adapter
                * */
                questionAndAnswerListLiveData.observe(this@MediaPlaybackActivity, Observer {
                    if (it != null) {
                        exoCommentLayoutBinding?.apply {
                            questionLayout.visibility = View.VISIBLE
                            replyLayout.replyContainerLayout.visibility = View.GONE
                        }
                        enteredQuestionLiveData.value = KEY_BLANK
                        exoCommentLayoutBinding?.rvComment?.apply {
                            videoQuestionReplyAdapter = VideoQuestionReplyAdapter()
                            videoQuestionReplyAdapter?.apply {
                                layoutManager =
                                    LinearLayoutManager(
                                        this@MediaPlaybackActivity,
                                        RecyclerView.VERTICAL,
                                        false
                                    )
                                mediaPlaybackViewModel = viewModel
                                askQuestionList = it
                                (layoutManager as LinearLayoutManager).isSmoothScrollbarEnabled = true
                                (layoutManager as LinearLayoutManager).scrollToPosition(itemCount - 1)
                                adapter = this
                            }
                        }
                    }
                })

                conversationCloseLiveData.observe(this@MediaPlaybackActivity, Observer {
                    if (it != null) {
                        exoCommentLayoutBinding?.apply {
                            questionLayout.visibility = View.VISIBLE
                            replyLayout.replyContainerLayout.visibility = View.GONE
                        }
                    }
                })
            }

        })

        viewModel.apply {
            onClickCloseCommentLiveData.observe(this@MediaPlaybackActivity, Observer {
                if (it != null) {
                    if (it == true) {
                        mainConstraint?.removeView(view1)
                        constraintSet?.clone(mainConstraint)
                        normalConstrains()
                        exoBtnComment.visibility = View.VISIBLE
                        lblComment.visibility = View.VISIBLE
                        onClickCloseCommentLiveData.value = false
                    }
                }
            })

            onClickSendQuestionLiveData.observe(this@MediaPlaybackActivity, Observer {
                if (it != null) {
                    if (it == true) {
                        val question = exoCommentLayoutBinding?.etAskQuestion?.text.toString()
                        if (enteredQuestionLiveData.value?.isNotBlank()!!) {
                            insertAskQuestion(question)
                        } else {
                            AppLog.infoLog("Blank Question not added.")
                        }
                        onClickSendQuestionLiveData.value = false
                    }
                }
            })

            /*
            * This is for Reply or ViewReply
            * */
            onClickReplyLiveData.observe(this@MediaPlaybackActivity, Observer {
                if (it != null) {
                    exoCommentLayoutBinding?.apply {
                        questionLayout.visibility = View.GONE
                        replyLayout.apply {
                            replyContainerLayout.visibility = View.VISIBLE
                            askQuestionModel = it

                            /*
                            * Conversation is close for the particular question the set GONE
                            * */
                            if (it.isConversionOpen == KEY_N){
                                lblAskNewQuestion.visibility = View.GONE
                                etAddReply.visibility = View.GONE
                                ivAddReply.visibility = View.GONE
                            }else{
                                lblAskNewQuestion.visibility = View.VISIBLE
                                etAddReply.visibility = View.VISIBLE
                                ivAddReply.visibility = View.VISIBLE
                            }
                            /*strUserPic = it.userPic
                            characterDrawable = it.setCharacterDrawable*/
                        }

                        /*
                        * call API for the question
                        * */
                        getConversationData(it.questionId)
                    }

                    observerForAddReplyScreen(it)

                    onClickReplyLiveData.value = null
                }
            })
        }



        doubleTapBackward.setOnTouchListener(object :
            OnSwipeTouchListener(this@MediaPlaybackActivity) {

            override fun onDoubleTap() {
                simpleExoPlayer?.seekTo(simpleExoPlayer!!.currentPosition - 12000)
            }

            override fun onSwipeTop() {
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                if (currentVolume < maxVolume) {
                    audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_SHOW_UI
                    )
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume + 2, 0)
                }
            }

            @RequiresApi(Build.VERSION_CODES.P)
            override fun onSwipeBottom() {
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                var currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                var minVolume = audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC)
                if (currentVolume > minVolume) {
                    audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_SHOW_UI
                    )
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume - 2, 0)
                }
            }
        })

        doubleTapForward.setOnTouchListener(object :
            OnSwipeTouchListener(this@MediaPlaybackActivity) {

            override fun onDoubleTap() {
                simpleExoPlayer?.seekTo(simpleExoPlayer!!.currentPosition + 12000)
            }

            override fun onSwipeTop() {
                val curBrightnessValue =
                    Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
                val SysBackLightValue = curBrightnessValue + 25
                Settings.System.putInt(
                    contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    SysBackLightValue
                )
            }

            @RequiresApi(Build.VERSION_CODES.P)
            override fun onSwipeBottom() {
                val curBrightnessValue =
                    Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
                val SysBackLightValue = curBrightnessValue - 25
                Settings.System.putInt(
                    contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    SysBackLightValue
                )
            }
        })

        viewModel.apply {

            /*
            * Click event for the Like
            * */
            likeLayout.setOnClickListener(View.OnClickListener {
                if (NetworkConnectivity.isConnectedToInternet()) {
                    if (isUnlikeClick) {
                        AppLog.infoLog("BtnLike :: true -- Y")
                        insertSectionContentLog(mediaId, mediaInfoModel?.apply {
                            likeVideo = KEY_Y
                        }!!)
                        exoBtnLike.setImageDrawable(BindingUtils.drawable(R.drawable.ic_thumb_up_checked))
                        isLikeClick = true
                        exoBtnDislike.setImageDrawable(BindingUtils.drawable(R.drawable.ic_thumb_down_exo))
                        isUnlikeClick = false
                    } else {
                        if (isLikeClick) {
                            AppLog.infoLog("BtnLike :: false -- ")
                            insertSectionContentLog(mediaId, mediaInfoModel?.apply {
                                likeVideo = KEY_BLANK
                            }!!)
                            exoBtnLike.setImageDrawable(BindingUtils.drawable(R.drawable.ic_thumb_up))
                            isLikeClick = false
                        } else {
                            AppLog.infoLog("BtnLike :: true -- Y")
                            insertSectionContentLog(mediaId, mediaInfoModel?.apply {
                                likeVideo = KEY_Y
                            }!!)
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
            disLikeLayout.setOnClickListener(View.OnClickListener {

                if (NetworkConnectivity.isConnectedToInternet()) {

                    if (isLikeClick) {
                        AppLog.infoLog("BtnUnlike :: true -- N")
                        insertSectionContentLog(mediaId, mediaInfoModel?.apply {
                            likeVideo = KEY_N
                        }!!)
                        exoBtnDislike.setImageResource(R.drawable.ic_thumb_down_checked)
                        isUnlikeClick = true
                        exoBtnLike.setImageDrawable(BindingUtils.drawable(R.drawable.ic_thumb_up))
                        isLikeClick = false
                    } else {
                        if (isUnlikeClick) {
                            AppLog.infoLog("BtnUnlike :: false -- ")
                            insertSectionContentLog(mediaId, mediaInfoModel?.apply {
                                likeVideo = KEY_BLANK
                            }!!)
                            exoBtnDislike.setImageDrawable(BindingUtils.drawable(R.drawable.ic_thumb_down_exo))
                            isUnlikeClick = false
                        } else {
                            AppLog.infoLog("BtnUnlike :: true -- N")
                            insertSectionContentLog(mediaId, mediaInfoModel?.apply {
                                likeVideo = KEY_N
                            }!!)
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
            wishListLayout.setOnClickListener(View.OnClickListener {
                if (NetworkConnectivity.isConnectedToInternet()) {
                    if (addToWishList) {
                        AppLog.infoLog("BtnWishlist :: false -- ")
                        insertSectionContentLog(mediaId, mediaInfoModel?.apply {
                            favouriteVideo = KEY_BLANK
                        }!!)
                        exoBtnWishlist.setImageDrawable(BindingUtils.drawable(R.drawable.ic_heart))
                        addToWishList = false
                    } else {
                        AppLog.infoLog("BtnWishlist :: true -- Y")
                        insertSectionContentLog(mediaId, mediaInfoModel?.apply {
                            favouriteVideo = KEY_Y
                        }!!)
                        exoBtnWishlist.setImageDrawable(BindingUtils.drawable(R.drawable.ic_heart_checked))
                        addToWishList = true
                    }
                } else {
                    it.snackbar(BindingUtils.string(R.string.no_internet_message))
                }
            })

        }
    }

    private fun observerForAddReplyScreen(questionsModel: AskQuestionModel) {

        viewModel.apply {

            /*
            * Set the Reply list Adapter
            * */
            questionsReplyListLiveData.observe(this@MediaPlaybackActivity, Observer {
                if (it != null) {
                    /*
                    * Add on 0th position, student ask question
                    * */
                    val askQuestionReplyList = ArrayList<AskQuestionReplyModel>()
                    askQuestionReplyList.add(0, AskQuestionReplyModel().apply {
                        createDateTime = questionsModel.createdDateTime
                        answer = questionsModel.question
                        userName = questionsModel.userName
                        userType = KEY_STUDENT
                    })
                    askQuestionReplyList.addAll(1,it)

                    enteredReplyLiveData.value = KEY_BLANK
                    exoCommentLayoutBinding?.replyLayout?.rvReply?.apply {
                        videoReplyAdapter = VideoReplyAdapter(askQuestionReplyList)
                        videoReplyAdapter?.apply {
                            layoutManager =
                                LinearLayoutManager(
                                    this@MediaPlaybackActivity,
                                    RecyclerView.VERTICAL,
                                    false
                                )
                            (layoutManager as LinearLayoutManager).isSmoothScrollbarEnabled = true
                            (layoutManager as LinearLayoutManager).scrollToPosition(itemCount - 1)
                            adapter = this
                        }
                    }
                }
            })

            onClickBackLiveData.observe(this@MediaPlaybackActivity, Observer {
                if (it != null) {
                    if (it == true) {
                        exoCommentLayoutBinding?.apply {
                            questionLayout.visibility = View.VISIBLE
                            replyLayout.replyContainerLayout.visibility = View.GONE
                        }
                        getQuestionAndAnswer()
                        onClickBackLiveData.value = false

                    }
                }
            })

            /*
            * Add Reply for the question
            * */
            onClickAddReplyLiveData.observe(this@MediaPlaybackActivity, Observer { questionId ->
                if (questionId != null) {
                    if (questionId > 0) {
                        //val question = exoCommentLayoutBinding.etAskQuestion.text.toString()
                        val enteredReply = enteredReplyLiveData.value.toString()
                        if (enteredReply.isNotBlank()) {
                            insertQuestionsReply(questionId, enteredReply)
                        } else {
                            AppLog.infoLog("Blank Reply not added.")
                        }
                        onClickAddReplyLiveData.value = 0
                    }
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

            /*
            * this is for the Landscape orientation and full mode
            * */
            //
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
            R.id.exoBtnLike,
            ConstraintSet.START,
            R.id.player_view,
            ConstraintSet.START,
            50
        )

        constraintSet?.connect(
            R.id.lblWishList,
            ConstraintSet.END,
            R.id.player_view,
            ConstraintSet.END,
            10
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
            R.id.exoBtnWishlist,
            ConstraintSet.END,
            R.id.exoBtnComment,
            ConstraintSet.START,
            0
        )
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

    override fun onDestroy() {
        super.onDestroy()
        //viewModel.insertAskQuestion()
    }

    /*
    * remove button bar at the bottom screen
    * */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            this.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

}
