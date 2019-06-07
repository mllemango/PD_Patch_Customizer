package com.example.hellopd;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.ToggleButton;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    EditText freqText;
    SeekBar freqSlider;

    // Start, Pause, Stop for Beatles mp3 music:
    MediaPlayer music_beatle;
    int paused;
    // code continues at bottom

    private void loadPDPatch(){
        File dir = getFilesDir();
        try {
            IoUtils.extractZipResource(getResources().openRawResource(R.raw.simplepatch), dir, true);
            Log.i("unzipping", dir.getAbsolutePath());
        } catch (IOException e) {
            Log.i("unzipping", "error unzipping");
        }
        File pdPatch = new File(dir, "simplepatch.pd");
        try {
            PdBase.openPatch(pdPatch.getAbsolutePath());
        } catch (IOException e) {
            Log.i("opening patch", "error opening patch");
            Log.i("opening patch", e.toString());
        }

        int i = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            initPD();
            loadPDPatch();
        }
        catch (IOException e) {
            Log.i("onCreate", "initialization and loading gone wrong :(");
            finish();
        }
        initGUI();

        //play beatles.mp3 file using button:
        final MediaPlayer beatleMusic = MediaPlayer.create(this, R.raw.the_beatles_yellow_submarine);
        final ToggleButton play_beatles = (ToggleButton) findViewById(R.id.toggle_beatles);
        play_beatles.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    beatleMusic.start();
                    // The toggle is enabled
                } else {
                    beatleMusic.pause();
                    // The toggle is disabled
                }
            }
        });
        // end of beatles insert
    }

    private void initGUI(){
        freqText = (EditText) findViewById(R.id.freqNum);
        freqSlider = findViewById(R.id.freqSlider);

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // updated continuously as the user slides the thumb
                freqText.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // called when the user first touches the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // called after the user finishes moving the SeekBar
            }
        };

        freqSlider.setOnSeekBarChangeListener(seekBarChangeListener);

        Switch onOffSwitch = findViewById(R.id.onOffSwitch);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("onOffSwitch", String.valueOf(isChecked));
                float val = (isChecked) ? 1.0f: 0.0f;
                float freq = Float.parseFloat(freqText.getText().toString());
                PdBase.sendFloat("onOff", val);
                PdBase.sendFloat("freqNum", freq);
            }
        });
    }



    private PdUiDispatcher dispatcher;

    private void initPD() throws IOException {
        int samplerate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(samplerate, 0 , 2, 8, true);

        dispatcher = new PdUiDispatcher();
        PdBase.setReceiver(dispatcher);
    }

    @Override
    protected void onResume(){
        super.onResume();
        PdAudio.startAudio(this);

    }

    @Override
    protected void onPause(){
        super.onPause();
        PdAudio.stopAudio();
    }

    // Continued [Start, Pause, Stop for Beatles mp3 music]:

    public void play(View view) {

        if(music_beatle == null) {//only play when there's no sound (won't create multiple instances)
            music_beatle = MediaPlayer.create(this, R.raw.the_beatles_yellow_submarine);
            music_beatle.start();
        } else if (! music_beatle.isPlaying()){//after pause (and not playing):
            music_beatle.seekTo(paused);
            music_beatle.start();
        }
    }

    public void pause(View view) {
        if(music_beatle != null) {// resolves "stop" -> "pause" bug
            music_beatle.pause();
            paused = music_beatle.getCurrentPosition(); //get position where paused
        }
    }

    public void stop(View view) {
        music_beatle.release();
        music_beatle = null;
    }

    // End of [Start, Pause, Stop for Beatles mp3 music]

}
