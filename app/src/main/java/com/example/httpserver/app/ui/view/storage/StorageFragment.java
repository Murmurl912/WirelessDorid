package com.example.httpserver.app.ui.view.storage;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.httpserver.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.Random;

public class StorageFragment extends Fragment {

    private StorageViewModel model;
    private LineChart chart;

    private TimeDataSet dataSet = new TimeDataSet(30, "IO");
    private Random random = new Random();
    private LineData data = new LineData(dataSet);

    public static StorageFragment newInstance() {
        return new StorageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_storage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        chart = view.findViewById(R.id.io_speed);
        chart.getDescription().setEnabled(false);
        chart.setData(data);
        chart.getAxisLeft().setEnabled(false);
        YAxis yAxis = chart.getAxisRight();
        yAxis.setEnabled(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.invalidate();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = new ViewModelProvider(this).get(StorageViewModel.class);

        long base = System.currentTimeMillis();

        model.time().observe(getViewLifecycleOwner(), time -> {
            dataSet.add(random.nextInt(100));
            dataSet.notifyDataSetChanged();
            data.notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.invalidate();
        });
    }


    private void buildLineChart() {
        chart.setViewPortOffsets(0, 0, 0, 0);
        chart.setBackgroundColor(Color.rgb(104, 241, 175));

        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        chart.setMaxHighlightDistance(300);

        XAxis x = chart.getXAxis();
        x.setEnabled(true);

        YAxis y = chart.getAxisLeft();
        y.setEnabled(true);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);

        chart.getAxisRight().setEnabled(false);

        chart.getLegend().setEnabled(false);

        chart.animateXY(2000, 2000);

        // don't forget to refresh the drawing
        chart.invalidate();
    }

    private void update() {

    }

}