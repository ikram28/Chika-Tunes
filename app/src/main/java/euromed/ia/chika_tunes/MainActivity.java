package euromed.ia.chika_tunes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String[] items;

    final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());


    private static final String PERMISSION_EXTRNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;

    private static final int PERMISSION_REQ_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listViewSong);

        requestRuntimePermission();
        displaySongs();

    }

    private void requestRuntimePermission(){
        if(ActivityCompat.checkSelfPermission(this,PERMISSION_EXTRNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Permision Granted",
                    Toast.LENGTH_SHORT).show();

        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(this,PERMISSION_EXTRNAL_STORAGE)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This app requires ACCESS_FILE permission for particular feature to work as expected.")
                    .setTitle("Permission Required")
                    .setCancelable(false)
                    .setPositiveButton("Ok", (dialog,which) -> {
                        ActivityCompat.requestPermissions(MainActivity.this,new String[] {PERMISSION_EXTRNAL_STORAGE},
                                PERMISSION_REQ_CODE);
                        dialog.dismiss();

                    }).setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));
            builder.show();

        }
        else {
            ActivityCompat.requestPermissions(this,new String[]{PERMISSION_EXTRNAL_STORAGE},
                    PERMISSION_REQ_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions, grantResults);

        if(requestCode == PERMISSION_REQ_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted" ,
                        Toast.LENGTH_SHORT).show();
            }
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION_EXTRNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You cannot use ChiKa_Tunes. You need to allow access")
                    .setTitle("Permission Required")
                    .setCancelable(false)
                    .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()))
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);

                            dialog.dismiss();

                        }
                    });

            builder.show();




        } else{
            requestRuntimePermission();
        }

    }




    public ArrayList<File> findSong(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden() && !singleFile.getName().equals("Android")) {
                    arrayList.addAll(findSong(singleFile));
                } else {
                    if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
                        arrayList.add(singleFile);
                    }
                }
            }
        }

        return arrayList;
    }

    void displaySongs() {
        items = new String[mySongs.size()];

        for (int i = 0; i < mySongs.size(); i++) {
            items[i] = mySongs.get(i).getName().replace(".mp3", "").replace(".wav", "");
        }

        /*ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(myAdapter);*/

        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName = (String) listView.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                        .putExtra("songs", mySongs)
                        .putExtra("songname", songName)
                        .putExtra("pos",i));

            }
        });
    }


    class customAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View myView = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView textsong = myView.findViewById(R.id.txtartistname);
            textsong.setSelected(true);
            //textsong.setText(items[i]);


            // Extract title and artist from the file name
            String fileName = items[i];
            String[] parts = fileName.split("-");

            if (parts.length > 0) {
                // Set title
                textsong.setText(parts[0].trim());

                // If there's an artist, display it in italics
                if (parts.length > 1) {
                    TextView artistText = myView.findViewById(R.id.txtsongname);
                    artistText.setVisibility(View.VISIBLE);
                    artistText.setText(parts[1].trim());
                }
            }

            ImageView imageSong = myView.findViewById(R.id.imgsong);

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(mySongs.get(i).getPath());
            byte[] artwork = retriever.getEmbeddedPicture();

            if (artwork != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(artwork, 0, artwork.length);
                imageSong.setImageBitmap(bitmap);
            } else {
                // Set a default image if the embedded image doesn't exist
                imageSong.setImageResource(R.drawable.ic_music_note);
            }

            return myView;


        }
    }
}