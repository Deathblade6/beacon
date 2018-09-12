package com.example.deathblade.beaconurl;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class App_settings extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView qr,password,exp,imp;
    SharedPreferences settings ;
    SharedPreferences.Editor editor ;
    String pref = "App settings";
    public App_settings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment App_settings.
     */
    // TODO: Rename and change types and number of parameters
    public static App_settings newInstance(String param1, String param2) {
        App_settings fragment = new App_settings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        settings = getContext().getSharedPreferences(pref, 0);
        editor = settings.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view1= inflater.inflate(R.layout.fragment_app_settings, container, false);
        getActivity().setTitle("App Settings");

        qr=view1.findViewById(R.id.textView6);
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view1.getContext());
                builder.setTitle("QR/Barcode URL Timeout");

// Set up the input
                final EditText input = new EditText(view1.getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                SharedPreferences settings = getContext().getSharedPreferences(pref, 0);
                if (settings.getInt("Interval",0)!=0){
                    Log.e("Value=",Integer.toString(settings.getInt("Interval",0)));
                input.setHint(Integer.toString(settings.getInt("Interval",0)));}
                builder.setView(input);
// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putInt("Interval",Integer.parseInt(input.getText().toString()));
                        Boolean b=editor.commit();
                        Log.e("Commit=",Boolean.toString(b));
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
        password = view1.findViewById(R.id.textView7);
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Change Password");

// Set up the input
                final EditText input = new EditText(getContext());
                final EditText input1 = new EditText(getContext());
                LinearLayout linearLayout = new LinearLayout(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                input.setHint("Set New Password");
                input1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                input1.setHint("Confirm New Password");
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(input);
                linearLayout.addView(input1);
                builder.setView(linearLayout);
// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        m_Text = input.getText().toString();
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

//        exp = view.findViewById(R.id.textView8);
//        exp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DisplayMetrics displayMetrics = new DisplayMetrics();
//                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                final int height = displayMetrics.heightPixels;
//                final int width = displayMetrics.widthPixels;
//                View popupView = getLayoutInflater().inflate(R.layout.fragment_configuration,null);
//                final PopupWindow popupWindow = new PopupWindow(popupView, (int) Math.round(0.75*width)+50, (int) Math.round(0.75*height)+50, true);
//                int location[] = new int[2];
//                // Get the View's(the one that was clicked in the Fragment) location
//                view.getLocationOnScreen(location);
//
//                // Using location, the PopupWindow will be displayed right under anchorView
//                popupWindow.showAtLocation(view, Gravity.CENTER,
//                        location[0], location[1] + view.getHeight());
//            }
//        });

        return view1;
    }
}
