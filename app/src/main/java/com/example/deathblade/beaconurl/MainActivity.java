package com.example.deathblade.beaconurl;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide Action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        //Home-Screen
        Intent extras = getIntent();
        if (extras.getStringExtra("Logo_path")!=null) {
            try {
                File f=new File(extras.getStringExtra("Logo_path"));
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                ImageView img=findViewById(R.id.logo);
                img.setImageBitmap(b);
            }
            catch (FileNotFoundException e)
            {
                Log.i("LogoError","y");
                e.printStackTrace();
            }
           }
//        if (extras.getStringExtra("BG_path")!=null) {
//            try {
//                File f=new File(extras.getStringExtra("BG_path"));
//                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//                ImageView bg=findViewById(R.id.bg);
//                bg.setImageBitmap(b);
//            }
//            catch (FileNotFoundException e)
//            {
//                Log.i("BGError","Y");
//                e.printStackTrace();
//            }
//        }
        if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs Storage rights");
            builder.setMessage("Please grant storage access so this app can detect beacons in the background.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @TargetApi(23)
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_COARSE_LOCATION);
                }

            });
            builder.show();
        }


        //Admin Settings
        ImageView back;
        back = findViewById(R.id.imageView5);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,Admin_Settings.class);
                startActivity(i);
            }
        });



        //POPUP
        layout_MainMenu = findViewById( R.id.mainmenu);
        layout_MainMenu.getForeground().setAlpha(0);
        ImageView login;
        login = findViewById(R.id.imageView4);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(MainActivity.this);
            }
        });


        //QR code
        ImageView qr,bar;
        qr=findViewById(R.id.imageView2);
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,QR.class);
                startActivity(i);
            }
        });
        bar=findViewById(R.id.imageView3);
        bar.setOnClickListener(new View.OnClickListener() {
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
    }



    //POPUP
    public void showPopup(final Activity context){
        layout_MainMenu.getForeground().setAlpha(200); // dim
        Log.i("Reached","here");
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.registration,null);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        Log.i("height=", Integer.toString(height) + "width=" + Integer.toString(width));
        final PopupWindow popupWindow = new PopupWindow(popupView, (int) Math.round(0.75*width)+50, (int) Math.round(0.75*height)+50, true);
        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);}
        else{
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);}
        Button login;
        login= popupView.findViewById(R.id.button4);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popupView = inflater.inflate(R.layout.login,null);
                popupWindow.dismiss();
                final PopupWindow popupWindow = new PopupWindow(popupView, (int) Math.round(0.75*width)+50, (int) Math.round(0.5*height)+50, true);
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                layout_MainMenu.getForeground().setAlpha(200); // dim
                Button cp;
                cp = popupView.findViewById(R.id.button6);
                cp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View popupView = inflater.inflate(R.layout.change_password,null);
                        popupWindow.dismiss();
                        final PopupWindow popupWindow = new PopupWindow(popupView, (int) Math.round(0.75*width)+100, (int) Math.round(0.75*height)+50, true);
                        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                        layout_MainMenu.getForeground().setAlpha(200); // dim
                        Button cp_login;
                        cp_login = popupView.findViewById(R.id.button8);
                        cp_login.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                View popupView = inflater.inflate(R.layout.login,null);
                                popupWindow.dismiss();
                                final PopupWindow popupWindow = new PopupWindow(popupView, (int) Math.round(0.75*width)+50, (int) Math.round(0.5*height)+50, true);
                                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                                layout_MainMenu.getForeground().setAlpha(200); // dim
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
                });
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        layout_MainMenu.getForeground().setAlpha(0); // dim
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
                }
                for (Beacon beacon: beacons) {
                    if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x10) {
                        // This is a Eddystone-URL frame
                        String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());

                        if(!currentURL.equals(url)) {
                            currentURL=url;
                            updateDisplay("Beacon update");
                            //webView.loadUrl(currentURL);
                            //Intent myIntent = new Intent(getApplicationContext(), WebViewer.class);
                            //myIntent.putExtra("url",currentURL);
                            //startActivity(myIntent);
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
                        finish();
                        System.exit(0);
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
                    finish();
                    System.exit(0);
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
                    Bundle bundle=new Bundle();
                    bundle.putString("URL",currentURL);
                    intent.putExtras(bundle);
                    startActivity(intent);
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
