package com.owncloud.android.files.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.owncloud.android.authentication.AccountUtils;

import com.owncloud.android.operations.RemoteOperationResult;

public class instantDownloadSharedFilesService extends IntentService {

    public static String TAG = "instantDownloadSharedFilesService";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.owncloud.android.service.instantDownloadSharedFilesService";
    RemoteOperationResult result;

    public instantDownloadSharedFilesService() {
        super("instantDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent instantDownloadIntent = new Intent(NOTIFICATION);
        instantDownloadIntent.putExtra("message", "data");
        Log.d(TAG,"message"+"*************** ");
        if(AccountUtils.getCurrentOwnCloudAccount(getApplicationContext()) != null) {
            Log.d(TAG," ************************* "+AccountUtils.getCurrentOwnCloudAccount(getApplicationContext()).name);
            sendBroadcast(instantDownloadIntent);
        }
    }
}
