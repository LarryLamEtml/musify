package musify.lamho.musify;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lamho on 07.06.2017.
 */

public class ListFileActivity extends ListActivity {

    private String path;
    private String extensions[] = {"mp3"};
    MainActivity main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        main = new MainActivity();
        //check for permission
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        // Use the current directory as title
        //path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        path = Environment.getExternalStorageDirectory().getPath();

        if (getIntent().hasExtra("path")) {
            path = getIntent().getStringExtra("path");
        }
        setTitle(path);

        // Read all files sorted into the values-array
        List<String> values = new ArrayList<>();
        File dir = new File(path);
        if (!dir.canRead()) {
            setTitle(getTitle() + " (inaccessible)");
        }
        File[] files = new File(path).listFiles();

        if (files != null) {
            for (File file : files) {

                if (!file.getName().startsWith(".")) {
                    for (String extension:extensions) {
                        if(file.getName().endsWith(extension)||file.isDirectory())
                        {
                            values.add(file.getName());
                            break;
                        }
                    }
                }
            }
        }
        Collections.sort(values);

        // Put the data into the list
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, values);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String filename = (String) getListAdapter().getItem(position);
        if (path.endsWith(File.separator)) {
            filename = path + filename;
        } else {
            filename = path + File.separator + filename;
        }
        if (new File(filename).isDirectory()) {
            Intent intent = new Intent(this, ListFileActivity.class);
            intent.putExtra("path", filename);
            startActivity(intent);
        } else{
            String filenameArray[] = filename.split("/");
            String fileName[] = filenameArray[filenameArray.length-1].split("\\.");
            String extension_ = fileName[fileName.length-1];
            for (String extension:extensions) {
                if(extension_.endsWith(extension))
                {
                    main.playMusic(new File(filename).getPath());
                    super.onBackPressed();

                    return;
                }
            }
            Toast.makeText(this, fileName[0] + " ne peut être joué", Toast.LENGTH_LONG).show();
        }
    }
}