package com.socbox.zharif.videobank;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

/**
 * Claritas: 'Clarity Through Innovation
 *
 * Project: SocBox
 * Purpose: Video Handler
 * Code File Name:
 *
 * Description: This video handler have the button functionality of play, mute, fullscreen, replay, seekbar.
 * The user can track the current time and the total time of the video played.
 * The user can hide the media control when tap on the surface view.
 *
 * Notes:
 *      The manifest must contain:
 *      <uses-permission android:name="android.permission.INTERNET"></uses-permission>
 *
 *      Reference for seekbar: http://www.androidhive.info/2012/03/android-building-audio-player-tutorial/
 *
 *
 * Initial Authors: Muhammad Zharif Hadi
 *                  David Reteif
 *
 * Change History:
 * Version:0.1
 * Author: Muhammad Zharif Hadi & David Reteif
 * Change: Created original version
 * 3/20/15
 *
 * Traceability:
 *
 */

public class VideoHandler implements AudioManager.OnAudioFocusChangeListener,MediaPlayer.OnPreparedListener, SurfaceHolder.Callback{

    //Declare all variables

    private Activity        activity;

    //Instantiate the surfaceview and holder
    private SurfaceHolder   surfaceHolder;
    public SurfaceView      surfaceView;

    public Utilities        utilities;

    //Instantiate the media handler
    public MediaPlayer      mPlayer;
    public AudioManager     audioManager;
    public Handler          mHandler = new Handler();

    //holds the URLs content that is streamed from
    public String   vidUrl  = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

    private String  TAG     = "VideoPlayerActivityTAG";

    //Instantiate the controller for media player
    public ImageButton     playMedia;
    public ImageButton     muteMedia;
    public ImageButton     replayMedia;
    public ImageButton     fullscreenMedia;
    public SeekBar         seekBar;
    public TextView        currentTimeLabel;
    public TextView        totalTimeLabel;
    public RelativeLayout  container;

    //control the position in the video
    public int      position = 0;

    public boolean  AudioReady;

    public boolean  mFullScreen = false;


    //set up the media player when the class is call
    VideoHandler(Activity calledFrom) {

        activity = calledFrom;

        surfaceView = (SurfaceView) this.activity.findViewById(R.id.surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }


    /**
     * the SurfaceHolder.Callback interface needs to be implemented.
     * SurfaceHolder.Callback interface has three methods
     * @param holder
     * control the surface
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        loadVideoSource();
        setSize();
        setAudioFocus();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        mPlayer.setDisplay(null);
        mHandler.removeCallbacks(mUpdateTimeTask);
//        mPlayer.release();
        mPlayer = null;
        position = 0;
    }

    //End of Surface

    public void onPrepared(MediaPlayer mp) {

        // set Progress bar values
        seekBar.setProgress(0);
        seekBar.setMax(100);

        // Updating progress bar
        updateProgressBar();

        if (mPlayer != null) {
            mPlayer.seekTo(position);
            mPlayer.start();
        }
        AudioReady = true;
    }

    /**
     * set the video source from the URL and
     * prepare the video
     */
    public void loadVideoSource() {

        try {
            videoController();
            setMediaPlayer();
            utilities = new Utilities();
            mPlayer.setDataSource(vidUrl);
            //Set the display
            mPlayer.setDisplay(surfaceHolder);
            AudioReady = false;
            mPlayer.prepareAsync();

        } catch (IOException e) {
            Log.v(TAG,"Video source failed to load.");
        }
    }

    /**
     * declares the button for the media player
     */
    public void videoController(){

        playMedia   = (ImageButton)this.activity.findViewById(R.id.buttonPlay);
        playMedia.setImageResource(R.drawable.ic_media_pause);

        replayMedia = (ImageButton)this.activity.findViewById(R.id.buttonReplay);
        replayMedia.setImageResource(R.drawable.ic_media_replay);

        muteMedia   = (ImageButton)this.activity.findViewById(R.id.buttonMute);
        muteMedia.setImageResource(R.drawable.ic_media_unmute);

        fullscreenMedia = (ImageButton)this.activity.findViewById(R.id.buttonFullScreen);
        fullscreenMedia.setImageResource(R.drawable.ic_media_fullscreen_stretch);

        container   = (RelativeLayout)this.activity.findViewById(R.id.mainRelativeLayout);
        surfaceView = (SurfaceView)this.activity.findViewById(R.id.surface);

//        forwardMedia  = (ImageButton)findViewById(R.id.buttonForward);
//        backwardMedia = (ImageButton)findViewById(R.id.buttonBackward);

        seekBar = (SeekBar)this.activity.findViewById(R.id.seekBarMedia);
        currentTimeLabel    = (TextView)this.activity.findViewById(R.id.currentTime);
        totalTimeLabel      = (TextView)this.activity.findViewById(R.id.totalTime);

        seekBar.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) this.activity);
//        mPlayer.setOnCompletionListener(this);

    }

    /**
     * set the size of the surface view video
     */
    public void setSize() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        // Apply the display dimensions to videoSurface
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();

        params.width = width * 2;
        params.height = height/4;
        surfaceView.setLayoutParams(params);
    }

    /**
     * Instantiate the media player
     */
    public void setMediaPlayer(){

        mPlayer = new MediaPlayer();

        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mPlayer.setOnPreparedListener(this);

    }

    /**
     * This method is play and pause depending on the video state of
     * the media player. It also change the image button when
     * it is click.
     */
    public void setPlayPause(){
        if(mPlayer == null) {
            return;
        }

        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            playMedia.setImageResource(R.drawable.ic_media_play);
            Log.v(TAG, "Player pause");
        } else {
            mPlayer.start();
            playMedia.setImageResource(R.drawable.ic_media_pause);
            Log.v(TAG, "Player play");
        }
    }

    /**
     * This method is to stop and release the video when
     * user exit the app
     */
    public void deletePlayer() {
        if (mPlayer != null) {
            replayMedia.setImageResource(R.drawable.ic_media_replay);
            mPlayer.stop();
            mHandler.removeCallbacks(mUpdateTimeTask);
            Log.v(TAG, "Player stop");
            mPlayer.reset();
            mPlayer.release();
            Log.v(TAG, "Player release");
            mPlayer = null;
        }
    }

    /**
     * If needed reset in mediaplayer
     */
    public void resetPlayer(){
        Intent intent = this.activity.getIntent();
        this.activity.finish();

        //opening your activity
        this.activity.startActivity(intent);

        //removing the screen transition
        this.activity.overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    }

    /**
     * This method is mute and unmute the video when depending on the state
     * of the media player.It also change the image button when
     * it is click.
     */
    public void setMute() {

        audioManager = (AudioManager)this.activity.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            muteMedia.setImageResource(R.drawable.ic_media_unmute);
            Log.v(TAG, "Player unmute");
        } else {
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            muteMedia.setImageResource(R.drawable.ic_media_mute);
            Log.v(TAG, "Player mute");
        }
    }

    /**
     * This method are fullscreen and unfullscreen of the video depending on
     * the user which mode the user prefer.
     */
    public void toggleFullScreen() {
        if(mPlayer == null) {
            return;
        }
        if(isFullScreen()) {
            fullscreenMedia.setImageResource(R.drawable.ic_media_fullscreen_shrink);
            Log.v("FullScreen", "-----------------click toggleFullScreen-----------");
            updateFullScreen(mFullScreen);

        } else {
            fullscreenMedia.setImageResource(R.drawable.ic_media_fullscreen_stretch);
            Log.v("FullScreen", "-----------------click toggleFullScreen-----------");
            updateFullScreen(!mFullScreen);
        }

    }

    private void updateFullScreen(boolean fullScreen) {
        if (isFullScreen())
        {
            Log.v(TAG, "Set full screen SCREEN_ORIENTATION_PORTRAIT");
            this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            setUnFullscreen(true);

            mFullScreen = !fullScreen;
        }
        else {
            Log.v(TAG, "Set full screen SCREEN_ORIENTATION_LANDSCAPE");
            this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            setFullScreen(false);

            mFullScreen = fullScreen;

        }
    }

    public boolean isFullScreen() {
        if (mFullScreen) {
            Log.v("FullScreen", "--set icon full screen--");
            mFullScreen = false;
            return false;
        } else {
            Log.v("FullScreen", "--set icon small full screen--");
            mFullScreen = true;
            return true;
        }
    }


    private void setFullScreen(boolean fullScreen) {
        // Load parent display dimensions
        Log.v(TAG, "Set full screen");
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        // Apply parent display dimensions to videoSurface
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();

        params.width = width;
        params.height = height;
        surfaceView.setLayoutParams(params);

    }

    private void setUnFullscreen(boolean fullscreen) {
        // Load parent display dimensions
        Log.v(TAG, "Set Un full screen");
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        // Apply parent display dimensions to videoSurface
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();

        params.width = width;
        params.height = height/4;
        surfaceView.setLayoutParams(params);

    }

    //End of fullscreen method

    /**
     * Requests the main audio focus of the phone
     * Reference from Audio Handler (Socbox)
     *
     */
    public void setAudioFocus(){
        audioManager = (AudioManager)this.activity.getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {

            //gains audio focus
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mPlayer == null) {
                    loadVideoSource();
                } else if (!mPlayer.isPlaying()) {
                    if (AudioReady) {
                        mPlayer.start();
                        mPlayer.setVolume(1.0f, 1.0f);
                    }
                }
                break;
            //loses audio focus
            case AudioManager.AUDIOFOCUS_LOSS:
//                if (mPlayer.isPlaying())
//                    deletePlayer();
                break;

            //temporarily loses audio focus
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:

                if (mPlayer.isPlaying())
                    mPlayer.pause();
                break;

            //app is ducked
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:

                if (mPlayer.isPlaying())
                    mPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    /**
     * Update timer on seekbarMedia
     * Seekbar, progress media is reference from website
     * http://www.androidhive.info/2012/03/android-building-audio-player-tutorial/
     * */
    public void updateProgressBar() {

        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Create an inner class
     */
    public static class Utilities {
        /**
         * Converting the milliseconds time to timer format
         * Format (Hours:Minutes:Seconds)
         *
         */
        public String milliSecondsToTimer(long milliseconds) {
            String finalTimerString = "";
            String secondsString = "";

            // Converting the total of duration into time
            int hours = (int) (milliseconds / (1000 * 60 * 60));
            int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
            int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
            // Add hours
            if (hours > 0) {
                finalTimerString = hours + ":";
            }

            // Prepending 0 to seconds if it is one digit
            if (seconds < 10) {
                secondsString = "0" + seconds;
            } else {
                secondsString = "" + seconds;
            }
            finalTimerString = finalTimerString + minutes + ":" + secondsString;

            return finalTimerString;
        }

        /**
         * This method is to get the percentage of progress
         *
         */
        public int getProgressPercentage(long currentDuration, long totalDuration) {
            Double percentage = (double) 0;
            long currentSeconds = (int) (currentDuration / 1000);
            long totalSeconds = (int) (totalDuration / 1000);

            // calculating the percentage
            percentage = (((double) currentSeconds) / totalSeconds) * 100;

            return percentage.intValue();
        }

        /**
         * Function to change progress to timer
         * returns current duration in milliseconds
         */
        public int progressToTimer(int progress, int totalDuration) {
//            int currentDuration = 0;
//            totalDuration = (int) (totalDuration / 1000);
//            currentDuration = (int) ((((double) progress) / 100) * totalDuration);

//             return current duration in milliseconds
//            return currentDuration * 1000;
            return (int) ((((double) progress) / 100) * totalDuration / 1000) * 1000;
        }
    }

    /**
     * Background Runnable thread
     *
     * */
    public Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mPlayer.getDuration();
            long currentDuration = mPlayer.getCurrentPosition();

            // Displaying Total Duration time
            totalTimeLabel.setText(""+utilities.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            currentTimeLabel.setText(""+utilities.milliSecondsToTimer(currentDuration));

            // Updating the progress bar
            int progress = utilities.getProgressPercentage(currentDuration, totalDuration);
            seekBar.setProgress(progress);

            // Running thread after 100
            mHandler.postDelayed(this, 100);
        }
    };

    /**
     * The user can move the seekbar track.
     *
     * */
    public void onStartTrackingTouch() {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mPlayer.seekTo(progress);
            seekBar.setProgress(progress);
        }
    }

    public void onStopTrackingTouch() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mPlayer.getDuration();
        int currentPosition = utilities.progressToTimer(seekBar.getProgress(), totalDuration);

        // seek to forward or backward
        mPlayer.seekTo(currentPosition);

        updateProgressBar();
    }


}
