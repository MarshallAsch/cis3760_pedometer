package de.j4velin.pedometer.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

        //showing month selections for spinner drop down
        Spinner timeSpinner = (Spinner) v.findViewById(R.id.time_label);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.time_labels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);

        //Default place holder value so the graph appears even if there is no data
        GraphView graph = (GraphView) v.findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 0),
        });
        graph.addSeries(series);

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getItemAtPosition(i).toString().equals("All Time")){
                    Log.d("Analytics","All Time");
                    //TODO: change fake values
                    GraphView graph = (GraphView) v.findViewById(R.id.graph);
                    graph.removeAllSeries();
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                            new DataPoint(0, 1400204),
                            new DataPoint(1, 1205125),
                            new DataPoint(2, 1099323),
                            new DataPoint(3, 1600633),
                            new DataPoint(4, 1200432),
                            new DataPoint(5, 1350295),
                            new DataPoint(6, 1400595),
                            new DataPoint(7, 1220001),
                    });
                    graph.addSeries(series);

                }
                else if(adapterView.getItemAtPosition(i).toString().equals("Year")){
                    Log.d("Analytics","Year");
                    //TODO: change fake values
                    GraphView graph = (GraphView) v.findViewById(R.id.graph);
                    graph.removeAllSeries();
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                            new DataPoint(1, 40200),
                            new DataPoint(2, 10200),
                            new DataPoint(3, 11142),
                            new DataPoint(4, 14101),
                            new DataPoint(5, 11542),
                            new DataPoint(6, 12041),
                            new DataPoint(7, 19004),
                            new DataPoint(8, 61111),
                            new DataPoint(9, 44122),
                            new DataPoint(10, 10221),
                            new DataPoint(11, 11901),
                            new DataPoint(12, 14018),
                    });
                    graph.addSeries(series);


                }
                else if(adapterView.getItemAtPosition(i).toString().equals("Month")){
                    //TODO: change fake values
                    Log.d("Analytics","Month");
                    GraphView graph = (GraphView) v.findViewById(R.id.graph);
                    graph.removeAllSeries();
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                            new DataPoint(1, 4022),
                            new DataPoint(2, 1020),
                            new DataPoint(3, 1190),
                            new DataPoint(4, 1401),
                            new DataPoint(5, 1150),
                            new DataPoint(6, 1204),
                            new DataPoint(7, 1900),
                            new DataPoint(8, 6002),
                            new DataPoint(9, 4022),
                            new DataPoint(10, 1020),
                            new DataPoint(11, 1190),
                            new DataPoint(12, 1401),
                            new DataPoint(13, 1150),
                            new DataPoint(14, 1204),
                            new DataPoint(15, 1900),
                            new DataPoint(16, 6008),
                            new DataPoint(17, 4029),
                            new DataPoint(18, 1020),
                            new DataPoint(19, 1190),
                            new DataPoint(20, 1401),
                            new DataPoint(21, 1150),
                            new DataPoint(22, 1204),
                            new DataPoint(23, 1900),
                            new DataPoint(24, 6001),
                            new DataPoint(25, 4022),
                            new DataPoint(26, 1020),
                            new DataPoint(27, 1190),
                            new DataPoint(28, 1401),
                            new DataPoint(29, 1150),
                            new DataPoint(30, 1204),
                            new DataPoint(31, 1900),
                    });
                    graph.addSeries(series);

                }
                else if(adapterView.getItemAtPosition(i).toString().equals("Day")){
                    Log.d("Analytics","Day");
                    //TODO: change fake values
                    GraphView graph = (GraphView) v.findViewById(R.id.graph);
                    graph.removeAllSeries();
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                            new DataPoint(0, 0),
                            new DataPoint(1, 0),
                            new DataPoint(2, 0),
                            new DataPoint(3, 0),
                            new DataPoint(4, 0),
                            new DataPoint(5, 0),
                            new DataPoint(6, 0),
                            new DataPoint(7, 0),
                            new DataPoint(8, 0),
                            new DataPoint(9, 0),
                            new DataPoint(10, 150),
                            new DataPoint(11, 30),
                            new DataPoint(12, 40),
                            new DataPoint(13, 50),
                            new DataPoint(14, 80),
                            new DataPoint(15, 90),
                            new DataPoint(16,240),
                            new DataPoint(17, 144),
                            new DataPoint(18, 14),
                            new DataPoint(19, 5),
                            new DataPoint(20, 3),
                            new DataPoint(21, 145),
                            new DataPoint(22, 10),
                            new DataPoint(23, 15),
                            new DataPoint(24, 1),
                    });
                    graph.addSeries(series);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //Get Db instance to use db functions
        Database db = Database.getInstance(getActivity());
        total_start = db.getAllSteps();
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
