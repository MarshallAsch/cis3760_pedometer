package de.j4velin.pedometer.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.NumberFormat;
import java.util.Locale;

import de.j4velin.pedometer.Database;
import de.j4velin.pedometer.R;


public class Fragment_Analytics extends android.app.Fragment {
    private int total_days,total_start;
    private TextView caloriesView, stepsView, distanceView;
    private final static NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());

    public Fragment_Analytics() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Fragment_Analytics newInstance(String param1, String param2) {
        Fragment_Analytics fragment = new Fragment_Analytics();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_analytics, null);

        //Get XML textviews
        stepsView = (TextView) v.findViewById(R.id.total_steps);
        distanceView = (TextView) v.findViewById(R.id.distance);
        caloriesView = (TextView) v.findViewById(R.id.calories);

        //sample graphing points
        //TODO:: replace with real values
        GraphView graph = (GraphView) v.findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3)
        });
        graph.addSeries(series);

        //showing month selections for spinner drop down
        Spinner timeSpinner = (Spinner) v.findViewById(R.id.time_label);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.time_labels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);


        //Get Db instance to use db functions
        Database db = Database.getInstance(getActivity());
        total_start = db.getTotalWithoutToday();
        total_days = db.getDays();


        //set updated text

        setTotalSteps();
        setTotalDistance();
        setCalories();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem pause = menu.getItem(0);
        Drawable d;
        if (getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE)
                .contains("pauseCount")) { // currently paused
            pause.setTitle(R.string.resume);
            d = getResources().getDrawable(R.drawable.ic_resume);
        } else {
            pause.setTitle(R.string.pause);
            d = getResources().getDrawable(R.drawable.ic_pause);
        }
        d.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        pause.setIcon(d);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return ((Activity_Main) getActivity()).optionsItemSelected(item);
    }

    public void setTotalSteps(){
        stepsView.setText(formatter.format(total_start));
    }

    public void setTotalDistance(){
        // distance formula based off https://www.convertunits.com/from/steps/to/kilometers
        distanceView.setText(formatter.format(total_start * 0.000762));
    }

    public void setCalories(){
        //not very accurate without the weight which has yet to be implemmetned
        //hardcoded (distance * 50 (const))
        caloriesView.setText(formatter.format((total_start * 0.000762) * 50));
    }



}
