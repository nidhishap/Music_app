package com.example.my_music;

import static android.os.Environment.getExternalStorageState;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String[] items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        runtimePermission();
    }
    public void runtimePermission()
    {
       Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
               .withListener(new MultiplePermissionsListener() {
                   @Override
                   public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                       displaySongs();
                   }

                   @Override
                   public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                   }
               }).check();





    }
    class customAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {

            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myview = getLayoutInflater().inflate(R.layout.list_item,null);
            TextView textsong = myview.findViewById(R.id.textsongname);
            textsong.setSelected(true);
            textsong.setText(items[i]);
            return myview;
        }
    }
    public ArrayList<File> fetchSongs(File file) {
        ArrayList arraylist = new ArrayList();
        File[] songs = file.listFiles();
        if (songs != null) {
            for (File myFile : songs) {
                if (!myFile.isHidden() && myFile.isDirectory()) {
                    arraylist.addAll(fetchSongs(myFile));
                } else {
                    if (myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")) {
                        arraylist.add(myFile);
                    }
                }
            }

        }
        return arraylist;
    }
    void displaySongs()
    {
        ArrayList<File> mysongs= fetchSongs(Environment.getExternalStorageDirectory());
        Toast.makeText(getApplicationContext(),getExternalStorageState(),Toast.LENGTH_SHORT).show();
        items = new String[mysongs.size()];
        for(int i=0;i<mysongs.size();i++)
        {
            items[i] = mysongs.get(i).getName().replace(".mp3","");

        }

        //ArrayAdapter<String> adapter=new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
        //listView.setAdapter(adapter);
        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                String songname = (String) listView.getItemAtPosition(position);
                                startActivity(new Intent(getApplicationContext(),PlaySong.class)
                                .putExtra("songList",mysongs)
                                .putExtra("songname", songname)
                                .putExtra("pos",position)
                                );


                            }
                        });
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position , long id)
            {
                Intent intent = new Intent(MainActivity.this,PlaySong.class);
                String currentSong = listView.getItemAtPosition(position).toString();
                intent.putExtra("songList",mysongs);
                intent.putExtra("currentSong", currentSong);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });*/
    }
}


