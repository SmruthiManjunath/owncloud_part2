package com.owncloud.android.db;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.owncloud.android.Log_OC;

public class DbFriends {
    private SQLiteDatabase mDB;
    private OpenerHelper mHelper;
    private final String mDatabaseName = "ownCloud";
    private final int mDatabaseVersion = 3;

    private final String TABLE_YOUR_FRIENDS = "your_friends";
    private final String TABLE_ACCEPT_FRIENDS = "received_requests";
    String[] accept_friends_cloumns = {"_ID","friendRequestForm","account"};
    String[] your_friends_cloumns = {"_ID","friend","account"};
    public DbFriends(Context context) {
        mHelper = new OpenerHelper(context);
        mDB = mHelper.getWritableDatabase();
    }

    public void close() {
        mDB.close();
    }

    public boolean putNewFriendRequest(String friendAccountName, String account) {
        ContentValues cv = new ContentValues();
        cv.put("friendRequestFrom", friendAccountName);
        cv.put("account", account);
        long result = mDB.insert(TABLE_ACCEPT_FRIENDS, null, cv);
        Log_OC.d(TABLE_ACCEPT_FRIENDS, "putNewFriendRequest returns with: " + result + " for friend: " + friendAccountName);
        return result != -1;
    }

    public List<String> updateFriendRequestStatus(ArrayList<String> friendRequests,String account) {
        ContentValues cv = new ContentValues();
        Cursor cursor = mDB.query(TABLE_ACCEPT_FRIENDS, accept_friends_cloumns,null,null,null,null,null);
        cursor.moveToFirst();
        Set<String> presentInDatabase = new HashSet<String>();
        List<String> sendNotificationList = new ArrayList<String>();
        while (!cursor.isAfterLast()) {
          presentInDatabase.add(cursor.getString(1));
          cursor.moveToNext();
        }
        
        for(int i = 0;i<friendRequests.size();i++) {
            if(!presentInDatabase.contains(friendRequests.get(i))) {
                putNewFriendRequest(friendRequests.get(i), account);
                sendNotificationList.add(friendRequests.get(i));
            }
        }
       for(String s : presentInDatabase) {
           if(!friendRequests.contains(s)) {
                mDB.delete(TABLE_ACCEPT_FRIENDS, "friendRequestFrom = ?", new String[] {s});
           }
       }
       
       return sendNotificationList;
    }

    public boolean putNewFriends(String friendAccountName, String account) {
        ContentValues cv = new ContentValues();
        cv.put("friendRequestFrom", friendAccountName);
        cv.put("account", account);
        long result = mDB.insert(TABLE_ACCEPT_FRIENDS, null, cv);
        Log_OC.d(TABLE_ACCEPT_FRIENDS, "putNewFriendRequest returns with: " + result + " for friend: " + friendAccountName);
        return result != -1;
    }

    public List<String> updateFriendStatus(ArrayList<String> friendRequests,String account) {
        ContentValues cv = new ContentValues();
        Cursor cursor = mDB.query(TABLE_YOUR_FRIENDS, your_friends_cloumns,null,null,null,null,null);
        cursor.moveToFirst();
        Set<String> presentInDatabase = new HashSet<String>();
        List<String> sendNotificationList = new ArrayList<String>();
        while (!cursor.isAfterLast()) {
          presentInDatabase.add(cursor.getString(1));
          cursor.moveToNext();
        }
        
        for(int i = 0;i<friendRequests.size();i++) {
            if(!presentInDatabase.contains(friendRequests.get(i))) {
                putNewFriends(friendRequests.get(i), account);
                sendNotificationList.add(friendRequests.get(i));
            }
        }
       for(String s : presentInDatabase) {
           if(!friendRequests.contains(s)) {
                mDB.delete(TABLE_YOUR_FRIENDS, "friend = ?", new String[] {s});
           }
       }
       
       return sendNotificationList;
    }

    public void clearFiles() {
        mDB.delete(TABLE_INSTANT_UPLOAD, null, null);
    }

    /**
     * 
     * @param localPath
     * @return true when one or more pending files was removed
     */
    public boolean removeIUPendingFile(String localPath) {
        long result = mDB.delete(TABLE_INSTANT_UPLOAD, "path = ?", new String[] { localPath });
        Log_OC.d(TABLE_INSTANT_UPLOAD, "delete returns with: " + result + " for file: " + localPath);
        return result != 0;

    }

    private class OpenerHelper extends SQLiteOpenHelper {
        public OpenerHelper(Context context) {
            super(context, mDatabaseName, null, mDatabaseVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_YOUR_FRIENDS + " (" + " _id INTEGER PRIMARY KEY, "+" friend TEXT, " + " account TEXT);");
            db.execSQL("CREATE TABLE " + TABLE_ACCEPT_FRIENDS + " (" + " _id INTEGER PRIMARY KEY, "+" friendrequestfrom TEXT, " + " account TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            
        }
    }
}

