package lk.webstudio.elecshop.navigations;

import static androidx.core.app.NotificationCompat.getColor;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

import lk.webstudio.elecshop.R;


public class DashboardFragment extends Fragment {




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        BarChart barChart1 = rootView.findViewById(R.id.barchart1);
        ArrayList<BarEntry> barEntryArrayList = new ArrayList<>();
        barEntryArrayList.add(new BarEntry(1,25));
        barEntryArrayList.add(new BarEntry(2,30));
        barEntryArrayList.add(new BarEntry(3,5));
        barEntryArrayList.add(new BarEntry(4,22));
        barEntryArrayList.add(new BarEntry(5,32));
        barEntryArrayList.add(new BarEntry(6,24));
        barEntryArrayList.add(new BarEntry(7,20));
        barEntryArrayList.add(new BarEntry(8,12));
        barEntryArrayList.add(new BarEntry(9,3));
        barEntryArrayList.add(new BarEntry(10,14));
        barEntryArrayList.add(new BarEntry(11,20));
        barEntryArrayList.add(new BarEntry(12,23));


        BarDataSet barDataSet = new BarDataSet(barEntryArrayList,null);
//barDataSet.setColor(R.color.darkOrange);

        barDataSet.setGradientColor(
                ContextCompat.getColor(requireContext(), R.color.darkOrange),
                ContextCompat.getColor(requireContext(), R.color.lightOrange)
        );


barDataSet.setBarShadowColor(R.color.transparent);
        BarData barData = new BarData();
        barData.addDataSet(barDataSet);
        barChart1.setPinchZoom(false);
        barChart1.setScaleXEnabled(false);
        barChart1.setScaleYEnabled(false);
barChart1.animateY(2000, Easing.EaseInCubic);
barChart1.setDescription(null);
barChart1.setDrawGridBackground(false );
barData.setBarWidth(Float.parseFloat("0.5"));
barChart1.setFitBars(true);

barChart1.setGridBackgroundColor(ContextCompat.getColor(requireContext(),R.color.transparent));
barChart1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

    }
});

        barChart1.setData(barData);
        barChart1.invalidate();



        return rootView;
    }
}