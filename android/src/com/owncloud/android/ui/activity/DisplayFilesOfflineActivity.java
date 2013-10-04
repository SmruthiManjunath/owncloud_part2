package com.owncloud.android.ui.activity;

import java.io.File;
import java.util.ArrayList;

import com.owncloud.android.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.webkit.MimeTypeMap;

public class DisplayFilesOfflineActivity extends Activity {

    // adapter;
    ListView fileviews;
    String TAG="ownCloudFileDisplayActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout)
        setContentView(R.layout.listfiles);
        ListView fileviews = (ListView)findViewById(R.id.filelist);

        File owncloudDirectory = new File(Environment.getExternalStorageDirectory(),"ownCloud/moment@128.111.52.151/shared");
        File[] owncloudFiles = owncloudDirectory.listFiles();
        ArrayList<String> fileArrayList = new ArrayList<String>();
        
        for(int i = 0;i < owncloudFiles.length;i++) {

            fileArrayList.add(owncloudFiles[i].getName());
        }
        /*try {
            getAssets().open("/sdcard/ownCloud/"+owncloudFiles[1].getName(), 0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        /*Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file:///sdcard/ownCloud/batt_angry.txt"), "text/plain");
        startActivity(intent);*/

        Log.d("eijworeewq ",fileviews+" ");
        //Log.d("wqejqw ",owncloudFiles.length+" ");
        //adapter = new FileArrayAdapter(this,android.R.layout.simple_list_item_1,fileArrayList);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,fileArrayList);
        fileviews.setAdapter(adapter1);
        Log.d("ewqweuqeeweq ",fileArrayList.size()+" ");
        fileviews.setOnItemClickListener(onFileClick);
        //Log.d("qweeowqor ",adapter.getItem(0));
        fileviews.setVisibility(View.VISIBLE);



    }


    private OnItemClickListener onFileClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {

            //Log.d("wqeqeqwewqewqewqwqr ",arg0.)
            //File selectedFile = arg0.getItemAtPosition(arg2);
            String[] file = ((String)arg0.getItemAtPosition(arg2)).split("\\.");
            //String Extension = file[file.length-1];
            String Extension = MimeTypeMap.getFileExtensionFromUrl((String)arg0.getItemAtPosition(arg2));
            String type = null;
            if(Extension!=null) {
                MimeTypeMap typeMap = MimeTypeMap.getSingleton();
                type = typeMap.getMimeTypeFromExtension(Extension);
            }
                
                Log.d(TAG,Extension+" "+type+" "+((String)arg0.getItemAtPosition(arg2)));
                Log.d(TAG,Extension);
                /*if(Extension.equals("jpeg") || Extension.equals("bmp")|| Extension.equals("gif")||Extension.equals("jpg")||Extension.equals("png"))
                {

                    mimeType = "image/"+Extension;
                } else if(Extension.equals("mpeg") || Extension.equals("ogg"))
                    mimeType = "audio/"+Extension;
                else
                    mimeType="text/plain"; */
                
                //if(type != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file:///sdcard/ownCloud/moment@128.111.52.151/shared/"+(String)arg0.getItemAtPosition(arg2)), type);
                startActivity(intent);
                //}

            }

        };
    }


