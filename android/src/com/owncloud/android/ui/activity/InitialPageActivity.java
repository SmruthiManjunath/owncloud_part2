package com.owncloud.android.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.owncloud.android.R;

public class InitialPageActivity extends Activity {
    String TAG = "Initial page activity";
    Toast toast;
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Log.d(TAG,"in initial page");
        setContentView(R.layout.optiontoviewfiles);
        toast = new Toast(this);
        toast.makeText(this,"Your are not connected to the internet! So, you cannot login to owncloud",Toast.LENGTH_SHORT);
        //toast.setDuration(Toast.LENGTH_SHORT);
        Button viewFilesButton = (Button) findViewById(R.id.displayFiles);
        Button ownCloudFilesButton = (Button) findViewById(R.id.owncloudFiles); 
        if(!isNetworkAvailable())
        {
            ownCloudFilesButton.setEnabled(false);
            ownCloudFilesButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                  toast.show();
                }
            });
        } else {
            ownCloudFilesButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                   finish();
                }
            });
        }
        final Intent intent = new Intent(this,DisplayFilesOfflineActivity.class);
        viewFilesButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
               startActivity(intent);
            }
        });
        
        
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo == null?false:true;
    }
    @Override
    public
    void onBackPressed() {
        finish();
    }
    
}
