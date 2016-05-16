package com.sanron.lyricview;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.sanron.library.model.Lyric;
import com.sanron.library.view.LyricView;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    LyricView mLyricView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File file = Environment.getExternalStorageDirectory();
        Lyric lyric = Lyric.read(new File(file, "test.lrc"), "UTF-8");
        mLyricView = (LyricView) findViewById(R.id.lyric_view);
        mLyricView.setLyric(lyric);

        TimerTask timerTask = new TimerTask() {
            int i = 4;

            @Override
            public void run() {
                mLyricView.setCurrentSentenceIndex(i++);
            }
        };
        Timer timer = new Timer("s");
        timer.schedule(timerTask, 0, 2000);
    }
}
