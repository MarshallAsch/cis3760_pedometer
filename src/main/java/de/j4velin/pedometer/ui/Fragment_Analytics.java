package de.j4velin.pedometer.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.j4velin.pedometer.Database;
import de.j4velin.pedometer.R;
import de.j4velin.pedometer.util.Util;


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
                int steps;
                Database db = Database.getInstance(getActivity());
                Calendar calendar = Calendar.getInstance();
                Calendar calendarCal = Calendar.getInstance();

                if(adapterView.getItemAtPosition(i).toString().equals("All Time")){
                    Log.d("Analytics","All Time");
                    //TODO: change fake values
                    GraphView graph = (GraphView) v.findViewById(R.id.graph);
                    graph.removeAllSeries();
                    // get current year's steps
                    List<Pair<Long, Integer>> allYears = db.getDayMonthSteps(Util.getFirstDayYear(1970),Util.getLastDayYear(calendar.get(Calendar.YEAR))+1);

                    Log.d("Analytics","allYears: "+ allYears);
                    DataPoint[] yearPoints = new DataPoint[allYears.size()];

                    for(int c = 0; c < yearPoints.length; c++){
                        steps = allYears.get(c).second;
                        yearPoints[c] = new DataPoint(c+1, steps);
                    }


                    BarGraphSeries <DataPoint> series = new BarGraphSeries<>(yearPoints);
                    graph.getViewport().setScrollable(true);
                    graph.addSeries(series);
                    series.setSpacing(50);
                    series.setDrawValuesOnTop(true);
                    series.setValuesOnTopColor(Color.BLACK);

                }
                else if(adapterView.getItemAtPosition(i).toString().equals("Year")){
                    Log.d("Analytics","Year");
                    GraphView graph = (GraphView) v.findViewById(R.id.graph);
                    graph.removeAllSeries();
                    // get current year's steps
                    List<Pair<Long, Integer>> yearSteps = db.getDayMonthSteps(Util.getFirstDayYear(calendar.get(Calendar.YEAR)+1),Util.getLastDayYear(calendar.get(Calendar.YEAR))+1);

                    DataPoint[] yearPoints = new DataPoint[12];

                    for(int c = 0; c < yearPoints.length; c++){
                        steps = 0;
                        for(int d = 0; d<yearSteps.size(); d++){
                            calendarCal.setTimeInMillis(yearSteps.get(d).first);
                            if(calendarCal.get(Calendar.MONTH) -1 == c){
                                steps = yearSteps.get(d).second;
                                break;
                            }
                        }
                        yearPoints[c] = new DataPoint(c, steps);
                    }


                    BarGraphSeries <DataPoint> series = new BarGraphSeries<>(yearPoints);
                    graph.getViewport().setScrollable(true);
                    graph.addSeries(series);
                    series.setSpacing(50);
                    series.setDrawValuesOnTop(true);
                    series.setValuesOnTopColor(Color.BLACK);


                }
                else if(adapterView.getItemAtPosition(i).toString().equals("Month")){
                    Log.d("Analytics","Month");

                    GraphView graph = (GraphView) v.findViewById(R.id.graph);
                    int totalDaysInThisMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    //clear the graph
                    graph.removeAllSeries();

                    //get current month's steps
                    List<Pair<Long, Integer>> monthSteps = db.getDayMonthSteps(Util.getFirstDayMonth(calendar.get(Calendar.MONTH)+1), Util.getLastDayMonth(calendar.get(Calendar.MONTH)+1));
                    DataPoint[] monthPoints = new DataPoint[totalDaysInThisMonth];
                    for(int c = 0; c < monthPoints.length; c++){
                        steps = 0;
                        for(int d = 0; d < monthSteps.size(); d ++) {
                            calendarCal.setTimeInMillis(monthSteps.get(d).first);
                            if(calendarCal.get(Calendar.DAY_OF_MONTH) - 1 == c){
                                steps = monthSteps.get(d).second;
                                break;
                            }
                        }
                        monthPoints[c] = new DataPoint(c+1, steps);

                    }


                    BarGraphSeries <DataPoint> series = new BarGraphSeries<>(monthPoints);
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setMinX(1);
                    graph.getViewport().setMaxX(5);
                    graph.getViewport().setScrollable(true);
                    graph.addSeries(series);
                    series.setSpacing(50);
                    series.setDrawValuesOnTop(true);
                    series.setValuesOnTopColor(Color.BLACK);

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
