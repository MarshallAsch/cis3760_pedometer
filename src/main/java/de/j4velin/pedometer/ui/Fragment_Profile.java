package de.j4velin.pedometer.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.j4velin.pedometer.R;

/**
 * Created by william on 3/21/18.
 */

public class Fragment_Profile extends android.app.Fragment {
    private EditText nameField;
    private EditText weightField;
    private EditText biographyField;
    private Button saveButton;

    public void initializeProfileFields(View v) {
        nameField = (EditText) v.findViewById(R.id.name_field);
        weightField = (EditText) v.findViewById(R.id.weight_field);
        biographyField  = (EditText) v.findViewById(R.id.biography_field);
        saveButton = (Button) v.findViewById(R.id.save_profile);

        // setting the shared preferences
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String name = sharedPref.getString("name", "Bob");
        String biography = sharedPref.getString("biography", "Enter a funky fact about yourself...");
        float weight = sharedPref.getFloat("weight", 130);

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
                editor.commit();

                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(nameField.getWindowToken(), 0);
                mgr.hideSoftInputFromWindow(biographyField.getWindowToken(), 0);
                mgr.hideSoftInputFromWindow(weightField.getWindowToken(), 0);
                getFragmentManager().popBackStackImmediate();
            }
        });
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
