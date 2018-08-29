package com.example.deathblade.beaconurl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.InputStream;


public class Home_screen extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    int PICK_PHOTO_FOR_AVATAR=1;
    int buttonid;
    Intent i;
    Bundle bundle;
    Bitmap bm;

    public Home_screen() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home_screen.
     */
    // TODO: Rename and change types and number of parameters
    public static Home_screen newInstance(String param1, String param2) {
        Home_screen fragment = new Home_screen();
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
        i = new Intent(getActivity(),MainActivity.class);
        bundle = new Bundle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home_screen, container, false);

        Spinner dropdown =  view.findViewById(R.id.spinner1);
//create a list of items for the spinner.
        String[] items = new String[]{"Center", "Top-right", "Top-left"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        dropdown.setSelection(0);
        final Button button = view.findViewById( R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
                buttonid=view.getId();
            }
        });
        Button bg  =view.findViewById(R.id.bg_change);
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
                buttonid=view.getId();
            }
        });

        Button change = view.findViewById(R.id.button10);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(i);
            }
        });
        return view;
    }
    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            if (buttonid==R.id.button3){
                Uri selectedImageUri = data.getData();
                String logoPath = getPath( getActivity().getApplicationContext(), selectedImageUri );
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                    ImageView logo=getActivity().findViewById(R.id.imageView);
                    logo.setImageBitmap(BitmapFactory.decodeStream(inputStream));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                Log.d("Logo Path", logoPath);
                bundle.putString("Logo_path",logoPath);
                i.putExtras(bundle);
            }
            else if (buttonid==R.id.bg_change){
                Uri selectedImageUri = data.getData( );
                String logoPath = getPath( getActivity().getApplicationContext(), selectedImageUri );
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                    ImageView bg=getActivity().findViewById(R.id.imageView8);
                    bg.setImageBitmap(BitmapFactory.decodeStream(inputStream));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                Log.d("Background Path", logoPath);
                bundle.putString("BG_path",logoPath);
                i.putExtras(bundle);
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }
    public static String getPath( Context context, Uri uri ) {
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
            result = "Not found";
        }
        return result;
    }
}
