package com.owncloud.android.ui.activity;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.owncloud.android.R;
import com.owncloud.android.files.services.instantDownloadSharedFilesService;

public class InitialPageActivity extends Activity {
    String TAG = "Initial page activity";
    Toast toast;
    Button viewSharedFilesButton;
    Button viewMyFilesButton;
    Button ownCloudFilesButton;
    static int i = 0;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt(instantDownloadSharedFilesService.RESULT);
                Log.d(TAG, resultCode + " ");
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Log.d(TAG, "in initial page");
        setContentView(R.layout.optiontoviewfiles);
        viewSharedFilesButton = (Button) findViewById(R.id.displayFiles);
        viewMyFilesButton = (Button) findViewById(R.id.displaymyfiles);
        ownCloudFilesButton = (Button) findViewById(R.id.owncloudFiles);

        ownCloudFilesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    finish();
                } else {
                    Toast.makeText(InitialPageActivity.this, "Please connect to the Internet", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        final Intent intent1 = new Intent(this, DisplayFilesOfflineActivity.class);
        viewSharedFilesButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                intent1.putExtra("folder", "shared");
                intent1.putExtra("isShared", true);
                startActivity(intent1);
            }
        });
        viewMyFilesButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                intent1.putExtra("folder", "");
                intent1.putExtra("isShared", false);
                startActivity(intent1);
            }
        });
        /*
         * Intent intent = new
         * Intent(this,instantDownloadSharedFilesService.class); AlarmManager
         * alarm = (AlarmManager)getSystemService(ALARM_SERVICE); PendingIntent
         * pintent = PendingIntent.getService(InitialPageActivity.this, 0,
         * intent, 0); Calendar cal = Calendar.getInstance();
         * alarm.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),
         * 60*1000, pintent);
         */
        // startService(intent);
        Intent intent2 = new Intent(this,instantDownloadSharedFilesService.class); 
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE); 
        PendingIntent pintent = PendingIntent.getService(this, 0,intent2, 0); 
        Calendar cal = Calendar.getInstance();
        //alarm.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),60*1000, pintent);
        Log.d(TAG, "Service started");
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(instantDownloadSharedFilesService.NOTIFICATION));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo == null ? false : true;
    }

    @Override
    public void onBackPressed() {

    }

}
