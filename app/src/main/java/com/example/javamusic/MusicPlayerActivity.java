package com.example.javamusic;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import android.media.MediaMetadataRetriever;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

public class MusicPlayerActivity extends AppCompatActivity {

    private ArrayList<AudioModel> favoriteSongsList = new ArrayList<>();
    private ImageView favoriteButton;

    TextView titleTv, currentTimeTv, totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay, nextBtn, previousBtn, musicIcon, shuffle, repeat_btn, backbtn;
    ArrayList<AudioModel> songsList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    int x = 0;

    private boolean isShuffle = false;
    private static final int Repeat_None = 0;
    private static final int Repeat_One = 1;
    private int repeatMode = Repeat_None;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        favoriteButton = findViewById(R.id.favourite);
        favoriteButton.setOnClickListener(v -> onFavoriteButtonClick());
        titleTv = findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.play_pause_button);
        nextBtn = findViewById(R.id.next_button);
        previousBtn = findViewById(R.id.previous_button);
        musicIcon = findViewById(R.id.album_art);
        shuffle = findViewById(R.id.shuffle_button);
        repeat_btn = findViewById(R.id.repeat_button);
        backbtn = findViewById(R.id.go_back);
        backbtn.setOnClickListener(v -> {
            Intent intent = new Intent(MusicPlayerActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        titleTv.setSelected(true);
        musicIcon.setSelected(true);
        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        setResourcesWithMusic();

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition() + ""));

                    if (mediaPlayer.isPlaying()) {
                        pausePlay.setImageResource(R.drawable.baseline_pause_circle_24);
                        musicIcon.setRotation(x++);
                    } else {
                        pausePlay.setImageResource(R.drawable.baseline_play_circle_24);
                        musicIcon.setRotation(0);
                    }
                }
                new Handler().postDelayed(this, 100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFavoriteButton(); // Update the favorite button when the activity is resumed
    }

    private void onFavoriteButtonClick() {
        FavoriteManager favoriteManager = FavoriteManager.getInstance(this);

        if (favoriteManager.isFavorite(currentSong)) {
            favoriteManager.removeFavorite(currentSong);
            favoriteButton.setImageResource(R.drawable.ic_favorite_border);
        } else {
            favoriteManager.addFavorite(currentSong);
            favoriteButton.setImageResource(R.drawable.ic_favorite);
        }
    }

    private void updateFavoriteButton() {
        FavoriteManager favoriteManager = FavoriteManager.getInstance(this);
        if (favoriteManager.isFavorite(currentSong)) {
            favoriteButton.setImageResource(R.drawable.ic_favorite);
        } else {
            favoriteButton.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    void setResourcesWithMusic() {
        if (songsList.isEmpty()) return;

        currentSong = songsList.get(MyMediaPlayer.currentIndex);

        titleTv.setText(currentSong.getTitle());
        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        updateFavoriteButton(); // Update the favorite button based on the current song

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(currentSong.getPath());
            byte[] art = retriever.getEmbeddedPicture();
            if (art != null) {
                Glide.with(this)
                        .asBitmap()
                        .load(art)
                        .transform(new CircleCrop())
                        .placeholder(R.drawable.ic_music)
                        .into(musicIcon);
            } else {
                Glide.with(this)
                        .load(R.drawable.ic_music)
                        .transform(new CircleCrop())
                        .into(musicIcon);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Glide.with(this)
                    .load(R.drawable.ic_music)
                    .transform(new CircleCrop())
                    .into(musicIcon);
        }

        playMusic();
        pausePlay.setOnClickListener(v -> pausePlay());
        nextBtn.setOnClickListener(v -> playNextSong());
        previousBtn.setOnClickListener(v -> playPreviousSong());
        shuffle.setOnClickListener(v -> toggleShuffle());
        repeat_btn.setOnClickListener(v -> T_repeatmode());
    }

    private void playMusic() {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.setOnCompletionListener(mp -> playNextSong());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playNextSong() {
        if (repeatMode == Repeat_One) {
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        } else if (MyMediaPlayer.currentIndex < songsList.size() - 1) {
            MyMediaPlayer.currentIndex++;
            setResourcesWithMusic();
        } else {
            mediaPlayer.stop();
            MyMediaPlayer.currentIndex = 0;
            setResourcesWithMusic();
        }
    }

    private void playPreviousSong() {
        if (MyMediaPlayer.currentIndex == 0)
            return;
        MyMediaPlayer.currentIndex -= 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void pausePlay() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }

    public static String convertToMMSS(String duration) {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    private void toggleShuffle() {
        isShuffle = !isShuffle;
        if (isShuffle) {
            ArrayList<AudioModel> upcomingSongs = new ArrayList<>(songsList.subList(MyMediaPlayer.currentIndex + 1, songsList.size()));
            Collections.shuffle(upcomingSongs);
            ArrayList<AudioModel> shuffledList = new ArrayList<>();
            shuffledList.add(songsList.get(MyMediaPlayer.currentIndex));
            shuffledList.addAll(upcomingSongs);
            songsList = shuffledList;
            MyMediaPlayer.currentIndex = 0;
            setResourcesWithMusic();
        } else {
            setResourcesWithMusic();
        }
    }

    private void T_repeatmode() {
        if (repeatMode == Repeat_None) {
            repeatMode = Repeat_One;
            repeat_btn.setImageResource(R.drawable.ic_repeat_one);
        } else {
            repeatMode = Repeat_None;
            repeat_btn.setImageResource(R.drawable.ic_repeat);
        }
    }
}
