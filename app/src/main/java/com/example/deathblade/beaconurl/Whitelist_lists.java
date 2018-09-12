package com.example.deathblade.beaconurl;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Whitelist_lists extends AppCompatActivity {
    private String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_whitelist_lists);
        final Context context = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final ListView listView = findViewById(R.id.List_view);
        final List<String> list = new ArrayList<>();
        final File file = new File(Environment.getExternalStorageDirectory()+"/Beacon","Ignore.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                Log.e("Reading","here");
                list.add(line);
            }
            ArrayAdapter adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,android.R.id.text1,list);
            listView.setAdapter(adapter);
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        LinearLayout add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Add UUID");

// Set up the input
                final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT );
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            File dir = new File(Environment.getExternalStorageDirectory()+"/Beacon");
                            if (!dir.exists()){
                                Boolean v= dir.mkdirs();
                                Log.e("Directory made for ig",Boolean.toString(v));
                            }
                            FileOutputStream fileOutputStream = new FileOutputStream(file,true);
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                            Log.e("Writing","here");
                            outputStreamWriter.append(input.getText().toString()+'\n');
                            outputStreamWriter.close();
                            fileOutputStream.close();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                       list.add(input.getText().toString());
                       ArrayAdapter adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,android.R.id.text1,list);
                       listView.setAdapter(adapter);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {
                // TODO Auto-generated method stub
                Log.e("Rec","her");
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure you want to delete it?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            PrintWriter printWriter = new PrintWriter(file);

                        printWriter.write("");
                        printWriter.close();}
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        list.remove(pos);
                        ArrayAdapter adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,android.R.id.text1,list);
                        listView.setAdapter(adapter);
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file,true);
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                            Log.e("Writing","here");
                            for (int pos=0;pos<list.size();pos++){
                            outputStreamWriter.append(list.get(pos)+'\n');}
                            outputStreamWriter.close();
                            fileOutputStream.close();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(),  Admin_Settings.class);
        Bundle bundle = new Bundle();
        bundle.putString("Whitelist","yup");
        myIntent.putExtras(bundle);
        startActivity(myIntent);
        return true;
    }
}
