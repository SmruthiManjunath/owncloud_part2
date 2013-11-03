/* ownCloud Android client application
 *   Copyright (C) 2011  Bartek Przybylski
 *   Copyright (C) 2012-2013 ownCloud Inc.
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License version 2,
 *   as published by the Free Software Foundation.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.owncloud.android.ui.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.accounts.Account;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.owncloud.android.DisplayUtils;
import com.owncloud.android.R;
import com.owncloud.android.authentication.AccountUtils;
import com.owncloud.android.datamodel.DataStorageManager;
import com.owncloud.android.datamodel.OCFile;
import com.owncloud.android.db.DbFriends;
import com.owncloud.android.files.services.FileDownloader.FileDownloaderBinder;
import com.owncloud.android.files.services.FileUploader.FileUploaderBinder;
import com.owncloud.android.ui.activity.FileDisplayActivity;
import com.owncloud.android.ui.activity.TransferServiceGetter;
import com.owncloud.android.utils.FileStorageUtils;


/**
 * This Adapter populates a ListView with all files and folders in an ownCloud
 * instance.
 * 
 * @author Bartek Przybylski
 * 
 */
public class FileListListAdapter extends BaseAdapter implements ListAdapter, OnClickListener {
    private Context mContext;
    private OCFile mFile = null;
    private Vector<OCFile> mFiles = null;
    private DataStorageManager mStorageManager;
    private Account mAccount;
    private TransferServiceGetter mTransferServiceGetter;
    //total size of a directory (recursive)
    private Long totalSizeOfDirectoriesRecursive = null;
    private Long lastModifiedOfAllSubdirectories = null;
    private ArrayAdapter<String> shareWithFriends;
    private static String shareType="0";
    private static String permissions="17";
    private String accountName;
    private String url;
    DbFriends dataSource;
    public FileListListAdapter(Context context, TransferServiceGetter transferServiceGetter) {
        mContext = context;
        mAccount = AccountUtils.getCurrentOwnCloudAccount(mContext);
        mTransferServiceGetter = transferServiceGetter;
        
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public int getCount() {
        return mFiles != null ? mFiles.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        if (mFiles == null || mFiles.size() <= position)
            return null;
        return mFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (mFiles == null || mFiles.size() <= position)
            return 0;
        return mFiles.get(position).getFileId();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflator = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(R.layout.list_item, null);
        }
    
        if (mFiles != null && mFiles.size() > position) {
            OCFile file = mFiles.get(position);
            TextView fileName = (TextView) view.findViewById(R.id.Filename);
            String name = file.getFileName();
            ImageView shareButton = (ImageView) view.findViewById(R.id.shareItem);
            fileName.setText(name);
            ImageView fileIcon = (ImageView) view.findViewById(R.id.imageView1);
            fileIcon.setImageResource(DisplayUtils.getResourceId(file.getMimetype()));
            ImageView localStateView = (ImageView) view.findViewById(R.id.imageView2);
            FileDownloaderBinder downloaderBinder = mTransferServiceGetter.getFileDownloaderBinder();
            FileUploaderBinder uploaderBinder = mTransferServiceGetter.getFileUploaderBinder();
            if (downloaderBinder != null && downloaderBinder.isDownloading(mAccount, file)) {
                localStateView.setImageResource(R.drawable.downloading_file_indicator);
                localStateView.setVisibility(View.VISIBLE);
            } else if (uploaderBinder != null && uploaderBinder.isUploading(mAccount, file)) {
                localStateView.setImageResource(R.drawable.uploading_file_indicator);
                localStateView.setVisibility(View.VISIBLE);
            } else if (file.isDown()) {
                localStateView.setImageResource(R.drawable.local_file_indicator);
                localStateView.setVisibility(View.VISIBLE);
            } else {
                localStateView.setVisibility(View.INVISIBLE);
            }
            dataSource = new DbFriends(mContext);
            TextView fileSizeV = (TextView) view.findViewById(R.id.file_size);
            TextView lastModV = (TextView) view.findViewById(R.id.last_mod);
            ImageView checkBoxV = (ImageView) view.findViewById(R.id.custom_checkbox);
            shareButton.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Log.d("filelistlstadapter","here sssss");
                    
                    final Dialog dialog = new Dialog(mContext);
                   // int position = arg0;
                    final OCFile fileToBeShared = (OCFile) getItem(position);
                    final ArrayAdapter<String> shareWithFriends;
                    dialog.setContentView(R.layout.share_file_with);
                    dialog.setTitle("Share");
                    Log.d("ewhqo oieqjoqejruihoh uh =u h ",fileToBeShared.getFileName()+" "+fileToBeShared.getFileId()+" "+fileToBeShared.getParentId());
                    Account account = AccountUtils.getCurrentOwnCloudAccount(mContext);
                    String [] accountNames = account.name.split("@");
                    
                    if(accountNames.length > 2)
                    {
                        accountName = accountNames[0]+"@"+accountNames[1];
                        url = accountNames[2];
                    }
                        
                    final AutoCompleteTextView textView = (AutoCompleteTextView)dialog.findViewById(R.id.autocompleteshare);
                    Button shareBtn = (Button)dialog.findViewById(R.id.ShareBtn);
                    textView.setThreshold(2);
                    //textView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                    final String itemType;
                    if(fileToBeShared.isDirectory())
                        itemType = "folder";
                    else
                        itemType="file";
                    //fileToBeShared.
                    final String itemSource = String.valueOf(fileToBeShared.getFileId());
                    //Now it is only members, then will change it to groups
                   
                    //Permissions disabled with friends app
                    ArrayList<String> friendList = dataSource.getFriendList(accountName);
                    dataSource.close();
                    shareWithFriends = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1,friendList);
                    Log.d("filelistlistadapter",friendList.size()+" "+friendList.get(0));
                    //textView.set
                    textView.setAdapter(shareWithFriends);
                    textView.setFocusableInTouchMode(true);
                    dialog.show();
                    textView.setOnItemClickListener(new OnItemClickListener() {
                        
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                
                                }
                    });
                    
                    shareBtn.setOnClickListener(new OnClickListener() {
                        
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            final String shareWith = textView.getText().toString();
                            if(shareWith == null) {
                                textView.setHint("Share With");
                            } else {
                            textView.setText("");
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                            HttpPost post = new HttpPost("http://" + url + "/owncloud/androidshare.php");
                            final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                            //Log.d("ILEListListAdapter ",itemType+" "+itemSource+" "+shareType+" "+shareWith+" "+accountName);
                            
                            //Log.d()
                            Log.d("fileListListAdapter ",itemType+" "+itemSource+" "+shareType+" "+shareWith+" "+permissions+" "+accountName+" "+fileToBeShared.getFileId());

                            params.add(new BasicNameValuePair("itemType", itemType));
                            params.add(new BasicNameValuePair("itemSource",itemSource));
                            params.add(new BasicNameValuePair("shareType",shareType));
                            params.add(new BasicNameValuePair("shareWith",shareWith));
                            params.add(new BasicNameValuePair("permission",permissions));
                            params.add(new BasicNameValuePair("uidOwner",accountName));
                            HttpEntity entity;
                            try {
                                entity = new UrlEncodedFormEntity(params, "utf-8");
                                HttpClient client = new DefaultHttpClient();
                                post.setEntity(entity);
                                HttpResponse response = client.execute(post);
                                
                                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                                    HttpEntity entityresponse = response.getEntity();
                                    Log.d("hiho ohohoh jgh gho ",entityresponse.toString());
                                } }catch(Exception e) {
                                    e.printStackTrace();
                                }
                                }
                                };
                                new Thread(runnable).start();
                            }
                        }
                    });
                    
                }
            });
            if (!file.isDirectory()) {
                fileSizeV.setVisibility(View.VISIBLE);
                fileSizeV.setText(DisplayUtils.bytesToHumanReadable(file.getFileLength()));
                lastModV.setVisibility(View.VISIBLE);
                lastModV.setText(DisplayUtils.unixTimeToHumanReadable(file.getModificationTimestamp()));
                // this if-else is needed even thoe fav icon is visible by default
                // because android reuses views in listview
                if (!file.keepInSync()) {
                    view.findViewById(R.id.imageView3).setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.imageView3).setVisibility(View.VISIBLE);
                }
                
                ListView parentList = (ListView)parent;
                if (parentList.getChoiceMode() == ListView.CHOICE_MODE_NONE) { 
                    checkBoxV.setVisibility(View.GONE);
                } else {
                    if (parentList.isItemChecked(position)) {
                        checkBoxV.setImageResource(android.R.drawable.checkbox_on_background);
                    } else {
                        checkBoxV.setImageResource(android.R.drawable.checkbox_off_background);
                    }
                    checkBoxV.setVisibility(View.VISIBLE);
                }
                
            } 
            else {
                
                fileSizeV.setVisibility(View.VISIBLE);
                fileSizeV.setText(DisplayUtils.bytesToHumanReadable(file.getFileLength()));
                lastModV.setVisibility(View.VISIBLE);
                lastModV.setText(DisplayUtils.unixTimeToHumanReadable(file.getModificationTimestamp()));
               checkBoxV.setVisibility(View.GONE);
               view.findViewById(R.id.imageView3).setVisibility(View.GONE);
            }
        }

        return view;
    }

    public void shareFile() {
        
    }
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return (mFiles == null || mFiles.isEmpty());
    }

    /**
     * Change the adapted directory for a new one
     * @param directory                 New file to adapt. Can be NULL, meaning "no content to adapt".
     * @param updatedStorageManager     Optional updated storage manager; used to replace mStorageManager if is different (and not NULL)
     */
    public void swapDirectory(OCFile directory, DataStorageManager updatedStorageManager) {
        mFile = directory;
        if (updatedStorageManager != null && updatedStorageManager != mStorageManager) {
            mStorageManager = updatedStorageManager;
            mAccount = AccountUtils.getCurrentOwnCloudAccount(mContext);
        }
        if (mStorageManager != null) {
            mFiles = mStorageManager.getDirectoryContent(mFile);
        } else {
            mFiles = null;
        }
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View arg0) {
        
        /*View view;
        PopupWindow windowPopup;
        Account account = AccountUtils.getCurrentOwnCloudAccount(mContext);
        String [] accountNames = account.name.split("@");
        String accountName = null;
        if(accountNames.length > 2)
            accountName = accountNames[0]+"@"+accountNames[1];
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.share_file_with, null); 
        windowPopup = new PopupWindow(view,300,370,true);
        windowPopup.showAtLocation(view, Gravity.CENTER, 0, 0);
        //windowPopup.
        MultiAutoCompleteTextView textView = (MultiAutoCompleteTextView)view.findViewById(R.id.autocompleteshare);
        //(MutiAutoCompleteTextView)view.findViewById(R.id.autocompleteshare);
        //Log.d("whqeojqwer eiwjrperq ",textView.getText().toString());
        textView.setThreshold(2);
        textView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        ArrayList<String> friendList = dataSource.getFriendList(accountName);
        shareWithFriends = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1,friendList);
        //Log.d("filelistlistadapter",friendList.size()+" "+friendList.get(0));
        textView.setAdapter(shareWithFriends);
        textView.setFocusableInTouchMode(true);
        textView.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // TODO Auto-generated method stub
                
                        //Toast.makeText(FileDisplayActivity.this, "Got cclicked"+ adapter.getItem(position),Toast.LENGTH_SHORT);
                        Log.d("got clicked",shareWithFriends.getItem(position));
                        
                    }
                        
            
        }); */
        
        
    }
    
}
