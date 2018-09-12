package com.example.deathblade.beaconurl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Whitelist extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_whitelist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void list(View view){
        Intent intent=new Intent(this,Whitelist_lists.class);
        startActivity(intent);
    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), Admin_Settings.class);
        startActivityForResult(myIntent, 0);
        return true;

    }
}
