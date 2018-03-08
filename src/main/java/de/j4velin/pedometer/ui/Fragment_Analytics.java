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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import de.j4velin.pedometer.R;


public class Fragment_Analytics extends android.app.Fragment {

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
        GraphView graph = (GraphView) v.findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3)
        });

        graph.addSeries(series);

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

}
