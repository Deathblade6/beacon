package com.example.deathblade.beaconurl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


public class whitelist1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public whitelist1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment whitelist1.
     */
    // TODO: Rename and change types and number of parameters
    public static whitelist1 newInstance(String param1, String param2) {
        whitelist1 fragment = new whitelist1();
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
        getActivity().setTitle("Beacon Whitelist");
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences("Ignore",0);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_whitelist, container, false);
        final CheckBox checkBox = view.findViewById(R.id.checkBox);

        String use = sharedPreferences.getString("Use","");
        if (use.equals("true")){
            checkBox.setChecked(true);
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("Use",Boolean.toString(checkBox.isChecked()));
                Log.e("Use=",Boolean.toString(checkBox.isChecked()));
                editor.apply();
            }
        });


        TextView textView = view.findViewById(R.id.textView4);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Whitelist_lists.class);
                startActivity(intent);
            }
        });
        return view;
    }

}
