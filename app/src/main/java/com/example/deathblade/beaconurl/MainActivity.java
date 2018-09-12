package com.example.deathblade.beaconurl;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

public class MainActivity extends Activity implements BeaconConsumer{

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static Intent intent;
    private BeaconManager beaconManager = null;
    private WebView webView;
    private String currentURL="";
    private String loadedtURL="";
    protected static final String TAG = "WebViewActivity";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private FrameLayout layout_MainMenu;
    private WebView wb;
    private Boolean bool;
    private Integer clicks=0;
    SharedPreferences sharedPreferences;
    SharedPreferences main;
    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPreferences = getSharedPreferences("logo_position",0);
        SharedPreferences background = getSharedPreferences("Background",0);
         main = getApplicationContext().getSharedPreferences("MainActivity",0);
        SharedPreferences.Editor main_editor = main.edit();
        //Hide Action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);






        //Background set-up
        try{
            File bg = new File(background.getString("BG_path",""));
            RelativeLayout relativeLayout = findViewById(R.id.activity_main);
            File bg_color = new File(background.getString("Color",""));
            if (bg.exists()){
                relativeLayout.setBackground(new BitmapDrawable(getResources(),BitmapFactory.decodeStream(new FileInputStream(bg))));
                Log.e("Setting","Bg image");
            }
            else if (bg_color.exists()){
                Log.e("Setting","BG color");
                relativeLayout.setBackgroundColor(Integer.parseInt(new BufferedReader(new FileReader(bg_color)).readLine()));
            }
            else{
                relativeLayout.setBackgroundResource(R.drawable.bg);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }




        if (!background.getString("BG_path","").equals(main.getString("BG_path","")) && background.getString("Colored","").equals("false")){
            try {
                File f=new File(background.getString("BG_path",""));
                Log.e("Setting up","new bg image");
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                RelativeLayout relativeLayout = findViewById(R.id.activity_main);
                relativeLayout.setBackground(new BitmapDrawable(getResources(),b));
            }
            catch (FileNotFoundException e)
            {
                Log.i("BGError","Y");
                e.printStackTrace();
            }
        }
        Log.e("color=",background.getString("Color",""));
        if (!background.getString("Color","").equals("") && background.getString("Colored","").equals("true")) {
            try {
                int color=Integer.parseInt(background.getString("Color",""));
                RelativeLayout relativeLayout = findViewById(R.id.activity_main);
                relativeLayout.setBackgroundResource(0);
                relativeLayout.setBackgroundColor(color);
            }
            catch (Exception e)
            {
                Log.i("BGError","Y");
                e.printStackTrace();
            }
        }
        if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs Storage rights");
            builder.setMessage("Please grant storage access so this app can detect beacons in the background.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @TargetApi(23)
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_COARSE_LOCATION);
                }

            });
            builder.show();
        }


        //Admin Settings
        final ImageView back;
        back = findViewById(R.id.imageView5);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,Admin_Settings.class);
                startActivity(i);
            }
        });


        //Logo_Set_Up
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        ImageView logo = findViewById(R.id.logo);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)Math.round(0.5*width), (int)Math.round(0.5*height));
        if (!background.getString("Position","").equals("")){
            if(background.getString("Position","").equals("Top-Left")){
                params.addRule(RelativeLayout.ALIGN_START, RelativeLayout.TRUE);
            }
            else if(background.getString("Position","").equals("Top-Right")){
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            }
            else if(background.getString("Position","").equals("Center")){
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            }
        }
        else{
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);}
        logo.setLayoutParams(params);
        if (!sharedPreferences.getString("Logo_Path","").equals(main.getString("Logo path",""))) {
            Log.e("Setting up ","Logo");
            try {
                File f = new File(sharedPreferences.getString("Logo_Path", ""));
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                logo.setImageBitmap(b);
            }
            catch (FileNotFoundException e)
            {
                Log.i("LogoError","y");
                e.printStackTrace();
            }
           }

        try{
            String pos = sharedPreferences.getString("Position","");
            Double width1;
            if (sharedPreferences.getString("Width","").equals("")){
                width1 = 0.5;
            }
            else {
                width1 = Double.parseDouble(sharedPreferences.getString("Width",""))/100;
            }
            Log.e("Meh",pos);
            params = new RelativeLayout.LayoutParams((int)Math.round(width1*width), (int)Math.round(0.5*height));
            File bg = new File(sharedPreferences.getString("Logo_Path",""));
            if (bg.exists()) {
                logo.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(bg)));
                Log.e("Setting", "Bg image");
                if(pos.equals("Top-Left")){
                    params.addRule(RelativeLayout.ALIGN_START, RelativeLayout.TRUE);
                }
                else if(pos.equals("Top-Right")){
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                }
                else if(pos.equals("Center")){
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                }
            }
            else {
                logo.setImageResource(R.drawable.mid);
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        logo.setLayoutParams(params);


        //POPUP
        layout_MainMenu = findViewById( R.id.mainmenu);
        layout_MainMenu.getForeground().setAlpha(0);
        final ImageView login;
        login = findViewById(R.id.imageView4);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(MainActivity.this);
            }
        });


        //QR code
        final ImageView qr,bar;
        qr=findViewById(R.id.imageView2);
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,QR.class);
                startActivity(i);
            }
        });



        //Beacon
        intent = new Intent(this,Website.class);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        verifyBluetooth();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons in the background.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }

                });
                builder.show();
            }
        }
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        beaconManager.bind(this);

        //Button-show
        final CountDownTimer count = new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                login.setVisibility(View.INVISIBLE);
                qr.setVisibility(View.INVISIBLE);
                back.setVisibility(View.INVISIBLE);
            }
        };
        RelativeLayout relativeLayout = findViewById(R.id.activity_main);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicks++;
                if (clicks>=6){
                    login.setVisibility(View.VISIBLE);
                    qr.setVisibility(View.VISIBLE);
                    back.setVisibility(View.VISIBLE);
                    clicks=0;
                    count.start();
                }
            }
        });
    }



    //POPUP
    public void showPopup(final Activity context){

        final View layout=findViewById(R.id.activity_main);
        Log.i("Reached","here");
        layout_MainMenu.getForeground().setAlpha(200); // dim
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.login,null);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        Log.i("height=", Integer.toString(height) + "width=" + Integer.toString(width));
        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
          popupWindow = new PopupWindow(popupView, (int) Math.round(0.75*width)+100, (int) Math.round(0.5*height), true);}
        else {
              popupWindow = new PopupWindow(popupView, (int) Math.round(0.75*width)+100, (int) Math.round(0.75*height)+100, true);}
        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);}
        else{
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);}
        Button registration;
        registration= popupView.findViewById(R.id.button6);
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popupView = inflater.inflate(R.layout.registration,null);
                popupWindow.dismiss();
                final PopupWindow popupWindow = new PopupWindow(popupView, (int) Math.round(0.75*width)+100, (int) Math.round(0.75*height)+100, true);
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                layout_MainMenu.getForeground().setAlpha(200); // dim
                Button cp;
                cp = popupView.findViewById(R.id.button4);
                cp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View popupView = inflater.inflate(R.layout.login,null);
                        popupWindow.dismiss();
                        PopupWindow popupWindow1;
                        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
                              popupWindow1 = new PopupWindow(popupView, (int) Math.round(0.75*width)+100, (int) Math.round(0.5*height), true);}
                        else {
                              popupWindow1 = new PopupWindow(popupView, (int) Math.round(0.75*width)+100, (int) Math.round(0.75*height)+100, true);}
                        popupWindow1.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                        layout_MainMenu.getForeground().setAlpha(200); // dim
                        popupWindow1.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                layout_MainMenu.getForeground().setAlpha(0); // dim
                            }
                        });
                    }
                });
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        layout_MainMenu.getForeground().setAlpha(0); // dim
                    }
                });

            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                layout_MainMenu.getForeground().setAlpha(0); // dim
            }
        });
    }



    //BEACON
    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    //EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
                    Beacon firstBeacon = beacons.iterator().next();
                    Log.d(TAG, "The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");
                    //logToDisplay("The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");
                    bool=false;
                }
                else {
                    bool=true;
                    wb=findViewById(R.id.webview);
                    wb.setVisibility(View.INVISIBLE);
                }
                for (Beacon beacon: beacons) {
                    if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x10) {
                        // This is a Eddystone-URL frame
                        String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                        if(!currentURL.equals(url)) {
                            currentURL=url;
                            updateDisplay("Beacon update");
                        }
                    }
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }

    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
//                        finish();
//                        System.exit(0);
                    }
                });
                builder.show();
            }
        }
        catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
//                    finish();
//                    System.exit(0);
                }

            });
            builder.show();

        }

    }

    public void updateDisplay(final String line) {

        runOnUiThread(new Runnable() {
            public void run() {
                Log.d(TAG, line);
                if(!loadedtURL.equals(currentURL)) {
                    wb=findViewById(R.id.webview);
                    wb.setWebViewClient(new WebViewClient() {
                        public boolean shouldOverrideUrlLoading(WebView view, String url){
                            // do your handling codes here, which url is the requested url
                            // probably you need to open that url rather than redirect:
                            view.loadUrl(url);
                            return false; // then it is not handled by default action
                        }
                    });
                    wb.setVisibility(View.VISIBLE);
                    wb.loadUrl(currentURL);
                }
            }
        });
    }


    //Margins
    public void setMargins (View v) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            p.setMargins(displayMetrics.widthPixels/4, displayMetrics.heightPixels-30, (int) Math.round(0.75*displayMetrics.widthPixels), 30);
            v.requestLayout();
        }
    }
}
