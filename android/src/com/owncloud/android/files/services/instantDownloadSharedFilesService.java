package com.owncloud.android.files.services;

import android.app.IntentService;
import android.content.Intent;

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
        Intent instantDownloadIntent = new Intent("instantdownloadreceiver");
        instantDownloadIntent.putExtra("message", "data");
        if(AccountUtils.getCurrentOwnCloudAccount(getApplicationContext()) != null) {
        sendBroadcast(instantDownloadIntent);
        }
    }
}
