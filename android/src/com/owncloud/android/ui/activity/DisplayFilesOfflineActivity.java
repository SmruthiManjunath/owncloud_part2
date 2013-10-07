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

    ListView fileviews;
    FileAdapter adapter;
    String TAG = "ownCloudFileDisplayActivity";
    Toast toast;
    File owncloudDirectory;
    File[] owncloudFiles;
    Map<String, String> fileNamePath;
    ArrayList<String> fileArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listfiles);
        ListView fileviews = (ListView) findViewById(R.id.filelist);

        owncloudDirectory = new File(Environment.getExternalStorageDirectory(),
                "ownCloud/moment@Macha@128.111.52.151/shared");
        owncloudFiles = owncloudDirectory.listFiles();
        fileArrayList = new ArrayList<String>();
        fileNamePath = new HashMap<String, String>();
        for (int i = 0; i < owncloudFiles.length; i++) {

            fileArrayList.add(owncloudFiles[i].getName());
            fileNamePath.put(owncloudFiles[i].getName(), owncloudFiles[i].getAbsolutePath());

        }
        adapter = new FileAdapter(this, R.layout.local_file_display, fileArrayList);
        fileviews.setAdapter(adapter);
        fileviews.setOnItemClickListener(onFileClick);

    }

    private OnItemClickListener onFileClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            String fileName = ((String) arg0.getItemAtPosition(arg2));
            File file1 = new File(owncloudDirectory, fileName);
            File list[];
            if (!file1.isFile()) {
                owncloudDirectory = new File(owncloudDirectory, fileName);
                list = owncloudDirectory.listFiles();
                fileArrayList.clear();
                for (int i = 0; i < list.length; i++) {
                    fileArrayList.add(list[i].getName());
                }
                adapter.notifyDataSetChanged();
            } else {
                String[] file = ((String) arg0.getItemAtPosition(arg2)).split("\\.");
                String Extension = file[file.length - 1];

                int start = owncloudDirectory.getPath().indexOf("ownCloud");
                String Path = "file:///sdcard/" + owncloudDirectory.getPath().substring(start) + "/" + fileName;
                Log.d(TAG, Path);
                String type = null;
                if (Extension.equals("jpeg") || Extension.equals("bmp") || Extension.equals("gif")
                        || Extension.equals("jpg") || Extension.equals("png")) {
                    if (Extension.equals("jpg")) {
                        type = "image/jpeg";
                    } else
                        type = "image/" + Extension;
                } else if (Extension.equals("mpeg") || Extension.equals("ogg") || Extension.equals("mp3")
                        || Extension.equals("mp4"))
                    type = "audio/" + Extension;
                else if (Extension.equals("avi"))
                    type = "video/" + Extension;
                else if(Extension.equals("pdf") || Extension.equals("odt"))
                    type = "application/pdf";
                else
                    type = "text/plain";

                if (type != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(Path), type);
                    startActivity(intent);
                }

            }

        }
    };

    protected void notifyDataChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        String parent = owncloudDirectory.getParent().toString();
        String[] paths = owncloudDirectory.toString().split("/");
        if((!paths[paths.length-1].equals("shared"))) {
        owncloudDirectory = new File(parent);
        File list[] = owncloudDirectory.listFiles();
        fileArrayList.clear();
        Log.d(TAG, "length of list " + list.length + " ");
        for (int i = 0; i < list.length; i++) {
            fileArrayList.add(list[i].getName());
        }
        
        adapter.notifyDataSetChanged();
        }else
        {
            finish();
        }
    }

    class FileAdapter extends ArrayAdapter<String> {
        ArrayList<String> fileList;
        Context context;
        int layoutResourceId;
        RowView rowView;
        Map<String, Integer> fileMimeImageMap;

        public FileAdapter(Context context, int layoutResourceId, ArrayList<String> fileArrayList) {
            super(context, layoutResourceId, fileArrayList);
            this.context = context;
            this.layoutResourceId = layoutResourceId;
            this.fileList = fileArrayList;
            fileMimeImageMap = new HashMap<String, Integer>();
            fileMimeImageMap.put("txt", R.drawable.file_doc);
            fileMimeImageMap.put("docx", R.drawable.file_doc);
            fileMimeImageMap.put("odt", R.drawable.file_pdf);
            fileMimeImageMap.put("html", R.drawable.file);
            fileMimeImageMap.put("pdf", R.drawable.file_pdf);
            fileMimeImageMap.put("csv", R.drawable.file);
            fileMimeImageMap.put("xml", R.drawable.file);
            fileMimeImageMap.put("jpeg", R.drawable.file_image);
            fileMimeImageMap.put("jpg", R.drawable.file_image);
            fileMimeImageMap.put("png", R.drawable.file_image);
            fileMimeImageMap.put("gif", R.drawable.file_image);
            fileMimeImageMap.put("bmp", R.drawable.file_image);
            fileMimeImageMap.put("mp3", R.drawable.file_sound);
            fileMimeImageMap.put("wav", R.drawable.file_sound);
            fileMimeImageMap.put("ogg", R.drawable.file_sound);
            fileMimeImageMap.put("mid", R.drawable.file_sound);
            fileMimeImageMap.put("midi", R.drawable.file_sound);
            fileMimeImageMap.put("amr", R.drawable.file_sound);
            fileMimeImageMap.put("mpeg", R.drawable.file_movie);
            fileMimeImageMap.put("3gp", R.drawable.file_movie);
            fileMimeImageMap.put("dir", R.drawable.folder);
            // fileMimeImageMap.put(arg0, arg1)
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                view = inflater.inflate(layoutResourceId, parent, false);
                rowView = new RowView();
                rowView.text = (TextView) view.findViewById(R.id.file_name);
                rowView.image = (ImageView) view.findViewById(R.id.file_type);

                view.setTag(rowView);
            } else
                rowView = (RowView) view.getTag();

            String[] file = (fileList.get(position)).split("\\.");
            rowView.text.setText(fileList.get(position));
            String Extension = file[file.length - 1];
            if (fileMimeImageMap.containsKey(Extension))
                rowView.image.setImageResource(fileMimeImageMap.get(Extension));
            else if (!new File(owncloudDirectory, fileList.get(position)).isFile())
                rowView.image.setImageResource(fileMimeImageMap.get("dir"));
            else
                rowView.image.setImageResource(R.drawable.file);

            return view;

        }

        protected class RowView {
            TextView text;
            ImageView image;
        }

    }
}
