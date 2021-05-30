package com.ccmall.audiolistview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listViewMP3;
    Button btnPlay, btnStop;
    TextView tvMP3, tvmp3, tvtime;
    SeekBar seek;
    ProgressBar pbMP3;
    MediaPlayer mplayer;
    ArrayList<String> mP3List;
    String selectedMP3;
    String mP3Path = Environment.getExternalStorageDirectory().getPath() + "/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("간단 MP3 플레이어");
        listViewMP3 = findViewById(R.id.listViewMP3);
        btnPlay = findViewById(R.id.btnPlay);
        btnStop = findViewById(R.id.btnStop);
        tvMP3 = findViewById(R.id.tvMP3);
        pbMP3 = findViewById(R.id.pbMP3);
        tvmp3 = findViewById(R.id.tvmp3);
        tvtime = findViewById(R.id.tvtime);
        seek = findViewById(R.id.seek);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

        mP3List = new ArrayList<>();
        File[] listfiles = new File(mP3Path).listFiles();

        String fileName, extName;

        for (File file : listfiles) {
            fileName = file.getName();
            extName = fileName.substring(fileName.length() - 3);
            if (extName.equals("mP3")) {
                mP3List.add(fileName);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice);
        listViewMP3.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewMP3.setAdapter(adapter);
        listViewMP3.setItemChecked(0, true);

        listViewMP3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMP3 = mP3List.get(position);
            }
        });

        selectedMP3 = mP3List.get(0);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mplayer = new MediaPlayer();
                    mplayer.setDataSource(mP3Path + selectedMP3);
                    mplayer.prepare();
                    mplayer.start();
                    btnPlay.setClickable(false);
                    btnStop.setClickable(true);
                    tvMP3.setText("실행 중인 음악 : " + selectedMP3);
                    pbMP3.setVisibility(View.VISIBLE);
                    tvMP3.setTextColor(Color.BLUE);
                    //동작 부분
                    new Thread() {
                        SimpleDateFormat timeformat = new SimpleDateFormat("mm:ss");

                        @Override
                        public void run() {
                            if (mplayer == null)
                                return;
                            seek.setMax(mplayer.getDuration());
                            while (mplayer.isPlaying()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        seek.setProgress(mplayer.getCurrentPosition());
                                        tvtime.setText("진행시간 : " + timeformat.format(mplayer.getCurrentPosition()));
                                    }
                                });
                                SystemClock.sleep(200);
                            }
                        }
                    }.start();
                } catch (IOException e) {

                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mplayer.stop();
                mplayer.reset();
                btnPlay.setClickable(true);
                btnStop.setClickable(false);
                tvMP3.setText("실행 중인 음악 : ");
                pbMP3.setVisibility(View.INVISIBLE);
            }
        });
    }
}