package com.example.hellopd;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextClock;
import android.widget.TimePicker;

import java.util.Timer;
import java.util.TimerTask;

import static android.media.RingtoneManager.getRingtone;

public class Alarm extends AppCompatActivity {

    TimePicker alarmTime;
    TextClock currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        alarmTime = findViewById(R.id.timePicker);
        currentTime = findViewById(R.id.textClock);
        final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));

        Log.d("currtime", currentTime.getText().toString());
        Log.d("alrmtime", AlarmTime());

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (currentTime.getText().toString().equals(AlarmTime())) {
                    r.play();
                } else {
                    r.stop();
                }

            }
        }, 0, 1000); //period in milli-seconds
    }

    public String AlarmTime(){
        Integer alarmHours = alarmTime.getCurrentHour(); //24 hours format
        Integer alarmMinutes = alarmTime.getCurrentMinute();

        String stringAlarmMinutes;

        if (alarmMinutes < 10) {
            stringAlarmMinutes = "0";
            stringAlarmMinutes = stringAlarmMinutes.concat(alarmMinutes.toString());
        } else {
            stringAlarmMinutes = alarmMinutes.toString();
        }

        String stringAlarmTime;

        if(alarmHours > 12){ //PM
            alarmHours = alarmHours - 12;
            stringAlarmTime = alarmHours.toString().concat(":").concat(stringAlarmMinutes).concat(" PM");

        } else { //AM
            if (alarmHours == 0) {
                alarmHours = 12; //12 am
            }
            stringAlarmTime = alarmHours.toString().concat(":").concat(stringAlarmMinutes).concat(" AM");
        }

        return stringAlarmTime;
    }
}
