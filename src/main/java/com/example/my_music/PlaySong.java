package com.example.my_music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {
    TextView txtsstart,txtsstop;
    ImageView imageView;
    BarVisualizer visualizer;
     protected void onDestroy() {
         if(visualizer != null) {
             visualizer.release();
         }
        super.onDestroy();
        mediaPlayer.stop(); //stops media player
        mediaPlayer.release(); //releases media player
        updateSeek.interrupt();
    }
    TextView textView;
    ImageView play, previous, next;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    SeekBar seekBar;
    Thread updateSeek;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        visualizer= findViewById(R.id.blast);
        txtsstart= findViewById(R.id.txtsstrart);
        txtsstop= findViewById(R.id.txtsstop);
        imageView = findViewById(R.id.imageView2);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList)bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        textView.setSelected(true);
        position = intent.getIntExtra("position", 0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        updateSeek = new Thread() {
            public void run() {
                int currentPosition = 0;
                try {
                    while(currentPosition<mediaPlayer.getDuration()) {
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(800);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        String endTime = createTime(mediaPlayer.getDuration());
        txtsstop.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                txtsstart.setText(currentTime);
                handler.postDelayed(this, delay);
            }
        }, delay);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()) {
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else {
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                next.performClick();
            }
        });

        int audiosessionId = mediaPlayer.getAudioSessionId();
        if(audiosessionId != -1) {
            visualizer.setAudioSessionId(audiosessionId);
        }
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position-1 >= 0) {
                    position = position - 1;
                } else {
                    position = songs.size() - 1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                startAnimation(imageView);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
                int audiosessionId = mediaPlayer.getAudioSessionId();
                if(audiosessionId != -1) {
                    visualizer.setAudioSessionId(audiosessionId);
                }
            }
        });
        next .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                /*if(position != songs.size() - 1) {
                    position = position + 1;
                } else {
                    position = songs.size() - 1;
                }*/
                position=((position+1)%songs.size());

                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                startAnimation(imageView);

                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
                int audiosessionId = mediaPlayer.getAudioSessionId();
                if(audiosessionId != -1) {
                    visualizer.setAudioSessionId(audiosessionId);
                }
            }
        });

    }
    public void startAnimation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }
    public String createTime(int duration) {
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;
        time+=min+":";
        if(sec < 10) {
            time +="0";
        }
        time+=sec;
        return time;
    }
}

