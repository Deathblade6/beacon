package com.example.deathblade.beaconurl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class Logo_selector extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    int PICK_PHOTO_FOR_AVATAR=1;
    Bundle bundle;
    Intent intent;
    String pref = "logo_position";
    SharedPreferences.Editor editor;
    Context context;

    public Logo_selector() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Logo_selector.
     */
    // TODO: Rename and change types and number of parameters
    public static Logo_selector newInstance(String param1, String param2) {
        Logo_selector fragment = new Logo_selector();
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
        intent = new Intent(getActivity(),MainActivity.class);
        bundle = new Bundle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_logo_selector, container, false);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(pref,0);
        editor=sharedPreferences.edit();
        context=getContext();
        final Spinner dropdown =  view.findViewById(R.id.spinner2);
//create a list of items for the spinner.
        List<String> items = new ArrayList<String>();
        for (int i=5;i<=100;i+=5){
            items.add(i+"%");
        }
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        if (sharedPreferences.getString("Width","").equals("")){
            dropdown.setSelection(0);
        }
        else {
            Log.e("width=",sharedPreferences.getString("Width",""));
            Log.e("index=",Integer.toString(items.indexOf(sharedPreferences.getString("Width",""))));
            dropdown.setSelection(items.indexOf(sharedPreferences.getString("Width","")+"%"));
        }



        final Spinner position = view.findViewById(R.id.spinner3);
        final List<String> strings = new ArrayList<String>();
        strings.add("Center");
        strings.add("Top-Left");
        strings.add("Top-Right");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,strings);
        position.setAdapter(adapter1);
        String pos = sharedPreferences.getString("Position","");
        switch (pos){
            case "Center":
                position.setSelection(0);
                break;
            case "Top-Left":
                position.setSelection(1);
                break;
            case "Top-Right":
                position.setSelection(2);
                break;
            case "":
                position.setSelection(0);
        }

        final ImageView imageView = view.findViewById( R.id.imageView13);

        try{
            File bg = new File(sharedPreferences.getString("Logo_Path",""));
            if (bg.exists()){
                imageView.setImageDrawable(new BitmapDrawable(getResources(),BitmapFactory.decodeStream(new FileInputStream(bg))));
                Log.e("Setting","Bg image");
            }
            else{
                imageView.setImageResource(R.drawable.mid);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        Button change = view.findViewById(R.id.button3);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("Position",position.getSelectedItem().toString());
                String string = dropdown.getSelectedItem().toString();
                string = string.replaceAll("\\D+","");
                Log.e("Width=",string);
                editor.putString("Width",string);
                editor.putString("Position",position.getSelectedItem().toString());
                editor.apply();
                Toast toast = Toast.makeText(context,"Changed",Toast.LENGTH_SHORT);
                toast.show();
//                intent.putExtras(bundle);
//                startActivity(intent);
            }
        });
        return view;
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(intent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_PHOTO_FOR_AVATAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            Uri selectedImageUri = data.getData();
            String logoPath = getPath( getActivity().getApplicationContext(), selectedImageUri );
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                ImageView logo=getActivity().findViewById(R.id.imageView13);
                logo.setImageBitmap(BitmapFactory.decodeStream(inputStream));
            }
            catch (Exception e){
                e.printStackTrace();
            }
            Log.d("Logo Path", logoPath);

//            bundle.putString("Logo_Path",logoPath);
            editor.putString("Logo_Path",logoPath);
            editor.apply();
            //Now you can do whatever you want with your inputstream, save it as file, upload to a server, decode a bitmap...
        }
    }
    public static String getPath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            Toast toast = Toast.makeText(context,"Path not found. Use gallery to select images",Toast.LENGTH_SHORT);
            toast.show();
            result = "Not found";
        }
        return result;
    }


}
