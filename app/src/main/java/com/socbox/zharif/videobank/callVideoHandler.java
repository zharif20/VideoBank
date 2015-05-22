package com.socbox.zharif.videobank;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;


public class callVideoHandler extends ActionBarActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{

    MediaPlayer mPlayer;
    private String  TAG     = "VideoPlayerActivityTAG";
    public String   vidUrl  = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

    //Instantiate the handler
    private VideoHandler videoHandler;

    private boolean isChecked = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_video_handler);

        videoHandler = new VideoHandler(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_bank_handler, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Instantiate the click for the button
     */
    public void onClick(View v) {
        switch( v.getId() ) {
            case R.id.buttonPlay :
                videoHandler.setPlayPause();
                break;
            case R.id.buttonReplay :
                videoHandler.deletePlayer();
                videoHandler.loadVideoSource();
                videoHandler.position = 0;
//                videoHandler.resetPlayer();
                Log.v(TAG, "Player start");
                break;
            case R.id.buttonMute :
                videoHandler.setMute();
                break;
            case R.id.buttonFullScreen :
                videoHandler.toggleFullScreen();
                Log.v(TAG, "Player fullscreen");
                break;
            case R.id.seekBarMedia :
                Log.v(TAG, "Player seekBar");
                break;
            case R.id.surface :
                if (isChecked){
                    /**
                     * Remove the controller from the screen.
                     */
                    videoHandler.container.setVisibility(View.GONE);
                    isChecked = false;
                } else{
                    /**
                     * Show the controller on screen.
                     */
                    videoHandler.container.setVisibility(View.VISIBLE);
                    isChecked=true;
                }
        }
    }

    /**
     * When user start moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        videoHandler.onStartTrackingTouch();
    }

    /**
     * Notify that the progress level has changed
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        videoHandler.onProgressChanged(seekBar, progress, fromUser);
    }

    /**
     * When user stops moving the progress handler
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        videoHandler.onStopTrackingTouch();
    }

    /**
     * change configuration when fullscreen and vice versa.
     * Reference : http://developer.samsung.com/technical-doc/view.do?v=T000000107
     * @param newConfig
     */
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Landscape", Toast.LENGTH_SHORT).show();

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "Portrait", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * stop the media player when back is press
     */
    @Override
    public void onBackPressed() {
        videoHandler.deletePlayer();
        super.onBackPressed();
    }
}