package com.example.musicplayer;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String s = "https://drive.google.com/uc?export=download&id=1_c9xmC1VPiCQ_xKI7pgKNUreDKsD0TWa"; // Song for testing
        final String[] url = {"No link"};// = {"https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_5MG.mp3"}; // Song for testing
        final MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

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
                    //url[0] = s; // Test song 1
                    //url[0] = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"; // Test song 2
                    url[0] = String.valueOf(textURL.getText());

                    // Reset media player
                    rep.setVisibility(View.INVISIBLE);
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
                    }

                    // Set controls layout to visible on successful mp3 song
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            layout.setVisibility(View.VISIBLE);
                        }
                    });

                    // Get song title from meta data
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(url[0], new HashMap<String, String>());

                    // Set title text to song title
                    String text = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    if (text != null)
                        title.setText(text);
                    else
                        title.setText(R.string.noTitle);

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
                //mediaPlayer.release();
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
        showInstr(this);
    }

    // Opens a popup window of instructions
    public static void showInstr(Activity a) {

        // Get instructions layout
        LayoutInflater inflater = a.getLayoutInflater();
        View layout = inflater.inflate(R.layout.instructions,
                (ViewGroup) a.findViewById(R.id.instructions1));

        // initialize popup window
        final PopupWindow pw = new PopupWindow(layout,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);

        pw.showAtLocation(layout, Gravity.CENTER | Gravity.TOP, 0, 500);

        // Set click event to close popup
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do anything when popupWindow was clicked
                pw.dismiss(); // dismiss the window
            }
        });
    }
}