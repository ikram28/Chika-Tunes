package euromed.ia.chika_tunes;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaParser;
import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.animation.LinearInterpolator;


import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerActivity extends AppCompatActivity {

    ImageButton btnplay, btnnext, btnprev, btnff, btnfr;
    TextView txtname, txtstart, txtstop;
    SeekBar seekmusic;
    String sname;

    ImageView imageView;
    ObjectAnimator rotationAnimator;
    private ValueAnimator zoomAnimator;

    private float initialScale = 1.0f;


    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;
    Thread updateseekbar;
    private BottomSheetBehavior bottomSheetBehavior;
    private FrameLayout bottomSheet;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Now Playing");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }








        btnprev = findViewById(R.id.btnprev);
        btnnext = findViewById(R.id.btnnext);
        btnplay = findViewById(R.id.playbtn);
        btnff = findViewById(R.id.btnff);
        btnfr = findViewById(R.id.btnfr);
        txtname = findViewById(R.id.txtsn);
        txtstart = findViewById(R.id.txtstart);
        txtstop = findViewById(R.id.txtstop);
        seekmusic = findViewById(R.id.seekbar);
        imageView = findViewById(R.id.imageview);
        bottomSheet = findViewById(R.id.sheet);

        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(200);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);







        rotationAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        rotationAnimator.setDuration(2000);
        rotationAnimator.setInterpolator(new LinearInterpolator());
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);



        zoomAnimator = ValueAnimator.ofFloat(initialScale, 1.2f, initialScale);
        zoomAnimator.setRepeatCount(ValueAnimator.INFINITE);
        zoomAnimator.setRepeatMode(ValueAnimator.RESTART);
        zoomAnimator.setDuration(500);
        zoomAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                imageView.setScaleX(animatedValue);
                imageView.setScaleY(animatedValue);
            }
        });

        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent i= getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getSerializable("songs");
        String songName = i.getStringExtra("songname");
        position = bundle.getInt("pos", 0);
        txtname.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        sname = mySongs.get(position).getName();
        txtname.setText(sname);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        rotationAnimator.start();
        zoomAnimator.start();


        String sanitizedSongName = sname.toLowerCase().replaceAll("[^a-zA-Z0-9]", "_").replace("mp3", "_lyrics");

        int lyricsResourceId = getResources().getIdentifier(
                sanitizedSongName,
                "string",
                getPackageName()
        );

        if (lyricsResourceId != 0) {
            String lyrics = getString(lyricsResourceId);

            TextView lyricsTextView = findViewById(R.id.lyricsTextView);
            lyricsTextView.setText(lyrics);
        } else {
            TextView lyricsTextView = findViewById(R.id.lyricsTextView);
            lyricsTextView.setText("Lyrics not available for this song");
        }

        updateseekbar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentposition = 0;

                while (currentposition<totalDuration)
                {
                    try {
                        sleep(500);
                        currentposition = mediaPlayer.getCurrentPosition();
                        seekmusic.setProgress(currentposition);
                    }
                    catch (InterruptedException | IllegalStateException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        seekmusic.setMax(mediaPlayer.getDuration());
        updateseekbar.start();
        seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.purple),PorterDuff.Mode.MULTIPLY);
        seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.purple),PorterDuff.Mode.SRC_IN);

        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });

        String endTime = createTime(mediaPlayer.getDuration());
        txtstop.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                txtstart.setText(currentTime);
                handler.postDelayed(this,delay);

            }
        },delay);

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    btnplay.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                    rotationAnimator.pause();
                    zoomAnimator.cancel();
                } else {
                    btnplay.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                    zoomAnimator.start();
                    if (rotationAnimator.isPaused()) {
                        rotationAnimator.resume();
                    } else {
                        rotationAnimator.start();
                    }
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnnext.performClick();
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%mySongs.size());

                playSong();


            }
        });
        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1)<0)?(mySongs.size()-1):(position-1);

                playSong();




            }
        });

        btnff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });

        btnfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                mediaPlayer.stop();
                finish();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);





    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mediaPlayer.stop();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void playSong() {
        Uri u = Uri.parse(mySongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
        sname = mySongs.get(position).getName();
        txtname.setText(sname);
        mediaPlayer.start();
        btnplay.setBackgroundResource(R.drawable.ic_pause);
        zoomAnimator.start();
        rotationAnimator.start();

        // Update lyrics
        updateLyrics();
    }

    private void updateLyrics() {
        String sanitizedSongName = sname.toLowerCase().replaceAll("[^a-zA-Z0-9]", "_").replace("mp3", "_lyrics");
        int lyricsResourceId = getResources().getIdentifier(
                sanitizedSongName,
                "string",
                getPackageName()
        );

        if (lyricsResourceId != 0) {
            String lyrics = getString(lyricsResourceId);

            TextView lyricsTextView = findViewById(R.id.lyricsTextView);
            lyricsTextView.setText(lyrics);
        } else {
            TextView lyricsTextView = findViewById(R.id.lyricsTextView);
            lyricsTextView.setText("Lyrics not available for this song");
        }
    }




    public String createTime(int duration)
    {
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time+=min+":";

        if(sec<10)
        {
            time+="0";
        }
        time+=sec;
        return time;
    }
}