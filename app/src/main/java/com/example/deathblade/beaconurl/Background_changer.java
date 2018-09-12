package com.example.deathblade.beaconurl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;

import top.defaults.colorpicker.ColorPickerPopup;


public class Background_changer extends Fragment {
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
    Context context;
    SharedPreferences bg1;
    SharedPreferences.Editor editor;

    public Background_changer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Background_changer.
     */
    // TODO: Rename and change types and number of parameters
    public static Background_changer newInstance(String param1, String param2) {
        Background_changer fragment = new Background_changer();
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
        context=getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home_screen, container, false);
        getActivity().setTitle("Home Settings");

        bg1 = getContext().getSharedPreferences("Background",0);
        editor = bg1.edit();

        final ImageView imageView = view.findViewById( R.id.imageView8);



        try{
            File bg = new File(bg1.getString("BG_path",""));
            if (bg.exists()){
                imageView.setImageDrawable(new BitmapDrawable(getResources(),BitmapFactory.decodeStream(new FileInputStream(bg))));
                Log.e("Setting","Bg image");
            }
            else{
                imageView.setImageResource(R.drawable.bg);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
                buttonid=view.getId();
            }
        });
        final ImageView bg  =view.findViewById(R.id.imageView11);
        try{
            String bg_color = bg1.getString("Color","");
            if (!bg_color.equals("")){
                Log.e("Setting","BG color");
                bg.setBackgroundColor(Integer.parseInt(bg_color));
            }
            else{
                bg.setBackgroundColor(Color.CYAN);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new ColorPickerPopup.Builder(getActivity())
                        .initialColor(Color.RED) // Set initial color
                        .enableAlpha(true) // Enable alpha slider or not
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(view, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                bg.setBackgroundColor(color);
                                editor.putString("Color",Integer.toString(color));
                                editor.apply();
//                                bundle.putString("Color",Integer.toString(color));
//                                i.putExtras(bundle);

                            }

                            @Override
                            public void onColor(int color, boolean fromUser) {

                            }
                        });
            }
        });
//
        Button change = view.findViewById(R.id.button10);
        final Switch s=view.findViewById(R.id.switch1);
        Log.e("Look here",bg1.getString("Colored",""));
        if (bg1.getString("Colored","").equals("true")){
            s.setChecked(true);
        }
        else {
            s.setChecked(false);
        }
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(s.isChecked()){
                    editor.putString("Colored","true");
//                    bundle.putString("Colored","true");
                }
                else {
                    editor.putString("Colored","false");
//                    bundle.putString("Colored","false");
                }
//                i.putExtras(bundle);
//                startActivity(i);
                editor.apply();
                Toast toast =  Toast.makeText(context,"Changed background",Toast.LENGTH_SHORT);
                toast.show();
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
                    ImageView bg=getActivity().findViewById(R.id.imageView8);
                    bg.setImageBitmap(BitmapFactory.decodeStream(inputStream));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                Log.d("Background Path", logoPath);
//                bundle.putString("BG_path",logoPath);
                editor.putString("BG_path",logoPath);
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
