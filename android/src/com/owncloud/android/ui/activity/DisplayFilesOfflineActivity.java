package com.owncloud.android.ui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.owncloud.android.R;

public class DisplayFilesOfflineActivity extends Activity {

    // adapter;
    ListView fileviews;
    String TAG="ownCloudFileDisplayActivity";
    Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout)
        setContentView(R.layout.listfiles);
        ListView fileviews = (ListView)findViewById(R.id.filelist);

        File owncloudDirectory = new File(Environment.getExternalStorageDirectory(),"ownCloud/moment@Macha@128.111.52.151/shared");
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
        toast = new Toast(this);
        toast.makeText(this,"Unable to open file, not recognized ",Toast.LENGTH_SHORT);
        Log.d("eijworeewq ",fileviews+" ");
        //Log.d("wqejqw ",owncloudFiles.length+" ");
        //adapter = new FileArrayAdapter(this,android.R.layout.simple_list_item_1,fileArrayList);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.local_file_display,fileArrayList);
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
            String Extension = file[file.length-1];
            //String Extension = MimeTypeMap.getFileExtensionFromUrl((String)arg0.getItemAtPosition(arg2));
            String type = null;
            /*if(Extension!=null) {
                MimeTypeMap typeMap = MimeTypeMap.getSingleton();
                type = typeMap.getMimeTypeFromExtension(Extension);
            }*/
                
                Log.d(TAG,Extension+" "+type+" "+((String)arg0.getItemAtPosition(arg2)));
                Log.d(TAG,Extension);
                //type = "audio/mp3";
                if(Extension.equals("jpeg") || Extension.equals("bmp")|| Extension.equals("gif")||Extension.equals("jpg")||Extension.equals("png"))
                {
                    type = "image/"+Extension;
                } else if(Extension.equals("mpeg") || Extension.equals("ogg") || Extension.equals("mp3") || Extension.equals("mp4"))
                    type = "audio/"+Extension;
                else if(Extension.equals("avi"))
                    type="video/"+Extension; 
                else
                    type="text/plain";
                
                if(type != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file:///sdcard/ownCloud/moment@Macha@128.111.52.151/shared/"+(String)arg0.getItemAtPosition(arg2)), type);
                startActivity(intent);
                } 

            }

        };
    }

class fileAdapter extends ArrayAdapter<String> {
    ArrayList<String> fileList;
    Context context;
    Map<String,Integer> fileMimeImageMap;
    public fileAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.fileList = objects;
        fileMimeImageMap = new HashMap<String,Integer>();
        fileMimeImageMap.put("txt", R.drawable.local_file_indicator);
        fileMimeImageMap.put("docx", R.drawable.local_file_indicator);
        fileMimeImageMap.put("odt", R.drawable.local_file_indicator);
        fileMimeImageMap.put("html", R.drawable.local_file_indicator);
        fileMimeImageMap.put("pdf", R.drawable.local_file_indicator);
        fileMimeImageMap.put("csv", R.drawable.local_file_indicator);
        fileMimeImageMap.put("xml", R.drawable.local_file_indicator);
        fileMimeImageMap.put("jpeg",R.drawable.owncloud_logo_small_white);
        fileMimeImageMap.put("jpg",R.drawable.owncloud_logo_small_white);
        fileMimeImageMap.put("png",R.drawable.owncloud_logo_small_white);
        fileMimeImageMap.put("gif",R.drawable.owncloud_logo_small_white);
        fileMimeImageMap.put("bmp",R.drawable.owncloud_logo_small_white);
        fileMimeImageMap.put("mp3",R.drawable.music);
        fileMimeImageMap.put("wav",R.drawable.music);
        fileMimeImageMap.put("ogg",R.drawable.music);
        fileMimeImageMap.put("mid",R.drawable.music);
        fileMimeImageMap.put("midi",R.drawable.music);
        fileMimeImageMap.put("amr",R.drawable.music);
        fileMimeImageMap.put("mpeg",R.drawable.arrow_left);
        fileMimeImageMap.put("3gp",R.drawable.arrow_left);
        
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.local_file_display, parent,false);
        RowView rowView = new RowView();
        rowView.text = (TextView) view.findViewById(R.id.file_name);
        rowView.image = (ImageView) view.findViewById(R.id.file_type);
        rowView.text.setText(fileList.get(position));
        String[] file = (fileList.get(position)).split("\\.");
        String Extension = file[file.length-1];
        if(fileMimeImageMap.containsKey(Extension))
            rowView.image.setImageResource(fileMimeImageMap.get(Extension));
        else
            rowView.image.setImageResource(R.drawable.folder);
        
        return view;
        
    }
    
    static class RowView {
        TextView text;
        ImageView image;
    }
    
    
    
}
