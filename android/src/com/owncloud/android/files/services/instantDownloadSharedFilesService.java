package com.owncloud.android.files.services;

import java.io.IOException;

import com.owncloud.android.authentication.AccountUtils;
import com.owncloud.android.authentication.AccountUtils.AccountNotFoundException;
import com.owncloud.android.datamodel.DataStorageManager;
import com.owncloud.android.datamodel.FileDataStorageManager;
import com.owncloud.android.datamodel.OCFile;
import com.owncloud.android.network.OwnCloudClientUtils;
import com.owncloud.android.operations.RemoteOperationResult;
import com.owncloud.android.operations.SynchronizeFolderOperation;
import com.owncloud.android.syncadapter.AbstractOwnCloudSyncAdapter;
import com.owncloud.android.syncadapter.FileSyncAdapter;
import com.owncloud.android.ui.activity.InitialPageActivity;

import eu.alefzero.webdav.WebdavClient;

import android.accounts.Account;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class instantDownloadSharedFilesService extends IntentService{

   public static String TAG = "instantDownloadSharedFilesService";
   public static final String RESULT = "result";
   public static final String NOTIFICATION = "com.owncloud.android.service.instantDownloadSharedFilesService";
   RemoteOperationResult result;
    public instantDownloadSharedFilesService() {
        super("instantdownloadshreadi");
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        
        int parentId = DataStorageManager.ROOT_PARENT_ID;
        String remotePath = OCFile.PATH_SEPARATOR+"Shared/";
        //AbstractOwnCloudSyncAdapter adp = new A;
        Account account = AccountUtils.getCurrentOwnCloudAccount(getApplicationContext());
        WebdavClient client;
        Log.d(TAG,parentId+" ");
        Log.d(TAG,remotePath);
        try {
            Log.d(TAG,"started instant dw service");
            client = OwnCloudClientUtils.createOwnCloudClient(account, getApplicationContext());
            DataStorageManager storagemanager = new FileDataStorageManager(AccountUtils.getCurrentOwnCloudAccount(getApplicationContext()),getContentResolver());
            SynchronizeFolderOperation synchFolderOp = new SynchronizeFolderOperation(  remotePath, 
                    System.currentTimeMillis(), 
                    parentId, 
                    storagemanager,
                    AccountUtils.getCurrentOwnCloudAccount(getApplicationContext()),
                    getApplicationContext()
                  );
            
    result = synchFolderOp.execute(client);
    publishResults(result.getHttpCode());
    //Log.d(TAG,result.getHttpCode()+" ");
    if(result.isSuccess()) {
        
    }
        } catch (OperationCanceledException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (AccountNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //publishResults(result.getHttpCode());
    }
    private void publishResults(int result) {
        Intent intent = new Intent(this,InitialPageActivity.class);
        //intent.putExtra(FILEPATH, outputPath);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
      }

}
