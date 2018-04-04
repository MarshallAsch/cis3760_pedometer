package de.j4velin.pedometer.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.j4velin.pedometer.R;

import static android.app.Activity.RESULT_OK;

/**
 * Created by william on 3/21/18.
 */

public class Fragment_Profile extends android.app.Fragment {
    public static final int IMAGE_GALLERY_REQUEST = 20;
    public static final int CAMERA_REQUEST = 21;
    private EditText nameField;
    private EditText weightField;
    private EditText biographyField;
    private Button saveButton;
    private Button takePhotoButton;
    private Button selectPhotoButton;
    private ImageView profilePicture;

    public static String encodeTobase64(Bitmap image) {
        Bitmap newImage = image;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        newImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] b = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    public void initializeProfileFields(View v) {
        takePhotoButton = (Button) v.findViewById(R.id.take_photo);
        selectPhotoButton = (Button) v.findViewById(R.id.select_photo);
        nameField = (EditText) v.findViewById(R.id.name_field);
        weightField = (EditText) v.findViewById(R.id.weight_field);
        biographyField  = (EditText) v.findViewById(R.id.biography_field);
        saveButton = (Button) v.findViewById(R.id.save_profile);
        profilePicture = (ImageView) v.findViewById(R.id.profile_picture);

        // setting the shared preferences
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String name = sharedPref.getString("name", "Bob");
        String biography = sharedPref.getString("biography", "Enter a funky fact about yourself...");
        float weight = sharedPref.getFloat("weight", 130);
        String profileImageBase64 = sharedPref.getString("profileImage", null);

        if (profileImageBase64 != null) {
            byte[] decodedString = Base64.decode(profileImageBase64, Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profilePicture.setImageBitmap(image);
        }

        nameField.setText(name, TextView.BufferType.EDITABLE);
        biographyField.setText(biography, TextView.BufferType.EDITABLE);
        weightField.setText(Float.toString(weight), TextView.BufferType.EDITABLE);

        // saving the shared preferences on click
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("name", nameField.getText().toString()).apply();
                editor.putString("biography", biographyField.getText().toString()).apply();
                editor.putFloat("weight", Float.parseFloat(weightField.getText().toString())).apply();

                String encodedImage = encodeTobase64(((BitmapDrawable) profilePicture.getDrawable()).getBitmap());
                editor.putString("profileImage", encodedImage);
                editor.commit();

                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(nameField.getWindowToken(), 0);
                mgr.hideSoftInputFromWindow(biographyField.getWindowToken(), 0);
                mgr.hideSoftInputFromWindow(weightField.getWindowToken(), 0);
                getFragmentManager().popBackStackImmediate();
            }
        });

        selectPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirectoryPath = pictureDirectory.getPath();

                Uri data = Uri.parse(pictureDirectoryPath);
                photoPickerIntent.setDataAndType(data, "image/*");

                startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);
            }
        });

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                Uri imageUri = data.getData();

                InputStream inputStream;

                    try {
                    inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                    Bitmap image = BitmapFactory.decodeStream(inputStream);

                    profilePicture.setImageBitmap(image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CAMERA_REQUEST) {
                Bitmap cameraImage = (Bitmap) data.getExtras().get("data");
                profilePicture.setImageBitmap(cameraImage);
            }
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, null);

        // initializing edit fields
        initializeProfileFields(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(nameField.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(biographyField.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(weightField.getWindowToken(), 0);
        return ((Activity_Main) getActivity()).optionsItemSelected(item);
    }
}
