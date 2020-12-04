package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String s = "https://drive.google.com/uc?export=download&id=1_c9xmC1VPiCQ_xKI7pgKNUreDKsD0TWa";
        final String[] url = {"No link"};// = {"https://drive.google.com/uc?export=download&id=1_c9xmC1VPiCQ_xKI7pgKNUreDKsD0TWa"};
        //String url = "https://uc42a8d384c20b83009ff2d1c4ec.dl.dropboxusercontent.com/cd/0/get/BEO-kVRQm2D5Hcwkr7vFt4pxBDDypXjuENu_KymmfNTUIBhMjX76kFkXx5LaRnu7-B0tx4ENvCzau24FIvTjnPBAvcrDgHZuwQwQrEIkMoZ1sw/file?_download_id=5799156576555939177195727906199755596122995110995005868153469585025&_notify_domain=www.dropbox.com&dl=1"; // your URL here
        final MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        //mediaPlayer.start();

        // Initialize buttons, text views, and control layout
        final Button play = findViewById(R.id.start);
        final Button replay = findViewById(R.id.replay);
        final Button stop = findViewById(R.id.stop);
        final EditText textURL = findViewById(R.id.textURL);
        final Button urlB = findViewById(R.id.urlButton);
        final Button instr = findViewById(R.id.rButton);
        final ImageView rep = findViewById(R.id.imageView);
        final TextView title = findViewById(R.id.title);
        final LinearLayout layout = findViewById(R.id.controlsLayout);

        // Set booleans for pause, repeat, and valid url
        final boolean[] isPaused = {true};
        final boolean[] isLooping = {false};

        // URL button listener
        urlB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textURL.getText() != null) {
                    url[0] = s;
                    //url[0] = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";
                    //url[0] = String.valueOf(textURL.getText());

                    // Reset media player
                    mediaPlayer.reset();
                    final boolean[] tr = {false};

                    // Try using url for media player
                    try {
                        mediaPlayer.setDataSource(url[0]);
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                        tr[0] = true;
                        layout.setVisibility(View.INVISIBLE);
                    }

                    // If error occurred while finding song, show toast for invalid url
                    if (tr[0]) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Invalid link, please try again", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }/*
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer player) {
                            //player.start();
                        }
                    });
                    */
                    /*
                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                     */
                    // Set controls layout to visible on successful mp3 song
                    layout.setVisibility(View.VISIBLE);

                    // Get song title from meta data
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(url[0], new HashMap<String, String>());

                    // Set title text to song title
                    String text = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    if (text != null)
                        title.setText(text);
                    else
                        title.setText("No Title Found for Current Song");

                    // Handles case if playing old song while getting new song
                    if (!isPaused[0]) {
                        play.setBackgroundResource(R.drawable.ic_play);
                        isPaused[0]= true;
                    }
                    // If was on repeat reset to default
                    if (isLooping[0]) {
                        isLooping[0] = false;
                        replay.setBackgroundResource(R.drawable.ic_repeat_off);
                        rep.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        // Listener for instructions button
        instr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.release();
                openInstructions();
            }
        });

        // Listener for play/pause button
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPaused[0]) {
                    mediaPlayer.start();
                    play.setBackgroundResource(R.drawable.ic_pause);
                    isPaused[0] = false;
                } else {
                    mediaPlayer.pause();
                    play.setBackgroundResource(R.drawable.ic_play);
                    isPaused[0]= true;
                }
            }
        });

        // Listener to stop button
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPaused[0])
                    mediaPlayer.pause();
                mediaPlayer.seekTo(0);
                isPaused[0] = true;
                play.setBackgroundResource(R.drawable.ic_play);

            }
        });

        // Listener for repeat button
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLooping[0]) {
                    mediaPlayer.setLooping(false);
                    isLooping[0] = false;
                    replay.setBackgroundResource(R.drawable.ic_repeat_off);
                    rep.setVisibility(View.INVISIBLE);

                } else {
                    mediaPlayer.setLooping(true);
                    isLooping[0] = true;
                    replay.setBackgroundResource(R.drawable.ic_repeat_one);
                    rep.setVisibility(View.VISIBLE);

                }
            }
        });

    }

    private void openInstructions() {
        Intent intent = new Intent(this, Instructions.class);
        startActivity(intent);
    }
}