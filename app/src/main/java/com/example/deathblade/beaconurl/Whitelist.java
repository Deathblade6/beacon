package com.example.deathblade.beaconurl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Whitelist extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whitelist);
    }
    public void list(View view){
        Intent intent=new Intent(this,Whitelist_lists.class);
        startActivity(intent);
    }
}
